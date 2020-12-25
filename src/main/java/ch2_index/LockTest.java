package ch2_index;

import common.TestUtil;
import junit.framework.TestCase;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class LockTest extends TestCase {
    private Directory dir;
    Path dirPath = Paths.get(System.getProperty("java.io.tmpdir", "tmp") + System.getProperty("file.separator") + "index");

    protected void setUp() throws IOException {
        dir = FSDirectory.open(dirPath);
    }

    public void testWriteLock() throws IOException {
        Analyzer analyzer = new SimpleAnalyzer();
        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
        iwc.setUseCompoundFile(false);
        IndexWriter writer1 = new IndexWriter(dir, iwc);
        IndexWriter writer2 = null;

        try {
            writer2 = new IndexWriter(dir, iwc);
            fail("We should never reach this point");
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } finally {
            writer1.close();
            assertNull(writer2);
            TestUtil.rmDir(dirPath);
        }
    }
}
