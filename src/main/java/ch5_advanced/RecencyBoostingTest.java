package ch5_advanced;

import common.TestUtil;
import junit.framework.TestCase;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.expressions.Expression;
import org.apache.lucene.expressions.SimpleBindings;
import org.apache.lucene.expressions.js.JavascriptCompiler;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queries.function.FunctionScoreQuery;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;

import java.text.ParseException;
import java.util.Date;

public class RecencyBoostingTest extends TestCase {
    double multiplier;
    int today;
    int maxDaysAgo;
    String dayField;
    static int MSEC_PER_DAY = 1000 * 3600 * 24;

    public void setUp() {
        this.today = (int) (new Date().getTime() / MSEC_PER_DAY);
    }

    private FunctionScoreQuery RecencyBoostingQuery(Query q, double multiplier, int maxDaysAgo, String dayField) throws ParseException {
        SimpleBindings bindings = new SimpleBindings();
        bindings.add("score", DoubleValuesSource.SCORES);
        bindings.add("publishDay", DoubleValuesSource.fromIntField("pubmonthAsDayVal"));

        // Todo: if statement inside String.format is not implemented here.
        Expression expr = JavascriptCompiler.compile(
                String.format("%f * (%d - (%d - publishDay)) / %d ", multiplier, maxDaysAgo, today, maxDaysAgo)
        );
        return new FunctionScoreQuery(q, expr.getDoubleValuesSource(bindings));
    }

    public void testRecency() throws Throwable {
        Directory dir = TestUtil.getBookIndexDirectory();
        IndexReader r = DirectoryReader.open(dir);
        IndexSearcher s = new IndexSearcher(r);

        QueryParser parser = new QueryParser("contents", new StandardAnalyzer());
        Query q = parser.parse("java in action");
        Query q2 = RecencyBoostingQuery(q, 2.0, 17 * 365, "pubmonthAsDayVal");

        Sort sort = new Sort(SortField.FIELD_SCORE, new SortField("title2", SortField.Type.STRING));
        TopDocs hits = s.search(q2, 5, sort, true);
        for (int i=0; i<hits.scoreDocs.length; i++) {
            Document doc = r.document(hits.scoreDocs[i].doc);
            System.out.println((1 + i) + ": " +
                    doc.get("title") +
                    ": pubmonth=" +
                    doc.get("pubmonthVal") +
                    " pubmonthAsDay=" +
                    doc.get("pubmonthAsDayVal") +
                    " score=" +
                    hits.scoreDocs[i].score);
        }
        r.close();
        dir.close();
    }

}
