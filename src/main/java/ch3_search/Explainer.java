package ch3_search;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.nio.file.Paths;

public class Explainer {
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("Usage: Explainer <index dir> <query>");
            System.exit(1);
        }

        String indexDir = args[0];
        String queryExpression = args[1];

        Directory directory = FSDirectory.open(Paths.get(indexDir));
        Analyzer analyzer = new SimpleAnalyzer();
        QueryParser parser = new QueryParser("contents", analyzer);
        Query query = parser.parse(queryExpression);
        System.out.println("Query: " + queryExpression);

        DirectoryReader reader = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(reader);
        TopDocs topDocs = searcher.search(query, 10);
        for (ScoreDoc match : topDocs.scoreDocs) {
            Explanation explanation = searcher.explain(query, match.doc);
            System.out.println("----------");
            Document doc = searcher.doc(match.doc);
            System.out.println(doc.get("title"));
            System.out.println(explanation.toString());
        }
        directory.close();
    }
}
