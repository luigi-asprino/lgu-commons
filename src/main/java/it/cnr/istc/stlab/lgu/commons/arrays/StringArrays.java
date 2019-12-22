package it.cnr.istc.stlab.lgu.commons.arrays;

public class StringArrays {

	public static String toString(String[] a) {

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < a.length - 1; i++) {
			sb.append('"');
			sb.append(a[i]);
			sb.append('"');
			sb.append(',');
		}
		sb.append('"');
		sb.append(a[a.length - 1]);
		sb.append('"');
		return sb.toString();

	}

}
