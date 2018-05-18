package com.liu.bbs.control;

import com.liu.bbs.service.ArticleServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by 26522 on 2018/5/13.
 */
@Controller
@RequestMapping(value = "/kindupload")
public class KindController {
    @Autowired
    private ArticleServiceImpl service;


    @RequestMapping(value = "/up")
    public void ok(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String s = service.uploadPic(req);
        PrintWriter out = resp.getWriter();

        out.print(s);
        out.flush();
        out.close();

    }
}