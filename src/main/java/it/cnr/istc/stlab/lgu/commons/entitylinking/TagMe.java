package it.cnr.istc.stlab.lgu.commons.entitylinking;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import it.cnr.istc.stlab.lgu.commons.entitylinking.model.Mention;
import it.cnr.istc.stlab.lgu.commons.entitylinking.model.Result;
import it.cnr.istc.stlab.lgu.commons.entitylinking.model.ScoredResult;
import it.cnr.istc.stlab.lgu.commons.files.FileUtils;
import it.cnr.istc.stlab.lgu.commons.model.Lang;

public class TagMe {

	private static String tagMeUrl = "https://tagme.d4science.org/tagme/tag";
	private static final String CACHED_RESULTS_FOLDER = "tagme_cached_results";
	private static String tagme_key;
	private static Logger logger = LogManager.getLogger(TagMe.class);

	private static enum SenseInventory {
		WIKI, DBPEDIA
	};

	public static List<String> getMentionedEntitiesURIUsingTagMe(String text, Lang l) throws IOException {

		List<String> result = new ArrayList<>();
		Result r = getMentions(text, l, "dbpedia");
		r.getAnnotations().forEach(m -> {
			m.getMentionedEntities().forEach(s -> {
				result.add(((String) ((ScoredResult) s).getEntity()));
			});
		});
		return result;
	}

	public static List<String> getMentionedEntitiesURIUsingTagMe(String text, Lang l, String senseInventory)
			throws IOException {

		List<String> result = new ArrayList<>();
		Result r = getMentions(text, l, senseInventory);
		r.getAnnotations().forEach(m -> {
			m.getMentionedEntities().forEach(s -> {
				result.add(((String) ((ScoredResult) s).getEntity()));
			});
		});
		return result;
	}

	public static Result getMentions(String text, Lang l, String senseInventory) throws IOException {

		new File(CACHED_RESULTS_FOLDER).mkdir();
		String result = "";
		String textDigest = DigestUtils.md5Hex(text + l.toString() + senseInventory);
		if (new File(CACHED_RESULTS_FOLDER + "/" + textDigest).exists()) {

			logger.trace("Retrieving Cached Result");

			result = FileUtils.readFile(CACHED_RESULTS_FOLDER + "/" + textDigest);

		} else {

			logger.trace("Issuing POST Request");

			URL myUrl = new URL(tagMeUrl);
			HttpsURLConnection conn = (HttpsURLConnection) myUrl.openConnection();
			conn.setRequestMethod("POST");

			// Send post request
			conn.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(conn.getOutputStream());

			wr.writeBytes(getURLParameters(text, l));
			wr.flush();
			wr.close();

			InputStream is = conn.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);

			StringBuilder sb = new StringBuilder();
			String inputLine;

			while ((inputLine = br.readLine()) != null) {
				sb.append(inputLine);
			}

			br.close();

			result = sb.toString();
			FileUtils.toTextFile(result, CACHED_RESULTS_FOLDER + "/" + textDigest);

		}

		SenseInventory si = getSenseInventoryFromString(senseInventory);

		List<Mention> results = new ArrayList<>();

		JSONObject obj = new JSONObject(result);
		JSONArray annotations = obj.getJSONArray("annotations");
		for (int i = 0; i < annotations.length(); i++) {

			JSONObject annotation = annotations.getJSONObject(i);

			int start = annotation.getInt("start");
			int end = annotation.getInt("end");
			double score = annotation.getDouble("link_probability");

			Mention m = new Mention(start, end, text.substring(start, end));

			m.addMentionedEntities(new ScoredResult(score, getURI(annotation.getString("title"), l, si)));

			results.add(m);
		}

		Result r = new Result(text, results);

		return r;
	}

	private static String getURLParameters(String text, Lang lang) {

		if (tagme_key == null) {
			tagme_key = FileUtils.readFile("tagme.key");
		}

		switch (lang) {
		case IT:
			return "gcube-token=" + tagme_key + "&lang=it&include_categories=true&text=" + new String(urlify(text));
		case EN:
			return "gcube-token=" + tagme_key + "&lang=en&include_categories=true&text=" + new String(urlify(text));
		}
		return null;
	}

	private static String urlify(String s) {
		return s.replaceAll("\\s", "%20");
	}

	private static String getDBpediaURI(String tag, Lang l) {
		tag = tag.replace(' ', '_');
		switch (l) {
		case EN:
			return "http://dbpedia.org/resource/" + tag;
		case IT:
			return "http://it.dbpedia.org/resource/" + tag;
		}
		return null;
	}

	private static String getURI(String tag, Lang l, SenseInventory s) {
		switch (s) {
		case WIKI:
			return getWikipediaURI(tag, l);
		case DBPEDIA:
		default:
			return getDBpediaURI(tag, l);

		}
	}

	private static String getWikipediaURI(String tag, Lang l) {
		tag = tag.replace(' ', '_');
		switch (l) {
		case EN:
			return "https://en.wikipedia.org/wiki/" + tag;
		case IT:
			return "https://it.wikipedia.org/wiki/" + tag;
		}
		return null;
	}

	private static SenseInventory getSenseInventoryFromString(String senseInventorString) {
		SenseInventory r = null;
		if (senseInventorString.equalsIgnoreCase("Wikipedia") || senseInventorString.equalsIgnoreCase("wiki")) {
			r = SenseInventory.WIKI;
		} else {
			r = SenseInventory.DBPEDIA;
		}

		return r;
	}

//	public static void main(String[] args) throws IOException {
//		getMentionedEntitiesURIUsingTagMe("Palazzo Montecitorio - Camera dei Deputati", Lang.IT).forEach(s -> {
//			System.out.println(s);
//		});
//	}

}
