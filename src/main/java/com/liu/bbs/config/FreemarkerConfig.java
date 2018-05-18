package com.liu.bbs.config;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Map;

/**
 * Created by 26522 on 2018/5/13.
 */
@Configuration
public class FreemarkerConfig {
    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;

    @Bean
    public freemarker.template.Configuration getFreeMarkerConfiguration(){
        return freeMarkerConfigurer.getConfiguration();
    }
    public void resolveMap(Map<String,String> model, String templateName){
        try {
            Template template = this.getFreeMarkerConfiguration().getTemplate(templateName);
            template.process(model, new OutputStreamWriter(System.out));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        }
    }
}
