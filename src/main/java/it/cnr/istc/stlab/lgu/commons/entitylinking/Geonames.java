package it.cnr.istc.stlab.lgu.commons.entitylinking;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.vocabulary.RDF;

import it.cnr.istc.stlab.lgu.commons.files.FileUtils;
import it.cnr.istc.stlab.lgu.commons.model.Entity;
import it.cnr.istc.stlab.lgu.commons.web.HTTPUtils;

public class Geonames {

	private static final String URL_BASE = "http://api.geonames.org/search?type=rdf",
			CACHED_RESULTS_FOLDER = "CACHED_RESULTS";
	private static String USERNAME = null;

	public static String getGeoNamesRDF(String text, String country, String lang, String[] featureClasses)
			throws IOException {
		new File(CACHED_RESULTS_FOLDER).mkdir();

		StringBuilder sb = new StringBuilder();
		sb.append(URL_BASE);

		if (USERNAME == null) {
			USERNAME = FileUtils.readFile("geonames.username");
		}

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

		if (USERNAME == null) {
			USERNAME = FileUtils.readFile("geonames.username");
		}

		String r;
		String textDigest = DigestUtils.md5Hex(text + sb.toString());

		if (new File(CACHED_RESULTS_FOLDER + "/" + textDigest).exists()) {
			r = FileUtils.readFile(CACHED_RESULTS_FOLDER + "/" + textDigest);
		} else {
			r = HTTPUtils.makeGetRequest(sb.toString());
			FileUtils.toTextFile(r, CACHED_RESULTS_FOLDER + "/" + textDigest);
		}
		return r;

	}

	public static List<String> getGeoNames(String text, String country, String lang, String[] featureClasses)
			throws IOException {

		String r = getGeoNamesRDF(text, country, lang, featureClasses);

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

	public static String getGeoNamesRDF(String text) throws IOException {
		return getGeoNamesRDF(text, "IT", "it", new String[] { "A", "L" });
	}

	public static String getGeoNamesRDF(String text, String[] featureClass) throws IOException {
		return getGeoNamesRDF(text, "IT", "it", featureClass);
	}

	public static List<String> getGeoNames(String text, String[] featureClass) throws IOException {
		return getGeoNames(text, "IT", "it", featureClass);
	}

	public static Entity getGeolocalization(String text, String[] featureClass) throws IOException {
		String q2 = "PREFIX gn: <http://www.geonames.org/ontology#> "
				+ "PREFIX wgs84: <http://www.w3.org/2003/01/geo/wgs84_pos#> " + "SELECT ?lat ?lon {"
				+ "?f a gn:Feature . " + "?f wgs84:lat ?lat . " + "?f wgs84:long ?lon . " + "}";

		Model gn_model = ModelFactory.createDefaultModel();
		RDFDataMgr.read(gn_model, new ByteArrayInputStream(getGeoNamesRDF(text, featureClass).getBytes()), Lang.RDFXML);
		QueryExecution qexecGn = QueryExecutionFactory.create(q2, gn_model);
		ResultSet rsGn = qexecGn.execSelect();
		if (rsGn.hasNext()) {
			QuerySolution qsGn = (QuerySolution) rsGn.next();
			String lat = qsGn.getLiteral("lat").toString();
			String lon = qsGn.getLiteral("lon").toString();
			Entity e = new Entity();
			e.setLatitude(lat);
			e.setLongitude(lon);
			return e;
		}
		return null;
	}

	public static Entity getGeolocalization(String text) throws IOException {
		return getGeolocalization(text, new String[0]);
	}

}
