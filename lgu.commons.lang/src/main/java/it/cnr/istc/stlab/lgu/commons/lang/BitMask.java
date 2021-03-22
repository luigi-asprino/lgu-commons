package it.cnr.istc.stlab.lgu.commons.lang;

public class BitMask {

	public static long[] longBitMask(int index, int numberOfBits) {
		int length = (numberOfBits / 64) + 1;
		int elem = index / 64;
		long[] result = new long[length];
		for (int i = result.length - 1; i >= 0; i--) {
			result[i] = 0;
		}
		result[elem] = result[elem] | (1L << (index % 64));
		return result;
	}

	public static String longBitMaskToString(long[] bitmask) {
		StringBuilder sb = new StringBuilder();
		for (int i = bitmask.length - 1; i >= 0; i--) {
			sb.append(String.format("%64s", Long.toBinaryString(bitmask[i])).replace(' ', '0'));
		}
		return sb.toString();
	}

	public static long[] or(long[]... bitmasks) {
		for (int i = 1; i < bitmasks.length; i++) {
			if (bitmasks[i].length != bitmasks[i - 1].length) {
				throw new RuntimeException("Bitmask must be of the same length!");
			}
		}
		long[] result = bitmasks[0];
		for (int i = 1; i < bitmasks.length; i++) {
			for (int j = 0; j < result.length; j++) {
				result[j] = result[j] | bitmasks[i][j];
			}
		}
		return result;
	}

	public static void main(String[] args) {
		System.out.println(longBitMaskToString(or(longBitMask(0, 867), longBitMask(1, 867), longBitMask(2, 867))));
	}

}
