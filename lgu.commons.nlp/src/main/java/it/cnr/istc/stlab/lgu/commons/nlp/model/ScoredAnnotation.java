package it.cnr.istc.stlab.lgu.commons.nlp.model;

public class ScoredAnnotation {

	private String annotation;
	private double score;

	public ScoredAnnotation(String annotation, double score) {
		super();
		this.annotation = annotation;
		this.score = score;
	}

	public String getAnnotation() {
		return annotation;
	}

	public void setAnnotation(String annotation) {
		this.annotation = annotation;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	@Override
	public String toString() {
		return "ScoredAnnotation [annotation=" + annotation + ", score=" + score + "]";
	}

}
