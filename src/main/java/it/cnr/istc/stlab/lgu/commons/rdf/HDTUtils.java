package it.cnr.istc.stlab.lgu.commons.rdf;

import java.io.IOException;

import org.rdfhdt.hdt.enums.RDFNotation;
import org.rdfhdt.hdt.exceptions.NotFoundException;
import org.rdfhdt.hdt.hdt.HDT;
import org.rdfhdt.hdt.hdt.HDTManager;
import org.rdfhdt.hdt.listener.ProgressListener;
import org.rdfhdt.hdt.options.HDTSpecification;
import org.rdfhdt.hdt.triples.IteratorTripleString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HDTUtils {

	private static Logger logger = LoggerFactory.getLogger(HDTUtils.class);

	public static void queryHDT(String hdtFile, CharSequence s, CharSequence p, CharSequence o, int limit)
			throws IOException, NotFoundException {
		HDT hdt = HDTManager.mapIndexedHDT(hdtFile, null);
		IteratorTripleString its = hdt.search(s, p, o);
		logger.info("Num of results {}", its.estimatedNumResults());
		int c = 0;
		while (its.hasNext()) {
			logger.info("{}", its.next().toString());
			if (c++ == limit)
				break;
		}
	}

	public static void transformInHDT(String fileIn, String fileOut, String base) throws Exception {
		logger.info("Generate HDT from {}", fileIn);
		HDTManager.generateHDT(fileIn, base, RDFNotation.NTRIPLES, new HDTSpecification(), new ProgressListener() {
			@Override
			public void notifyProgress(float level, String message) {
				logger.info("{}", message);
			}
		});
		logger.info("{} generated!",fileOut);
//		TripleWriter writer = HDTManager.getHDTWriter(fileOut, base, new HDTSpecification());
//		logger.trace("Getting writer");
//		ClosableIterator<TripleString> itsw = StreamRDFUtils.createIteratorTripleStringWrapperFromFile(fileIn);
//		while (itsw.hasNext()) {
//			writer.addTriple(itsw.next());
//		}
//		itsw.close();
//		logger.trace("Closing");
//		writer.close();
	}

}
