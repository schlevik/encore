package com.gitlab.aidb.encore.features;

import com.gitlab.aidb.encore.features.definition.FeatureConsumer;
import com.gitlab.aidb.encore.features.definition.FeatureProvider;
import com.gitlab.aidb.encore.features.definition.FeatureProviderHandler;
import org.testng.Assert;
import org.testng.annotations.Test;

@Test
public class FeatureExampleTest {
    private FeatureProvider provider;
    private ExampleFeatureConsumer consumer;

    @Test
    public void successfullyCreateConsumerAndProvider() {

        provider = new ExampleFeatureProvider();
        consumer = new ExampleFeatureConsumer();
        Assert.assertEquals(consumer.getExampleFeature(), "Providing: ExampleFeature!");
    }

    interface ExampleFeature {
        interface Provider extends FeatureProvider {
            String getExampleFeature();
        }

        interface Consumer extends FeatureConsumer {
            default String getExampleFeature() {
                return this.getFeatureProviderHandler().getFeatureProvider(Provider.class).getExampleFeature();
            }
        }
    }

    class ExampleFeatureProvider implements ExampleFeature.Provider {


        @Override
        public String getExampleFeature() {
            return "Providing: ExampleFeature!";

        }
    }


    class ExampleFeatureConsumer implements ExampleFeature.Consumer {


        @Override
        public FeatureProviderHandler getFeatureProviderHandler() {
            return new FeatureProviderHandler() {
                @Override
                public <T extends FeatureProvider> T getFeatureProvider(Class<T> feature) {
                    return (T) provider;
                }
            };
        }
    }
}
