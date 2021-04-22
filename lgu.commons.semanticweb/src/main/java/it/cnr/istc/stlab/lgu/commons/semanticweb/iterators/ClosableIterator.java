package it.cnr.istc.stlab.lgu.commons.semanticweb.iterators;

import java.io.InputStream;

public interface ClosableIterator<T> extends org.apache.jena.util.iterator.ClosableIterator<T> {
	
	public InputStream getInputStream();

}
