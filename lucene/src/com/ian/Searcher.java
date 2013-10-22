package com.ian;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.util.Version;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.DirectoryReader;

import java.io.File;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: IanQiu
 * Date: 13-7-29
 * Time: 下午10:52
 * To change this template use File | Settings | File Templates.
 */
public class Searcher {
    public static void main(String[] args) throws IllegalArgumentException, IOException, ParseException {
        if (args.length != 2) {
            throw new IllegalArgumentException("Usage: Java " + Searcher.class.getName() + " <index dir> <query>");
        }
        String indexDir = args[0];
        String q = args[1];
        search(indexDir,q);

    }

    public static void search(String indexDir, String q) throws IOException, ParseException {
        Directory dir = FSDirectory.open(new File(indexDir));
        IndexReader reader = DirectoryReader.open(dir);
        IndexSearcher is = new IndexSearcher(reader);
        QueryParser parser = new QueryParser(Version.LUCENE_44, "contents", new StandardAnalyzer(Version.LUCENE_44));
        Query query = parser.parse(q);
        long start = System.currentTimeMillis();
        TopDocs hits = is.search(query, 10);
        long end = System.currentTimeMillis();

        System.err.println("Found " + hits.totalHits + " document(s) (in " + (end - start) + " milliseconds) that matched query '" + q + "':");
        for (ScoreDoc scoreDoc : hits.scoreDocs) {
            Document doc = is.doc(scoreDoc.doc);
            System.out.println(doc.get("fullpath"));
        }
        reader.close();
    }
}
