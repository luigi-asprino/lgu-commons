package it.cnr.istc.stlab.lgu.commons.rdf;

import java.util.Iterator;

import org.apache.jena.graph.Triple;
import org.rdfhdt.hdt.enums.ResultEstimationType;
import org.rdfhdt.hdt.triples.IteratorTripleString;
import org.rdfhdt.hdt.triples.TripleString;

public class IteratorTripleStringWrapper implements IteratorTripleString {

	private Iterator<Triple> its;

	public IteratorTripleStringWrapper(Iterator<Triple> its) {
		this.its = its;
	}

	@Override
	public boolean hasNext() {
		return its.hasNext();
	}

	@Override
	public TripleString next() {
		Triple t = its.next();
		return new TripleString(t.getSubject().toString(), t.getPredicate().toString(), t.getObject().toString());
	}

	@Override
	public void goToStart() {
		throw new UnsupportedOperationException();
	}

	@Override
	public long estimatedNumResults() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ResultEstimationType numResultEstimation() {
		throw new UnsupportedOperationException();
	}
	
	

}
