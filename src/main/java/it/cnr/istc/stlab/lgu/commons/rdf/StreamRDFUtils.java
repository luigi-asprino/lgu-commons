package it.cnr.istc.stlab.lgu.commons.rdf;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import org.apache.commons.compress.compressors.CompressorException;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFLanguages;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.rdfhdt.hdt.triples.IteratorTripleString;
import org.rdfhdt.hdt.triples.TripleString;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;

import it.cnr.istc.stlab.lgu.commons.files.InputStreamFactory;

public class StreamRDFUtils {

	public static Iterator<Triple> createIteratorTripleFromFile(String filename)
			throws CompressorException, IOException {
		InputStream is = InputStreamFactory.getInputStream(filename);
		return RDFDataMgr.createIteratorTriples(is, RDFLanguages.filenameToLang(filename), "");
	}

	public static IteratorTripleStringWrapper createIteratorTripleStringWrapperFromFile(String filename)
			throws CompressorException, IOException {
		InputStream is = InputStreamFactory.getInputStream(filename);
		Iterator<Triple> it = RDFDataMgr.createIteratorTriples(is, RDFLanguages.filenameToLang(filename), "");
		return new IteratorTripleStringWrapper(it);
	}

	public static Iterator<Triple> filterByPredicate(Iterator<Triple> it, String predicate) {
		Iterator<Triple> it1 = Iterators.filter(it, new Predicate<Triple>() {
			@Override
			public boolean apply(Triple input) {
				return input.predicateMatches(NodeFactory.createURI(predicate));
			}
		});
		return it1;
	}

	public static Iterator<TripleString> filter(IteratorTripleString its, CharSequence s, CharSequence p,
			CharSequence o) {
		Iterator<TripleString> r = Iterators.filter(its, new Predicate<TripleString>() {

			@Override
			public boolean apply(@Nullable TripleString ts) {
				return ts.match(new TripleString(s, p, o));
			}
		});

		return r;
	}

	public static Iterator<TripleString> createFilteredIteratorTripleStringFromFile(String f, CharSequence s,
			CharSequence p, CharSequence o) throws CompressorException, IOException {

		IteratorTripleStringWrapper itsw = createIteratorTripleStringWrapperFromFile(f);
		return filter(itsw, s, p, o);

	}

	public static long estimateSearchResults(String f, CharSequence s, CharSequence p, CharSequence o)
			throws CompressorException, IOException {
		long r = 0;
		Iterator<TripleString> itsw = createFilteredIteratorTripleStringFromFile(f, s, p, o);
		while (itsw.hasNext()) {
			itsw.next();
			r++;
		}
		return r;

	}

}
