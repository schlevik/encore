package com.gitlab.aidb.encore.model.text;

import com.gitlab.aidb.encore.model.wordtypes.NamedEntityType;
import com.gitlab.aidb.encore.model.wordtypes.POSType;

import java.util.Objects;

public class Token {
    private int index;
    private String word;
    private String originalWord;
    private POSType partOfSpeech;
    private NamedEntityType namedEntityType;

    public Token(int index, String word, String originalWord, POSType pos, NamedEntityType neType) {
        this.index = index;
        this.word = word;
        this.originalWord = originalWord;
        this.partOfSpeech = pos;
        this.namedEntityType = neType;
    }

    public String getLower() {
        return word.toLowerCase();
    }

    public int getIndex() {
        return index;
    }

    public String getWord() {
        return word;
    }

    public String getOriginalWord() {
        return originalWord;
    }

    public POSType getPartOfSpeech() {
        return partOfSpeech;
    }

    public NamedEntityType getNamedEntityType() {
        return namedEntityType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Token token = (Token) o;
        return index == token.index &&
                Objects.equals(word, token.word) &&
                Objects.equals(originalWord, token.originalWord) &&
                partOfSpeech == token.partOfSpeech &&
                namedEntityType == token.namedEntityType;
    }

    @Override
    public int hashCode() {

        return Objects.hash(index, word, originalWord, partOfSpeech, namedEntityType);
    }

    @Override
    public String toString() {
        return "Token{" +
                "index=" + index +
                ", word='" + word + '\'' +
                ", partOfSpeech=" + partOfSpeech +
                ", namedEntityType=" + namedEntityType +
                '}';
    }


}
