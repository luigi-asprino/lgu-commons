package it.cnr.istc.stlab.lgu.commons.semanticweb.datasets;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.compress.compressors.CompressorException;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFLanguages;

import it.cnr.istc.stlab.lgu.commons.streams.InputStreamFactory;

public class Prova {
	
	public static void main(String[] args) throws CompressorException, IOException {
		String filename = "src/main/resources/test1.ttl";
		InputStream is = InputStreamFactory.getInputStream(filename);
		RDFDataMgr.createIteratorTriples(is, RDFLanguages.filenameToLang(filename), "").forEachRemaining(t->{
			System.out.println(t.toString());
		});
	}

}
