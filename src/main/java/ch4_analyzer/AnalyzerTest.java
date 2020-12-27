package ch4_analyzer;

import junit.framework.TestCase;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

import java.io.IOException;

public class AnalyzerTest extends TestCase {
    protected  CharArraySet stopWords;
    protected  Analyzer[] analyzers;

    private void analyze(String text) throws IOException {
        System.out.println("Analyzing \"" + text + "\"");
        for (Analyzer analyzer : analyzers) {
            String name = analyzer.getClass().getSimpleName();
            System.out.println(" " + name + ":");
            System.out.print("   ");
            AnalyzerUtils.displayTokens(analyzer, text);
            System.out.println("\n");
        }
    }

    public void testAnalyze() throws Exception {
        stopWords = EnglishAnalyzer.getDefaultStopSet();
        analyzers =  new Analyzer[] {
                new WhitespaceAnalyzer(),
                new SimpleAnalyzer(),
                new StopAnalyzer(stopWords),
                new StandardAnalyzer()
        };

        String[] examples = {
                "The quick brown fox jumped over the lazy dog",
                "XY&Z Corporation - xyz@example.com",
                "192.168.0.1 this is my ip 2001:0db8:85a3:0000:0000:8a2e:0370:7334"
        };
        for (String text : examples) {
            analyze(text);
        }
    }

    public void testSimpleAnalyzer() throws Exception {
        AnalyzerUtils.displayTokensWithFullDetails(new SimpleAnalyzer(), "The quick brown fox....");
    }

    public void testStandardAnalyzer() throws Exception {
        AnalyzerUtils.displayTokensWithFullDetails(new StandardAnalyzer(), "i'll email you at xyz@example.com");
    }

    public void testStopAnalyzer2() throws Exception {
        AnalyzerUtils.assertAnalyzesTo(new StopAnalyzer2(), "The quick brown", new String[]{"quick", "brown"});
    }

    public void testStopAnalyzerFlawed() throws Exception {
        AnalyzerUtils.assertAnalyzesTo(new StopAnalyzerFlawed(), "The quick brown...", new String[]{"the", "quick", "brown"});
    }
}
