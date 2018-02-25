package com.gitlab.aidb.encore.model.impl.resolvers;

import com.gitlab.aidb.encore.model.GenderClassifier;
import com.gitlab.aidb.encore.model.ReferenceResolver;
import com.gitlab.aidb.encore.features.definition.FeatureProviderHandler;
import com.gitlab.aidb.encore.features.FirstNameMentionFeature;
import com.gitlab.aidb.encore.features.NameReferencesFeature;
import com.gitlab.aidb.encore.features.PredominantGenderFeature;
import com.gitlab.aidb.encore.model.impl.BayesGenderClassifier;
import com.gitlab.aidb.encore.model.text.Sentence;
import com.gitlab.aidb.encore.model.text.Text;
import com.gitlab.aidb.encore.model.text.Token;
import com.gitlab.aidb.encore.model.references.NameReference;
import com.gitlab.aidb.encore.model.references.Substitution;
import com.gitlab.aidb.encore.model.wordtypes.GenderType;
import com.gitlab.aidb.encore.model.wordtypes.NamedEntityType;
import com.gitlab.aidb.encore.model.wordtypes.QuantityType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.util.*;

public class NameReferenceResolver extends BaseResolver
        implements ReferenceResolver<NameReference>, NameReferencesFeature.Provider, FirstNameMentionFeature.Provider,
        PredominantGenderFeature.Consumer {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private Marker marker = MarkerFactory.getMarker("name");

    private final GenderClassifier genderClassifier;
    private final List<NameReference> nameReferences;
    private Text text;
    private boolean collected;
    private boolean resolved;

    public NameReferenceResolver(FeatureProviderHandler featureProviderHandler) {
        super(featureProviderHandler);
        this.genderClassifier = BayesGenderClassifier.create();
        this.collected = false;
        this.nameReferences = new ArrayList<>();
    }


    @Override
    public List<NameReference> collect(Text text) {

        logger.debug(marker, "...begin to collect name references.");
        this.text = text;
        for (Sentence sentence : text.getSentences()) {
            List<Token> entityTokens = new LinkedList<>();
            for (Token token : sentence.getTokens()) {
                if (token.getNamedEntityType() != NamedEntityType.Other) {
                    entityTokens.add(token);
                } else {
                    if (entityTokens.size() > 0 && isValidNamedEntityType(entityTokens)) {

                        NameReference nameReference = new NameReference(
                                sentence.getIndex(),
                                entityTokens,
                                entityTokens.get(0).getNamedEntityType(),
                                classifyGender(entityTokens),
                                detectQuantity(entityTokens)
                        );
                        nameReferences.add(nameReference);
                    }
                    entityTokens = new LinkedList<>();

                }
            }

        }

        logger.debug(marker, "...collected: {}", nameReferences);
        this.collected = true;
        return nameReferences;
    }

    private GenderType classifyGender(List<Token> tokens) {
        return genderClassifier.classify(tokens.get(0).getWord());
    }

    private QuantityType detectQuantity(List<Token> tokens) {
        // yea, this hacked together
        // also specific to english
        boolean endsWithS = tokens.get(tokens.size() - 1).getWord().endsWith("s");
        return endsWithS ? QuantityType.Plural : QuantityType.Singular;
    }

    @Override
    public List<Substitution> resolve() {
        List<Substitution> result = new ArrayList<>();

        NameReference firstName = getFirstNameMention();

        for (NameReference nameReference : nameReferences) {
            NameReference reference;
            if (firstName.getPlainText().contains(nameReference.getPlainText())) {
                reference = firstName;
            } else {
                reference = getLongestPrecedent(nameReference);

            }
            result.add(new Substitution(nameReference.getSentenceIndex(), nameReference.getTokens(), reference));
        }
        resolved = true;
        return result;
    }

    private NameReference getLongestPrecedent(NameReference subReference) {
        NameReference reference = subReference;
        for (NameReference nameReference : nameReferences) {
            if (nameReference.getSentenceIndex() < subReference.getSentenceIndex() &&
                    nameReferences.size() > reference.getTokens().size() &&
                    nameReference.getPlainText().contains(reference.getPlainText())) {
                reference = nameReference;
            }
        }
        return reference;
    }

    private final static Collection<NamedEntityType> allowed =
            Arrays.asList(NamedEntityType.Person, NamedEntityType.Other, NamedEntityType.None);

    private boolean isValidNamedEntityType(List<Token> entityTokens) {
        return entityTokens.stream().allMatch(token -> allowed.contains(token.getNamedEntityType()));
    }


    @Override
    public String toString() {
        return String.format(
                "Class: %s, Using GenderClassifier implementation: %s",
                getClass().getCanonicalName(),
                genderClassifier.getClass().getCanonicalName()
        );
    }

    @Override
    public NameReference getFirstNameMention() {
        GenderType predominantGender = getPredominantGender();
        for (NameReference nameReference : nameReferences) {
            if (nameReference.getSentenceIndex() == 0 && nameReference.getTokens().get(0).getIndex() < 10) {
                if (predominantGender == GenderType.Male || predominantGender == GenderType.Female) {
                    return new NameReference(
                            0,
                            nameReference.getTokens(),
                            NamedEntityType.Person,
                            predominantGender,
                            QuantityType.Singular
                    );
                } else {
                    return new NameReference(
                            0,
                            nameReference.getTokens(),
                            NamedEntityType.Other,
                            GenderType.Neutral,
                            QuantityType.Singular
                    );
                }
            }
        }
        for (Sentence sentence : text.getSentences()) {
            if (sentence.getTokens().size() > 0) {
                List<Token> constructedFirstMention = new LinkedList<>();
                for (Token token : sentence.getTokens()) {
                    if (token.getWord().length() > 0 && Character.isUpperCase(token.getWord().charAt(0))) {
                        constructedFirstMention.add(token);
                    } else {
                        return new NameReference(
                                0,
                                constructedFirstMention,
                                NamedEntityType.Other,
                                GenderType.Neutral,
                                QuantityType.Singular

                        );
                    }
                }

            }
        }
        return null;
    }


    @Override
    public List<NameReference> getNameReferences() {
        if (!collected) {
            throw new IllegalStateException("No name references collected! Tried to get Name References before collected!");
        }
        return Collections.unmodifiableList(this.nameReferences);
    }
}
