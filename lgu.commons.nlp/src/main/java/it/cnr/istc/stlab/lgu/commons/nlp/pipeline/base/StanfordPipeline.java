package it.cnr.istc.stlab.lgu.commons.nlp.pipeline.base;

import java.util.List;
import java.util.Properties;

import org.apache.jena.ext.com.google.common.collect.Lists;

import edu.stanford.nlp.ling.CoreAnnotations.CharacterOffsetBeginAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.CharacterOffsetEndAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import it.cnr.istc.stlab.lgu.commons.nlp.model.AnnotatedMultiWord;
import it.cnr.istc.stlab.lgu.commons.nlp.model.AnnotatedSentence;
import it.cnr.istc.stlab.lgu.commons.nlp.model.HasText;
import it.cnr.istc.stlab.lgu.commons.nlp.model.MultiWord;
import it.cnr.istc.stlab.lgu.commons.nlp.model.Sentence;
import it.cnr.istc.stlab.lgu.commons.nlp.pipeline.Pipeline;

public class StanfordPipeline implements Pipeline<HasText> {

	private StanfordCoreNLP pipeline;
	private static StanfordPipeline instance;
	

	private StanfordPipeline() {
		super();
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit, pos, lemma");
		this.pipeline = new StanfordCoreNLP(props);
	}

	private StanfordPipeline(int cores) {
		super();
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit, pos, lemma");
		props.setProperty("threads", cores + "");
		this.pipeline = new StanfordCoreNLP(props);
	}
	
	public static StanfordPipeline getInstance() {
		if(instance==null) {
			instance = new StanfordPipeline();
		}
		return instance;
	}

	public void annotate(HasText text) {

		final List<Sentence> result = Lists.newArrayListWithExpectedSize(50);

		Annotation annotation = new Annotation(text.getText());
		pipeline.annotate(annotation);

		annotation.get(SentencesAnnotation.class).forEach(sentence -> {
			String textSentence = sentence.get(TextAnnotation.class);
			final List<MultiWord> annotatedMWEs = Lists.newArrayListWithExpectedSize(sentence.size() * 2);

			List<CoreLabel> t = sentence.get(TokensAnnotation.class);
			CoreLabel[] tokens = t.toArray(new CoreLabel[t.size()]);

			int begSentence = sentence.get(CharacterOffsetBeginAnnotation.class);
			int endSentence = sentence.get(CharacterOffsetEndAnnotation.class);

			for (int i = 0; i < tokens.length; i++) {
				MultiWord amw = new AnnotatedMultiWord();
				amw.setLemma(tokens[i].lemma().toLowerCase());
				amw.setMultiWord(tokens[i].word().toLowerCase());
				amw.setPos(tokens[i].get(PartOfSpeechAnnotation.class));
				amw.setBegin(tokens[i].beginPosition());
				amw.setEnd(tokens[i].endPosition());
				annotatedMWEs.add(amw);
			}
			result.add(new AnnotatedSentence(textSentence, begSentence, endSentence, text, annotatedMWEs));
		});

		text.setSentences(result);

	}


}
