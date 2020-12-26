package ch3_search;

import common.TestUtil;
import junit.framework.TestCase;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;

public class BooleanQueryTest extends TestCase {
    public void testAnd() throws Exception {
        TermQuery searchingBooks = new TermQuery(new Term("subject", "search"));
        Query books2010 = IntPoint.newRangeQuery("pubmonth", 201001, 201012);

        BooleanQuery.Builder builder = new BooleanQuery.Builder();
        builder.add(new BooleanClause(searchingBooks, BooleanClause.Occur.MUST));
        builder.add(new BooleanClause(books2010, BooleanClause.Occur.MUST));
        BooleanQuery searchingBooks2010 = builder.build();

        Directory dir = TestUtil.getBookIndexDirectory();
        DirectoryReader reader = DirectoryReader.open(dir);
        IndexSearcher searcher = new IndexSearcher(reader);
        TopDocs matches = searcher.search(searchingBooks2010, 10);

        assertTrue(TestUtil.hitsIncludeTitle(searcher, matches, "Lucene in Action, Second Edition"));
        reader.close();
        dir.close();
    }

    public void testOr() throws Exception {
        TermQuery methodologyBooks = new TermQuery(
                new Term("category", "/technology/computers/programming/methodology"));
        TermQuery easternPhilosophyBooks = new TermQuery(
                new Term("category", "/philosophy/eastern")
        );
        BooleanQuery.Builder builder = new BooleanQuery.Builder();
        builder.add(new BooleanClause(methodologyBooks, BooleanClause.Occur.SHOULD));
        builder.add(new BooleanClause(easternPhilosophyBooks, BooleanClause.Occur.SHOULD));
        BooleanQuery enlightenmentBooks = builder.build();

        Directory dir = TestUtil.getBookIndexDirectory();
        DirectoryReader reader = DirectoryReader.open(dir);
        IndexSearcher searcher = new IndexSearcher(reader);
        TopDocs matches = searcher.search(enlightenmentBooks, 10);
        System.out.println("or = " + enlightenmentBooks);
        assertTrue(TestUtil.hitsIncludeTitle(searcher, matches, "Extreme Programming Explained"));
        assertTrue(TestUtil.hitsIncludeTitle(searcher, matches, "Tao Te Ching \u9053\u5FB7\u7D93"));
        reader.close();
        dir.close();
    }
}
