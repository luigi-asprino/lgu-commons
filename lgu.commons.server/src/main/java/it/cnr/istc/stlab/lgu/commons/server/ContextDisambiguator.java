package it.cnr.istc.stlab.lgu.commons.server;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.cnr.istc.stlab.lgu.commons.nlp.model.AnnotatedText;
import it.cnr.istc.stlab.lgu.commons.nlp.model.AnnotationType;
import it.cnr.istc.stlab.lgu.commons.nlp.wfd.UKBBasedWFD;
import it.cnr.istc.stlab.lgu.commons.nlp.wsd.UKB;
import it.cnr.istc.stlab.lgu.commons.nlp.wsd.UKB.SenseInventory;

@Path("cd")
public class ContextDisambiguator {

	private static UKB wsd = UKB.getInstance();
	private static UKBBasedWFD wfd = UKBBasedWFD.getInstance();

	private static final Logger logger = LoggerFactory.getLogger(ContextDisambiguator.class);

	@GET
	@Produces("application/json")
	public Response getdisambiguate(@QueryParam(value = "text") String text,
			@QueryParam(value = "senseInventory") String senseInventory) {

		logger.trace("Disambigate {} senseInventory {} ", text, senseInventory);

		final SenseInventory si;
		final boolean doWFD;

		if (senseInventory != null) {
			if (senseInventory.equalsIgnoreCase("wn30framester") || senseInventory.equalsIgnoreCase("framester")) {
				si = SenseInventory.WN30_FRAMESTER;
				doWFD = false;
			} else if (senseInventory.equalsIgnoreCase("fn17")) {
				doWFD = true;
				si = SenseInventory.WN30_FRAMESTER;
			} else {
				si = SenseInventory.WN30;
				doWFD = false;
			}
		} else {
			si = SenseInventory.WN30;
			doWFD = false;
		}
		AnnotatedText at;
		if (doWFD) {
			logger.info("WFD on {}", text);
			at = wfd.disambiguate(text);
		} else {
			at = wsd.disambiguate(text, si);
		}
		JSONArray ja = new JSONArray();

		at.getSentences().forEach(s -> {
			s.getMultiWords().forEach(mw -> {
				switch (si) {
				case WN30:
					if (mw.getTopScoredAnnotation(AnnotationType.WN30) != null) {
						ja.put(mw.getTopScoredAnnotation(AnnotationType.WN30).getAnnotation());
					}
					break;
				case WN30_FRAMESTER:
					if (mw.getTopScoredAnnotation(AnnotationType.WN30_FRAMESTER) != null) {
						ja.put(mw.getTopScoredAnnotation(AnnotationType.WN30_FRAMESTER).getAnnotation());
						if (doWFD && mw.getAnnotation(AnnotationType.FN17) != null) {
							ja.put(mw.getAnnotation(AnnotationType.FN17));
						}
					}
					break;
				}
			});
		});

		return Response.ok(ja.toString()).build();
	}
}
