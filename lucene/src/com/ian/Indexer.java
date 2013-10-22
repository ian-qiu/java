package com.ian;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: IanQiu
 * Date: 13-7-29
 * Time: 下午10:09
 * To change this template use File | Settings | File Templates.
 */
public class Indexer {
    public static void main(String args[]) throws Exception {
        if (args.length != 2) {
            throw new IllegalArgumentException("Usage: Java " + Indexer.class.getName() + " <index dir> <data dir>");
        }
        String indexDir = args[0];
        String dataDir = args[1];
        long start = System.currentTimeMillis();
        Indexer indexer = new Indexer(indexDir);
        int numIndexed;
        try {
            numIndexed = indexer.index(dataDir, new TextFilesFilter());
        } finally {
            indexer.close();
        }
        long end = System.currentTimeMillis();
        System.out.println("Indexing " + numIndexed + " files took " + (end - start) + " milliseconds!");
    }

    private IndexWriter writer;

    public Indexer(String indexDir) throws IOException {
        Directory dir = FSDirectory.open(new File(indexDir));
        writer = new IndexWriter(dir, new IndexWriterConfig(Version.LUCENE_44, new StandardAnalyzer(Version.LUCENE_44)));
    }

    public void close() throws IOException {
        writer.close();
    }

    public int index(String dataDir, FileFilter filter) throws Exception {
        File[] files = new File(dataDir).listFiles();
        for (File f : files) {
            if (!f.isDirectory() && !f.isHidden() && f.exists() && f.canRead() && (filter == null || filter.accept(f))) {
                indexFile(f);
            }
        }
        return writer.numDocs();
    }

    private static class TextFilesFilter implements FileFilter {
        @Override
        public boolean accept(File path) {
            return path.getName().toLowerCase().endsWith(".html");
        }
    }

    protected Document getDocument(File f) throws Exception {
        Document doc = new Document();
        doc.add(new TextField("contents", new FileReader(f)));
        doc.add(new TextField("filename", f.getName(), Field.Store.YES));
        doc.add(new TextField("fullpath", f.getCanonicalPath(), Field.Store.YES));
        return doc;
    }

    private void indexFile(File f) throws Exception {
        System.out.println("Indexing " + f.getCanonicalPath());
        Document doc = getDocument(f);
        writer.addDocument(doc);
    }
}
