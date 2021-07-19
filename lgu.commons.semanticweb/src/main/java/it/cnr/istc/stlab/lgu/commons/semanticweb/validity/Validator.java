package it.cnr.istc.stlab.lgu.commons.semanticweb.validity;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.iri.IRI;
import org.apache.jena.iri.IRIFactory;
import org.apache.jena.sparql.core.Quad;
import org.apache.jena.util.iterator.ClosableIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.cnr.istc.stlab.lgu.commons.semanticweb.streams.StreamRDFUtils;

public class Validator {

	private static final Logger logger = LoggerFactory.getLogger(Validator.class);

	public static List<String> validateFolder(String folder) throws IOException {
		List<String> notValid = new ArrayList<>();
		Files.walk(Paths.get(folder)).filter(f -> !f.toFile().isHidden()).forEach(f -> {
			logger.info("Processing {}", f.toFile().getAbsolutePath());
			if (!f.toFile().isDirectory() && !validateFile(f.toFile().getAbsolutePath())) {
				logger.info("Not valid {}", f.toFile().getAbsolutePath());
				notValid.add(f.toFile().getAbsolutePath());
			}
		});

		return notValid;

	}

	public static boolean validateFile(String file) {

		try {
			ClosableIterator<?> it = StreamRDFUtils.createIteratorFromFile(file);
			while (it.hasNext()) {
				it.next();
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;

	}

	public static boolean isValid(Quad q) {
		return isValid(q.getGraph()) && isValid(q.asTriple());
	}

	public static boolean isValid(Triple t) {
		return isValid(t.getSubject()) && isValid(t.getPredicate()) && isValid(t.getObject());
	}

	public static boolean isValid(Node n) {
		if (n.isURI()) {
			IRIFactory factory = IRIFactory.iriImplementation();
			try {
				IRI iri = factory.create(n.getURI());
				return !iri.hasViolation(false);
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
		return true;
	}

}
