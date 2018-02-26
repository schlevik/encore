package com.gitlab.aidb.encore.features;

import com.gitlab.aidb.encore.EnCore;
import org.apache.commons.io.IOUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.InputStream;
import java.util.Arrays;

public class EnCoreTest {
    private EnCore encore;

    @BeforeClass
    public void beforeClass() {
        encore = new EnCore(true);
    }


    @Test(invocationCount = 1, dataProvider = "texts", dataProviderClass = EnCoreTest.class)
    public void test(String input, String expected) {
        System.out.println("RUNNING");
        Assert.assertEquals(encore.resolveCoreference(input), expected);

    }

    @DataProvider(name = "texts")
    public static Object[][] createTestSet() throws Exception {
        InputStream is = ClassLoader.getSystemResourceAsStream("Barack_Obama.expected.txt");
        String obamaExpected = IOUtils.toString(is, "utf-8");
        is = ClassLoader.getSystemResourceAsStream("Barack_Obama.input.txt");
        String obamaInput = IOUtils.toString(is, "utf-8");
        is = ClassLoader.getSystemResourceAsStream("short.expected.txt");
        String shortExpected = IOUtils.toString(is, "utf-8");
        is = ClassLoader.getSystemResourceAsStream("short.input.txt");
        String shortInput = IOUtils.toString(is, "utf-8");
        is = ClassLoader.getSystemResourceAsStream("shorter.expected.txt");
        String shorterExpected = IOUtils.toString(is, "utf-8");
        is = ClassLoader.getSystemResourceAsStream("shorter.input.txt");
        String shorterInput = IOUtils.toString(is, "utf-8");

        IOUtils.closeQuietly(is);
        Object[][] result = {
                {
                        String.join(" ", Arrays.asList(shortInput.split("\n"))),
                        String.join(" ", Arrays.asList(shortExpected.split("\n")))
                },
                {
                        String.join(" ", Arrays.asList(obamaInput.split("\n"))),
                        String.join(" ", Arrays.asList(obamaExpected.split("\n")))
                },
                {
                        String.join(" ", Arrays.asList(shorterInput.split("\n"))),
                        String.join(" ", Arrays.asList(shorterExpected.split("\n")))
                },
        };


        return result;
    }
}

