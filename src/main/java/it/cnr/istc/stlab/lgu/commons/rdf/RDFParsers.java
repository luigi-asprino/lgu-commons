package it.cnr.istc.stlab.lgu.commons.rdf;

import java.io.InputStream;

import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParserRegistry;
import org.apache.jena.riot.ReaderRIOT;
import org.apache.jena.riot.ReaderRIOTFactory;
import org.apache.jena.riot.lang.LangNTriples;
import org.apache.jena.riot.system.ErrorHandlerFactory;
import org.apache.jena.riot.system.FactoryRDFStd;
import org.apache.jena.riot.system.IRIResolver;
import org.apache.jena.riot.system.ParserProfileStd;
import org.apache.jena.riot.system.PrefixMap;
import org.apache.jena.riot.system.PrefixMapFactory;
import org.apache.jena.riot.tokens.Tokenizer;
import org.apache.jena.riot.tokens.TokenizerFactory;
import org.apache.jena.sparql.util.Context;
import org.rdfhdt.hdt.triples.TripleString;

public class RDFParsers {

	public static ReaderRIOT getReaderRIOTNT(String baseIRI) {
		@SuppressWarnings("deprecation")
		ReaderRIOTFactory r = RDFParserRegistry.getFactory(Lang.NT);
		PrefixMap prefixMap = PrefixMapFactory.createForInput();
		ParserProfileStd parserProfile = new ParserProfileStd(new FactoryRDFStd(), ErrorHandlerFactory.errorHandlerStd,
				IRIResolver.create(baseIRI), prefixMap, new Context(), true, true);
		ReaderRIOT reader = r.create(Lang.NT, parserProfile);
		

		return reader;
	}

	private static PrefixMap prefixMap = PrefixMapFactory.createForInput();
	private static ParserProfileStd parserProfile = new ParserProfileStd(new FactoryRDFStd(),
			ErrorHandlerFactory.errorHandlerStd, IRIResolver.create("http://example.org/"), prefixMap, new Context(),
			true, true);

	public static LangNTriples getNTriplesParser(InputStream is) {
		Tokenizer tokenizer = TokenizerFactory.makeTokenizerUTF8(is);
		LangNTriples parser = new LangNTriples(tokenizer, parserProfile, null);
		return parser;

	}

}
