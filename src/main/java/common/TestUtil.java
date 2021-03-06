package common;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class TestUtil {
    public static boolean hitsIncludeTitle(IndexSearcher searcher, TopDocs hits, String title) throws IOException {
        for (ScoreDoc match : hits.scoreDocs) {
            Document doc = searcher.doc(match.doc);
            System.out.println(doc.get("title") + " " + doc.get("pubmonthVal") + " " + doc.get("title2"));
            if (title.equals(doc.get("title"))) {
                return true;
            }
        }
        System.out.println("title '" + title + "' not found");
        return false;
    }

    public static long hitCount(IndexSearcher searcher, Query query) throws IOException {
        return searcher.search(query, 1).totalHits.value;
    }

    public static void dumpHits(IndexSearcher searcher, TopDocs hits) throws IOException {
        if (hits.totalHits.value == 0) {
            System.out.println("No hits");
        }

        for (ScoreDoc match : hits.scoreDocs) {
            Document doc = searcher.doc(match.doc);
            System.out.println(match.score + ":" + doc.get("title"));
        }
    }

    public static String getIndexDir() {
        return System.getProperty("user.dir") + "/build/index";
    }

    public static String getDataDir() {
        return System.getProperty("user.dir") + "/data";
    }

    public static Directory getBookIndexDirectory() throws IOException {
        return FSDirectory.open(Paths.get(getIndexDir()));
    }

    public static void rmDir(Path path) throws IOException {
        if (Files.isDirectory(path)) {
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }
            });
        }
    }
}
