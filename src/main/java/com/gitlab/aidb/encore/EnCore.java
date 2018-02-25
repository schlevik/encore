package com.gitlab.aidb.encore;

import com.gitlab.aidb.encore.model.NLPAnnotator;
import com.gitlab.aidb.encore.model.ReferenceResolver;
import com.gitlab.aidb.encore.features.definition.FeatureProvider;
import com.gitlab.aidb.encore.features.definition.FeatureProviderHandler;
import com.gitlab.aidb.encore.model.impl.CoreNLPAnnotator;
import com.gitlab.aidb.encore.model.impl.resolvers.NameReferenceResolver;
import com.gitlab.aidb.encore.model.impl.resolvers.PronominalReferenceResolver;
import com.gitlab.aidb.encore.model.references.Reference;
import com.gitlab.aidb.encore.model.references.Substitution;
import com.gitlab.aidb.encore.model.text.Sentence;
import com.gitlab.aidb.encore.model.text.Text;
import com.gitlab.aidb.encore.model.wordtypes.Pronoun;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.*;
import java.util.stream.Collectors;


public class EnCore implements FeatureProviderHandler {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private Marker marker = MarkerFactory.getMarker("EnCore");
    // Order matters!
    private List<ReferenceResolver<? extends Reference>> resolvers;

    private NLPAnnotator annotator;


    public EnCore() {

        logger.debug(marker, "Creating EnCore instance...");
        annotator = new CoreNLPAnnotator();

    }

    private void initialize() {
        //TODO: read from config or sth
        resolvers = new ArrayList<>();
        resolvers.add(new NameReferenceResolver(this));
        resolvers.add(new PronominalReferenceResolver(this));
        logger.debug(marker, "... with following resolvers: {}", resolvers);
    }


    public String resolveCoreference(String text) {
        initialize();
        return substitute(resolve(text));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends FeatureProvider> T getFeatureProvider(Class<T> feature) {

        return (T) resolvers
                .stream()
                .filter(r -> feature.isAssignableFrom(r.getClass()))
                .findFirst()
                .orElseThrow(NotImplementedException::new);
    }


    private Text resolve(String rawText) {
        Text text = new Text(rawText, annotator);
        logger.info(marker, "Collecting references...");
        // for each resolver - collect
        for (ReferenceResolver<? extends Reference> resolver : resolvers) {
            resolver.collect(text);
        }
        logger.info(marker, "...collected. Resolving...");
        // for each resolver - resolve
        for (ReferenceResolver<? extends Reference> resolver : resolvers) {
            text.appendSubstitutions(resolver.resolve());
        }

        logger.info(marker, "found {} substitutions.", text.getSubstitutions().size());
        return text;
    }


    private String substitute(Text text) {
        logger.info(marker, "Substituting...");
        for (Sentence sentence : text.getSentences()) {
            for (Substitution substitution : text.getSubstitutions()) {
                if (sentence.getIndex() == substitution.getSentenceIndex()) {
                    substituteCoreference(substitution, sentence);
                }
            }
        }
        return text.getSentences().stream()
                .map(Sentence::getText)
                .map(String::trim)
                .collect(Collectors.joining(" "));
    }

    private void substituteCoreference(Substitution substitution, Sentence sentence) {

        String original = String.format(" %s ", substitution.getPlainText());
        String reference = String.format(" %s ", substitution.getReference().getPlainText());
        logger.debug(marker, "Substituting reference \n '{}' for \n '{}' in sentence \n {}", reference, original, sentence);
        String newSentence = String.format(" %s ", sentence.getText());
        if (Pronoun.fromString(substitution.getOriginal().get(0).getWord()).getType() == Pronoun.Type.Possessive) {
            sentence.setText(newSentence.replace(original, " " + reference.trim() + "'s "));
        } else {
            sentence.setText(newSentence.replace(original, reference));
        }
    }


}
