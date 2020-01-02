package it.cnr.istc.stlab.lgu.commons.rdf;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.rdfhdt.hdt.hdt.HDTManager;
import org.rdfhdt.hdt.options.HDTSpecification;
import org.rdfhdt.hdt.rdf.TripleWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.cnr.istc.stlab.lgu.commons.files.FileUtils;

public class RDFMergeUtils {

	private static final String[] EXTENSIONS = new String[] { "xml", "rdf", "ttl", "nt", "owl" };
	private static Logger logger = LoggerFactory.getLogger(RDFMergeUtils.class);

	public static void mergeFolders(Map<String, String> nsPrefixes, String[] folderPath, String fileOut, String lang)
			throws FileNotFoundException {
		Model m = mergeFolders(nsPrefixes, folderPath);
		logger.info("Writing model");
		m.write(new FileOutputStream(new File(fileOut)), lang);
		logger.info("Model Written");
	}

	public static Model mergeFolders(Map<String, String> nsPrefixes, String[] folderPath) throws FileNotFoundException {
		Model m = ModelFactory.createDefaultModel();
		m.setNsPrefixes(nsPrefixes);
		for (int i = 0; i < folderPath.length; i++) {
			logger.info("Processing folder {}", folderPath[i]);
			for (String f : FileUtils.getFilesUnderTreeRec(folderPath[i])) {
				logger.trace("Processing file {}", f);
				if (FilenameUtils.isExtension(f, EXTENSIONS)) {
					try {
						RDFDataMgr.read(m, f);
					} catch (Exception e) {
						logger.error("{} {}", e.getMessage(), f);
					}
				}
			}
			logger.info("{} processed", folderPath[i]);
		}
		logger.info("Model size {}", m.size());
		return m;
	}

	public static void mergeAsHDT(List<String> files, String fileOut, String base) {
		System.out.println("merging");

		try {

			int filenum = 0, triples = 0;
			logger.info("Getting HDT writer");
			TripleWriter writer = HDTManager.getHDTWriter(fileOut, base, new HDTSpecification());
			for (String file : files) {


				logger.info("{}/{} - Creating iterator for {}", filenum++, files.size(), file);
				IteratorTripleStringWrapper itsw = StreamRDFUtils.createIteratorTripleStringWrapperFromFile(file);
				while (itsw.hasNext()) {
					writer.addTriple(itsw.next());
					triples++;
				}

			}
			logger.info("Triples {}", triples);
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void mergeAsNTBZ2(List<String> filesToMerge, String fileOut) {

//		TODO
//		StreamRDFUtils.createIteratorTripleFromFile(filename)
	}

}
