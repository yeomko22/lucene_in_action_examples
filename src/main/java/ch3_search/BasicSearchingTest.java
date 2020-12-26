package ch3_search;

import common.TestUtil;
import junit.framework.TestCase;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;

public class BasicSearchingTest extends TestCase {
    public void testTerm() throws Exception {
        Directory dir = TestUtil.getBookIndexDirectory();
        DirectoryReader reader = DirectoryReader.open(dir);
        IndexSearcher searcher = new IndexSearcher(reader);
        Term t = new Term("subject", "ant");
        Query query = new TermQuery(t);
        TopDocs docs = searcher.search(query, 10);
        assertEquals(1, docs.totalHits.value);

        t = new Term("subject", "junit");
        docs = searcher.search(new TermQuery(t), 10);
        assertEquals(2, docs.totalHits.value);
        reader.close();
        dir.close();
    }

    public void testQueryParser() throws Exception {
        Directory dir = TestUtil.getBookIndexDirectory();
        DirectoryReader reader = DirectoryReader.open(dir);
        IndexSearcher searcher = new IndexSearcher(reader);
        QueryParser parser = new QueryParser("contents", new SimpleAnalyzer());
        Query query = parser.parse("+JUNIT +ANT -MOCK");
        TopDocs docs = searcher.search(query, 10);
        assertEquals(1, docs.totalHits.value);
        Document d  =searcher.doc(docs.scoreDocs[0].doc);
        assertEquals("Ant in Action", d.get("title"));

        query = parser.parse("mock OR junit");
        docs = searcher.search(query,10);
        assertEquals( 2, docs.totalHits.value);
        reader.close();
        dir.close();
    }
}
