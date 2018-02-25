package com.gitlab.aidb.encore.model.wordtypes;

import edu.stanford.nlp.dcoref.Dictionaries;
import javafx.geometry.Pos;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public enum Pronoun {


    //TODO: this is english-dependant atm
    None(GenderType.None, QuantityType.None, Type.None),

    // personal
    He(GenderType.Male, QuantityType.Singular, Type.Personal),
    She(GenderType.Female, QuantityType.Singular, Type.Personal),
    It(GenderType.Neutral, QuantityType.Singular, Type.Personal),
    They(GenderType.Neutral, QuantityType.Plural, Type.Personal),

    // possessive
    His(GenderType.Male, QuantityType.Singular, Type.Possessive),

    Him(GenderType.Male, QuantityType.Singular, Type.Possessive), //him is not possessive, eh?

    Her(GenderType.Female, QuantityType.Singular, Type.Possessive),

    // reflexive
    Himself(GenderType.Male, QuantityType.Singular, Type.Reflexive),

    Herself(GenderType.Female, QuantityType.Singular, Type.Reflexive);


    private final Type type;

    private final GenderType gender;

    private final QuantityType quantity;


    public enum Type {
        None,
        Personal,
        Possessive,
        Reflexive
    }



    Pronoun(GenderType gender, QuantityType quantity, Type type) {
        this.gender = gender;
        this.quantity = quantity;
        this.type = type;
    }

    public GenderType getGender() {
        return gender;
    }

    public QuantityType getQuantity() {
        return quantity;
    }

    public static Pronoun fromString(String pronoun) {
        for (Pronoun type : Pronoun.class.getEnumConstants()) {
            if (type.name().toLowerCase().equals(pronoun.toLowerCase())) {
                return type;
            }
        }
        return None;
    }


    public Pronoun.Type getType() {
        return type;
    }
}
