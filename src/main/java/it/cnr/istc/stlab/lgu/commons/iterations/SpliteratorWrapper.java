package it.cnr.istc.stlab.lgu.commons.iterations;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;

public class SpliteratorWrapper<T> implements Spliterator<T> {

	private Iterator<T> baseIterator;
	private long estimatedSize;
	private long smallestSegment = 10000;

	public SpliteratorWrapper(Iterator<T> baseIterator, long estimatedSize) {
		this.baseIterator = baseIterator;
		this.estimatedSize = estimatedSize;
	}

	@Override
	public boolean tryAdvance(Consumer<? super T> action) {
		T elem = null;
		synchronized (baseIterator) {
			if (baseIterator.hasNext()) {
				elem = baseIterator.next();
			}
		}
		if (elem != null) {
			action.accept(elem);
			return true;
		}
		return false;
	}

	@Override
	public Spliterator<T> trySplit() {
		if (estimatedSize > smallestSegment) {
			return new SpliteratorWrapper<>(baseIterator, estimatedSize / 2);
		}

		return null;
	}

	@Override
	public long estimateSize() {
		return estimatedSize;
	}

	@Override
	public int characteristics() {
		return 0;
	}

}
