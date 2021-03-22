package it.cnr.istc.stlab.lgu.commons.nlp.model;

import java.util.List;

public class AnnotatedText implements HasText {

	private String text;
	private List<Sentence> sentences;

	public AnnotatedText() {
	}

	public AnnotatedText(String text) {
		super();
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public List<Sentence> getSentences() {
		return sentences;
	}

	public void setSentences(List<Sentence> sentences) {
		this.sentences = sentences;
	}

}
