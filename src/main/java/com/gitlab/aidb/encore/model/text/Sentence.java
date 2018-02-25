package com.gitlab.aidb.encore.model.text;

import java.util.List;
import java.util.Objects;

public class Sentence {
    private int index;
    private String text;
    private List<Token> tokens;

    public Sentence(int index, String text, List<Token> tokens) {
        this.index = index;
        this.text = Objects.requireNonNull(text);
        this.tokens = Objects.requireNonNull(tokens);
    }


    public int getIndex() {
        return index;
    }

    public String getText() {
        return text;
    }

    public List<Token> getTokens() {
        return tokens;
    }

    public void setText(String text) {
        Objects.requireNonNull(text);
        this.text = text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sentence sentence = (Sentence) o;
        return index == sentence.index &&
                Objects.equals(text, sentence.text) &&
                Objects.equals(tokens, sentence.tokens);
    }

    @Override
    public int hashCode() {

        return Objects.hash(index, text, tokens);
    }

    @Override
    public String toString() {
        return "Sentence{" +
                "index=" + index +
                ", text='" + text + '\'' +
                '}';
    }
}
