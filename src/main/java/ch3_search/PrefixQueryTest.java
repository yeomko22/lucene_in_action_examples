package ch3_search;

import common.TestUtil;
import junit.framework.TestCase;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;

public class PrefixQueryTest extends TestCase {
    public void testPrefix() throws Exception {
        Directory dir = TestUtil.getBookIndexDirectory();
        DirectoryReader reader = DirectoryReader.open(dir);
        IndexSearcher searcher = new IndexSearcher(reader);
        Term term = new Term("category", "/technology/computers/programming");
        PrefixQuery query = new PrefixQuery(term);

        TopDocs matches = searcher.search(query, 10);
        long programmingAndBelow = matches.totalHits.value;

        matches = searcher.search(new TermQuery(term), 10);
        long justProgramming = matches.totalHits.value;

        assertTrue(programmingAndBelow > justProgramming);
        reader.close();
        dir.close();
    }
}
