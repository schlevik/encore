package com.gitlab.aidb.encore.model.impl;

import com.gitlab.aidb.encore.model.NLPAnnotator;
import com.gitlab.aidb.encore.model.text.Sentence;
import com.gitlab.aidb.encore.model.text.Token;
import com.gitlab.aidb.encore.model.wordtypes.NamedEntityType;
import com.gitlab.aidb.encore.model.wordtypes.POSType;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class CoreNLPAnnotator implements NLPAnnotator {

    private StanfordCoreNLP pipeline;
    private Logger logger = LoggerFactory.getLogger(getClass());
    private Marker marker = MarkerFactory.getMarker("corenlp");

    public CoreNLPAnnotator() {
        logger.debug(marker, "Creating CoreNLP instance...");
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner");
        logger.debug(marker, "...with following annotators: {}", props.getProperty("annotators"));
        pipeline = new StanfordCoreNLP(props);
        logger.debug(marker, "... created.");

    }

    @Override
    public List<Sentence> annotate(String text) {
        logger.info(marker, "Starting annotation...");
        Annotation annotated = new Annotation(text);
        pipeline.annotate(annotated);
        logger.info(marker, "...annotated, converting now...");
        AtomicInteger sentenceIndex = new AtomicInteger(0);

        return annotated.get(CoreAnnotations.SentencesAnnotation.class).stream()
                .map(s -> {
                    AtomicInteger wordIndex = new AtomicInteger(0);
                    Sentence sentence = new Sentence(
                            sentenceIndex.getAndIncrement(),
                            s.toString(),
                            s.get(CoreAnnotations.TokensAnnotation.class).stream()
                                    .map(w -> convertFromCoreNLP(wordIndex.getAndIncrement(), w))
                                    .collect(Collectors.toList()));

                    return sentence;
                })
                .collect(Collectors.toList());

    }

    private static Token convertFromCoreNLP(int index, CoreLabel token) {
        return new Token(
                index, //index
                token.word(), // words
                token.originalText(), //original word
                convertPos(token.get(CoreAnnotations.PartOfSpeechAnnotation.class)), // POStag
                convertNe(token.get(CoreAnnotations.NamedEntityTagAnnotation.class))// NEType
        );
    }

    static POSType convertPos(String pos) {
        if (pos == null || pos.length() < 1) {
            return POSType.None;
        }
        if (pos.equals("NN")) {
            return POSType.NN;
        }
        return POSType.Other;
    }

    static NamedEntityType convertNe(String ne) {
        if (ne == null || ne.length() < 1) {
            return NamedEntityType.None;
        }
        switch (ne) {

            case "PERSON":
                return NamedEntityType.Person;
            case "THING":
                return NamedEntityType.Thing;
            case "DATE":
                return NamedEntityType.Date;
            case "NUMBER":
                return NamedEntityType.Number;
            case "SET":
                return NamedEntityType.Set;
            case "MONEY":
                return NamedEntityType.Money;
            case "PERCENT":
                return NamedEntityType.Percent;
            case "DURATION":
                return NamedEntityType.Duration;
            case "MISC":
                return NamedEntityType.Misc;
            case "ORDINAL":
                return NamedEntityType.Ordinal;
            case "O":
            default:
                return NamedEntityType.Other;
        }
    }


}
