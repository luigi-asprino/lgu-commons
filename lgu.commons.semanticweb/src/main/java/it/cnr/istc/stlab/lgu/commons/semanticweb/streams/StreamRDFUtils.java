package it.cnr.istc.stlab.lgu.commons.semanticweb.streams;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.stream.Stream;

import org.apache.commons.compress.compressors.CompressorException;
import org.apache.jena.graph.Triple;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFLanguages;
import org.apache.jena.sparql.core.Quad;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.rdfhdt.hdt.exceptions.ParserException;
import org.rdfhdt.hdt.triples.TripleString;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;

import it.cnr.istc.stlab.lgu.commons.misc.ProgressCounter;
import it.cnr.istc.stlab.lgu.commons.semanticweb.files.SWFileUtils;
import it.cnr.istc.stlab.lgu.commons.semanticweb.iterators.ClosableIterator;
import it.cnr.istc.stlab.lgu.commons.semanticweb.iterators.ClosableIteratorFromInputStream;
import it.cnr.istc.stlab.lgu.commons.streams.InputStreamFactory;

public class StreamRDFUtils {

	public static ClosableIterator<Triple> createIteratorTripleFromFile(String filename)
			throws CompressorException, IOException {
		InputStream is = InputStreamFactory.getInputStream(filename);
		return new ClosableIteratorFromInputStream<>(
				RDFDataMgr.createIteratorTriples(is, RDFLanguages.filenameToLang(filename), ""), is);
	}

	public static ClosableIterator<Quad> createIteratorQuadsFromFile(String filename)
			throws CompressorException, IOException {
		InputStream is = InputStreamFactory.getInputStream(filename);
		return new ClosableIteratorFromInputStream<>(
				RDFDataMgr.createIteratorQuads(is, RDFLanguages.filenameToLang(filename), ""), is);
	}

	public static ClosableIterator<?> createIteratorFromFile(String filename) throws CompressorException, IOException {
		if (SWFileUtils.isTripleExension(filename)) {
			return createIteratorTripleFromFile(filename);
		} else {
			return createIteratorQuadsFromFile(filename);
		}
	}

	public static Stream<TripleString> createFilteredTripleStream(String f, CharSequence s, CharSequence p,
			CharSequence o) throws CompressorException, IOException {
		return createTripleStringStream(f).filter(t -> {
			return t.match(new TripleString(s, p, o));
		});
	}

	public static Stream<TripleString> createTripleStringStream(String file) throws CompressorException, IOException {
		Stream<String> lineStream = InputStreamFactory.getInputLineStream(file);
		return lineStream.map(lineString -> {

			TripleString ts = new TripleString();
			try {
				ts.read(lineString);
			} catch (ParserException e) {
				e.printStackTrace();
			}
			return ts;
		}).onClose(() -> lineStream.close());
	}

	public static long estimateSearchResults(String f, CharSequence s, CharSequence p, CharSequence o)
			throws CompressorException, IOException {
		long r = 0;
		ProgressCounter pc = new ProgressCounter();
		ClosableIterator<TripleString> itsw = createFilteredIteratorTripleStringFromFile(f, s, p, o);
		while (itsw.hasNext()) {
			pc.increase();
			itsw.next();
			r++;
		}
		itsw.close();
		return r;

	}

	public static ClosableIterator<TripleString> createFilteredIteratorTripleStringFromQuadsFile(String f,
			CharSequence s, CharSequence p, CharSequence o) throws CompressorException, IOException {

		IteratorTripleStringWrapper itsw = createIteratorTripleStringWrapperFromQuadsFile(f);
		return filter(itsw, s, p, o);

	}

	public static IteratorTripleStringWrapper createIteratorTripleStringWrapperFromQuadsFile(String filename)
			throws CompressorException, IOException {
		return new IteratorTripleStringWrapper(
				new IteratorTripleFromQuads(StreamRDFUtils.createIteratorQuadsFromFile(filename)));
	}

	public static ClosableIterator<TripleString> createFilteredIteratorTripleStringFromFile(String f, CharSequence s,
			CharSequence p, CharSequence o) throws CompressorException, IOException {

		ClosableIterator<TripleString> itsw = createIteratorTripleStringWrapperFromFile(f);
		return filter(itsw, s, p, o);

	}

	public static ClosableIterator<TripleString> filter(ClosableIterator<TripleString> its, CharSequence s,
			CharSequence p, CharSequence o) {
		Iterator<TripleString> r = Iterators.filter(its, new Predicate<TripleString>() {
			@Override
			public boolean apply(@Nullable TripleString ts) {
				return ts.match(new TripleString(s, p, o));
			}
		});

		return new ClosableIteratorFromInputStream<>(r, its.getInputStream());
	}

	public static ClosableIterator<TripleString> createIteratorTripleStringWrapperFromFile(String filename)
			throws CompressorException, IOException {
		InputStream is = InputStreamFactory.getInputStream(filename);
		SWFileUtils.Format f = SWFileUtils.getSWFormat(filename);
		switch (f) {
		case NQ:
			return new ClosableIteratorFromInputStream<>(createIteratorTripleStringWrapperFromQuadsFile(filename), is);
		case NT:
		case TTL:
		default:
			return new IteratorTripleStringWrapper(new ClosableIteratorFromInputStream<>(
					RDFDataMgr.createIteratorTriples(is, RDFLanguages.filenameToLang(filename), ""), is));
		}

	}

}
