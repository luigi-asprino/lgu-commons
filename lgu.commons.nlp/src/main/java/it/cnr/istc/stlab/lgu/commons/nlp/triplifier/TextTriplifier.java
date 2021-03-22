package it.cnr.istc.stlab.lgu.commons.nlp.triplifier;

import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;

import it.cnr.istc.stlab.lgu.commons.nlp.model.AnnotationType;
import it.cnr.istc.stlab.lgu.commons.nlp.model.HasText;
import it.cnr.istc.stlab.lgu.commons.nlp.model.MultiWord;
import it.cnr.istc.stlab.lgu.commons.nlp.model.ScoredAnnotation;
import it.cnr.istc.stlab.lgu.commons.nlp.model.Sentence;
import it.cnr.istc.stlab.lgu.commons.semanticweb.utils.URIUtils;

public class TextTriplifier {

	public static final String EARMARK_PREFIX = "http://www.essepuntato.it/2008/12/earmark#";
	public static final String EARMARK_StringDocuverse = EARMARK_PREFIX + "StringDocuverse";
	public static final String EARMARK_hasContent = EARMARK_PREFIX + "hasContent";
	public static final String EARMARK_begins = EARMARK_PREFIX + "begins";
	public static final String EARMARK_ends = EARMARK_PREFIX + "ends";
	public static final String EARMARK_PointerRange = EARMARK_PREFIX + "PointerRange";
	public static final String EARMARK_refersTo = EARMARK_PREFIX + "refersTo";

	public static final String NLP_PREFIX = "https://w3id.org/nlp/";
	public static final String NLP_LEMMA = NLP_PREFIX + "lemma";
	public static final String NLP_SCOREDANNOATION = NLP_PREFIX + "ScoredAnnotation";
	public static final String NLP_DENOTEDENTITY = NLP_PREFIX + "denotedEntity";
	public static final String NLP_HAS_SCORED_ANNOTATION = NLP_PREFIX + "hasScoredAnnotation";
	public static final String NLP_SCORE = NLP_PREFIX + "score";

	public static final String SEMIOTICS_PREFIX = "http://ontologydesignpatterns.org/cp/owl/semiotics.owl#";
	public static final String SEMIOTICS_DENOTES = SEMIOTICS_PREFIX + "denotes";

	public void triplify(Model m, HasText as, Resource r) {

		m.add(r, RDF.type, m.createResource(EARMARK_StringDocuverse));
		if (as.getText() != null) {
			m.add(r, m.createProperty(EARMARK_hasContent), m.createLiteral(as.getText()));
		}
		List<Sentence> sentences = as.getSentences();
		if (sentences != null) {
			int c = 0;
			for (Sentence s : sentences) {
				if (r.isAnon()) {
					triplify(m, s, m.createResource(), r);
					s.getMultiWords().forEach(mw -> triplify(m, mw, m.createResource(), r));
				} else {
					triplify(m, s, m.createResource(r.getURI() + "_" + c), r);
					s.getMultiWords().forEach(mw -> triplify(m, mw,
							m.createResource(r.getURI() + "_" + mw.getBegin() + "_" + mw.getEnd()), r));
				}

			}
			c++;
		}
	}

	public void triplify(Model m, Sentence as, Resource rSentence, Resource r) {
		m.add(r, RDF.type, m.createResource(EARMARK_PointerRange));
		m.add(rSentence, m.createProperty(EARMARK_refersTo), r);
		if (as.getSentence() != null) {
			m.add(rSentence, RDFS.label, m.createLiteral(as.getSentence()));
		}
		if (as.getBegin() > -1 && as.getEnd() > -1) {
			m.add(rSentence, m.createProperty(EARMARK_begins), m.createTypedLiteral(as.getBegin()));
			m.add(rSentence, m.createProperty(EARMARK_ends), m.createTypedLiteral(as.getEnd()));
		}
	}

	public void triplify(Model m, Sentence as, Resource r) {
		m.add(r, RDF.type, m.createResource(EARMARK_StringDocuverse));
		if (as.getSentence() != null) {
			m.add(r, m.createProperty(EARMARK_hasContent), m.createLiteral(as.getSentence()));
		}
		if (as.getBegin() > -1 && as.getEnd() > -1) {
			m.add(r, m.createProperty(EARMARK_begins), m.createTypedLiteral(as.getBegin()));
			m.add(r, m.createProperty(EARMARK_ends), m.createTypedLiteral(as.getEnd()));
		}

		if (r.isAnon()) {
			as.getMultiWords().forEach(mw -> triplify(m, mw, m.createResource(), r));
		} else {
			as.getMultiWords().forEach(
					mw -> triplify(m, mw, m.createResource(r.getURI() + "_" + mw.getBegin() + "_" + mw.getEnd()), r));
		}

	}

	public void triplify(Model m, MultiWord at, Resource uri, Resource docuverse) {
		m.add(uri, RDF.type, m.createResource(EARMARK_PointerRange));
		m.add(uri, m.createProperty(EARMARK_refersTo), docuverse);
		if (at.getMultiWord() != null) {
			m.add(uri, RDFS.label, m.createLiteral(at.getMultiWord()));
		}
		if (at.getLemma() != null) {
			m.add(uri, m.createProperty(NLP_LEMMA), m.createLiteral(at.getLemma()));
		}

		if (at.getBegin() > -1 && at.getEnd() > -1) {
			m.add(uri, m.createProperty(EARMARK_begins), m.createTypedLiteral(at.getBegin()));
			m.add(uri, m.createProperty(EARMARK_ends), m.createTypedLiteral(at.getEnd()));
		}

		if (at.getTopScoredAnnotation(AnnotationType.WN30) != null) {

			m.add(uri, m.createProperty(SEMIOTICS_DENOTES),
					m.createResource(at.getTopScoredAnnotation(AnnotationType.WN30).getAnnotation()));
		}

		if (at.getScoredAnnotations(AnnotationType.WN30) != null) {
			at.getScoredAnnotations(AnnotationType.WN30).forEach(sa -> {
				String annotationURI = uri + "_" + URIUtils.getID(sa.getAnnotation());
				if (uri.isAnon()) {
					triplify(m, sa, m.createResource(), uri);
				} else {
					triplify(m, sa, m.createResource(annotationURI), uri);
				}
			});
		}

		if (at.getTopScoredAnnotation(AnnotationType.WN30_FRAMESTER) != null) {
			m.add(uri, m.createProperty(SEMIOTICS_DENOTES),
					m.createResource(at.getTopScoredAnnotation(AnnotationType.WN30_FRAMESTER).getAnnotation()));
		}

		if (at.getScoredAnnotations(AnnotationType.WN30_FRAMESTER) != null) {
			at.getScoredAnnotations(AnnotationType.WN30_FRAMESTER).forEach(sa -> {
				String annotationURI = uri.getURI() + "_" + URIUtils.getID(sa.getAnnotation());
				if (uri.isAnon()) {
					triplify(m, sa, m.createResource(), uri);
				} else {
					triplify(m, sa, m.createResource(annotationURI), uri);
				}
			});
		}

	}

	public void triplify(Model m, ScoredAnnotation sa, Resource annotationURI, Resource mwuri) {
		m.add(annotationURI, RDF.type, m.createResource(NLP_SCOREDANNOATION));
		m.add(annotationURI, m.createProperty(NLP_SCORE), m.createTypedLiteral(sa.getScore()));
		m.add(annotationURI, m.createProperty(NLP_DENOTEDENTITY), m.createResource(sa.getAnnotation()));
		m.add(mwuri, m.createProperty(NLP_HAS_SCORED_ANNOTATION), annotationURI);
	}

//	public static void main(String[] args) {
//		TextTriplifier tt = new TextTriplifier();
//
//		UKB wsd = UKB.getInstance();
//		Model m = ModelFactory.createDefaultModel();
//		AnnotatedText at = wsd.disambiguate("the cat is on the table", SenseInventory.WN30_FRAMESTER);
//
//		tt.triplify(m, at, m.createResource("http://example.org/t"));
//
//		m.write(System.out, "TTL");
//	}

}
