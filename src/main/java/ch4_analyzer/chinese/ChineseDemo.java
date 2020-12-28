package ch4_analyzer.chinese;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.awt.*;
import java.io.IOException;
import java.io.StringReader;

public class ChineseDemo {
    public static String[] strings = {"道德經"};

    private static Analyzer[] analyzers = {
            new SimpleAnalyzer(),
            new StandardAnalyzer(),
            new CJKAnalyzer()
    };

    public static void main(String[] args) throws Exception {
        for (String string : strings) {
            for (Analyzer analyzer : analyzers) {
                analyze(string, analyzer);
            }
        }
    }

    public static void analyze(String string, Analyzer analyzer) throws IOException {
        StringBuffer buffer = new StringBuffer();
        TokenStream stream = analyzer.tokenStream("contents", new StringReader(string));
        CharTermAttribute term = stream.addAttribute(CharTermAttribute.class);
        stream.reset();
        while (stream.incrementToken()) {
           buffer.append("[");
           buffer.append(term.toString());
           buffer.append("] ");
        }
        stream.close();
        String output = buffer.toString();
        System.out.println(output);

        Frame f = new Frame();
        f.setTitle(analyzer.getClass().getSimpleName() + " : " + string);
        f.setResizable(true);

        Font font = new Font(null, Font.PLAIN, 36);
        int width = getWidth(f.getFontMetrics(font), output);
        f.setSize((width < 250) ? 250 : width + 50, 75);

        Label label = new Label(output);
        label.setSize(width, 75);
        label.setAlignment(Label.CENTER);
        label.setFont(font);
        f.add(label);
        f.setVisible(true);
    }

    private static int getWidth(FontMetrics metrics, String s) {
        int size = 0;
        int length = s.length();
        for (int i=0; i<length; i++) {
            size += metrics.charWidth(s.charAt(i));
        }
        return size;
    }
}
