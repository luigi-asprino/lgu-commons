
package it.cnr.istc.stlab.lgu.commons.semanticweb.iterators;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.riot.RDFParserBuilder;
import org.apache.jena.riot.RiotException;
import org.apache.jena.riot.system.StreamRDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * It reads from the input stream a bulk of 10K lines at time, then it tries to
 * parse the bulk as a whole, if succeeds triples and quads are accessible via
 * streamOut. Otherwise, the bulk is processed line by line, if exceptions are
 * thrown when parsing a bulk.
 *
 */
public class RobustQuadReader {

	private static Logger log = LoggerFactory.getLogger(RobustQuadReader.class);

	private InputStream is;
	private StreamRDF streamOut;
	private RDFParserBuilder b = RDFParser.create().lang(Lang.NQUADS);

	public RobustQuadReader(InputStream is, StreamRDF streamOut) {
		super();
		this.is = is;
		this.streamOut = streamOut;
	}

	public void read() throws IOException {
		final BufferedReader br = new BufferedReader(new InputStreamReader(is), 20 * 1024);
		final AtomicLong al = new AtomicLong(0L);
		String l;
		StringBuilder sb = new StringBuilder();
		while ((l = br.readLine()) != null) {
			sb.append(l);
			sb.append('\n');
			al.incrementAndGet();
			if (al.get() % 10000 == 0) {
				log.trace("Reading {}-{}", al.get() - 10000, al.get());
				sb = parse(streamOut, sb);
			}
		}

		if (al.get() % 10000 > 0) {
			sb = parse(streamOut, sb);
		}
		br.close();
	}

	private StringBuilder parse(final StreamRDF s, StringBuilder sb) {
		String toParse = sb.toString();
		try {
			b.fromString(toParse).parse(s);
		} catch (RiotException e) {
			for (String ll : toParse.split("\n")) {
				try {
					b.fromString(ll).parse(s);
				} catch (RiotException e1) {
					log.error("Skipping line {} ", ll);
					log.error(e1.toString());
				}
			}
		}
		sb = new StringBuilder();
		return sb;
	}

}
