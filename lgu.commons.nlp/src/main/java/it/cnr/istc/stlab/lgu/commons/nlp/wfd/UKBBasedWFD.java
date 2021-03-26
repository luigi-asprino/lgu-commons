package it.cnr.istc.stlab.lgu.commons.nlp.wfd;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.vocabulary.SKOS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.cnr.istc.stlab.lgu.commons.nlp.model.AnnotatedText;
import it.cnr.istc.stlab.lgu.commons.nlp.model.AnnotationType;
import it.cnr.istc.stlab.lgu.commons.nlp.model.ScoredAnnotation;
import it.cnr.istc.stlab.lgu.commons.nlp.wsd.UKB;
import it.cnr.istc.stlab.lgu.commons.nlp.wsd.UKB.SenseInventory;

public class UKBBasedWFD {

	private UKB ukb = UKB.getInstance();
	private Map<String, Set<String>> closeMatch = new HashMap<>();
	private static UKBBasedWFD instance;
	private static final Logger logger = LoggerFactory.getLogger(UKBBasedWFD.class);

	private UKBBasedWFD() {
		try {
			Configurations configs = new Configurations();
			Configuration config = configs.properties("config.properties");
			Model m = ModelFactory.createDefaultModel();
			RDFDataMgr.read(m, config.getString("fn2wn-base"));
			AtomicInteger c = new AtomicInteger(0);
			m.listStatements(null, SKOS.closeMatch, (RDFNode) null).forEach(s -> {
				Set<String> frames = closeMatch.get(s.getObject().asResource().getURI());
				if (frames == null) {
					frames = new HashSet<>();
				}
				frames.add(s.getSubject().asResource().getURI());
				closeMatch.put(s.getObject().asResource().getURI(), frames);
				c.incrementAndGet();
			});
			logger.info("Number of mappings {}", c.get());
			System.out.println("Number of mappings " + c.get());
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}

	}

	public static UKBBasedWFD getInstance() {
		if (instance == null) {
			instance = new UKBBasedWFD();
		}
		return instance;
	}

	public AnnotatedText disambiguate(String text) {

		AnnotatedText at = ukb.disambiguate(text, SenseInventory.WN30_FRAMESTER);

		at.getSentences().forEach(sentence -> {
			sentence.getMultiWords().forEach(mw -> {
				ScoredAnnotation sa = mw.getTopScoredAnnotation(AnnotationType.WN30_FRAMESTER);
				if (sa != null) {
					logger.trace("Annotation " + sa.getAnnotation());
					Set<String> frames = closeMatch.get(sa.getAnnotation());
					if (frames != null) {
						frames.forEach(frame -> {
							mw.addAnnotation(AnnotationType.FN17, frame);
						});
					}
				}
			});
		});

		return at;

	}

}
