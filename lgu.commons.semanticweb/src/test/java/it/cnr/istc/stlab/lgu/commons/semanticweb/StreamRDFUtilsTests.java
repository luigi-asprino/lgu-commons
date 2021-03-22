package it.cnr.istc.stlab.lgu.commons.semanticweb;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.commons.compress.compressors.CompressorException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.rdfhdt.hdt.triples.TripleString;

import it.cnr.istc.stlab.lgu.commons.semanticweb.iterators.ClosableIterator;
import it.cnr.istc.stlab.lgu.commons.semanticweb.streams.StreamRDFUtils;

public class StreamRDFUtilsTests {

	@Rule
	public TestName name = new TestName();

	private String getTestLocation(String fileName) throws URISyntaxException {
//		return getClass().getClassLoader().getResource(fileName).toURI().toString();
		return "src/main/resources/" + fileName;
	}

	@Test
	public void test1() {
		try {
			ClosableIterator<TripleString> ci = StreamRDFUtils
					.createIteratorTripleStringWrapperFromFile(getTestLocation(name.getMethodName() + ".ttl"));
			ci.forEachRemaining(ts -> {
				System.out.println(ts.toString());
			});
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (CompressorException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		
	}

}
