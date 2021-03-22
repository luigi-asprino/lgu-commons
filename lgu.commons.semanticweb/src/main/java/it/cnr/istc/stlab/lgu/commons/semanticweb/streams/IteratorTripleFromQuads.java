package it.cnr.istc.stlab.lgu.commons.semanticweb.streams;

import java.io.InputStream;
import java.util.Iterator;

import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.core.Quad;

import it.cnr.istc.stlab.lgu.commons.semanticweb.iterators.ClosableIterator;


public class IteratorTripleFromQuads implements Iterator<Triple>, ClosableIterator<Triple> {

	private ClosableIterator<Quad> originalIterator;

	public IteratorTripleFromQuads(ClosableIterator<Quad> originalIterator) {
		this.originalIterator = originalIterator;
	}

	@Override
	public boolean hasNext() {
		return originalIterator.hasNext();
	}

	@Override
	public Triple next() {
		return originalIterator.next().asTriple();
	}

	@Override
	public void close() {
		originalIterator.close();
	}

	@Override
	public InputStream getInputStream() {
		return originalIterator.getInputStream();
	}

}
