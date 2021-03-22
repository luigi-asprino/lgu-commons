package it.cnr.istc.stlab.lgu.commons.semanticweb.iterators;

import java.util.Iterator;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.core.Quad;

public class IteratorQuadFromTripleIterator implements Iterator<Quad> {

	private Iterator<Triple> originalIterator;
	private Node graphNode;

	public IteratorQuadFromTripleIterator(Iterator<Triple> originalIterator, String graphURI) {
		this.originalIterator = originalIterator;
		this.graphNode = NodeFactory.createURI(graphURI);
	}

	@Override
	public boolean hasNext() {
		return originalIterator.hasNext();
	}

	@Override
	public Quad next() {
		return new Quad(graphNode, originalIterator.next());
	}

}
