package com.gitlab.aidb.encore.model;

import com.gitlab.aidb.encore.model.text.Text;
import com.gitlab.aidb.encore.model.references.Reference;
import com.gitlab.aidb.encore.model.references.Substitution;

import java.util.List;

public interface ReferenceResolver<T extends Reference> {

    List<T> collect(Text text);

    List<Substitution> resolve();


}
