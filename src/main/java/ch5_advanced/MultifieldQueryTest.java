package ch5_advanced;

import common.TestUtil;
import junit.framework.TestCase;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;

public class MultifieldQueryTest extends TestCase {
    public void testDefaultOperator() throws Exception {
        Query query = new MultiFieldQueryParser(
                new String[]{"title", "subject"},
                new SimpleAnalyzer()
        ).parse("development");
        System.out.println(query);
        Directory dir = TestUtil.getBookIndexDirectory();
        IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(dir));
        TopDocs hits = searcher.search(query, 10);
        assertTrue(TestUtil.hitsIncludeTitle(searcher, hits, "Ant in Action"));
        assertTrue(TestUtil.hitsIncludeTitle(searcher, hits, "Extreme Programming Explained"));
        searcher.getIndexReader().close();
        dir.close();
    }

    public void testSpecifiedOperator() throws Exception {
        Query query = MultiFieldQueryParser.parse("lucene",
                new String[]{"title", "subject"},
                new BooleanClause.Occur[]{BooleanClause.Occur.MUST, BooleanClause.Occur.MUST},
                new SimpleAnalyzer());
        System.out.println(query);
        Directory dir = TestUtil.getBookIndexDirectory();
        IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(dir));
        TopDocs hits = searcher.search(query, 10);
        assertTrue(TestUtil.hitsIncludeTitle(searcher, hits, "Lucene in Action, Second Edition"));
        assertEquals("one and only one", 1, hits.scoreDocs.length);
        searcher.getIndexReader().close();
        dir.close();
    }
}
