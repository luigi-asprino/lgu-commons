package it.cnr.istc.stlab.lgu.commons.arrays;

import static it.unimi.dsi.fastutil.BigArrays.get;
import static it.unimi.dsi.fastutil.BigArrays.swap;

import java.util.Random;
import java.util.stream.IntStream;

import org.rocksdb.RocksDBException;

import it.unimi.dsi.fastutil.Arrays;
import it.unimi.dsi.fastutil.BigArrays;
import it.unimi.dsi.fastutil.ints.IntBigArrays;

public class testMerge {

	public static void main(String[] args) throws RocksDBException {

		int length = 100000000;
//		int length = 100000;
		int[][] ba1 = IntBigArrays.newBigArray(length);
		int[][] ba2 = IntBigArrays.newBigArray(length);
		int[][] ba3 = IntBigArrays.newBigArray(length);
		Random r = new Random();
		IntStream.range(0, length).parallel().forEach(l -> {
			int v = r.nextInt();
			BigArrays.set(ba1, l, v);
			BigArrays.set(ba2, l, v);
			BigArrays.set(ba3, l, v);
		});

		long t0 = System.currentTimeMillis();
		Arrays.parallelQuickSort(0, length, (k1, k2) -> get(ba3, k1) - get(ba3, k2), (k1, k2) -> swap(ba3, k1, k2));
		long t1 = System.currentTimeMillis();
		System.out.println(t1 - t0);

//		t0 = System.currentTimeMillis();
//		BigArrays.mergeSort(0, length, (k1, k2) -> get(ba1, k1) - get(ba1, k2), (k1, k2) -> swap(ba1, k1, k2));
//		t1 = System.currentTimeMillis();
//		System.out.println(t1 - t0);

		t0 = System.currentTimeMillis();
		ParallelMergeSort.mergeSort(0, length, (k1, k2) -> get(ba2, k1) - get(ba2, k2), (k1, k2) -> swap(ba2, k1, k2));
		t1 = System.currentTimeMillis();
		System.out.println(t1 - t0);

	}

}
