package it.cnr.istc.stlab.lgu.commons.nlp.model;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class AnnotatedMultiWord implements MultiWord {

	private String multiWord, lemma, pos;
	private int begin, end;
	private Map<AnnotationType, Collection<String>> annotations = new HashMap<>();
	private Map<AnnotationType, Collection<ScoredAnnotation>> scoredAnnotations = new HashMap<>();

	public Map<AnnotationType, Collection<String>> getAnnotations() {
		return annotations;
	}

	public void setAnnotations(Map<AnnotationType, Collection<String>> annotations) {
		this.annotations = annotations;
	}

	public String getLemma() {
		return lemma;
	}

	public void setLemma(String lemma) {
		this.lemma = lemma;
	}

	public String getPos() {
		return pos;
	}

	public void setPos(String pos) {
		this.pos = pos;
	}

	public int getBegin() {
		return begin;
	}

	public void setBegin(int begin) {
		this.begin = begin;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public String getMultiWord() {
		return multiWord;
	}

	public void setMultiWord(String multiWord) {
		this.multiWord = multiWord;
	}

	public void addAnnotation(AnnotationType type, String annotation) {
		Collection<String> annotationsOfType = annotations.get(type);
		if (annotationsOfType == null) {
			annotationsOfType = new HashSet<>();
			annotations.put(type, annotationsOfType);
		}
		annotationsOfType.add(annotation);

	}

	public void addScoredAnnotation(AnnotationType type, ScoredAnnotation annotation) {
		Collection<ScoredAnnotation> annotationsOfType = scoredAnnotations.get(type);
		if (annotationsOfType == null) {
			annotationsOfType = new HashSet<>();
			scoredAnnotations.put(type, annotationsOfType);
		}
		annotationsOfType.add(annotation);
	}

	public String getAnnotation(AnnotationType type) {
		Collection<String> annotationsOfType = annotations.get(type);
		if (annotationsOfType == null) {
			return null;
		}
		return annotationsOfType.iterator().next();
	}

	public ScoredAnnotation getScoredAnnotation(AnnotationType type) {
		Collection<ScoredAnnotation> annotationsOfType = scoredAnnotations.get(type);
		if (annotationsOfType == null) {
			return null;
		}
		return annotationsOfType.iterator().next();
	}

	public Collection<String> getAnnotations(AnnotationType type) {
		return annotations.get(type);
	}

	public Collection<ScoredAnnotation> getScoredAnnotations(AnnotationType type) {
		return scoredAnnotations.get(type);
	}

	@Override
	public ScoredAnnotation getTopScoredAnnotation(AnnotationType type) {
		Collection<ScoredAnnotation> annotationsOfType = scoredAnnotations.get(type);
		if (annotationsOfType == null) {
			return null;
		}
		return annotationsOfType.stream().max(new Comparator<ScoredAnnotation>() {

			@Override
			public int compare(ScoredAnnotation o1, ScoredAnnotation o2) {
				if (o1.getScore() > o2.getScore()) {
					return 1;
				} else if (o2.getScore() > o1.getScore()) {
					return -1;
				}
				return 0;
			}
		}).get();
	}

}
