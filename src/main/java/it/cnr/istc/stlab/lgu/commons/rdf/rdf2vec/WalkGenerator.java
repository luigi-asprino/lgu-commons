package it.cnr.istc.stlab.lgu.commons.rdf.rdf2vec;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.ParameterizedSparqlString;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.Syntax;
import org.apache.jena.tdb.TDBFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.cnr.istc.stlab.lgu.commons.iterations.ProgressCounter;

/**
 * 
 * A WalkGenerator derived from RDF2Vec
 * https://datalab.rwth-aachen.de/embedding/RDF2Vec/
 * 
 * @author lgu
 *
 */
public class WalkGenerator {

	private static Logger logger = LoggerFactory.getLogger(WalkGenerator.class);

	private int depth = 8, numWalksPerEntity = 200;
	private String separator = " ", entitySelectorQuery = defaultEntitySelectorQuery;
	private static final String defaultEntitySelectorQuery = "SELECT DISTINCT ?entity WHERE {?entity ?p _:o}";
	private static final String STRING_TOKENIZER_DELIMITERS = ".;,:?!'`\\/";
	private QueryWalks queryWalks = QueryWalks.RDF2Vec;
	private Map<String, String> prefixMap = new HashMap<String, String>();

	public enum QueryWalks {
		RDF2Vec, RDF2Vec_NO_PREDICATES
	}

	public void generateWalks(String tdbFilePath, String fileOut) throws IOException {
		Dataset d = TDBFactory.createDataset(tdbFilePath);
		generateWalks(d, fileOut);
	}

	public void generateWalks(Dataset d, String fileOut) throws IOException {

		String query;

		switch (queryWalks) {
		case RDF2Vec:
		default:
			query = generateQueryRDF2Vec(depth, numWalksPerEntity);
			break;
		case RDF2Vec_NO_PREDICATES:
			query = generateQueryNoPredicates(depth, numWalksPerEntity);
			break;
		}

		ParameterizedSparqlString pss = new ParameterizedSparqlString(query);
		FileOutputStream fos = new FileOutputStream(new File(fileOut));
		Set<String> entities = selectEntities(d, entitySelectorQuery);

		logger.info("Number of entities {}", entities.size());
		int entitiesProcessed = 0;
		ProgressCounter pc = new ProgressCounter(entitiesProcessed);

		for (String e : entities) {
			pss.setIri("entity", e);
			executeQuery(d, pss.toString(), e, separator, prefixMap).forEach(s -> {
				try {
					fos.write(s.getBytes());
					fos.write('\n');
					fos.flush();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			});
			pc.increase();
		}
		fos.close();

	}

	private static Set<String> selectEntities(Dataset d, String baseQuery) {
		Set<String> result = new HashSet<>();
		d.begin(ReadWrite.READ);
		QueryExecution qe = QueryExecutionFactory.create(baseQuery, d);
		ResultSet rs = qe.execSelect();
		while (rs.hasNext()) {
			QuerySolution querySolution = (QuerySolution) rs.next();
			if (querySolution.get("entity").isURIResource()) {
				result.add(querySolution.get("entity").asResource().getURI());
			}
		}

		qe.close();
		d.end();
		return result;
	}

	private static List<String> executeQuery(Dataset d, String queryStr, String entity, String separator,
			Map<String, String> prefixMap) {
		List<String> walkList = new ArrayList<>();
		Query query = QueryFactory.create(queryStr);

		d.begin(ReadWrite.READ);
		QueryExecution qe = QueryExecutionFactory.create(query, d);
		ResultSet results = qe.execSelect();

		while (results.hasNext()) {
			QuerySolution result = results.next();
			String singleWalk = shortenEntity(entity, prefixMap) + separator;
			// construct the walk from each node or property on the path
			for (String var : results.getResultVars()) {
				try {
					// clean it if it is a literal
					if (result.get(var) != null && result.get(var).isLiteral()) {
						String val = result.getLiteral(var).getValue().toString();
						val = val.replace("\n", " ").replace("\t", " ").replace(separator, " ");
						StringTokenizer st = new StringTokenizer(val, STRING_TOKENIZER_DELIMITERS);
						while (st.hasMoreElements()) {
							singleWalk += st.nextToken().toLowerCase() + separator;
						}
					} else if (result.get(var) != null) {
						singleWalk += shortenEntity(result.get(var).toString().replace(separator, ""), prefixMap)
								+ separator;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			walkList.add(singleWalk);
		}
		qe.close();
		d.end();

		return walkList;
	}

	private static String shortenEntity(String uri, Map<String, String> prefixMap) {
		for (Map.Entry<String, String> pair : prefixMap.entrySet()) {
			if (uri.startsWith(pair.getValue())) {
				return uri.replace(pair.getValue(), pair.getKey() + ":");
			}
		}
		return uri;
	}

	private static String generateQueryRDF2Vec(int depth, int numberWalks) {
		String selectPart = "SELECT ?p ?o1";
		String mainPart = "{ ?entity ?p ?o1  ";
		String query = "";
		
		for (int i = 1; i < depth; i++) {
			mainPart += ". OPTIONAL {?o" + i + " ?p" + i + "?o" + (i + 1);
			selectPart += " ?p" + i + "?o" + (i + 1);
		}
		
		for (int i = 1; i < depth; i++) {
			mainPart += "}";
		}
		
		query = selectPart + " WHERE " + mainPart + "} LIMIT " + numberWalks;
		return query;
	}

	private static String generateQueryNoPredicates(int depth, int numberWalks) {

		String selectPart = "SELECT ?o1";
		String mainPart = "{ ?entity ?p ?o1  ";
		String query = "";
		for (int i = 1; i < depth; i++) {
			mainPart += "  OPTIONAL {?o" + i + " ?p" + i + "?o" + (i + 1);
			selectPart += " ?o" + (i + 1);
		}

		for (int i = 1; i < depth; i++) {
			mainPart += "}";
		}

		query = selectPart + " WHERE " + mainPart + "} LIMIT " + numberWalks;
		return query;
	}

	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	public int getNumWalksPerEntity() {
		return numWalksPerEntity;
	}

	public void setNumWalksPerEntity(int numWalksPerEntity) {
		this.numWalksPerEntity = numWalksPerEntity;
	}

	public String getSeparator() {
		return separator;
	}

	public void setSeparator(String separator) {
		this.separator = separator;
	}

	public String getEntitySelectorQuery() {
		return entitySelectorQuery;
	}

	public void setEntitySelectorQuery(String entitySelectorQuery) {
		this.entitySelectorQuery = entitySelectorQuery;
	}

	public QueryWalks getQueryWalks() {
		return queryWalks;
	}

	public void setQueryWalks(QueryWalks queryWalks) {
		this.queryWalks = queryWalks;
	}

	public Map<String, String> getPrefixMap() {
		return prefixMap;
	}

	public void setPrefixMap(Map<String, String> prefixMap) {
		this.prefixMap = prefixMap;
	}

	public static void main(String[] args) throws IOException {
//		WalkGenerator wg = new WalkGenerator();
//		wg.setQueryWalks(QueryWalks.RDF2Vec_NO_PREDICATES);
//
//		Map<String, String> prefixes = new HashMap<>();
//		prefixes.put("fnschema", "https://w3id.org/framester/framenet/tbox/");
//		prefixes.put("owl", "http://www.w3.org/2002/07/owl#");
//		prefixes.put("lu", "https://w3id.org/framester/framenet/abox/lu/");
//		prefixes.put("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
//		prefixes.put("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
//		prefixes.put("frame", "https://w3id.org/framester/framenet/abox/frame/");
//		prefixes.put("fe", "https://w3id.org/framester/framenet/abox/fe/");
//
//		wg.setPrefixMap(prefixes);
//
//		wg.generateWalks("/Users/lgu/Desktop/fn17", "/Users/lgu/Desktop/fn17_walks");

		System.out.println(QueryFactory.create(generateQueryNoPredicates(8, 200)).toString(Syntax.syntaxSPARQL_11));

	}

}
