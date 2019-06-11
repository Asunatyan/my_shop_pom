package com.tamakiakoo.listener;

import com.tamakiakoo.entity.Goods;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RabbitMQListener {

    @Autowired
    private SolrClient solrClient;

    @RabbitListener(queues = "search_queue")
    public void goodsMsgHander(Goods goods) {
        System.out.println("同步索引库中..................................................");
        SolrInputDocument document = new SolrInputDocument();

        document.addField("id", goods.getId() + "");
        document.addField("gname", goods.getGname());
        document.addField("ginfo", goods.getGinfo());
        document.addField("gprice", goods.getGprice().floatValue());
        document.addField("gsave", goods.getGsave());
        document.addField("gimages", goods.getGimages());
        try {
            solrClient.add(document);
            solrClient.commit();
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


}
