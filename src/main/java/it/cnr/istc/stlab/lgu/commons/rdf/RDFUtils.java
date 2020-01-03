package it.cnr.istc.stlab.lgu.commons.rdf;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.apache.jena.graph.NodeFactory;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.core.DatasetGraphFactory;

import it.cnr.istc.stlab.lgu.commons.files.File.Format;

public class RDFUtils {

	public static void writeModelOnNQuadsFile(Model m, String uri, String file) throws FileNotFoundException {
		DatasetGraph d = DatasetGraphFactory.create();
		d.begin(ReadWrite.WRITE);
		d.addGraph(NodeFactory.createURI(uri), m.getGraph());
		d.commit();
		d.end();
		d.close();
		RDFDataMgr.write(new FileOutputStream(new File(file)), d, RDFFormat.NQ);
	}

	public static Format getFormat(String filename) {
		it.cnr.istc.stlab.lgu.commons.files.File f = new it.cnr.istc.stlab.lgu.commons.files.File(filename);
		return f.getFormat();
	}
}
