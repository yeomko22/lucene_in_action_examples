package ch4_analyzer.metaphone;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.LetterTokenizer;

public class MetaphoneReplacementAnalyzer extends Analyzer {
    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        LetterTokenizer src = new LetterTokenizer();
        TokenStream result = new MetaphoneReplacementFilter(src);
        return new TokenStreamComponents(src, result);
    }
}
