package it.cnr.istc.stlab.lgu.commons.semanticweb.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.io.FilenameUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.system.StreamRDF;
import org.apache.jena.riot.system.StreamRDFWriter;
import org.apache.jena.sparql.core.Quad;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.cnr.istc.stlab.lgu.commons.io.FileUtils;
import it.cnr.istc.stlab.lgu.commons.semanticweb.iterators.ClosableIterator;
import it.cnr.istc.stlab.lgu.commons.semanticweb.streams.StreamRDFUtils;
import it.cnr.istc.stlab.lgu.commons.semanticweb.validity.Validator;

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
			logger.info(String.format("Processing folder {}", folderPath[i]));
			for (String f : FileUtils.getFilesUnderTreeRec(folderPath[i])) {
				logger.trace(String.format("Processing file {}", f));
				if (FilenameUtils.isExtension(f, EXTENSIONS)) {
					try {
						RDFDataMgr.read(m, f);
					} catch (Exception e) {
						logger.error(String.format("{} {}", e.getMessage(), f));
					}
				}
			}
			logger.info(String.format("{} processed", folderPath[i]));
		}
		logger.info(String.format("Model size {}", m.size()));
		return m;
	}

	public static Model merge(Map<String, String> nsPrefixes, String[] paths) throws FileNotFoundException {
		Model m = ModelFactory.createDefaultModel();
		m.setNsPrefixes(nsPrefixes);
		for (int i = 0; i < paths.length; i++) {
			logger.info(String.format("Processing folder %s", paths[i]));
			if (new File(paths[i]).isDirectory()) {

				for (String f : FileUtils.getFilesUnderTreeRec(paths[i])) {
					logger.trace(String.format("Processing file %s", f));
					if (FilenameUtils.isExtension(f, EXTENSIONS)) {
						try {
							RDFDataMgr.read(m, f);
						} catch (Exception e) {
							logger.error(String.format("%s %s", e.getMessage(), f));
						}
					}
				}
				logger.info(String.format("%s processed", paths[i]));
			} else {
				RDFDataMgr.read(m, paths[i]);
			}
		}
		logger.info(String.format("Model size %s", m.size()));
		return m;
	}

	public static void mergeNQuadsFiles(Map<String, String> nsPrefixes, List<String> paths, String fileOut,
			Lang outSyntax, boolean compress) throws CompressorException, IOException {

		OutputStream os;

		if (compress) {
			os = new GZIPOutputStream(new FileOutputStream(new File(fileOut)));
		} else {
			os = new FileOutputStream(new File(fileOut));
		}
		StreamRDF streamOut = StreamRDFWriter.getWriterStream(os, outSyntax);

		nsPrefixes.forEach((k, v) -> {
			streamOut.prefix(k, v);
		});
		int i = 0;
		logger.info("Number of files to merge: {}", paths.size());

		for (String file : paths) {
			logger.info("Processing {}/{} {}", i++, paths.size(), file);
			ClosableIterator<Quad> streamIn = StreamRDFUtils.createIteratorQuadsFromFile(file);
			while (streamIn.hasNext()) {
				Quad q = (Quad) streamIn.next();
				if (Validator.isValid(q)) {
					streamOut.quad(q);
				} else {
					logger.warn("Skipping {}", q.toString());
				}
			}
			streamIn.close();
		}

		streamOut.finish();
		os.flush();
		os.close();

	}

}
