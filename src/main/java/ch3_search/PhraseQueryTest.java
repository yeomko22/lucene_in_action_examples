package ch3_search;

import junit.framework.TestCase;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;

import java.io.IOException;

public class PhraseQueryTest extends TestCase {
    private Directory dir;
    private IndexReader reader;
    private IndexSearcher searcher;

    protected void setUp() throws IOException {
        dir = new ByteBuffersDirectory();
        Analyzer analyzer = new WhitespaceAnalyzer();
        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
        IndexWriter writer = new IndexWriter(dir, iwc);

        Document doc = new Document();
        doc.add(new TextField("field", "the quick brown fox jumped over the lazy dog", Field.Store.YES));
        writer.addDocument(doc);
        writer.close();

        reader = DirectoryReader.open(dir);
        searcher = new IndexSearcher(reader);
    }

    protected void tearDown() throws IOException {
        reader.close();
        dir.close();
    }

    private boolean matched(String[] phrase, int slop) throws IOException {
        PhraseQuery.Builder builder = new PhraseQuery.Builder();
        builder.setSlop(slop);
        for (String word : phrase) {
            builder.add(new Term("field", word));
        }
        PhraseQuery query = builder.build();
        TopDocs matches = searcher.search(query, 10);
        return matches.totalHits.value > 0;
    }

    public void testSlopComparison() throws Exception {
        String[] phrase = new String[]{"quick", "fox"};
        assertFalse("exact phrase not found", matched(phrase, 0));
        assertTrue("close enough", matched(phrase, 1));
    }

    public void testReverse() throws Exception {
        String[] phrase = new String[]{"fox", "quick"};
        assertFalse("hop flop", matched(phrase, 2));
        assertTrue("hop hop slop", matched(phrase, 3));
    }

    public void testMultiple() throws Exception {
        assertFalse("not close enough", matched(new String[]{"quick", "jumped", "lazy"}, 3));
        assertTrue("just enough", matched(new String[]{"quick", "jumped", "lazy"}, 4));
        assertFalse("almost but not quite", matched(new String[]{"lazy", "jumped", "quick"}, 7));
        assertTrue("bingo", matched(new String[]{"lazy", "jumped", "quick"}, 8));
    }
}
