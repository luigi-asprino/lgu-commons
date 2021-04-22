package it.cnr.istc.stlab.lgu.commons.semanticweb.streams;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.compress.compressors.CompressorException;
import org.apache.jena.graph.Triple;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFLanguages;
import org.apache.jena.sparql.core.Quad;

import it.cnr.istc.stlab.lgu.commons.semanticweb.iterators.ClosableIterator;
import it.cnr.istc.stlab.lgu.commons.semanticweb.iterators.ClosableIteratorFromInputStream;
import it.cnr.istc.stlab.lgu.commons.streams.InputStreamFactory;

public class StreamRDFUtils {

	public static ClosableIterator<Triple> createIteratorTripleFromFile(String filename)
			throws CompressorException, IOException {
		InputStream is = InputStreamFactory.getInputStream(filename);
		return new ClosableIteratorFromInputStream<>(
				RDFDataMgr.createIteratorTriples(is, RDFLanguages.filenameToLang(filename), ""), is);
	}

	public static ClosableIterator<Quad> createIteratorQuadsFromFile(String filename)
			throws CompressorException, IOException {
		InputStream is = InputStreamFactory.getInputStream(filename);
		return new ClosableIteratorFromInputStream<>(
				RDFDataMgr.createIteratorQuads(is, RDFLanguages.filenameToLang(filename), ""), is);
	}

}
