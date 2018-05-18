package com.liu.bbs.service.kafka.producer;

import com.alibaba.fastjson.JSON;
import com.liu.bbs.po.Article;
import com.liu.bbs.po.Message;
import com.liu.bbs.service.ArticleServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;


@Component
public class MsgProducer {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;


    @Autowired
    private ArticleServiceImpl service;

    public void sendMsg(Article a){
        Message m=new Message();

        m.setUid(service.findOne(a.getRootid()).getUser().getUserid());//发主贴的用户ID

        m.setMeid(a.getUser().getUserid());//回帖用户id
        m.setTxt(service.findOne(a.getRootid()).getTitle());//把主帖子的主题当做消息发送
        m.setSendTime(new Date());//消息发送时间
        m.setRtitle(a.getTitle());//从贴的主题

        String topic="reply"+m.getUid();//我针对该主贴用户建立主题，我现在本身是回帖用户
        kafkaTemplate.setProducerListener(new KafkaProducerListener());
        System.out.println(JSON.toJSONString(m));
        kafkaTemplate.send(topic,JSON.toJSONString(m));
    }

}
