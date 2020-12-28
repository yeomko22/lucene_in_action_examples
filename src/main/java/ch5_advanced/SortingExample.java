package ch5_advanced;

import common.TestUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;

import java.io.IOException;
import java.io.PrintStream;
import java.text.DecimalFormat;

public class SortingExample {
    private Directory directory;

    public SortingExample(Directory directory) { this.directory = directory; }

    public void displayResults(Query query, Sort sort) throws IOException {
        IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(directory));
        TopDocs results = searcher.search(query, 20, sort, true);
        System.out.println(results.totalHits.value);
        System.out.println("\nResults for: " + query.toString() + " sorted by " + sort);
        System.out.println(StringUtils.rightPad("Title", 30) +
                StringUtils.rightPad("pubmonth", 10) +
                StringUtils.center("id", 4) +
                StringUtils.center("score", 15));
        PrintStream out = new PrintStream(System.out, true, "UTF-8");
        DecimalFormat scoreFormatter = new DecimalFormat("0.######");
        for (ScoreDoc sd : results.scoreDocs) {
            int docID = sd.doc;
            float score = sd.score;
            System.out.println(score);
            Document doc = searcher.doc(docID);
            out.println(
                    StringUtils.rightPad(
                            StringUtils.abbreviate(doc.get("title"), 29), 30) +
                            StringUtils.rightPad(doc.get("pubmonthVal"), 10) +
                            StringUtils.center("" + docID, 4) +
                            StringUtils.leftPad(scoreFormatter.format(score), 12));
            out.println("   " + doc.get("category"));
//            out.println(searcher.explain(query, docID));
        }
        searcher.getIndexReader().close();
    }

    public static void main(String[] args) throws Exception {
        Query allBooks = new MatchAllDocsQuery();
        QueryParser parser = new QueryParser("contents", new StandardAnalyzer());
        BooleanQuery.Builder builder = new BooleanQuery.Builder();
        builder.add(allBooks, BooleanClause.Occur.SHOULD);
        builder.add(parser.parse("java OR action"), BooleanClause.Occur.SHOULD);
        BooleanQuery query = builder.build();

        Directory directory = TestUtil.getBookIndexDirectory();
        SortingExample example = new SortingExample(directory);
        example.displayResults(query, Sort.RELEVANCE);
        example.displayResults(query, Sort.INDEXORDER);
        example.displayResults(query, new Sort(new SortField("category", SortField.Type.STRING)));
        example.displayResults(query, new Sort(new SortField("pubmonth", SortField.Type.INT, true)));
        example.displayResults(query, new Sort(new SortField("category", SortField.Type.STRING),
                SortField.FIELD_SCORE, new SortField("pubmonth", SortField.Type.INT, true)));
        example.displayResults(query,
                new Sort(new SortField[] {SortField.FIELD_SCORE,
                        new SortField("category", SortField.Type.STRING)}));
        directory.close();
    }













}
