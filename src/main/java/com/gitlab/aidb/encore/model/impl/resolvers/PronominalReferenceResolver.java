package com.gitlab.aidb.encore.model.impl.resolvers;

import com.gitlab.aidb.encore.model.ReferenceResolver;
import com.gitlab.aidb.encore.features.definition.FeatureProviderHandler;
import com.gitlab.aidb.encore.features.FirstNameMentionFeature;
import com.gitlab.aidb.encore.features.NameReferencesFeature;
import com.gitlab.aidb.encore.features.PredominantGenderFeature;
import com.gitlab.aidb.encore.model.references.NameReference;
import com.gitlab.aidb.encore.model.references.Reference;
import com.gitlab.aidb.encore.model.text.Sentence;
import com.gitlab.aidb.encore.model.text.Text;
import com.gitlab.aidb.encore.model.text.Token;
import com.gitlab.aidb.encore.model.references.PronominalReference;
import com.gitlab.aidb.encore.model.references.Substitution;
import com.gitlab.aidb.encore.model.wordtypes.GenderType;
import com.gitlab.aidb.encore.model.wordtypes.NamedEntityType;
import com.gitlab.aidb.encore.model.wordtypes.Pronoun;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class PronominalReferenceResolver extends BaseResolver implements ReferenceResolver<PronominalReference>, PredominantGenderFeature.Provider,
        NameReferencesFeature.Consumer, FirstNameMentionFeature.Consumer {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private Marker marker = MarkerFactory.getMarker("pronominal");

    private final List<PronominalReference> pronominalReferences;

    private Text text;
    private boolean collected;


    public PronominalReferenceResolver(FeatureProviderHandler featureProviderHandler) {
        super(featureProviderHandler);
        this.pronominalReferences = new ArrayList<>();
    }


    @Override
    public List<PronominalReference> collect(Text text) {
        logger.debug(marker, "Beginning to collect...");
        this.text = text;
        for (Sentence sentence : text.getSentences()) {
            for (Token token : sentence.getTokens()) {
                Pronoun pronoun = Pronoun.fromString(token.getLower());
                if (pronoun != Pronoun.None) {
                    pronominalReferences.add(new PronominalReference(
                            sentence.getIndex(),
                            Collections.singletonList(token),
                            pronoun,
                            getEntityType(token.getLower())
                    ));

                }
            }
        }
        collected = true;
        logger.debug(marker, "...collected: {}", pronominalReferences);
        return pronominalReferences;
    }

    private static final Set<String> personalPronouns = new HashSet<>(
            Arrays.asList("he", "she", "his", "her", "him", "hers", "they", "their", "theirs")
    );

    private NamedEntityType getEntityType(String token) {
        return personalPronouns.contains(token) ? NamedEntityType.Person : NamedEntityType.Thing;
    }

    @Override
    public List<Substitution> resolve() {
        List<Substitution> substitutions = new ArrayList<>();
        NameReference firstNameMention = getFirstNameMention();
        for (PronominalReference pronominalReference : this.pronominalReferences) {
            Reference reference;
            if (pronominalReference.getGender() == firstNameMention.getGender()) {
                reference = firstNameMention;
            } else {
                reference = getClosestMatchingNameMention(pronominalReference);
            }
            if (reference == null) {
                reference = pronominalReference;
            }
            substitutions.add(new Substitution(
                    pronominalReference.getSentenceIndex(), pronominalReference.getTokens(), reference)
            );
        }
        return substitutions;
    }


    private NameReference getClosestMatchingNameMention(PronominalReference pronominalReference) {
        List<NameReference> nameReferences = getNameReferences();
        List<NameReference> sortedNameReferences = nameReferences.stream()
                .sorted(Comparator.comparing(NameReference::getSentenceIndex).reversed())
                .filter(n -> n.getSentenceIndex() <= pronominalReference.getSentenceIndex())
                .collect(Collectors.toList());
        for (NameReference nameReference : sortedNameReferences) {
            if (nameReference.getGender() == pronominalReference.getGender() &&
                    nameReference.getNamedEntityType() == pronominalReference.getNamedEntityType())
                return nameReference;
        }
        return null;
    }

    @Override
    public GenderType getPredominantGender() {
        if (!collected) {
            throw new IllegalStateException("Tried to determine predominant gender when no references were collected!");
        }
        //count male & female pronominal refs
        long males = this.pronominalReferences.stream().filter(r -> r.getGender() == GenderType.Male).count();
        long females = this.pronominalReferences.stream().filter(r -> r.getGender() == GenderType.Female).count();
        if (males == 0 && females == 0) {
            return GenderType.Neutral;
        }
        return males > females ? GenderType.Male : GenderType.Female;
    }
}
