package it.cnr.istc.stlab.lgu.commons.urls;

public class URLUtils {

	static final int TRUE_LENGTH = 13;
	static final char[] STRING = { 'M', 'r', ' ', 'J', 'o', 'h', 'n', ' ', 'S', 'm', 'i', 't', 'h', ' ', ' ', ' ', ' ',
			' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ' };

	public static String URLify(String s) {
		return s.replaceAll("\\s", "_");
	}

	public static String getNamespace(String uri) {
		int prefixLastCharacter = uri.lastIndexOf('#');
		if (prefixLastCharacter < 0) {
			prefixLastCharacter = uri.lastIndexOf('/');
		}
		if (prefixLastCharacter < 0) {
			prefixLastCharacter = uri.lastIndexOf(':');
		}
		if (prefixLastCharacter < 0) {
			prefixLastCharacter = uri.length() - 1;
		}
		return uri.substring(0, prefixLastCharacter + 1);
	}

	public static String getID(String uri) {
		int prefixLastCharacter = uri.lastIndexOf('#');
		if (prefixLastCharacter < 0) {
			prefixLastCharacter = uri.lastIndexOf('/');
		}
		if (prefixLastCharacter < 0) {
			prefixLastCharacter = uri.lastIndexOf(':');
		}
		if (prefixLastCharacter < 0) {
			prefixLastCharacter = uri.length() - 1;
		}
		if (prefixLastCharacter + 1 == uri.length()) {
			return uri;
		}
		return uri.substring(prefixLastCharacter + 1);
	}

	public static void main(String[] args) {
		System.out.println(getNamespace(
				"http://www.ontologydesignpatterns.org/ont/mario/cohabitationstatus.owl#Co-HabitationStatus"));
		System.out.println(getNamespace("http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Agent"));
		System.out.println(getNamespace("http://xmlns.com/foaf/0.1/Organization"));
		System.out.println(getNamespace("dbpedia:Car"));
		System.out.println(getNamespace("testwrongstring"));

		System.out.println(
				getID("http://www.ontologydesignpatterns.org/ont/mario/cohabitationstatus.owl#Co-HabitationStatus"));
		System.out.println(getID("http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Agent"));
		System.out.println(getID("http://xmlns.com/foaf/0.1/Organization"));
		System.out.println(getID("dbpedia:Car"));
		System.out.println(getID("testwrongstring"));
	}

}
