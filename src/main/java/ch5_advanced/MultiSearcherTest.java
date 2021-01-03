package ch5_advanced;

import junit.framework.TestCase;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.*;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.BytesRef;

public class MultiSearcherTest extends TestCase {
    private IndexReader[] readers;

    public void setUp() throws Exception {
        String[] animals = {"aardvark", "beaver", "coati",
        "dog", "elephant", "frog", "gila monster",
        "horse", "iguana", "javelina", "kangaroo",
        "lemur", "moose", "nematode", "orca",
        "python", "quokka", "rat", "scorpion",
        "tarantula", "uromastyx", "vicuna",
        "walrus", "xiphias", "yak", "zebra"};

        Analyzer analyzer = new WhitespaceAnalyzer();
        Directory aTOmDirectory = new ByteBuffersDirectory();
        Directory nTOzDirectory = new ByteBuffersDirectory();

        IndexWriter aTOmWriter = new IndexWriter(aTOmDirectory,  new IndexWriterConfig(analyzer));
        IndexWriter nTOzWriter = new IndexWriter(nTOzDirectory,  new IndexWriterConfig(analyzer));

        for (int i= animals.length - 1; i >= 0; i--) {
            Document doc = new Document();
            String animal = animals[i];
            doc.add(new StringField("animal", animal, Field.Store.YES));
            if (animal.charAt(0) < 'n') {
                aTOmWriter.addDocument(doc);
            } else {
                nTOzWriter.addDocument(doc);
            }
        }
        aTOmWriter.close();
        nTOzWriter.close();

        readers = new IndexReader[2];
        readers[0] = DirectoryReader.open(aTOmDirectory);
        readers[1] = DirectoryReader.open(nTOzDirectory);
    }
    public void testMulti() throws Exception {
        MultiReader multiReader = new MultiReader(readers);
        IndexSearcher searcher = new IndexSearcher(multiReader);
        TermRangeQuery query = new TermRangeQuery("animal",
                new BytesRef("h"),
                new BytesRef("t"),
                true,
                true);
        TopDocs hits = searcher.search(query, 10);
        assertEquals("tarantula not included", 12, hits.totalHits.value);
    }
}
