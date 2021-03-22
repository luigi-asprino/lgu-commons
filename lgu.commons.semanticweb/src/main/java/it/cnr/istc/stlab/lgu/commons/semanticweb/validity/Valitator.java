package it.cnr.istc.stlab.lgu.commons.semanticweb.validity;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.util.iterator.ClosableIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.cnr.istc.stlab.lgu.commons.semanticweb.streams.StreamRDFUtils;

public class Valitator {

	private static final Logger logger = LoggerFactory.getLogger(Valitator.class);

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
	
	public static void main(String[] args) {
		Model m = ModelFactory.createDefaultModel();
		RDFDataMgr.read(m, "/Users/lgu/Desktop/a.ttl");
		m.setNsPrefix("earmark", "http://www.essepuntato.it/2008/12/earmark#");
		m.setNsPrefix("semiotics", "http://ontologydesignpatterns.org/cp/owl/semiotics.owl#");
		m.setNsPrefix("ex", "http://example.org/");
		m.setNsPrefix("xsd","http://www.w3.org/2001/XMLSchema#");
		m.setNsPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		m.setNsPrefix("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
		m.setNsPrefix("dbr", "http://dbpedia.org/resource/");
		m.setNsPrefix("marl", "http://www.gsi.upm.es/ontologies/marl/ns#");
		m.setNsPrefix("pluchick", "https://w3id.org/spice/SON/PlutchikEmotion/");
		System.out.println();
		m.write(System.out,"TTL");
	}

}
