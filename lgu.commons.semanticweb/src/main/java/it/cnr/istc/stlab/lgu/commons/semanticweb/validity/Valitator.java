package it.cnr.istc.stlab.lgu.commons.semanticweb.validity;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.core.DatasetGraphFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.cnr.istc.stlab.lgu.commons.semanticweb.files.SWFileUtils;

public class Valitator {

	private static final Logger logger = LoggerFactory.getLogger(Valitator.class);

	public static List<String> validateFolder(String folder) throws IOException {
		List<String> notValid = new ArrayList<>();
		Files.walk(Paths.get(folder)).filter(f -> !f.toFile().isHidden()).forEach(f -> {
			logger.info("Processing {}", f.toFile().getAbsolutePath());
			if (!validateFile(f.toFile().getAbsolutePath())) {
				logger.info("Not valid {}", f.toFile().getAbsolutePath());
				notValid.add(f.toFile().getAbsolutePath());
			}
		});

		return notValid;

	}

	public static boolean validateFile(String file) {

		try {
			if (SWFileUtils.isTripleExension(file)) {
				Model m = ModelFactory.createDefaultModel();
				RDFDataMgr.read(m, file);
			} else if (SWFileUtils.isQuadExtension(file)) {
				DatasetGraph ds = DatasetGraphFactory.create();
				RDFDataMgr.read(ds, file);
			}
			return true;
		} catch (Exception e) {
			logger.info("Not valid {}", file);
		}
		return false;

	}

}
