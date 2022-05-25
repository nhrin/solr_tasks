package org.example.app.solrclient;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.example.app.entity.Twit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TwitSolrClient {

    private static HttpSolrClient solrClient;

    public TwitSolrClient(@Value("${solr.url}") String url) {
        solrClient = new HttpSolrClient.Builder(url).build();
    }
    public void addDoc(Twit twit) throws SolrServerException, IOException {
        SolrInputDocument doc = new SolrInputDocument();
        doc.addField("id", twit.getId());
        doc.addField("content", twit.getContent());
        solrClient.add(doc);
        solrClient.commit();
    }

    public List<String> findListIdByContent(String userQuery) throws SolrServerException, IOException {
        String query = "content:" + userQuery;
        SolrQuery solrQuery = new SolrQuery(query);
        QueryResponse queryResponse = solrClient.query(solrQuery);
        SolrDocumentList solrDocumentList = queryResponse.getResults();
        return solrDocumentList.stream()
                .map(doc -> doc.get("id").toString())
                .collect(Collectors.toList());
    }
}
