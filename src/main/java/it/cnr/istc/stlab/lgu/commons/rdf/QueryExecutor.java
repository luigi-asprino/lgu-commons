package it.cnr.istc.stlab.lgu.commons.rdf;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.tdb.TDBFactory;

public class QueryExecutor {

	public static void queryTDB(String tdbPath, String query) {
		Dataset d = TDBFactory.createDataset(tdbPath);
		Query q = QueryFactory.create(query);
		d.begin(ReadWrite.READ);
		QueryExecution qe = QueryExecutionFactory.create(q, d);
		if (q.isSelectType()) {
			System.out.println(ResultSetFormatter.asText(qe.execSelect()));
		}
		d.end();
	}

	public static void main(String[] args) {
		queryTDB("/Users/lgu/Dropbox/repository/workspace/pss.services/tempFolder/TDB",
				"SELECT * {?s a <https://w3id.org/pss/ontology/Document>} LIMIT 100");
	}

}
