package common;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class TestUtil {
    public static boolean hitsIncludeTItle(IndexSearcher searcher, TopDocs hits, String title) throws IOException {
        for (ScoreDoc match : hits.scoreDocs) {
            Document doc = searcher.doc(match.doc);
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

    public static Directory getBookIndexDirectory() throws IOException {
        return FSDirectory.open(Paths.get("index.dir"));
    }

    public static void rmDir(File dir) throws IOException {
        if (dir.exists()) {
            File[] files = dir.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (!files[i].delete()) {
                    throw new IOException("could not delete " + files[i]);
                }
            }
            dir.delete();
        }
    }
}
