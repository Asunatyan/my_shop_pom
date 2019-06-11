package com.tamakiakoo.shop_search_service;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.SolrParams;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.Collection;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ShopSearchServiceApplicationTests {

    @Autowired
    private SolrClient solrClient;

    @Test
    public void contextLoads() throws IOException, SolrServerException {


        for (int i =1; i < 10; i++) {
        SolrInputDocument document = new SolrInputDocument();

        document.addField("id", 2+i);
        document.addField("gname", "华为"+i);
        document.addField("gprice", 156.2);
        document.addField("ginfo", "这个手机可便宜了"+i);
        document.addField("gsave", 152+i);
        solrClient.add(document);
        }

        solrClient.commit();//注意要提交要不然保存不到索引库里面
    }

    @Test
    public void testQuery() throws IOException, SolrServerException {
        SolrQuery query = new SolrQuery();
        solrClient.query(query);
    }

    @Test
    public void delete() throws IOException, SolrServerException {
        solrClient.deleteById("6");
        solrClient.deleteById("7");
        solrClient.deleteById("8");
        solrClient.deleteById("9");
        solrClient.deleteById("10");
        solrClient.deleteById("7");
        solrClient.commit();
    }

}
