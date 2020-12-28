package ch4_analyzer.stop;

import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.core.LetterTokenizer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;

public class StopAnalyzerFlawed extends Analyzer {
    private CharArraySet stopWords;

    public StopAnalyzerFlawed() {
        stopWords = EnglishAnalyzer.getDefaultStopSet();
    }

    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        LetterTokenizer src = new LetterTokenizer();
        TokenStream result = new StopFilter(src, stopWords);
        result = new LowerCaseFilter(result);
        return new TokenStreamComponents(src, result);
    }
}
