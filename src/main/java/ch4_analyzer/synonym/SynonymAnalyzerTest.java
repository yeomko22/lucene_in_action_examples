package ch4_analyzer.synonym;

import common.TestUtil;
import junit.framework.TestCase;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.ByteBuffersDirectory;

import java.io.StringReader;

public class SynonymAnalyzerTest extends TestCase {
    private IndexSearcher searcher;
    private static SynonymAnalyzer synonymAnalyzer = new SynonymAnalyzer(new TestSynonymEngine());

    public void setUp() throws Exception {
        ByteBuffersDirectory directory = new ByteBuffersDirectory();
        IndexWriterConfig iwc = new IndexWriterConfig(synonymAnalyzer);
        IndexWriter writer = new IndexWriter(directory, iwc);
        Document doc = new Document();
        doc.add(new TextField("content", "The quick brown fox jumps over the lazy dog", Field.Store.YES));
        writer.addDocument(doc);
        writer.close();

        searcher = new IndexSearcher(DirectoryReader.open(directory));
    }

    public void tearDown() throws Exception {
        searcher.getIndexReader().close();
    }

    public void testJumps() throws Exception {
        TokenStream stream = synonymAnalyzer.tokenStream("contents", new StringReader("jumps"));
        CharTermAttribute term = stream.addAttribute(CharTermAttribute.class);
        PositionIncrementAttribute posIncr = stream.addAttribute(PositionIncrementAttribute.class);

        int i = 0;
        String[] expected = new String[] {"jumps", "hops", "leaps"};
        stream.reset();
        while (stream.incrementToken()) {
            assertEquals(expected[i], term.toString());
            int expectedPos;
            if (i == 0) {
                expectedPos = 1;
            } else {
                expectedPos = 0;
            }
            assertEquals(expectedPos,
                    posIncr.getPositionIncrement());
            i++;
        }
        assertEquals(3, i);
        stream.close();
    }

    public void testSearchByAPI() throws Exception {
        TermQuery tq = new TermQuery(new Term("content", "hops"));
        assertEquals(1, TestUtil.hitCount(searcher, tq));
        PhraseQuery.Builder builder = new PhraseQuery.Builder();
        builder.add(new Term("content", "fox"));
        builder.add(new Term("content", "hops"));
        PhraseQuery pq = builder.build();
        assertEquals(1, TestUtil.hitCount(searcher, pq));
    }

    public void testWithQueryParser() throws Exception {
        Query query = new QueryParser("content", synonymAnalyzer).parse("\"fox jumps\"");
        assertEquals(1, TestUtil.hitCount(searcher, query));
        System.out.println("With SynonymAnalyzer, \"fox jumps\" parses to " + query.toString("content"));
        query = new QueryParser("content", new StandardAnalyzer()).parse("\"fox jumps\"");
        assertEquals(1, TestUtil.hitCount(searcher, query));
        System.out.println("With StandardAnalyzer, \"fox jumps\" parses to " + query.toString("content"));
    }
}
