package ch4_analyzer.keyword;

import common.TestUtil;
import junit.framework.TestCase;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;

public class KeywordAnalyzerTest extends TestCase {
    private IndexSearcher searcher;

    public void setUp() throws Exception {
        Directory directory = new ByteBuffersDirectory();
        Analyzer analyzer = new SimpleAnalyzer();
        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
        IndexWriter writer = new IndexWriter(directory, iwc);
        Document doc = new Document();
        doc.add(new StringField("partnum", "Q36", Field.Store.NO));
        doc.add(new TextField("description", "Illidium Space Modulator", Field.Store.YES));
        writer.addDocument(doc);
        writer.close();

        searcher = new IndexSearcher(DirectoryReader.open(directory));
    }

    public void testTermQuery() throws Exception {
        Query query = new TermQuery(new Term("partnum", "Q36"));
        assertEquals(1, TestUtil.hitCount(searcher, query));
    }

    public void testBasicQueryParser() throws Exception {
        Query query = new QueryParser("description", new SimpleAnalyzer()).parse("partnum:Q36 AND SPACE");
        assertEquals("note Q36 -> q", "+partnum:q +space", query.toString("description"));
        assertEquals("doc not found:(", 0, TestUtil.hitCount(searcher, query));
    }
}
