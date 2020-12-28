package ch4_analyzer.metaphone;

import org.apache.commons.codec.language.Metaphone;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;

import java.io.IOException;

public class MetaphoneReplacementFilter extends TokenFilter {
    public static final String METAPHONE = "metaphone";
    private Metaphone metaphoner = new Metaphone();
    private CharTermAttribute termAttr;
    private TypeAttribute typeAttr;

    public MetaphoneReplacementFilter(TokenStream input) {
        super(input);
        termAttr = addAttribute(CharTermAttribute.class);
        typeAttr = addAttribute(TypeAttribute.class);
    }

    @Override
    public final boolean incrementToken() throws IOException {
        if (!input.incrementToken())
            return false;
        String encodeded;
        encodeded = metaphoner.encode(termAttr.toString());
        System.out.println("org: " + termAttr.toString() + " encoded: " + encodeded);
        termAttr.setEmpty();
        termAttr.append(encodeded);
        typeAttr.setType(METAPHONE);
        return true;
    }
}