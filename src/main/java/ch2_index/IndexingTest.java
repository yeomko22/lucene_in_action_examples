package ch2_index;

import common.TestUtil;
import junit.framework.TestCase;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;

import java.io.IOException;

public class IndexingTest extends TestCase {
    protected String[] ids = {"1", "2"};
    protected String[] unindexed = {"Netherlands", "Italy"};
    protected String[] unstored = {"Amsterdam has lots of b ridges", "Venice has lots of canals"};
    protected String[] text = {"Amsterdam", "Venice"};
    private Directory directory;

    protected void setUp() throws Exception {
        directory = new ByteBuffersDirectory();
        IndexWriter writer = getWriter();
        for (int i=0; i< ids.length; i++) {
            Document doc = new Document();
            doc.add(new StringField("id", ids[i], Field.Store.YES));
            doc.add(new StoredField("country", unindexed[i]));
            doc.add(new TextField("contents", unstored[i], Field.Store.NO));
            doc.add(new StringField("city", text[i], Field.Store.YES));
            writer.addDocument(doc);
        }
        writer.close();
    }

    private IndexWriter getWriter() throws IOException {
        WhitespaceAnalyzer analyzer = new WhitespaceAnalyzer();
        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
        return new IndexWriter(directory, iwc);
    }

    protected long getHitCount(String fieldName, String searchString) throws IOException {
        IndexReader ir = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(ir);
        Term t = new Term(fieldName, searchString);
        Query query = new TermQuery(t);
        return TestUtil.hitCount(searcher, query);
    }

    public void testIndexWriter () throws IOException {
        IndexWriter writer = getWriter();
        assertEquals(ids.length, writer.getDocStats().numDocs);
        writer.close();
    }

    public void testIndexReader() throws IOException {
        IndexReader reader = DirectoryReader.open(directory);
        assertEquals(ids.length, reader.maxDoc());
        assertEquals(ids.length, reader.numDocs());
        reader.close();
    }
}
