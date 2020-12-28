package ch4_analyzer.synonym;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardTokenizer;

public class SynonymAnalyzer extends Analyzer {
    private SynonymEngine engine;

    public SynonymAnalyzer(SynonymEngine engine) {
        this.engine = engine;
    }

    @Override
    protected TokenStreamComponents createComponents(String s) {
        StandardTokenizer src = new StandardTokenizer();
        TokenStream result = new SynonymFilter(src, engine);
        return new TokenStreamComponents(src, result);
    }
}
