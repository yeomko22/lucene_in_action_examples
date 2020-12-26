package ch3_search;

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
import org.apache.lucene.search.*;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;

public class WildcardFuzzyQueryTest extends TestCase {
    private Directory dir;

    public void setUp() {
        dir = new ByteBuffersDirectory();
    }

    public void tearDown() throws Exception {
        dir.close();
    }

    private void indexSingleFieldDocs(TextField[] fields) throws Exception {

        Analyzer analyzer = new WhitespaceAnalyzer();
        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
        IndexWriter writer = new IndexWriter(dir, iwc);
        for (TextField f : fields) {
            Document doc = new Document();
            doc.add(f);
            writer.addDocument(doc);
        }
        writer.close();
    }

    public void testWildcard() throws Exception {
        indexSingleFieldDocs(new TextField[]{
                new TextField("contents", "wild", Field.Store.YES),
                new TextField("contents", "child", Field.Store.YES),
                new TextField("contents", "mild", Field.Store.YES),
                new TextField("contents", "mildew", Field.Store.YES),
        });
        DirectoryReader reader = DirectoryReader.open(dir);
        IndexSearcher searcher = new IndexSearcher(reader);
        Query query = new WildcardQuery(new Term("contents", "?ild*"));
        TopDocs matches = searcher.search(query, 10);
        assertEquals("child no match", 3, matches.totalHits.value);
        assertEquals("score the same", matches.scoreDocs[0].score, matches.scoreDocs[1].score, 0);
        assertEquals("score the same", matches.scoreDocs[1].score, matches.scoreDocs[2].score, 0);
    }

    public void testFuzzy() throws Exception {
        indexSingleFieldDocs(new TextField[]{
                new TextField("contents", "fuzzy", Field.Store.YES),
                new TextField("contents", "wuzzy", Field.Store.YES),
        });
        DirectoryReader reader = DirectoryReader.open(dir);
        IndexSearcher searcher = new IndexSearcher(reader);
        Query query = new FuzzyQuery(new Term("contents", "wuzza"));
        TopDocs matches = searcher.search(query, 10);

        assertEquals("both close enough", 2, matches.totalHits.value);
        assertTrue("wuzzy closer than fuzzy", matches.scoreDocs[0].score != matches.scoreDocs[1].score);

        Document doc = searcher.doc(matches.scoreDocs[0].doc);
        assertEquals("wuzza bear", "wuzzy", doc.get("contents"));
        reader.close();
    }
}
