package it.cnr.istc.stlab.lgu.commons.semanticweb.datasets;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.io.FilenameUtils;
import org.rdfhdt.hdt.exceptions.NotFoundException;
import org.rdfhdt.hdt.hdt.HDT;
import org.rdfhdt.hdt.hdt.HDTManager;
import org.rdfhdt.hdt.triples.TripleString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Iterators;

import it.cnr.istc.stlab.lgu.commons.semanticweb.streams.StreamRDFUtils;

public class Dataset {

	private static Logger logger = LoggerFactory.getLogger(Dataset.class);

//	private static Dataset instance;
	private List<HDT> hdts = new ArrayList<>();
	private List<String> tripleFiles = new ArrayList<>();
	private List<String> quadsFiles = new ArrayList<>();
	private static final String[] COMPRESSION_EXTENSIONS = { "gz", "bz2" };
	private static final String[] EXTENSIONS = { "nt", "ttl", "nq" };
	private static final String[] TRIPLE_EXTENSIONS = { "nt", "ttl" };
	private static final String[] QUADS_EXTENSIONS = { "nq" };

	private Dataset(List<String> files, List<HDT> hdts) throws IOException {
		this.hdts = hdts;
		for (String f : files) {

			if (FilenameUtils.isExtension(f, COMPRESSION_EXTENSIONS)) {
				if (FilenameUtils.isExtension(FilenameUtils.removeExtension(f), TRIPLE_EXTENSIONS)) {
					tripleFiles.add(f);
				}

				if (FilenameUtils.isExtension(FilenameUtils.removeExtension(f), QUADS_EXTENSIONS)) {
					quadsFiles.add(f);
				}
			} else {
				if (FilenameUtils.isExtension(f, TRIPLE_EXTENSIONS)) {
					tripleFiles.add(f);
				}

				if (FilenameUtils.isExtension(f, QUADS_EXTENSIONS)) {
					quadsFiles.add(f);
				}
			}

		}
	}

	public static Dataset getInstanceFromFileList(Iterable<String> files) throws IOException {
//		if (instance != null) {
//			return instance;
//		}

		List<HDT> hdts = new ArrayList<>();
		List<String> plainfiles = new ArrayList<>();
		List<String> filesToProcess = new ArrayList<>();

		for (String filename : files) {
			File file = new File(filename);
			if (file.isDirectory()) {
				for (File f : file.listFiles()) {
					filesToProcess.add(f.getAbsolutePath());
				}
			} else {
				filesToProcess.add(filename);
			}
		}

		for (String f : filesToProcess) {
			if (FilenameUtils.getExtension(f).equalsIgnoreCase("hdt")) {
				logger.info("Mapping HDT {}", f);
				HDT hdt = HDTManager.mapIndexedHDT(f, null);
				hdts.add(hdt);
				logger.info("HDT mapped");
			} else {
				if (FilenameUtils.isExtension(f, COMPRESSION_EXTENSIONS)) {
					if (FilenameUtils.isExtension(FilenameUtils.removeExtension(f), EXTENSIONS)) {
						plainfiles.add(f);
					}
				} else if (FilenameUtils.isExtension(f, EXTENSIONS)) {
					plainfiles.add(f);
				}
			}
		}
		return new Dataset(plainfiles, hdts);
	}

	public Iterator<TripleString> search(CharSequence s, CharSequence p, CharSequence o)
			throws NotFoundException, CompressorException, IOException {
		List<Iterator<TripleString>> listOfIterators = new ArrayList<>();
		for (HDT hdt : hdts) {
			listOfIterators.add(hdt.search(s, p, o));
		}
		for (String f : tripleFiles) {
			listOfIterators.add(StreamRDFUtils.createFilteredIteratorTripleStringFromFile(f, s, p, o));
		}
		for (String f : quadsFiles) {
			listOfIterators.add(StreamRDFUtils.createFilteredIteratorTripleStringFromQuadsFile(f, s, p, o));
		}

		return Iterators.concat(listOfIterators.iterator());
	}

	public Stream<TripleString> searchStream(CharSequence s, CharSequence p, CharSequence o)
			throws NotFoundException, CompressorException, IOException {

		Stream<TripleString> r = Stream.empty();
		for (HDT hdt : hdts) {
			Iterator<TripleString> its = hdt.search(s, p, o);
			Spliterator<TripleString> si = Spliterators.spliteratorUnknownSize(its, 0);
			r = Stream.concat(r, StreamSupport.stream(si, true));
		}

		for (String f : tripleFiles) {
			r = Stream.concat(r, StreamRDFUtils.createFilteredTripleStream(f, s, p, o));
		}
		for (String f : quadsFiles) {
			r = Stream.concat(r, StreamSupport.stream(Spliterators.spliteratorUnknownSize(StreamRDFUtils.createFilteredIteratorTripleStringFromQuadsFile(f, s, p, o), 0), true));
		}
		return r;
	}

	public long estimateSearch(CharSequence s, CharSequence p, CharSequence o)
			throws NotFoundException, CompressorException, IOException {
		logger.trace("\"{}\" \"{}\" \"{}\"", s, p, o);
		long result = 0;
		logger.trace("Estimating results on HDTs");
		int c = 0;
		for (HDT hdt : hdts) {
			result += hdt.search(s, p, o).estimatedNumResults();
			logger.trace("{}/{} files processed.", c, hdts.size());
			c++;
		}

		logger.trace("Estimating results on files");
		c = 0;
		for (String f : tripleFiles) {
			logger.trace("Processing file {}/{} {}", c, tripleFiles.size(), f);
			result += StreamRDFUtils.estimateSearchResults(f, s, p, o);
			logger.trace("{}/{} files processed.", c, tripleFiles.size());
			c++;
		}

		return result;
	}

	public List<HDT> getHdts() {
		return hdts;
	}

	public List<String> getFiles() {
		List<String> result = new ArrayList<>();
		result.addAll(tripleFiles);
		result.addAll(quadsFiles);
		return result;
	}

}
