package it.cnr.istc.stlab.lgu.commons.entitylinking.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Entity {

	private String dbpediaURL, wikipediaURL;
	@JsonIgnore
	private List<Category> categories = new ArrayList<>();

	public static Entity getEntityFromTagMeTag(String tag, Lang l) {
		Entity e = new Entity();
		tag = tag.replace(' ', '_');
		switch (l) {
		case EN:
			e.wikipediaURL = "https://en.wikipedia.org/wiki/" + tag;
			e.dbpediaURL = "http://dbpedia.org/page/" + tag;
			break;
		case IT:
			e.wikipediaURL = "https://it.wikipedia.org/wiki/" + tag;
			e.dbpediaURL = "http://it.dbpedia.org/resource/" + tag;
			break;
		}
		return e;
	}

	public void addCategory(Category g) {
		categories.add(g);
	}

	public List<Category> getCategories() {
		return categories;
	}

	public String getDbpediaURL() {
		return dbpediaURL;
	}

	public String getWikipediaURL() {
		return wikipediaURL;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(dbpediaURL + " " + wikipediaURL);
		for (Category category : categories) {
			sb.append("\n\t" + category.toString());
		}
		return sb.toString();
	}

}
