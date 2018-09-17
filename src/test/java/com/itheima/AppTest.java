package com.itheima;

import static org.junit.Assert.assertTrue;

import com.sun.org.apache.bcel.internal.generic.NEW;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void addDocument() throws Exception{
        //和服务器创建连接
        HttpSolrServer httpSolrServer = new HttpSolrServer("http://localhost:8080/solr/collection1");
        SolrInputDocument document = new SolrInputDocument();
        document.addField("id","c001");
        document.addField("title_ik","使用solrj添加的文档");
        document.addField("product_name","商品名称");
        httpSolrServer.add(document);
        httpSolrServer.commit();
    }

    @Test
    public void testDelete() throws IOException, SolrServerException {
        HttpSolrServer solrServer = new HttpSolrServer("http://localhost:8080/solr/collection1");
        solrServer.deleteById("c001");
        solrServer.commit();
    }

    @Test
    public void testDeleteByQuery() throws IOException, SolrServerException {
        HttpSolrServer solrServer = new HttpSolrServer("http://localhost:8080/solr/collection1");
        SolrQuery query = new SolrQuery();
        query.setQuery("*:*");

        solrServer.deleteByQuery(query.getQuery());

        solrServer.commit();
    }
    @Test
    public void testSimpleQuery() throws SolrServerException {
        HttpSolrServer solrServer = new HttpSolrServer("http://localhost:8080/solr/collection1");
        SolrQuery query = new SolrQuery();
        query.setQuery("*:*");
        QueryResponse response = solrServer.query(query);
        SolrDocumentList results = response.getResults();
        System.out.println("共查询到的商品数： " + results.getNumFound());
        System.out.println(results.size());
        for (SolrDocument solrDocument : results) {
            System.out.println(solrDocument.get("id"));
            System.out.println(solrDocument.get("product_name"));
            System.out.println(solrDocument.get("product_price"));
            System.out.println(solrDocument.get("product_catalog_name"));
            System.out.println(solrDocument.get("product_picture"));

        }
    }

    @Test
    public void testQuery() throws SolrServerException {
        HttpSolrServer solrServer = new HttpSolrServer("http://localhost:8080/solr/collection1");
        SolrQuery query = new SolrQuery();
        query.setQuery("天下");
        query.addFilterQuery("product_price:[10 TO 30]");
//        query.addFilterQuery("product_catalog_name:幽默杂货");
        query.setStart(0);
        query.setRows(20);
        query.setSort("product_name", SolrQuery.ORDER.asc);
        query.setHighlight(true);
        query.setHighlightSimplePre("<em>");
        query.setHighlightSimplePost("</em>");
        query.addHighlightField("product_name");
        query.set("df","product_keywords");
       System.out.println(query.getQuery());
        QueryResponse response = solrServer.query(query);
        SolrDocumentList solrDocumentList = response.getResults();
        for (SolrDocument solrDocument : solrDocumentList) {
            String id = (String) solrDocument.get("id");
            System.out.println(id);
            Map<String, Map<String, List<String>>> highlighting = response.getHighlighting();
            List<String> list = highlighting.get(id).get("product_name");
            String productNmae="";
            if(null != list){
                productNmae = list.get(0);
            } else {
                productNmae = (String) solrDocument.get("product_name");
            }
            if (highlighting.get(id) != null)
            System.out.println(productNmae);
            System.out.println(solrDocument.get("product_price"));
            System.out.println(solrDocument.get("product_catalog_name"));
            System.out.println(solrDocument.get("product_picture"));

        }
    }

    public static void main(String[] args) {

    }

}
