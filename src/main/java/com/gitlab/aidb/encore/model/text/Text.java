package com.gitlab.aidb.encore.model.text;

import com.gitlab.aidb.encore.model.NLPAnnotator;
import com.gitlab.aidb.encore.model.references.Substitution;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Text {
    private List<Sentence> sentences;
    private List<Substitution> substitutions;

    public List<Sentence> getSentences() {
        return Collections.unmodifiableList(sentences);
    }

    public List<Substitution> getSubstitutions() {
        return Collections.unmodifiableList(substitutions);
    }

    public void appendSubstitutions(Collection<Substitution> incomingSubstitutions) {
        this.substitutions.addAll(incomingSubstitutions);
    }

    public Text(String text, NLPAnnotator annotator) {
        this.sentences = annotator.annotate(text);
        this.substitutions = new ArrayList<>();
    }

}
