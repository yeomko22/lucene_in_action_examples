package ch4_analyzer.synonym;

import ch4_analyzer.AnalyzerUtils;

import java.io.IOException;

public class SynonymAnalyzerViewer {
    public static void main(String[] args) throws IOException {
        SynonymEngine engine = new TestSynonymEngine();
        AnalyzerUtils.displayTokensWithPositions(new SynonymAnalyzer(engine),
                "The quick brown fox jumps over the lazy dog");
    }
}
