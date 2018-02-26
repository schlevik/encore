package com.gitlab.aidb.encore.features;

import com.gitlab.aidb.encore.model.impl.CoreNLPAnnotator;
import com.gitlab.aidb.encore.model.impl.FasterCoreNLPAnnotator;
import com.gitlab.aidb.encore.model.text.Sentence;

import org.testng.annotations.Test;

import java.util.List;

public class CoreNLPTest {

    @Test
    public void test() {
        CoreNLPAnnotator usualAnnotator = new CoreNLPAnnotator();
        FasterCoreNLPAnnotator fasterAnnotator = new FasterCoreNLPAnnotator();
        String text = "Donald Trump is a weird man, Trump is the president.";
        List<Sentence> usualResult = usualAnnotator.annotate(text);
        List<Sentence> fasterResult = fasterAnnotator.annotate(text);
        usualResult.forEach(s -> System.out.println(s.getTokens()));
        fasterResult.forEach(s -> System.out.println(s.getTokens()));
    }

}
