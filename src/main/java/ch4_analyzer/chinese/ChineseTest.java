package ch4_analyzer.chinese;

import common.TestUtil;
import junit.framework.TestCase;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.Directory;

public class ChineseTest extends TestCase {
    public void testChinese() throws Exception {
        Directory dir = TestUtil.getBookIndexDirectory();
        IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(dir));
        Query query = new TermQuery(new Term("contents", "道"));
        assertEquals("tao", 1, TestUtil.hitCount(searcher, query));
    }
}
