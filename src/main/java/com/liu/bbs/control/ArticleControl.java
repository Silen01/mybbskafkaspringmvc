package com.liu.bbs.control;

import com.alibaba.fastjson.JSON;
import com.liu.bbs.po.Bbsuser;
import com.liu.bbs.service.ArticleServiceImpl;
import com.liu.bbs.po.Article;
import com.liu.bbs.po.PageBean;
import com.liu.bbs.util.FreemarkerUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by 26522 on 2018/5/13.
 */


@Controller
@RequestMapping(value="article")
public class ArticleControl {
    private Map<String,String> map=new HashMap();
    @Autowired
    private ArticleServiceImpl service;
    @RequestMapping(value = "/delc")
    private void delc(HttpServletRequest req, HttpServletResponse resp) {
        String rid=req.getParameter("rid");
        service.deleteCT(Integer.parseInt(rid));
        queryreply(req,resp);

    }
    @RequestMapping(value = "/queryid")
    private void queryreply(HttpServletRequest req, HttpServletResponse resp) {
        String id=req.getParameter("id");
        Map<String,Object> map=service.queryById(Integer.parseInt(id));

        String json=JSON.toJSONString(map,true);
        System.out.println(json);
        resp.setContentType("text/html");
        resp.setCharacterEncoding("utf-8");
        try {
            PrintWriter out=resp.getWriter();
            out.print(json);
            out.flush();
            out.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
    @RequestMapping(value = "/addz")
    private void add(@ModelAttribute Article article,
                     @RequestParam(value = "userid")String uid,
                     HttpServletRequest req, HttpServletResponse resp) {

        article.setUser(new Bbsuser(Integer.parseInt(uid)));
        article.setDatetime(new Date(System.currentTimeMillis()));


        if(service.savez(article)!=null){//增加成功，BUG，发主贴不用发消息
            if(article.getRootid()==0){//主贴
                RequestDispatcher dispatcher=null;
                dispatcher=req.getRequestDispatcher("/");
                try {
                    dispatcher.forward(req,resp);
                } catch (ServletException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }else{//增加完毕从贴，之后显示从贴
                this.queryreply(req,resp);
            }

        }
    }

    @RequestMapping(value = "/addc")
    private void addc(
            @RequestParam(value = "title")String title,
            @RequestParam(value = "content")String content,
            @RequestParam(value = "rootid")String rootid,
            @RequestParam(value = "id")String id,
            @RequestParam(value = "uid")String uid,
            HttpServletRequest req, HttpServletResponse resp) {
        Article article=new Article();
        article.setContent(content);
        article.setTitle(title);
        //article.setId(Integer.parseInt(id));BUG 增加回帖
        article.setUser(new Bbsuser(Integer.parseInt(uid)));
        article.setDatetime(new Date(System.currentTimeMillis()));
        article.setRootid(Integer.parseInt(rootid));

        if(service.savec(article)!=null){//增加成功
            this.queryreply(req,resp);
        }
    }
    @RequestMapping(value = "/delz/{id}")
    private void delz(@PathVariable String id, HttpServletRequest req, HttpServletResponse resp) {
        // String id=req.getParameter("id");
        service.deleteZT(Integer.parseInt(id));
        RequestDispatcher dispatcher=null;
        dispatcher=req.getRequestDispatcher("/");
        try {
            dispatcher.forward(req,resp);
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @RequestMapping(value = "/queryall/{page}")
    private String queryAll(@PathVariable String page, HttpServletRequest req, HttpServletResponse resp, Map map1) {

        Bbsuser user=(Bbsuser) req.getSession().getAttribute("user");


        int pageNum=5;
        if(user!=null){
            pageNum=user.getPagenum();
        }
        Sort sort=new Sort(Sort.Direction.DESC,"id");
        Pageable pageable=new PageRequest(Integer.parseInt(page)-1,pageNum,sort);
        Page<Article> pa=service.findAll(pageable,0);//rid查主贴，默认是0

        PageBean pb=new PageBean();
        pb.setRowsPerPage(pageNum);
        //pb.setCurPage(pageable.getPageNumber());
        pb.setCurPage(Integer.parseInt(page));//BUG
        pb.setMaxRowCount(pa.getTotalElements());
        pb.setData(pa.getContent());
        pb.setMaxPage(pa.getTotalPages());

        map1.put("pb",pb);
        return "show";

    }

}
