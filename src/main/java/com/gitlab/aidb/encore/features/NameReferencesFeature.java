package com.gitlab.aidb.encore.features;

import com.gitlab.aidb.encore.features.definition.FeatureConsumer;
import com.gitlab.aidb.encore.features.definition.FeatureProvider;
import com.gitlab.aidb.encore.model.references.NameReference;

import java.util.List;

public interface NameReferencesFeature {


    interface Provider extends FeatureProvider {
        List<NameReference> getNameReferences();
    }

    interface Consumer extends FeatureConsumer {
        default List<NameReference> getNameReferences() {
            return this.getFeatureProviderHandler().getFeatureProvider(Provider.class).getNameReferences();
        }
    }
}
