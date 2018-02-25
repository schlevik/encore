package com.gitlab.aidb.encore.model;

import com.gitlab.aidb.encore.model.text.Sentence;

import java.util.List;

public interface NLPAnnotator {

    List<Sentence> annotate(String text);
}
