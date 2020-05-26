package it.cnr.istc.stlab.lgu.commons.rdf;

import java.io.File;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.tdb.TDBFactory;

public class RDFTransformer {

	public enum Format {
		TTL, TDB, NT,NQ
	}

	private Format in, out;
	private String pathFileIn, pathFileOut, tempFolder;

	public RDFTransformer(Format in, Format out, String pathFileIn, String pathFileOut, String tempFolder) {
		super();
		this.in = in;
		this.out = out;
		this.pathFileIn = pathFileIn;
		this.pathFileOut = pathFileOut;
		this.tempFolder = tempFolder;
	}

	public void transform() {
		new File(tempFolder).mkdir();

		Dataset ds;
		if (out == Format.TDB) {
			ds = TDBFactory.createDataset(pathFileOut);
		} else {
			ds = TDBFactory.createDataset(tempFolder + "/tdb");
		}

		switch (in) {
		case TTL:
			Model m = ModelFactory.createDefaultModel();
			RDFDataMgr.read(m, pathFileIn);
			ds.begin(ReadWrite.WRITE);
			ds.getDefaultModel().add(m);
			ds.commit();
			ds.close();
			break;
		case NQ:
			RDFDataMgr.read(ds, pathFileIn);
			break;
		default:
			break;
		}

	}

	public static void main(String[] args) {
		RDFTransformer r = new RDFTransformer(Format.TTL, Format.TDB, "/Users/lgu/Desktop/fn17.ttl", "/Users/lgu/Desktop/fn17",
				"/Users/lgu/Desktop/tmp");
		r.transform();
	}

}
