package it.cnr.istc.stlab.lgu.commons.nlp.model;

import java.util.Collection;

public interface MultiWord {

	public void setMultiWord(String multiword);

	public String getMultiWord();

	public void setLemma(String lemma);

	public String getLemma();

	public void setPos(String pos);

	public String getPos();

	public void setBegin(int begin);

	public void setEnd(int end);

	public int getBegin();

	public int getEnd();

	public void addAnnotation(AnnotationType type, String annotation);
	
	public void addScoredAnnotation(AnnotationType type, ScoredAnnotation annotation);

	public String getAnnotation(AnnotationType type);
	
	public ScoredAnnotation getScoredAnnotation(AnnotationType type);
	
	public ScoredAnnotation getTopScoredAnnotation(AnnotationType type);

	public Collection<String> getAnnotations(AnnotationType type);
	
	public Collection<ScoredAnnotation> getScoredAnnotations(AnnotationType type);
}
