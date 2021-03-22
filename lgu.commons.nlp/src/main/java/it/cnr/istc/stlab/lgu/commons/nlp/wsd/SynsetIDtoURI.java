package it.cnr.istc.stlab.lgu.commons.nlp.wsd;

public class SynsetIDtoURI {

	public static String synsetId30toIRI(String id) {
		return "http://wordnet-rdf.princeton.edu/wn30/" + id;
	}

	public static String synsetId31toIRI(String id) {
		// char pos = id.charAt(id.length() - 1);
		// int poscode = posToInt(pos);
		return "https://w3id.org/framester/wn/wn31/" + id;
	}

	// static String synsetId31toIRI(String id) {
	// char pos = id.charAt(id.length() - 1);
	// int poscode = posToInt(pos);
	// return "http://wordnet-rdf.princeton.edu/wn31/" + poscode + id;
	// }

	/*
	 * Noun n 1 Verb v 2 Adjective a 3 Adverb r 4 Adjective Satellite s 3 Phrase p 4
	 */

	static int posToInt(char pos) {

		switch (pos) {
		case 'n':
			return 1;
		case 'v':
			return 2;
		case 'a':
			return 3;
		case 'r':
			return 4;
		case 's':
			return 3;
		case 'p':
			return 4;
		default:
			throw new RuntimeException();
		}
	}
}
