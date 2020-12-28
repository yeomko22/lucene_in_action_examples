package common;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class CreateTestIndex {
    public static Document getDocument(String rootDir, File file) throws IOException {
        Properties props = new Properties();
        props.load(new FileInputStream(file));

        Document doc = new Document();

        // category comes from relative path below the base directory
        String category = file.getParent().substring(rootDir.length());
        category = category.replace(File.separatorChar, '/');

        String isbn = props.getProperty("isbn");
        String title = props.getProperty("title");
        String author = props.getProperty("author");
        String url = props.getProperty("url");
        String subject = props.getProperty("subject");
        String pubmonth = props.getProperty("pubmonth");

        System.out.println(title + "\n" + author + "\n" + subject + "\n" + pubmonth + "\n" + category + "\n---------");

        doc.add(new StringField("isbn", isbn, Field.Store.YES));
        doc.add(new StringField("category", category, StringField.Store.YES));
        doc.add(new SortedDocValuesField("category", new BytesRef(category)));
        doc.add(new TextField("title", title, Field.Store.YES));
        doc.add(new TextField("title2", title.toLowerCase(), Field.Store.YES));

        // split multiple authors into unique field instances
        String[] authors = author.split(",");
        for (String a : authors) {
            doc.add(new StoredField("author", a));
        }

        doc.add(new StoredField("url", url));
        doc.add(new TextField("subject", subject, Field.Store.YES));
        doc.add(new NumericDocValuesField("pubmonth", Integer.parseInt(pubmonth)));
        doc.add(new StoredField("pubmonthVal", pubmonth));
        Date d;
        try {
            d = DateTools.stringToDate(pubmonth);
        } catch (ParseException pe) {
            throw new RuntimeException(pe);
        }
        doc.add(new IntPoint("pubmonthAsDay", (int) (d.getTime()/(1000*3600*24))));
        for(String text : new String[] {title, subject, author, category}) {
            doc.add(new TextField("contents", text, Field.Store.YES));
        }
        return doc;
    }

    private static String aggregate(String[] strings) {
        StringBuilder buffer = new StringBuilder();

        for (int i = 0; i < strings.length; i++) {
            buffer.append(strings[i]);
            buffer.append(" ");
        }

        return buffer.toString();
    }

    private static void findFiles(List<File> result, File dir) {
        for(File file : dir.listFiles()) {
            if (file.getName().endsWith(".properties")) {
                result.add(file);
            } else if (file.isDirectory()) {
                findFiles(result, file);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        System.out.println(System.getProperty("user.dir"));
        String dataDir = TestUtil.getDataDir();
        String indexDir = TestUtil.getIndexDir();

        List<File> results = new ArrayList<File>();
        findFiles(results, new File(dataDir));
        System.out.println(results.size() + " books to index");

        Analyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
        iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        Directory dir = FSDirectory.open(Paths.get(indexDir));
        IndexWriter w = new IndexWriter(dir, iwc);

        for(File file : results) {
            Document doc = getDocument(dataDir, file);
            w.addDocument(doc);
        }
        w.close();
        dir.close();
    }
}
