package it.cnr.istc.stlab.lgu.commons.nlp.wsd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.riot.RDFDataMgr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

import it.cnr.istc.stlab.lgu.commons.io.FileUtils;
import it.cnr.istc.stlab.lgu.commons.nlp.model.AnnotatedText;
import it.cnr.istc.stlab.lgu.commons.nlp.model.AnnotationType;
import it.cnr.istc.stlab.lgu.commons.nlp.model.MultiWord;
import it.cnr.istc.stlab.lgu.commons.nlp.model.ScoredAnnotation;
import it.cnr.istc.stlab.lgu.commons.nlp.model.Sentence;
import it.cnr.istc.stlab.lgu.commons.nlp.pipeline.base.StanfordPipeline;

public class UKB {

	private Pattern resultPattern = Pattern.compile("(\\S+?)/(.+?)\\s");
	private String ukb_bin;
	private String graph_wn30;
	private String dict_wn30;
	private String flags = " --ppr --nopos --allranks";
	private String tempFile = "tmp.txt";

//	public enum WnVersion {
//		WN30, WN31, WN30_FRAMESTER
//	}

	public enum SenseInventory {
		WN30, WN30_FRAMESTER
	}

//	private WnVersion wnversion;

	private static Logger logger = LoggerFactory.getLogger(UKB.class);

	private static UKB instance;
	private StanfordPipeline sp;

	private Map<String, String> offsetToURI;

	private UKB() {
		try {
			logger.info("Initialize UKB");
			sp = StanfordPipeline.getInstance();

			Configurations configs = new Configurations();
			Configuration config = configs.properties("config.properties");

//			ukb_bin = new File(getClass().getClassLoader().getResource("./ukb/bin/ukb_wsd").toURI()).getAbsolutePath();
			ukb_bin = config.getString("ukb.bin");
			Runtime.getRuntime().exec("chmod u+x " + ukb_bin);

//			graph_wn30 = new File(getClass().getClassLoader().getResource("./ukb/wn30/wnet30.bin").toURI())
//					.getAbsolutePath();
			graph_wn30 = config.getString("ukb.graph");

//			dict_wn30 = new File(getClass().getClassLoader().getResource("./ukb/wn30/wnet30_dict.txt").toURI())
//					.getAbsolutePath();

			dict_wn30 = config.getString("ukb.dict");

			logger.trace("{} {} {}", ukb_bin, graph_wn30, dict_wn30);

			initMap(config.getString("wn30.offsets"));

		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
	}

	private void initMap(String offsets) throws URISyntaxException {
		logger.trace("Offsets {}", offsets);
		Builder<String, String> b = ImmutableMap.builder();
		Model m = ModelFactory.createDefaultModel();
//		RDFDataMgr.read(m, new File(getClass().getClassLoader().getResource(offsets).toURI()).getAbsolutePath());
		RDFDataMgr.read(m, offsets);
		m.listStatements(null, m.createProperty("https://w3id.org/framester/wn/wn30/schema/ukbsynsetId"),
				(RDFNode) null).forEachRemaining(s -> {
					b.put(s.getObject().asLiteral().getValue().toString(), s.getSubject().getURI());
				});
		this.offsetToURI = b.build();
	}

	public static UKB getInstance() {
		if (instance == null) {
			instance = new UKB();
		}
		return instance;
	}

	public AnnotatedText disambiguate(String text) {
		return disambiguate(text, SenseInventory.WN30);
	}

	public AnnotatedText disambiguate(String text, SenseInventory si) {

		logger.trace("Disambiguating {}", text);

		AnnotatedText at = new AnnotatedText(text);
		sp.annotate(at);
		at.getSentences().forEach(s -> {
			String context = tokenToUKBContext(s).toLowerCase();

			List<MultiWord> mws = s.getMultiWords();

			try {

				FileUtils.toTextFile(context, tempFile);
				String commandToExecute;

				switch (si) {
				case WN30_FRAMESTER:
				case WN30:
				default:
					commandToExecute = ukb_bin.concat(flags).concat(" -K ").concat(graph_wn30).concat(" -D ")
							.concat(dict_wn30).concat(" " + tempFile);
					break;
				}

				logger.trace(commandToExecute);
				logger.trace(context);
				Process p = Runtime.getRuntime().exec(commandToExecute);
				p.waitFor();

				BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

				String line = "";

				while ((line = reader.readLine()) != null) {

					logger.trace(line);

					// The first line prints the command
					if (line.startsWith("!!")) {
						continue;
					}

					Matcher m = resultPattern.matcher(line);
					while (m.find()) {

						int wordId = getWordId(line);
						String synId = line.substring(m.start(1), m.end(1));
						String scoreString = line.substring(m.start(2), m.end(2));
						Double score = Double.parseDouble(scoreString);

						logger.trace("\t{} \"{}\" ===> \"{}\" ", wordId, synId, scoreString);

						switch (si) {
						case WN30:
							mws.get(wordId).addScoredAnnotation(AnnotationType.WN30,
									new ScoredAnnotation(SynsetIDtoURI.synsetId30toIRI(synId), score));
							break;
						case WN30_FRAMESTER:
							String uriFramester = this.offsetToURI.get(synId);
							logger.trace("URI Framester {}", uriFramester);
							if (uriFramester != null) {
								mws.get(wordId).addScoredAnnotation(AnnotationType.WN30_FRAMESTER,
										new ScoredAnnotation(uriFramester, score));
							}
							break;
//						case WN31:
//							mws.get(wordId).addScoredAnnotation(AnnotationType.WN31,
//									new ScoredAnnotation(SynsetIDtoURI.synsetId31toIRI(synId), score));
//							break;
						}
					}

				}
				reader.close();
				FileUtils.deleteFile(tempFile);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});

		return at;
	}

	private static int getWordId(String line) {
		String[] fs = line.split(" ");
		String stringNumber = fs[1].replace('w', '0');
		return Integer.parseInt(stringNumber);
	}

	private static String tokenToUKBContext(Sentence as) {
		StringBuilder sb = new StringBuilder();
		sb.append("context\n");
		int i = 0;
		for (MultiWord mw : as.getMultiWords()) {
			String word = mw.getLemma();
			sb.append(word.replaceAll("#", " ").replace(' ', '_'));
			sb.append("#"); // no pos
			sb.append("#w" + i++); // word identifier
			sb.append("#1 "); // 1 means disambiguate the word
		}
		return sb.toString();

	}

}
