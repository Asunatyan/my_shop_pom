package com.tamakiakoo.listener;

import com.tamakiakoo.controller.ItemController;
import com.tamakiakoo.entity.Goods;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

@Component
public class RabbiMQListener {


    @Autowired
    private Configuration configuration;

    @RabbitListener(queues = "item_queue")
    public void createHtml(Goods goods) {
        System.out.println("静态页面生成中..................................................");
        //静态页面输出的路径 - 输出的静态页面必须能够让外界访问
        String path = ItemController.class.getResource("/static/html/").getPath() + goods.getId() + ".html";
        try (
                Writer out = new FileWriter(path)
        ){
            //获得商品详情页模板
            Template template = configuration.getTemplate("goods.ftl");

            Map map = new HashMap();
            //获得商品的对应数据 - 调用该商品服务查询商品详细信息
            String[] gimages = goods.getGimages().split("\\|");
            map.put("goods", goods);
            map.put("gimages", gimages);

            //生成静态页
            template.process(map, out);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        }
    }

}
