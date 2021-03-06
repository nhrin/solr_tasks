package org.example.s3lambda;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.common.SolrInputDocument;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class TwitSolrClient {

    private HttpSolrClient solrClient;

    public TwitSolrClient() {
        try (InputStream inputStream = TwitSolrClient.class.getClassLoader().getResourceAsStream("application.properties")) {
            Properties prop = new Properties();
            prop.load(inputStream);
            solrClient = new HttpSolrClient.Builder(prop.getProperty("solr.url")).build();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addJsonDocToSolrIndex(JSONObject docContent) {
        if (!validateDocContent(docContent)) {
            throw new RuntimeException("Wrong document structure!");
        }
        SolrInputDocument doc = new SolrInputDocument();
        doc.addField("id", docContent.get("id").toString());
        doc.addField("content", docContent.get("content").toString());
        doc.addField("user_name", docContent.get("user_name").toString());
        doc.addField("publication_date", Timestamp.from(Instant.now()));
        try {
            solrClient.add(doc);
            solrClient.commit();
        } catch (SolrServerException | IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Solr error. Something went wrong.");
        }
    }

    public JSONObject parseStringToJson(String stringToParse) {
        JSONParser parser = new JSONParser();
        try {
            return (JSONObject) parser.parse(stringToParse.replaceAll("[[\\W]&&[\\S]&&[^А-Яа-я-.?!{}\"\"\\n)(,:]]", ""));
        } catch (ParseException e) {
            e.printStackTrace();
            throw new RuntimeException("Parsing error! Wrong document structure!");
        }
    }

    public boolean validateDocContent(JSONObject docContent) {
        List<String> keysForValidation = Arrays.asList("ad", "user_name", "content");
        for (String key : keysForValidation) {
            if (!docContent.containsKey(key)) {
                return false;
            }
        }
        return true;
    }
}
