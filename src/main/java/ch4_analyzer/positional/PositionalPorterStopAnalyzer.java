package ch4_analyzer.positional;

import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.core.LetterTokenizer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.en.PorterStemFilter;

import java.util.Set;

public class PositionalPorterStopAnalyzer extends Analyzer {
    private CharArraySet stopWords;

    public PositionalPorterStopAnalyzer(Set stopWords) {
        this.stopWords = EnglishAnalyzer.getDefaultStopSet();
    }

    @Override
    protected TokenStreamComponents createComponents(String s) {
        LetterTokenizer src = new LetterTokenizer();
        TokenStream result = new LowerCaseFilter(src);
        result = new StopFilter(src, stopWords);
        result = new PorterStemFilter(result);
        return new TokenStreamComponents(src, result);
    }
}
