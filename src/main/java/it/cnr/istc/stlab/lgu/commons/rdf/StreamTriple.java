package it.cnr.istc.stlab.lgu.commons.rdf;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;
import java.util.stream.Collector;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import org.apache.commons.compress.compressors.CompressorException;
import org.apache.jena.graph.Triple;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.riot.RDFParserRegistry;
import org.apache.jena.riot.ReaderRIOT;
import org.apache.jena.riot.ReaderRIOTFactory;
import org.apache.jena.riot.system.ErrorHandlerFactory;
import org.apache.jena.riot.system.FactoryRDFStd;
import org.apache.jena.riot.system.IRIResolver;
import org.apache.jena.riot.system.ParserProfileStd;
import org.apache.jena.riot.system.PrefixMap;
import org.apache.jena.riot.system.PrefixMapFactory;
import org.apache.jena.riot.system.StreamRDF;
import org.apache.jena.sparql.core.Quad;
import org.apache.jena.sparql.util.Context;

import it.cnr.istc.stlab.lgu.commons.files.InputStreamFactory;

public class StreamTriple implements Stream<Triple> {
	private String filename;
	private Lang lang;
	private String baseIRI;
	private Stream<String> lineStream;
	private RDFParser parser;
	private ReaderRIOT reader;

	public StreamTriple(String filename, Lang lang, String baseIRI) throws CompressorException, IOException {
		super();
		this.filename = filename;
		this.lang = lang;
		this.baseIRI = baseIRI;
		this.lineStream = InputStreamFactory.getInputLineStream(filename);
//		RDFParser.fromString("<http://example.org/a> <http://example.org/b> <http://example.org/c>.").lang(Lang.NT).parse(m);
//		this.parser = RDFParser.create().lang(lang).build();
		this.reader = getReaderRIOTNT();
		lineStream.map(x -> {
			return 1;
		});
	}

	private ReaderRIOT getReaderRIOTNT() {
		ReaderRIOTFactory r = RDFParserRegistry.getFactory(Lang.NT);
		PrefixMap prefixMap = PrefixMapFactory.createForInput();
		ParserProfileStd parserProfile = new ParserProfileStd(new FactoryRDFStd(), ErrorHandlerFactory.errorHandlerStd,
				IRIResolver.create(baseIRI), prefixMap, new Context(), true, true);
		ReaderRIOT reader = r.create(Lang.NT, parserProfile);
//		reader.read(new ByteArrayInputStream(content.getBytes()), "baseURI", null, new StreamRDFS(), new Context());
		return reader;
	}

	@Override
	public Iterator<Triple> iterator() {
		try {
			return RDFDataMgr.createIteratorTriples(InputStreamFactory.getInputStream(filename), lang, baseIRI);
		} catch (CompressorException | IOException e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	}

	@Override
	public Spliterator<Triple> spliterator() {

		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isParallel() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public Stream<Triple> sequential() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public Stream<Triple> parallel() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public Stream<Triple> unordered() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public Stream<Triple> onClose(Runnable closeHandler) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();

	}

	@Override
	public Stream<Triple> filter(Predicate<? super Triple> predicate) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public <R> Stream<R> map(Function<? super Triple, ? extends R> mapper) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public IntStream mapToInt(ToIntFunction<? super Triple> mapper) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public LongStream mapToLong(ToLongFunction<? super Triple> mapper) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public DoubleStream mapToDouble(ToDoubleFunction<? super Triple> mapper) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public <R> Stream<R> flatMap(Function<? super Triple, ? extends Stream<? extends R>> mapper) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public IntStream flatMapToInt(Function<? super Triple, ? extends IntStream> mapper) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public LongStream flatMapToLong(Function<? super Triple, ? extends LongStream> mapper) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public DoubleStream flatMapToDouble(Function<? super Triple, ? extends DoubleStream> mapper) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public Stream<Triple> distinct() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public Stream<Triple> sorted() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public Stream<Triple> sorted(Comparator<? super Triple> comparator) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public Stream<Triple> peek(Consumer<? super Triple> action) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public Stream<Triple> limit(long maxSize) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public Stream<Triple> skip(long n) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public void forEach(Consumer<? super Triple> action) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();

	}

	@Override
	public void forEachOrdered(Consumer<? super Triple> action) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();

	}

	@Override
	public Object[] toArray() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public <A> A[] toArray(IntFunction<A[]> generator) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public Triple reduce(Triple identity, BinaryOperator<Triple> accumulator) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public Optional<Triple> reduce(BinaryOperator<Triple> accumulator) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public <U> U reduce(U identity, BiFunction<U, ? super Triple, U> accumulator, BinaryOperator<U> combiner) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public <R> R collect(Supplier<R> supplier, BiConsumer<R, ? super Triple> accumulator, BiConsumer<R, R> combiner) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public <R, A> R collect(Collector<? super Triple, A, R> collector) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public Optional<Triple> min(Comparator<? super Triple> comparator) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public Optional<Triple> max(Comparator<? super Triple> comparator) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public long count() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean anyMatch(Predicate<? super Triple> predicate) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean allMatch(Predicate<? super Triple> predicate) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean noneMatch(Predicate<? super Triple> predicate) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public Optional<Triple> findFirst() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public Optional<Triple> findAny() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	class StreamRDFS implements StreamRDF {

		@Override
		public void start() {
			// TODO Auto-generated method stub

		}

		@Override
		public void triple(Triple triple) {
			// TODO Auto-generated method stub

		}

		@Override
		public void quad(Quad quad) {
			// TODO Auto-generated method stub

		}

		@Override
		public void base(String base) {
			// TODO Auto-generated method stub

		}

		@Override
		public void prefix(String prefix, String iri) {
			// TODO Auto-generated method stub

		}

		@Override
		public void finish() {
			// TODO Auto-generated method stub

		}

	}
}
