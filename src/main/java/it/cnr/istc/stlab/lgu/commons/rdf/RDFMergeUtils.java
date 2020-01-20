package it.cnr.istc.stlab.lgu.commons.rdf;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.commons.io.FilenameUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.system.StreamRDF;
import org.apache.jena.riot.system.StreamRDFWriter;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.rdfhdt.hdt.hdt.HDTManager;
import org.rdfhdt.hdt.hdt.writer.TripleWriterHDT;
import org.rdfhdt.hdt.listener.ProgressListener;
import org.rdfhdt.hdt.options.HDTOptions;
import org.rdfhdt.hdt.options.HDTSpecification;
import org.rdfhdt.hdt.triples.TripleString;

import it.cnr.istc.stlab.lgu.commons.files.File.CompressionFormat;
import it.cnr.istc.stlab.lgu.commons.files.FileUtils;
import it.cnr.istc.stlab.lgu.commons.iterations.ProgressCounter;
import it.cnr.istc.stlab.lgu.commons.process.MemoryUtils;

public class RDFMergeUtils {

	private static final String[] EXTENSIONS = new String[] { "xml", "rdf", "ttl", "nt", "owl" };
	private static Logger logger = LogManager.getLogger(RDFMergeUtils.class);

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

	public static void mergeAsHDT(List<String> files, String tempFolder, String fileOut, String base) {
		logger.info("Merging files using RDF-HDT");

		try {
			AtomicLong processedFiles = new AtomicLong(0);

			logger.info("Getting HDT writer");
			HDTOptions opts = new HDTSpecification();

			opts.set("tempDictionary.impl", "dictionaryRocks");
			opts.set("tempTriples.impl", "rocks");
			opts.set("tempfolder", tempFolder);

			TripleWriterHDT writer = (TripleWriterHDT) HDTManager.getHDTWriter(fileOut, base, opts);
			files.parallelStream().forEach(file -> {
				logger.info(String.format("Creating stream for %s", file));
				ProgressCounter pc = new ProgressCounter();
				pc.setPrefix(file);
				pc.setLogger(logger);
				try {
					Stream<TripleString> s = StreamRDFUtils.createTripleStringStream(file).parallel();
					s.forEach(ts -> {
						try {
							writer.addTriple(ts);
							pc.increase();
						} catch (IOException e) {
							e.printStackTrace();
						}
					});

					logger.info(String.format("Memory Used %s/%s",
							MemoryUtils.humanReadableByteCount(
									Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory(), true),
							MemoryUtils.humanReadableByteCount(Runtime.getRuntime().totalMemory(), true)));
					logger.info("Running Garbage Collector");
					s.close();
					System.gc();
					logger.info(String.format("Memory Used %s/%s",
							MemoryUtils.humanReadableByteCount(Runtime.getRuntime().freeMemory(), true),
							MemoryUtils.humanReadableByteCount(Runtime.getRuntime().totalMemory(), true)));
				} catch (CompressorException | IOException e) {
					e.printStackTrace();
				}
				logger.info(String.format("%d/%d", processedFiles.incrementAndGet(), files.size()));
			});
			writer.close(new ProgressListener() {
				@Override
				public void notifyProgress(float level, String message) {
					logger.info(message);
				}
			});
			logger.info(String.format("Memory Used %s/%s",
					MemoryUtils.humanReadableByteCount(Runtime.getRuntime().freeMemory(), true),
					MemoryUtils.humanReadableByteCount(Runtime.getRuntime().totalMemory(), true)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info(String.format("Memory Used %s/%s",
				MemoryUtils.humanReadableByteCount(Runtime.getRuntime().freeMemory(), true),
				MemoryUtils.humanReadableByteCount(Runtime.getRuntime().totalMemory(), true)));
	}

	public static void mergeFiles(List<String> filesToMerge, String fileOut, Lang lang, CompressionFormat cf)
			throws CompressorException, IOException {

		logger.info("Merging files");

		OutputStream os;
		switch (cf) {
		case BZ2:
			os = new CompressorStreamFactory().createCompressorOutputStream(CompressorStreamFactory.BZIP2,
					new FileOutputStream(new File(fileOut)));
			break;
		case GZ:
			os = new GZIPOutputStream(new FileOutputStream(new File(fileOut)));
			break;
		default:
			os = new FileOutputStream(new File(fileOut));
		}

		StreamRDF stream = StreamRDFWriter.getWriterStream(os, lang);
		int c = 0;
		for (String file : filesToMerge) {
			logger.info(String.format("Parsing {}/{} {}", c++, filesToMerge.size(), file));
			RDFDataMgr.parse(stream, file);
		}

		os.flush();
		stream.finish();
		os.close();
	}

}
