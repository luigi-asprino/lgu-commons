package it.cnr.istc.stlab.lgu.commons.rdf;

import java.util.Iterator;

import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.core.Quad;

public class IteratorTripleFromQuads implements Iterator<Triple> {

	private Iterator<Quad> originalIterator;

	public IteratorTripleFromQuads(Iterator<Quad> originalIterator) {
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

}
