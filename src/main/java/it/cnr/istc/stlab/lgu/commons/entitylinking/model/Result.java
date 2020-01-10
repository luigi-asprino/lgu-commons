package it.cnr.istc.stlab.lgu.commons.entitylinking.model;

import java.util.ArrayList;
import java.util.List;

public class Result {

	private String text;
	private List<Mention> annotations;

	public Result(String text, List<Mention> annotaions) {
		this.text = text;
		this.annotations = annotaions;
	}

	public Result(String text) {
		this(text, new ArrayList<>());
	}

	public Result() {
		this("",new ArrayList<>());
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public List<Mention> getAnnotations() {
		return annotations;
	}

	public void setAnnotations(List<Mention> annotations) {
		this.annotations = annotations;
	}

	public void addAnnotation(Mention annotation) {
		this.annotations.add(annotation);
	}

	@Override
	public String toString() {
		return "Result [text=" + text + ", annotations=" + annotations + "]";
	}

}
