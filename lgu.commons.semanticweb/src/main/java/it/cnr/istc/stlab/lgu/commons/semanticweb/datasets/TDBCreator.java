
package it.cnr.istc.stlab.lgu.commons.semanticweb.datasets;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.jena.query.ReadWrite;
import org.apache.jena.tdb2.TDB2Factory;

public class TDBCreator {

	public static void main(String[] args) throws IOException {
		org.apache.jena.query.Dataset dataset = TDB2Factory
				.connectDataset("/Users/lgu/Desktop/NOTime/EKR/Benchmark/input/dump_clean_TDB");
		FileOutputStream fos = new FileOutputStream(new File("/Users/lgu/Desktop/graphs"));
		dataset.begin(ReadWrite.READ);
		dataset.listNames().forEachRemaining(l -> {
			try {
				fos.write(l.getBytes());
				fos.write('\n');
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		fos.flush();
		fos.close();
		dataset.end();
		dataset.close();
	}

}
