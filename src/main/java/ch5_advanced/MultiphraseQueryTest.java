package ch5_advanced;

import ch4_analyzer.synonym.SynonymAnalyzer;
import ch4_analyzer.synonym.SynonymEngine;
import junit.framework.TestCase;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;

import java.io.IOException;

public class MultiphraseQueryTest extends TestCase {
    private IndexSearcher searcher;

    protected void setUp() throws Exception {
        Directory directory = new ByteBuffersDirectory();
        Analyzer analyzer = new WhitespaceAnalyzer();
        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
        IndexWriter writer = new IndexWriter(directory, iwc);
        Document doc1 = new Document();
        doc1.add(new TextField("field", "the quick brown fox jumped over the lazy dog", Field.Store.YES));
        writer.addDocument(doc1);

        Document doc2 = new Document();
        doc2.add(new TextField("field", "the fast fox hopped over the hound", Field.Store.YES));
        writer.addDocument(doc2);
        writer.close();

        searcher = new IndexSearcher(DirectoryReader.open(directory));
    }

    public void testBasic() throws Exception {
        MultiPhraseQuery.Builder builder = new MultiPhraseQuery.Builder();
        builder.add(new Term[]{
                new Term("field", "quick"),
                new Term("field", "fast")
        });
        builder.add(new Term("field", "fox"));
        MultiPhraseQuery query = builder.build();
        System.out.println(query);

        TopDocs hits = searcher.search(query, 10);
        assertEquals("fast fox match", 1, hits.totalHits.value);

        builder.setSlop(1);
        query = builder.build();
        hits = searcher.search(query, 10);
        assertEquals("both matched", 2, hits.totalHits.value);
    }

    public void testAgainstOR() throws Exception {
        PhraseQuery.Builder phraseBuilder = new PhraseQuery.Builder();
        phraseBuilder.setSlop(1);
        phraseBuilder.add(new Term("field", "quick"));
        phraseBuilder.add(new Term("field", "fox"));
        PhraseQuery quickFox = phraseBuilder.build();

        phraseBuilder = new PhraseQuery.Builder();
        phraseBuilder.add(new Term("field", "fast"));
        phraseBuilder.add(new Term("field", "fox"));
        PhraseQuery fastFox = phraseBuilder.build();

        BooleanQuery.Builder booleanBuilder = new BooleanQuery.Builder();
        booleanBuilder.add(quickFox, BooleanClause.Occur.SHOULD);
        booleanBuilder.add(fastFox, BooleanClause.Occur.SHOULD);
        BooleanQuery query = booleanBuilder.build();

        TopDocs hits = searcher.search(query, 10);
        assertEquals(2, hits.totalHits.value);
    }

    public void testQueryParser() throws Exception {
        SynonymEngine engine = new SynonymEngine() {
            @Override
            public String[] getSynonyms(String s) throws IOException {
                if (s.equals("quick"))
                    return new String[]{"fast"};
                else
                    return null;
            }
        };
        Query q = new QueryParser("field", new SynonymAnalyzer(engine)).parse("\"quick fox\"");
        assertEquals("analyzed", "field:\"(quick fast) fox\"", q.toString());
        assertTrue("parsed as MultiPhraseQuery", q instanceof MultiPhraseQuery);
    }
}
