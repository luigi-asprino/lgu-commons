
package it.cnr.istc.stlab.lgu.commons.semanticweb.cleaning;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.compress.compressors.CompressorException;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.system.StreamRDF;
import org.apache.jena.riot.system.StreamRDFWriter;

import it.cnr.istc.stlab.lgu.commons.io.streams.InputStreamFactory;
import it.cnr.istc.stlab.lgu.commons.semanticweb.iterators.RobustQuadReader;

public class DatasetCleaner {

	private String in, out;
	private boolean compress = true;

	public DatasetCleaner(String in, String out, boolean compress) {
		super();
		this.in = in;
		this.out = out;
		this.compress = compress;
	}

	public void clean() throws IOException, CompressorException {
		OutputStream os;

		if (compress) {
			os = new GZIPOutputStream(new FileOutputStream(new File(out)));
		} else {
			os = new FileOutputStream(new File(out));
		}
		StreamRDF streamOut = StreamRDFWriter.getWriterStream(os, Lang.NQ);
		InputStream is = InputStreamFactory.getInputStream(in);
		RobustQuadReader r = new RobustQuadReader(is, streamOut);
		r.read();

		os.flush();
		os.close();
	}

	public static void main(String[] args) throws IOException, CompressorException {

		DatasetCleaner dc = new DatasetCleaner("/Users/lgu/Desktop/NOTime/EKR/Benchmark/input/dump.nq.gz",
				"/Users/lgu/Desktop/NOTime/EKR/Benchmark/input/dump_clean.nq.gz", true);
		dc.clean();
	}

}
