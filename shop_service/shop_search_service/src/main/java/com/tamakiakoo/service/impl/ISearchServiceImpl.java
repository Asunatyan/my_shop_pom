package com.tamakiakoo.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.tamakiakoo.Service.ISearchService;
import com.tamakiakoo.entity.Goods;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ISearchServiceImpl implements ISearchService {

    @Autowired
    private SolrClient solrClient;

    @Override
    public List<Goods> getByKeyWord(String keyword) {

        SolrQuery solrQuery;
        if (StringUtils.isBlank(keyword)) {
            solrQuery = new SolrQuery("*:*");
        } else {
            solrQuery = new SolrQuery("gname:" + keyword + "|| ginfo:" + keyword);

        }

        List<Goods> list = new ArrayList<>();


        //设置高亮一定要放在SolrDocumentList results = query.getResults();查询结果之前要不然就位空
        //设置高亮
        solrQuery.setHighlight(true);//开启高亮
        solrQuery.setHighlightSimplePre("<font color='red'>");//设置前缀
        solrQuery.setHighlightSimplePost("</font>");//设置后缀
        solrQuery.addHighlightField("gname");

        //高亮的折叠
        solrQuery.setHighlightFragsize(10);//这个是设置高亮折叠后每次显示的长度
        solrQuery.setHighlightSnippets(3);//这个是设置高亮折叠的次数


        try {
            QueryResponse query = solrClient.query(solrQuery);

            SolrDocumentList results = query.getResults();

            Map<String, Map<String, List<String>>> highlighting = query.getHighlighting();


            for (SolrDocument document : results) {
                Goods goods = new Goods();
                goods.setId(Integer.parseInt(document.get("id") + ""));
                goods.setGname(document.get("gname") + "");
                goods.setGimages(document.get("gimages") + "");
                goods.setGinfo(document.get("ginfo") + "");
                goods.setGprice(BigDecimal.valueOf(Float.parseFloat(document.get("gprice") + "")));
                goods.setGsave((int) document.get("gsave"));

                //判断当前的商品是否存在高亮
                if (highlighting.containsKey(goods.getId() + "")) {
                    //当前商品存在高亮
                    Map<String, List<String>> stringListMap = highlighting.get(goods.getId() + "");
                    //获得高亮的字段
                    List<String> gname = stringListMap.get("gname");
                    if (gname != null) {

                        goods.setGname(gname.get(0));
                    }
                }

                list.add(goods);
            }


        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return list;
    }

    /**
     * 同步到索引库
     * @param goods
     * @return
     */
    @Override
    public int addGoods(Goods goods) {

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
            return 1;
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return 0;
    }

    @Override
    public int deleteGoods(int id) {

        try {
            solrClient.deleteById(id + "");
            solrClient.commit();
            return 1;
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return 0;
    }

    @Override
    public int updateGoods(Goods goods) {
        int i = addGoods(goods);
        return i;
    }

}
