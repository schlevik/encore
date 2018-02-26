package com.gitlab.aidb.encore.model.impl;

import com.gitlab.aidb.encore.model.NLPAnnotator;
import com.gitlab.aidb.encore.model.text.Sentence;
import com.gitlab.aidb.encore.model.text.Token;
import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.gitlab.aidb.encore.model.impl.CoreNLPAnnotator.convertNe;
import static com.gitlab.aidb.encore.model.impl.CoreNLPAnnotator.convertPos;

public class FasterCoreNLPAnnotator implements NLPAnnotator {

    private final AbstractSequenceClassifier<CoreLabel> classifier;
    private final MaxentTagger tagger;

    public FasterCoreNLPAnnotator() {
        tagger = new MaxentTagger("edu/stanford/nlp/models/pos-tagger/english-left3words/english-left3words-distsim.tagger");

        try {
            classifier = CRFClassifier.getClassifier("edu/stanford/nlp/models/ner/english.all.3class.distsim.crf.ser.gz");
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Could not load the NE classifier. Something is wrong with your dependencies.");
        }

    }

    @Override
    public List<Sentence> annotate(String text) {
        List<Sentence> sentences = new ArrayList<>();


        int i = 0; // sentence index
        List<List<CoreLabel>> classifiedResult = classifier.classify(text);
        for (List<CoreLabel> sentence : classifiedResult) { // sentence loop
            List<Token> words = new ArrayList<>();

            List<TaggedWord> taggedResult = tagger.tagSentence(sentence);
            for (int j = 0; j < sentence.size(); ++j) { // word loop w/ word index
                CoreLabel token = sentence.get(j);

                words.add(new Token(
                        j, //index
                        token.word(), // words
                        token.get(CoreAnnotations.BeforeAnnotation.class) + token.originalText(), //original word
                        convertPos(taggedResult.get(j).tag()), // POStag
                        convertNe(token.get(CoreAnnotations.AnswerAnnotation.class))// NEType
                ));

            }

            String mergedTokens = words.stream().map(Token::getOriginalWord).collect(Collectors.joining());

            sentences.add(new Sentence(
                    i,
                    mergedTokens.trim(),
                    words
            ));

            ++i; //sentence index getting increase
        }
        return sentences;

    }


}
