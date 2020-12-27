package ch4_analyzer;

import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.core.LetterTokenizer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;

public class StopAnalyzer2 extends Analyzer {
    private CharArraySet stopWords;

    public StopAnalyzer2() {
        stopWords = EnglishAnalyzer.getDefaultStopSet();
    }

    public StopAnalyzer2(String[] stopWords) {
        this.stopWords = StopFilter.makeStopSet(stopWords);
    }

    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        LetterTokenizer src = new LetterTokenizer();
        TokenStream result = new LowerCaseFilter(src);
        result = new StopFilter(result, stopWords);
        return new TokenStreamComponents(src, result);
    }
}
