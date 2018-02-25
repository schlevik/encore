package com.gitlab.aidb.encore.model.references;

import com.gitlab.aidb.encore.model.text.Token;
import com.gitlab.aidb.encore.model.wordtypes.GenderType;
import com.gitlab.aidb.encore.model.wordtypes.NamedEntityType;
import com.gitlab.aidb.encore.model.wordtypes.Pronoun;
import com.gitlab.aidb.encore.model.wordtypes.QuantityType;

import java.util.List;

public class PronominalReference extends Reference {
    private GenderType gender;
    private QuantityType quantity;
    private Pronoun.Type pronounType;
    private NamedEntityType namedEntityType;

    public PronominalReference(int sentenceIndex, List<Token> tokens, Pronoun pronoun, NamedEntityType namedEntityType) {
        super(sentenceIndex, tokens);
        this.gender = pronoun.getGender();
        this.quantity = pronoun.getQuantity();
        this.pronounType = pronoun.getType();
        this.namedEntityType = namedEntityType;
    }

    public PronominalReference(int sentenceIndex, List<Token> tokens) {
        this(sentenceIndex, tokens, Pronoun.None, NamedEntityType.None);
    }

    public GenderType getGender() {
        return gender;
    }

    public QuantityType getQuantity() {
        return quantity;
    }

    public NamedEntityType getNamedEntityType() {
        return namedEntityType;
    }

    @Override
    public String toString() {
        return "PronominalReference{" +
                "sentenceIndex=" + getSentenceIndex() +
                ", tokens=" + getTokens() +
                ", gender=" + gender +
                ", namedEntityType=" + namedEntityType +
                ", quantity=" + quantity +
                '}';
    }
}
