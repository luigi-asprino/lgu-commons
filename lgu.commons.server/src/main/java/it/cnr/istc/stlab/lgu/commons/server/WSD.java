package it.cnr.istc.stlab.lgu.commons.server;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.apache.commons.compress.utils.Sets;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.cnr.istc.stlab.lgu.commons.nlp.model.AnnotatedText;
import it.cnr.istc.stlab.lgu.commons.nlp.triplifier.TextTriplifier;
import it.cnr.istc.stlab.lgu.commons.nlp.wsd.UKB;
import it.cnr.istc.stlab.lgu.commons.nlp.wsd.UKB.SenseInventory;

@Path("wsd")
public class WSD {

	private static UKB wsd = UKB.getInstance();
	private static TextTriplifier tt = new TextTriplifier();

	private static final Logger logger = LoggerFactory.getLogger(WSD.class);

	private static final Set<String> RDF_SERIALIZATION = Sets.newHashSet("TTL", "ttl", "NT", "nt");

	@GET
	public Response getdisambiguate(@QueryParam(value = "text") String text,
			@QueryParam(value = "senseInventory") String senseInventory,
			@QueryParam(value = "serialization") String serialization, @QueryParam(value = "uri") String uri) {

		logger.trace("Disambigate {} senseInventory {}  lang {}", text, senseInventory, serialization);

		SenseInventory si = SenseInventory.WN30;

		if (senseInventory != null) {
			if (senseInventory.equalsIgnoreCase("wn30framester") || senseInventory.equalsIgnoreCase("framester")) {
				si = SenseInventory.WN30_FRAMESTER;
			}
		}

		AnnotatedText at = wsd.disambiguate(text, si);
		Model m = ModelFactory.createDefaultModel();

		if (uri != null) {
			tt.triplify(m, at, m.createResource(uri));
		} else {
			tt.triplify(m, at, m.createResource());
		}
		StringWriter sw = new StringWriter();
		if (serialization != null) {
			if (RDF_SERIALIZATION.contains(serialization)) {
				m.write(sw, serialization);
			} else if (serialization.equalsIgnoreCase("json")) {
				try {
					Utils.getObjectMapper().writeValue(sw, at);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {	
				logger.warn("Serialization {} unavailable, use json by default", serialization);
			}
		} else {
			m.write(sw);
		}

		return Response.ok(sw.toString()).build();
	}
}
