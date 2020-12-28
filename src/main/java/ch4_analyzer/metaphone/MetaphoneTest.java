package ch4_analyzer.metaphone;

import junit.framework.TestCase;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.ByteBuffersDirectory;

public class MetaphoneTest extends TestCase {
    public void testKoolKat() throws Exception {
        ByteBuffersDirectory directory = new ByteBuffersDirectory();
        Analyzer analyzer = new MetaphoneReplacementAnalyzer();
        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
        IndexWriter writer = new IndexWriter(directory, iwc);

        Document doc = new Document();
        doc.add(new TextField("contents", "cool cat", Field.Store.YES));
        writer.addDocument(doc);
        writer.close();

        IndexReader reader = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(reader);
        Query query = new QueryParser("contents", analyzer).parse("kool kat");
        TopDocs hits = searcher.search(query, 1);
        assertEquals(1, hits.totalHits.value);
        int docID = hits.scoreDocs[0].doc;
        doc = searcher.doc(docID);
        assertEquals("cool cat", doc.get("contents"));
        reader.close();
        directory.close();
    }
}
