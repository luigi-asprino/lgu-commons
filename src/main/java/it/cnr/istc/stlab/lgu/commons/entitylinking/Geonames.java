package it.cnr.istc.stlab.lgu.commons.entitylinking;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.vocabulary.RDF;

import it.cnr.istc.stlab.lgu.commons.files.FileUtils;
import it.cnr.istc.stlab.lgu.commons.web.HTTPUtils;

public class Geonames {

	private static final String URL_BASE = "http://api.geonames.org/search?type=rdf";
	private static String USERNAME = null;

	public static List<String> getGeoNames(String text, String country, String lang, String[] featureClasses)
			throws IOException {

		if (USERNAME == null) {
			USERNAME = FileUtils.readFile("geonames.username");
		}

		StringBuilder sb = new StringBuilder();
		sb.append(URL_BASE);
		sb.append("&username=" + USERNAME);

		if (country != null) {
			sb.append("&country=" + country);
		}

		if (lang != null) {
			sb.append("&lang=" + lang);
		}

		if (featureClasses != null) {
			for (String c : featureClasses) {
				sb.append("&featureClass=" + c);
			}
		}

		sb.append("&name_equals=" + text.replace(" ", "%20"));

//		System.out.println(sb.toString());

		String r = HTTPUtils.makeGetRequest(sb.toString());
		Model m = ModelFactory.createDefaultModel();
		RDFDataMgr.read(m, new ByteArrayInputStream(r.getBytes()), Lang.RDFXML);
		ResIterator ri = m.listSubjectsWithProperty(RDF.type,
				m.createResource("http://www.geonames.org/ontology#Feature"));
		List<String> result = new ArrayList<>();
		while (ri.hasNext()) {
			Resource resource = ri.next();
			result.add(resource.getURI());
		}

		return result;
	}

	public static List<String> getGeoNames(String text) throws IOException {
		return getGeoNames(text, "IT", "it", new String[] { "A", "L" });
	}

	public static List<String> getGeoNames(String text, String[] featureClass) throws IOException {
		return getGeoNames(text, "IT", "it", featureClass);
	}

	public static void main(String[] args) throws IOException {
		getGeoNames("Europa").forEach(u -> {
			System.out.println(u);
		});
		getGeoNames("Albano Laziale ").forEach(u -> {
			System.out.println(u);
		});
	}

}
