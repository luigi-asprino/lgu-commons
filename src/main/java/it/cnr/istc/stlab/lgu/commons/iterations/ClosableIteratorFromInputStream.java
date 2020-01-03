package it.cnr.istc.stlab.lgu.commons.iterations;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

public class ClosableIteratorFromInputStream<T> implements ClosableIterator<T> {
	private Iterator<T> it;
	private InputStream is;

	public ClosableIteratorFromInputStream(Iterator<T> it, InputStream is) {
		this.it = it;
		this.is = is;
	}

	public ClosableIteratorFromInputStream(ClosableIteratorFromInputStream<T> itOld) {
		this.it = itOld.it;
		this.is = itOld.is;
	}

	@Override
	public boolean hasNext() {
		return it.hasNext();
	}

	@Override
	public T next() {
		return it.next();
	}

	@Override
	public void close() {
		try {
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public InputStream getInputStream() {
		return is;
	}

}
