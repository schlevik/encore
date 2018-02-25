package com.gitlab.aidb.encore.model.references;

import com.gitlab.aidb.encore.model.text.Token;

import java.util.List;
import java.util.stream.Collectors;

public class Substitution {
    private int sentenceIndex;
    private List<Token> original;
    private Reference reference;
    private String plainText;


    public Substitution(int sentenceIndex, List<Token> original, Reference reference) {
        this.sentenceIndex = sentenceIndex;
        this.original = original;
        this.reference = reference;
        this.plainText = original.stream().map(Token::getWord).collect(Collectors.joining(" "));
    }

    public int getSentenceIndex() {
        return sentenceIndex;
    }

    public String getPlainText() {
        return plainText;
    }

    public List<Token> getOriginal() {
        return original;
    }

    public Reference getReference() {
        return reference;
    }

    @Override
    public String toString() {
        return "Substitution{" +
                "sentenceIndex=" + sentenceIndex +
                ", original=" + plainText +
                ", reference=" + reference.getPlainText() +
                '}';
    }
}
