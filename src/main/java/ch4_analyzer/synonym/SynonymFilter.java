package ch4_analyzer.synonym;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.util.AttributeSource;

import java.io.IOException;
import java.util.Stack;

public class SynonymFilter extends TokenFilter {
    public static final String TOKEN_TYPE_SYNONYM = "SYNONYM";
    private Stack<String> synonymStack;
    private SynonymEngine engine;
    private AttributeSource.State current;

    private CharTermAttribute termAtt;
    private PositionIncrementAttribute posIncrAtt;
    public SynonymFilter(TokenStream in, SynonymEngine engine) {
        super(in);
        synonymStack = new Stack<String>();
        this.engine = engine;
        this.termAtt = addAttribute(CharTermAttribute.class);
        this.posIncrAtt = addAttribute(PositionIncrementAttribute.class);
    }

    @Override
    public final boolean incrementToken() throws IOException {
        if (synonymStack.size() > 0) {
            String syn = synonymStack.pop();
            restoreState(current);
            termAtt.setEmpty();
            termAtt.append(syn);
            posIncrAtt.setPositionIncrement(0);
            return true;
        }

        if (!input.incrementToken())
            return false;

        if (addAliasToStack()){
            current = captureState();
        }
        return true;
    }

    private boolean addAliasToStack() throws IOException {
        String[] synonyms = engine.getSynonyms(termAtt.toString());
        if (synonyms == null) {
            return false;
        }
        for (String synonym : synonyms) {
            synonymStack.push(synonym);
        }
        return true;
    }
}
