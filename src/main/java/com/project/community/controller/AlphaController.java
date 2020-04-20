package com.project.community.controller;

import com.project.community.service.AlphaService;
import com.project.community.util.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * @author makwingchi
 * @Description
 * @create 2020-04-14 19:09
 */
@Controller
@RequestMapping("/alpha")
public class AlphaController {

    @Autowired
    private AlphaService alphaService;

    @RequestMapping("/data")
    @ResponseBody
    public String getDate() {
        return alphaService.find();
    }

    @RequestMapping("/http")
    public void http(HttpServletRequest request, HttpServletResponse response) {
        System.out.println(request.getMethod());
        System.out.println(request.getServletPath());

        Enumeration<String> headerNames = request.getHeaderNames();
        while(headerNames.hasMoreElements()) {
            String ele = headerNames.nextElement();
            String header = request.getHeader(ele);
            System.out.println(ele + ": " + header);
        }

        System.out.println(request.getParameter("code"));

        response.setContentType("text/html;charset=utf-8");
        PrintWriter writer = null;
        try {
            writer = response.getWriter();
            writer.write("<h1>hahaha</h1>");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            writer.close();
        }
    }

    @GetMapping("/students")
    @ResponseBody
    // /students?current=1&limit=10
    public String getStudents(@RequestParam(name = "current", required = false, defaultValue = "1") int current,
                              @RequestParam(name = "limit", required = false, defaultValue = "10") int limit) {
        return "Current: " + current + "; " + "Limit: " + limit;
    }

    // /student/1
    @GetMapping("/student/{id}")
    @ResponseBody
    public String getStudent(@PathVariable int id) {
        return "Student: " + id;
    }

    // POST request
    @PostMapping("/student")
    @ResponseBody
    public String saveStudent(String name, int age) {
        return "Student added --> " + name + ", " + age + " years old.";
    }

    // HTML response
    @GetMapping("/teacher")
    public ModelAndView getTeacher() {
        ModelAndView mv = new ModelAndView();
        mv.addObject("name", "Jack");
        mv.addObject("age", 30);
        mv.setViewName("/demo/view");
        return mv;
    }

    @GetMapping("/school")
    public String getSchool(Model model) {
        model.addAttribute("name", "MIT");
        model.addAttribute("age", 262);
        return "/demo/view";
    }

    // JSON (async)
    @GetMapping("/emp")
    @ResponseBody
    public Map<String, Object> getEmp() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", "hahaha");
        map.put("age", 23);
        map.put("salary", 80000);
        return map;
    }

    @GetMapping("/emps")
    @ResponseBody
    public List<Map<String, Object>> getEmps() {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("name", "jack");
        map.put("age", 23);
        map.put("salary", 80000);
        list.add(map);
        return list;
    }

    // cookie demo
    @GetMapping("/cookie/set")
    @ResponseBody
    public String setCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("code", CommunityUtil.generateUUID().substring(0, 10));
        // cookie scope
        cookie.setPath("/community/alpha");
        // cookie age
        cookie.setMaxAge(60 * 10);
        // send cookie
        response.addCookie(cookie);

        return "cookie set";
    }

    @GetMapping("/cookie/get")
    @ResponseBody
    public String getCookie(@CookieValue("code") String code) {
        return code;
    }

    // session demo
    @GetMapping("/session/set")
    @ResponseBody
    public String setSession(HttpSession session) {
        session.setAttribute("id", 1);
        session.setAttribute("name", "test");
        return "session set";
    }

    @GetMapping("/session/get")
    @ResponseBody
    public String getSession(HttpSession session) {
        Integer id = (Integer) session.getAttribute("id");
        String name = (String) session.getAttribute("name");
        return id + ": " + name;
    }

    // ajax
    @PostMapping("/ajax")
    @ResponseBody
    public String ajax(String name, int age) {
        System.out.println(name + ": " + age);
        return CommunityUtil.getJsonString(0, "ok");
    }

}
