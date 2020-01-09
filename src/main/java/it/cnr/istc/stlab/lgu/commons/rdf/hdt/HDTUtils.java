package it.cnr.istc.stlab.lgu.commons.rdf.hdt;

import java.io.IOException;

import org.rdfhdt.hdt.exceptions.NotFoundException;
import org.rdfhdt.hdt.hdt.HDT;
import org.rdfhdt.hdt.hdt.HDTManager;
import org.rdfhdt.hdt.hdt.writer.TripleWriterHDT;
import org.rdfhdt.hdt.listener.ProgressListener;
import org.rdfhdt.hdt.options.HDTOptions;
import org.rdfhdt.hdt.options.HDTSpecification;
import org.rdfhdt.hdt.rdf.TripleWriter;
import org.rdfhdt.hdt.triples.IteratorTripleString;
import org.rdfhdt.hdt.triples.TripleString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.cnr.istc.stlab.lgu.commons.iterations.ClosableIterator;
import it.cnr.istc.stlab.lgu.commons.iterations.ProgressCounter;
import it.cnr.istc.stlab.lgu.commons.rdf.StreamRDFUtils;

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

		TripleWriter writer = HDTManager.getHDTWriter(fileOut, base, new HDTSpecification());
		logger.trace("Getting writer");
		ClosableIterator<TripleString> itsw = StreamRDFUtils.createIteratorTripleStringWrapperFromFile(fileIn);
		ProgressCounter pc = new ProgressCounter();
		while (itsw.hasNext()) {
			writer.addTriple(itsw.next());
			pc.increase();
		}
		itsw.close();
		logger.trace("Closing");
		writer.close();
	}

	public static void transformInHDT(String fileIn, String fileOut, String tempFolder, String base) throws Exception {

		HDTOptions opts = new HDTSpecification();

		opts.set("tempDictionary.impl", "dictionaryRocks");
		opts.set("tempTriples.impl", "rocks");
		opts.set("tempfolder", tempFolder);
//		opts.set("dictionary.type", "dictionaryFourBig");

		TripleWriterHDT writer = (TripleWriterHDT) HDTManager.getHDTWriter(fileOut, base, opts);
		logger.info("Getting writer");
		ClosableIterator<TripleString> itsw = StreamRDFUtils.createIteratorTripleStringWrapperFromFile(fileIn);
		ProgressCounter pc = new ProgressCounter();
		while (itsw.hasNext()) {
			writer.addTriple(itsw.next());
			pc.increase();
		}
		itsw.close();
		logger.info("Closing");
		writer.close(new ProgressListener() {

			@Override
			public void notifyProgress(float level, String message) {
				logger.info("Close:: {}", message);
			}
		});
	}

}
