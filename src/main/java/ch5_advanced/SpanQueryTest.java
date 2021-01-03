package ch5_advanced;

import junit.framework.TestCase;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.search.spans.*;
import org.apache.lucene.store.ByteBuffersDirectory;

import java.io.IOException;
import java.io.StringReader;

public class SpanQueryTest extends TestCase {
    private ByteBuffersDirectory directory;
    private IndexSearcher searcher;
    private IndexReader reader;

    private SpanTermQuery quick;
    private SpanTermQuery brown;
    private SpanTermQuery red;
    private SpanTermQuery fox;
    private SpanTermQuery lazy;
    private SpanTermQuery sleepy;
    private SpanTermQuery dog;
    private SpanTermQuery cat;
    private Analyzer analyzer;

    private final int MAXINT = 2147483647;

    protected void setUp() throws Exception {
        directory = new ByteBuffersDirectory();
        analyzer = new WhitespaceAnalyzer();
        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
        IndexWriter writer = new IndexWriter(directory, iwc);

        Document doc = new Document();
        doc.add(new TextField("f", "the quick brown fox jumps over the lazy dog", Field.Store.YES));
        writer.addDocument(doc);

        doc = new Document();
        doc.add(new TextField("f", "the quick red fox jumps over the sleepy cat", Field.Store.YES));
        writer.addDocument(doc);

        writer.close();

        reader = DirectoryReader.open(directory);
        searcher = new IndexSearcher(reader);

        quick = new SpanTermQuery(new Term("f", "quick"));
        brown = new SpanTermQuery(new Term("f", "brown"));
        red = new SpanTermQuery(new Term("f", "red"));
        fox = new SpanTermQuery(new Term("f", "fox"));
        lazy = new SpanTermQuery(new Term("f", "lazy"));
        sleepy = new SpanTermQuery(new Term("f", "sleepy"));
        dog = new SpanTermQuery(new Term("f", "dog"));
        cat = new SpanTermQuery(new Term("f", "cat"));
    }

    private void assertOnlyBrownFox(Query query) throws Exception {
        TopDocs hits = searcher.search(query, 10);
        assertEquals(1, hits.totalHits.value);
        assertEquals("wrong doc", 0, hits.scoreDocs[0].doc);
    }

    private void assertBothFoxes(Query query) throws Exception {
        TopDocs hits = searcher.search(query, 10);
        assertEquals(2, hits.totalHits.value);
    }

    private void assertNoMatches(Query query) throws Exception {
        TopDocs hits = searcher.search(query, 10);
        assertEquals(0, hits.totalHits.value);
    }

    private void dumpsSpans(SpanQuery query) throws IOException {
        Spans spans = query
                .createWeight(searcher, ScoreMode.COMPLETE_NO_SCORES, 1f)
                .getSpans(searcher.getIndexReader().leaves().get(0), SpanWeight.Postings.POSITIONS);

        System.out.println(query + ":");
        int numSpans = 0;

        TopDocs hits = searcher.search(query, 10);
        float[] scores = new float[2];
        for (ScoreDoc sd : hits.scoreDocs) {
            scores[sd.doc] = sd.score;
        }

        while (spans.nextDoc() != MAXINT) {
            numSpans++;

           int id = spans.docID();
           Document doc = reader.document(id);

           TokenStream stream = analyzer.tokenStream("contents", new StringReader(doc.get("f")));
           CharTermAttribute term = stream.addAttribute(CharTermAttribute.class);

           StringBuilder buffer = new StringBuilder();
           buffer.append("    ");
           int i = 0;
           stream.reset();
           while (stream.incrementToken()) {
               if (i == spans.startPosition()) {
                   buffer.append("<");
               }
               buffer.append(term.toString());
               if (i+1 == spans.endPosition()) {
                   buffer.append(">");
               }
               buffer.append(" ");
               i++;
           }
           stream.close();
           buffer.append("(").append(scores[id]).append(") ");
           System.out.println(buffer);
        }
        if (numSpans == 0) {
            System.out.println("    No spans");
        }
        System.out.println();
    }

    public void testSpanFirstQuery() throws Exception {
        SpanFirstQuery sfq = new SpanFirstQuery(brown, 2);
        assertNoMatches(sfq);
        dumpsSpans(sfq);

        sfq = new SpanFirstQuery(brown, 3);
        dumpsSpans(sfq);
        assertOnlyBrownFox(sfq);
    }

    public void testSpanNearQuery() throws Exception {
        SpanQuery[] quick_brwon_dog = new SpanQuery[]{quick, brown, dog};
        SpanNearQuery snq = new SpanNearQuery(quick_brwon_dog, 0, true);
        assertNoMatches(snq);
        dumpsSpans(snq);

        snq = new SpanNearQuery(quick_brwon_dog, 4, true);
        assertNoMatches(snq);
        dumpsSpans(snq);

        snq = new SpanNearQuery(quick_brwon_dog, 5, true);
        assertOnlyBrownFox(snq);
        dumpsSpans(snq);

        // interesting - even a sloppy phrase query would require more slop to match
        snq = new SpanNearQuery(new SpanQuery[]{lazy, fox}, 3, false);
        assertOnlyBrownFox(snq);
        dumpsSpans(snq);

        PhraseQuery.Builder builder = new PhraseQuery.Builder();
        builder.add(new Term("f", "lazy"));
        builder.add(new Term("f", "fox"));
        builder.setSlop(4);
        PhraseQuery pq = builder.build();
        assertNoMatches(pq);

        builder.setSlop(5);
        pq = builder.build();
        assertOnlyBrownFox(pq);
    }

    public void testSpanNotQuery() throws Exception {
        SpanNearQuery quick_fox = new SpanNearQuery(new SpanQuery[]{quick, fox}, 1, true);
        assertBothFoxes(quick_fox);
        dumpsSpans(quick_fox);

        SpanNotQuery quick_fox_dog = new SpanNotQuery(quick_fox, dog);
        assertBothFoxes(quick_fox_dog);
        dumpsSpans(quick_fox_dog);

        SpanNotQuery no_quick_red_fox = new SpanNotQuery(quick_fox, red);
        assertOnlyBrownFox(no_quick_red_fox);
        dumpsSpans(no_quick_red_fox);
    }

    public void testSpanOrQuery() throws Exception {
        SpanNearQuery quick_fox = new SpanNearQuery(new SpanQuery[]{quick, fox}, 1, true);
        SpanNearQuery lazy_dog = new SpanNearQuery(new SpanQuery[]{lazy, dog}, 0, true);
        SpanNearQuery sleepy_cat = new SpanNearQuery(new SpanQuery[]{sleepy, cat}, 0, true);

        SpanNearQuery qf_near_ld = new SpanNearQuery(new SpanQuery[]{quick_fox, lazy_dog}, 3, true);
        assertOnlyBrownFox(qf_near_ld);
        dumpsSpans(qf_near_ld);

        SpanNearQuery qf_near_sc = new SpanNearQuery(new SpanQuery[]{quick_fox, sleepy_cat}, 3, true);
        dumpsSpans(qf_near_sc);

        SpanOrQuery or = new SpanOrQuery(new SpanQuery[]{qf_near_ld, qf_near_sc});
        assertBothFoxes(or);
        dumpsSpans(or);
    }
}
