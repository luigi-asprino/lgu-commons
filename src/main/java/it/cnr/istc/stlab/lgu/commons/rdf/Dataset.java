package it.cnr.istc.stlab.lgu.commons.rdf;

import java.io.IOException;
import java.util.ArrayList;
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

	private Dataset(List<String> files, List<HDT> hdts) throws IOException {
		this.hdts = hdts;
		this.files = files;
	}

	public static Dataset getInstanceFromFileList(String filelist) throws IOException {
		if (instance != null) {
			return instance;
		}
		String[] files = filelist.split(",");
		List<HDT> hdts = new ArrayList<>();
		List<String> plainfiles = new ArrayList<>();
		for (String f : files) {
			if (FilenameUtils.getExtension(f).equalsIgnoreCase("hdt")) {
				logger.info("Mapping HDT {}", f);
				HDT hdt = HDTManager.mapIndexedHDT(f, null);
				hdts.add(hdt);
				logger.info("HDT mapped");
			} else {
				plainfiles.add(f);
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
		long result = 0;
		for (HDT hdt : hdts) {
			result += hdt.search(s, p, o).estimatedNumResults();
		}

		for (String f : files) {
			result += StreamRDFUtils.estimateSearchResults(f, s, p, o);
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
