package ch4_analyzer.stop;

import ch4_analyzer.AnalyzerUtils;
import junit.framework.TestCase;

public class StopAnalyzerTest extends TestCase {

    public void testStopAnalyzer2() throws Exception {
        AnalyzerUtils.assertAnalyzesTo(new StopAnalyzer2(), "The quick brown", new String[]{"quick", "brown"});
    }

    public void testStopAnalyzerFlawed() throws Exception {
        AnalyzerUtils.assertAnalyzesTo(new StopAnalyzerFlawed(), "The quick brown...", new String[]{"the", "quick", "brown"});
    }
}
