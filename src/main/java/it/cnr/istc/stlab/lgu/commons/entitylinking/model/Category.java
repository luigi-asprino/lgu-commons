package it.cnr.istc.stlab.lgu.commons.entitylinking.model;

public class Category {

	private String wikipediaURL, dbpediaURL;

	public static Category getCategoryFromTagMeTag(String tag, Lang l) {
		Category c = new Category();
		tag = tag.replace(' ', '_');
		switch (l) {
		case EN:
			c.wikipediaURL = "https://en.wikipedia.org/wiki/Category:" + tag;
			c.dbpediaURL = "http://dbpedia.org/page/Category:" + tag;
			break;
		case IT:
			c.wikipediaURL = "https://it.wikipedia.org/wiki/Categoria:" + tag;
			c.dbpediaURL = "http://it.dbpedia.org/resource/Categoria:" + tag;
			break;
		default:
			break;
		}
		return c;
	}

	public String getWikipediaURL() {
		return wikipediaURL;
	}

	public String getDbpediaURL() {
		return dbpediaURL;
	}

	@Override
	public String toString() {
		return dbpediaURL + " " + wikipediaURL;
	}

}
