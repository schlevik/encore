package com.gitlab.aidb.encore.model;

import com.gitlab.aidb.encore.model.wordtypes.GenderType;

import java.util.List;

public interface GenderClassifier {

    public GenderType classify(String name);
}
