package it.cnr.istc.stlab.lgu.commons.semanticweb.querying;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.Syntax;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class ResultSetRetrieverWithLimitAndOffset {

	private static Logger logger = LogManager.getLogger(ResultSetRetrieverWithLimitAndOffset.class);

	private String query, sparqlEndpointURL;
	private int limit;
	private long currentOffset = 0;

	public ResultSetRetrieverWithLimitAndOffset(String query, String sparqlEndpointURL, int limit) {
		super();
		this.query = query;
		this.limit = limit;
		this.sparqlEndpointURL = sparqlEndpointURL;
	}

	public ResultSet next() {

		String effectiveQueryString = query + " LIMIT " + limit + " OFFSET " + currentOffset;
		logger.info("Retrieving results from " + currentOffset + " to " + (currentOffset + limit));
		

		Query q = QueryFactory.create(effectiveQueryString);
		logger.trace("Firing query \n"+q.toString(Syntax.syntaxSPARQL_11)+" \n on "+sparqlEndpointURL);
		QueryExecution qexec = QueryExecutionFactory.sparqlService(sparqlEndpointURL, q);
		ResultSet result = qexec.execSelect();

		currentOffset += limit;

		return result;

	}

}
