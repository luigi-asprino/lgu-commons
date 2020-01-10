package it.cnr.istc.stlab.lgu.commons.entitylinking;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.jena.query.ParameterizedSparqlString;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.riot.RDFDataMgr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.cnr.istc.stlab.lgu.commons.model.Entity;

public class DBPedia {

	private static final String CACHED_RESULTS_FOLDER = "dbpedia_cached_results";
	private static Logger logger = LoggerFactory.getLogger(DBPedia.class);

	public static Entity getGeoCodingFromURI(String uri) throws FileNotFoundException {
		new File(CACHED_RESULTS_FOLDER).mkdir();
		Model model = ModelFactory.createDefaultModel();

		String textDigest = DigestUtils.md5Hex(uri);
		if (new File(CACHED_RESULTS_FOLDER + "/" + textDigest + ".rdf").exists()) {

			logger.trace("Retrieving Cached Result");
			RDFDataMgr.read(model, CACHED_RESULTS_FOLDER + "/" + textDigest + ".rdf");

		} else {

			logger.trace("Issuing POST Request");
			RDFDataMgr.read(model, uri);
			model.write(new FileOutputStream(new File(CACHED_RESULTS_FOLDER + "/" + textDigest + ".rdf")), "RDF/XML");

		}

		StmtIterator it = model.listStatements(model.getResource(uri),
				model.getProperty("http://dbpedia.org/ontology/wikiPageRedirects"), (RDFNode) null);
		if (it.hasNext()) {
			logger.trace("has redirects");

			org.apache.jena.rdf.model.Statement s = it.next();
			uri = s.getObject().asResource().getURI();
			textDigest = DigestUtils.md5Hex(uri);
			Model mR = ModelFactory.createDefaultModel();

			if (new File(CACHED_RESULTS_FOLDER + "/" + textDigest + ".rdf").exists()) {

				logger.trace("Retrieving Cached Result");
				RDFDataMgr.read(mR, CACHED_RESULTS_FOLDER + "/" + textDigest + ".rdf");

			} else {

				// Get Model of Entity to Which this entity redirects to
				logger.trace("Issuing POST Request");
				RDFDataMgr.read(mR, s.getObject().asResource().getURI());
				mR.write(new FileOutputStream(new File(CACHED_RESULTS_FOLDER + "/" + textDigest + ".rdf")), "RDF/XML");

			}

			model = mR;

		}

		String query = "SELECT DISTINCT ?lat ?long {?r <http://dbpedia.org/property/latitude> ?lat . ?r <http://dbpedia.org/property/longitude> ?long . }";
		ParameterizedSparqlString pss = new ParameterizedSparqlString(query);
		pss.setIri("r", uri);
		QueryExecution qexec = QueryExecutionFactory.create(pss.asQuery(), model);
		ResultSet rs = qexec.execSelect();
		Entity e = new Entity();
		e.setURI(uri);
		if (rs.hasNext()) {
			QuerySolution qs = rs.next();
			e.setLatitude(qs.getLiteral("lat").getValue().toString());
			e.setLongitude(qs.getLiteral("long").getValue().toString());
		}

		return e;
	}

//	public static void main(String[] args) throws FileNotFoundException {
//		System.out.println(getGeoCodingFromURI("http://dbpedia.org/resource/Palazzo_Ludovisi").toString());
//		System.out.println(getGeoCodingFromURI("http://dbpedia.org/resource/Palazzo_Montecitorio").toString());
//
//	}

}
