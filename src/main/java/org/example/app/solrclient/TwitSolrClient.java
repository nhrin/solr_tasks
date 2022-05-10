package org.example.app.solrclient;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.beans.DocumentObjectBinder;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.example.app.entity.Twit;

import java.io.IOException;
import java.util.List;

public class TwitSolrClient {
    private final static String URL_SOLR = "http://localhost:8983/solr/twits";
    private final static HttpSolrClient SOLR_CLIENT = new HttpSolrClient.Builder(URL_SOLR).build();

    public void addDoc(Twit twit) throws SolrServerException, IOException {
        SOLR_CLIENT.addBean(twit);
        SOLR_CLIENT.commit();
    }

    public List<Twit> findByContent(String userQuery) throws SolrServerException, IOException {
        String query = "content:" + userQuery;
        SolrQuery solrQuery = new SolrQuery(query);
        QueryResponse queryResponse = SOLR_CLIENT.query(solrQuery);
        SolrDocumentList solrDocumentList = queryResponse.getResults();
        DocumentObjectBinder binder = new DocumentObjectBinder();
        return binder.getBeans(Twit.class, solrDocumentList);
    }
}
