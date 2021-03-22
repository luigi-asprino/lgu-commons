package it.cnr.istc.stlab.lgu.commons.nlp.model;

import java.util.List;

public class AnnotatedSentence implements Sentence {

	private String sentence;
	private List<MultiWord> multiWords;
	private int begin, end;

	public AnnotatedSentence(String sentence, int begin, int end, HasText hasText) {
		super();
		this.sentence = sentence;
		this.begin = begin;
		this.end = end;
	}

	public AnnotatedSentence(String sentence, int begin, int end, HasText hasText, List<MultiWord> multiWords) {
		super();
		this.sentence = sentence;
		this.begin = begin;
		this.end = end;
		this.multiWords = multiWords;
	}

	public String getSentence() {
		return sentence;
	}

	public void setSentence(String sentence) {
		this.sentence = sentence;
	}

	public List<MultiWord> getMultiWords() {
		return multiWords;
	}

	public void setMultiWords(List<MultiWord> multiWords) {
		this.multiWords = multiWords;
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

}
