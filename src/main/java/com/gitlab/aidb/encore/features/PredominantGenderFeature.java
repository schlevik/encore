package com.gitlab.aidb.encore.features;

import com.gitlab.aidb.encore.features.definition.FeatureConsumer;
import com.gitlab.aidb.encore.features.definition.FeatureProvider;
import com.gitlab.aidb.encore.model.wordtypes.GenderType;

public interface PredominantGenderFeature {
    interface Provider extends FeatureProvider {
        GenderType getPredominantGender();
    }

    interface Consumer extends FeatureConsumer {
        default GenderType getPredominantGender() {
            return this.getFeatureProviderHandler().getFeatureProvider(Provider.class).getPredominantGender();
        }
    }
}
