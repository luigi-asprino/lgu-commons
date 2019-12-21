package it.cnr.istc.stlab.lgu.commons.rdf;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.io.FilenameUtils;
import org.rdfhdt.hdt.exceptions.NotFoundException;
import org.rdfhdt.hdt.hdt.HDT;
import org.rdfhdt.hdt.hdt.HDTManager;
import org.rdfhdt.hdt.triples.TripleString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Iterators;

public class Dataset {

	private static Logger logger = LoggerFactory.getLogger(Dataset.class);

	private static Dataset instance;
	private List<HDT> hdts = new ArrayList<>();
	private List<String> files = new ArrayList<>();
	private static final String[] COMPRESSION_EXTENSIONS = { "gz", "bz2" };
	private static final String[] EXTENSIONS = { "nt", "ttl" };

	private Dataset(List<String> files, List<HDT> hdts) throws IOException {
		this.hdts = hdts;
		this.files = files;
	}

	public static Dataset getInstanceFromFileList(String filelist) throws IOException {
		if (instance != null) {
			return instance;
		}

		List<HDT> hdts = new ArrayList<>();
		List<String> plainfiles = new ArrayList<>();
		List<String> filesToProcess = new ArrayList<>();

		File folderfile = new File(filelist);
		if (folderfile.isDirectory()) {
			for (File f : folderfile.listFiles()) {
				filesToProcess.add(f.getAbsolutePath());
			}
		} else {
			filesToProcess.addAll(Arrays.asList(filelist.split(",")));
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
		instance = new Dataset(plainfiles, hdts);
		return instance;
	}

	public Iterator<TripleString> search(CharSequence s, CharSequence p, CharSequence o)
			throws NotFoundException, CompressorException, IOException {
		List<Iterator<TripleString>> listOfIterators = new ArrayList<>();
		for (HDT hdt : hdts) {
			listOfIterators.add(hdt.search(s, p, o));
		}
		for (String f : files) {
			listOfIterators.add(StreamRDFUtils.createFilteredIteratorTripleStringFromFile(f, s, p, o));
		}
		return Iterators.concat(listOfIterators.iterator());
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
		for (String f : files) {
			logger.trace("Processing file {}/{} {}", c, files.size(), f);
			result += StreamRDFUtils.estimateSearchResults(f, s, p, o);
			logger.trace("{}/{} files processed.", c, files.size());
			c++;
		}

		return result;
	}

	public List<HDT> getHdts() {
		return hdts;
	}

	public List<String> getFiles() {
		return files;
	}

}
