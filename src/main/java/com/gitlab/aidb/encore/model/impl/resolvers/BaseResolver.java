package com.gitlab.aidb.encore.model.impl.resolvers;

import com.gitlab.aidb.encore.features.definition.FeatureConsumer;
import com.gitlab.aidb.encore.features.definition.FeatureProviderHandler;


public abstract class BaseResolver implements FeatureConsumer {
    private FeatureProviderHandler featureProviderHandler;

    protected BaseResolver(FeatureProviderHandler featureProviderHandler) {
        this.featureProviderHandler = featureProviderHandler;
    }

    public FeatureProviderHandler getFeatureProviderHandler() {
        return featureProviderHandler;
    }

}
