package it.cnr.istc.stlab.lgu.commons.semanticweb.streams;

import java.io.InputStream;

import org.apache.jena.graph.Triple;
import org.rdfhdt.hdt.enums.ResultEstimationType;
import org.rdfhdt.hdt.triples.IteratorTripleString;
import org.rdfhdt.hdt.triples.TripleString;

import it.cnr.istc.stlab.lgu.commons.semanticweb.iterators.ClosableIterator;


public class IteratorTripleStringWrapper implements IteratorTripleString, ClosableIterator<TripleString> {

	private ClosableIterator<Triple> its;

	public IteratorTripleStringWrapper(ClosableIterator<Triple> its) {
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

	@Override
	public void close() {
		its.close();
	}

	@Override
	public InputStream getInputStream() {
		return its.getInputStream();
	}

}
