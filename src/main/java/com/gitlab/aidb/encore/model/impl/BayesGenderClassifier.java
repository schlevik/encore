package com.gitlab.aidb.encore.model.impl;

import com.gitlab.aidb.encore.model.GenderClassifier;
import com.gitlab.aidb.encore.model.wordtypes.GenderType;
import de.daslaboratorium.machinelearning.classifier.Classification;
import de.daslaboratorium.machinelearning.classifier.bayes.BayesClassifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;


import java.io.*;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BayesGenderClassifier implements GenderClassifier {
    private static URL PATH = GenderClassifier.class.getClassLoader().getSystemResource("classifier.bin");
    private BayesClassifier<String, GenderType> classifier;
    private static Logger logger = LoggerFactory.getLogger(GenderClassifier.class);
    private static Marker marker = MarkerFactory.getMarker("gender-classifier");

    private BayesGenderClassifier() {
        classifier = new BayesClassifier<>();
        classifier.setMemoryCapacity(6000);
        logger.info(marker, "Training classifier...");
        train();
        logger.info(marker, "Done training!");

    }

    private void train() {
        Stream<Classification> females = getNames("female.txt").
                map(s -> new Classification<>(asFeature(s), GenderType.Female));
        Stream<Classification> males = getNames("male.txt").
                map(s -> new Classification<>(asFeature(s), GenderType.Male));
        Stream.concat(females, males).collect(toShuffledList()).forEach(classifier::learn);
    }

    private static final Collector<?, ?, ?> SHUFFLER = Collectors.collectingAndThen(
            Collectors.toList(),
            list -> {
                Collections.shuffle(list);
                return list;
            }
    );

    @SuppressWarnings("unchecked")
    public static <T> Collector<T, ?, List<T>> toShuffledList() {
        return (Collector<T, ?, List<T>>) SHUFFLER;
    }

    private Collection<String> asFeature(String name) {
        List<String> featureSet = Arrays.asList(
                name.substring(name.length() - 1, name.length()),
                name.substring(name.length() - 2, name.length()),
                name.length() < 3 ? name.substring(name.length() - 2, name.length()) : name.substring(name.length() - 3, name.length())
        );
        return featureSet;
    }

    private Stream<String> getNames(String resourceName) {
        BufferedReader buf = new BufferedReader(new InputStreamReader(getClass().getClassLoader().getSystemResourceAsStream(resourceName)));
        return buf.lines();
    }


    public static BayesClassifier deserialize() throws IOException, ClassNotFoundException {
        ObjectInputStream inputStream = new ObjectInputStream(ClassLoader.getSystemResourceAsStream("classifier.bin"));
        return (BayesClassifier) inputStream.readObject();

    }

    private BayesGenderClassifier(BayesClassifier<String, GenderType> classifier) {
        this.classifier = classifier;
    }

    public static BayesGenderClassifier create() {
        if (BayesGenderClassifier.PATH != null) {
            try {
                logger.info(marker, "Deserializing pre-trained classifier.");
                return new BayesGenderClassifier(deserialize());
            } catch (IOException e) {
                logger.warn(marker, "Couldn't deserialize classifier, creating new.");
            } catch (Exception e) {
                throw new RuntimeException("Implementation error. Mea culpa.", e);
            }

        }
        try {
            return new BayesGenderClassifier();
        } catch (Exception e) {
            throw new RuntimeException("Implementation error. Mea culpa.", e);
        }

    }


    @Override
    public GenderType classify(String name) {
        GenderType result = classifier.classify(asFeature(name)).getCategory();
        logger.debug(marker, "Classifying {} as {}", name, result);
        return result;
    }
}