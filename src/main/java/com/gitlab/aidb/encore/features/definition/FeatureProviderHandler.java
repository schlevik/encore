package com.gitlab.aidb.encore.features.definition;

public interface FeatureProviderHandler {
    <T extends FeatureProvider> T getFeatureProvider(Class<T> feature);

}
