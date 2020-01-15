package it.cnr.istc.stlab.lgu.commons.arrays;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

import it.unimi.dsi.fastutil.BigSwapper;
import it.unimi.dsi.fastutil.longs.LongComparator;

public class ParallelMergeSort extends java.util.concurrent.RecursiveAction {

	private static final long serialVersionUID = 1L;
	private static final int SMALL = 7;
	private long from;
	private long to;
	private LongComparator comp;
	private BigSwapper swapper;

	public ParallelMergeSort(long from, long to, LongComparator comp, BigSwapper swapper) {
		super();
		this.from = from;
		this.to = to;
		this.comp = comp;
		this.swapper = swapper;
	}

	@Override
	protected void compute() {
		final long length = to - from;
		// Insertion sort on smallest arrays
		if (length < SMALL) {
			for (long i = from; i < to; i++) {
				for (long j = i; j > from && (comp.compare(j - 1, j) > 0); j--) {
					swapper.swap(j, j - 1);
				}
			}
			return;
		}
		// Recursively sort halves
		long mid = (from + to) >>> 1;
		ParallelMergeSort h0 = new ParallelMergeSort(from, mid, comp, swapper);
		ParallelMergeSort h1 = new ParallelMergeSort(mid, to, comp, swapper);
		invokeAll(h0, h1);
		// If list is already sorted, nothing left to do. This is an
		// optimization that results in faster sorts for nearly ordered lists.
		if (comp.compare(mid - 1, mid) <= 0)
			return;
		// Merge sorted halves
		invokeAll(new ParallelInPlaceMerge(from, mid, to, comp, swapper));
	}

	public static void mergeSort(final long from, final long to, final LongComparator comp, final BigSwapper swapper) {
		ParallelMergeSort pms = new ParallelMergeSort(from, to, comp, swapper);
		ForkJoinPool fjp = ForkJoinPool.commonPool();
		fjp.invoke(pms);
		try {
			fjp.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
