package com.gitlab.aidb.encore.model.references;

import com.gitlab.aidb.encore.model.text.Token;

import java.util.List;
import java.util.stream.Collectors;

public abstract class Reference {
    private int sentenceIndex;
    private List<Token> tokens;
    private String text;

    public Reference(int sentenceIndex, List<Token> tokens) {
        this.sentenceIndex = sentenceIndex;
        this.tokens = tokens;
        this.text = getTokens()
                .stream()
                .map(Token::getWord)
                .collect(Collectors.joining(" "));
    }

    public int getSentenceIndex() {
        return sentenceIndex;
    }

    public List<Token> getTokens() {
        return tokens;
    }

    public String getPlainText() {
        return text;
    }


    @Override
    public String toString() {
        return "Reference{" +
                "sentenceIndex=" + sentenceIndex +
                ", tokens=" + tokens +
                '}';
    }
}
