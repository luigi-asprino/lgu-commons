package it.cnr.istc.stlab.lgu.commons.nlp.model;

import java.util.List;


public interface Sentence {

	public List<MultiWord> getMultiWords();

	public void setMultiWords(List<MultiWord> multiwords);

	public String getSentence();

	public void setSentence(String sentence);

	public int getBegin();

	public void setBegin(int begin);

	public int getEnd();

	public void setEnd(int end);

}
