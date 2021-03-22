package it.cnr.istc.stlab.lgu.commons.nlp.model;

import java.util.List;

//import com.fasterxml.jackson.annotation.JsonSubTypes;
//import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
//import com.fasterxml.jackson.annotation.JsonTypeInfo;

//@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
//@JsonSubTypes({ @Type(value = AnnotatedText.class, name = "AnnotatedText") })
public interface HasText {

	public String getText();

	public List<Sentence> getSentences();

	public void setSentences(List<Sentence> sentences);

}
