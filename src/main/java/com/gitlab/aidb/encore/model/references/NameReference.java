package com.gitlab.aidb.encore.model.references;

import com.gitlab.aidb.encore.model.text.Token;
import com.gitlab.aidb.encore.model.wordtypes.GenderType;
import com.gitlab.aidb.encore.model.wordtypes.NamedEntityType;
import com.gitlab.aidb.encore.model.wordtypes.QuantityType;

import java.util.List;

public class NameReference extends Reference {
    private NamedEntityType namedEntityType;
    private GenderType gender;
    private QuantityType quantityType;

    public NameReference(int sentenceIndex, List<Token> token, NamedEntityType namedEntityType,
                         GenderType gender, QuantityType quantityType) {
        super(sentenceIndex, token);
        this.namedEntityType = namedEntityType;
        if (namedEntityType.equals(NamedEntityType.Person)) {
            this.gender = gender;
        } else {
            this.gender = GenderType.Neutral;
        }
        this.quantityType = quantityType;
    }

    public NamedEntityType getNamedEntityType() {
        return namedEntityType;
    }

    public GenderType getGender() {
        return gender;
    }

    public QuantityType getQuantityType() {
        return quantityType;
    }

    @Override
    public String toString() {
        return "NameReference{" +
                "sentenceIndex=" + getSentenceIndex() +
                ", tokens=" + getTokens() +
                ", namedEntityType=" + namedEntityType +
                ", gender=" + gender +
                ", quantityType=" + quantityType +
                '}';
    }
}
