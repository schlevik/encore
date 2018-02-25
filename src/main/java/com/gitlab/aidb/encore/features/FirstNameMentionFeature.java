package com.gitlab.aidb.encore.features;

import com.gitlab.aidb.encore.features.definition.FeatureConsumer;
import com.gitlab.aidb.encore.features.definition.FeatureProvider;
import com.gitlab.aidb.encore.model.references.NameReference;

public interface FirstNameMentionFeature {

    interface Provider extends FeatureProvider {
        NameReference getFirstNameMention();
    }

    interface Consumer extends FeatureConsumer {
        default NameReference getFirstNameMention() {
            return this.getFeatureProviderHandler().getFeatureProvider(Provider.class).getFirstNameMention();
        }
    }
}
