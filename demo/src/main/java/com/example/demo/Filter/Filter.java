//package com.example.demo.Filter;
//
//import com.alibaba.fastjson.JSON;
//import com.example.demo.unitls.JwtUtils;
//import com.example.demo.unitls.Response;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.ServletRequest;
//import jakarta.servlet.ServletResponse;
//import jakarta.servlet.annotation.WebFilter;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//import java.util.Arrays;
//import java.util.List;
//
//@Component
//@WebFilter(urlPatterns = "/*")
//public class Filter implements jakarta.servlet.Filter {
////只要资源开启拦截就开始
//
//
//    @Override
//    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
//        System.out.println("开始拦截");
//            HttpServletRequest request = (HttpServletRequest) servletRequest;
//            HttpServletResponse response = (HttpServletResponse) servletResponse;
////           获取请求路径
//           String url=  request.getRequestURL().toString();
////          判断字符串中是否包含login
//            System.out.println(url);
//
//        // 1. 定义需要放行的地址集合（可以提取为常量）
//        List<String> allowPaths = Arrays.asList("/login", "/register","/insertAddress","/getAddressinfo","/deleteAddress","/insertAccout","/selectAccout"
//        ,"/updateAccout","/insertPayMethod","/selectPayMethod","/updatePayMethod","/deletePayMethod");
//
//// 2. 判断当前URL是否包含集合中的任意一个地址
//        boolean isAllow = allowPaths.stream().anyMatch(path -> url.contains(path));
//
//        if (isAllow) {
//            filterChain.doFilter(request, response); // 放行
//            return;
//        }
//
//
//
//            System.out.println("没有拦截"+allowPaths);
//
//        String token= request.getHeader("Authorization");
//
//        System.out.println("提取的token: " + token);
//
//
//
//
//        if (token != null && token.startsWith("Bearer ")) {
//            // 截取解析 "Bearer " 之后的部分（从第7个字符开始，因为 "Bearer " 是7个字符）
//
//              JwtUtils.parseJwt(token.substring(7).trim());
//              System.out.println(JwtUtils.parseJwt(token.substring(7).trim()));
//              filterChain.doFilter(request, response);
//              System.out.println("开始放行");
//
//        }else{
//
//            Response response1 = Response.zifinfyiErro(400,"token值不正确");
//            JSON.toJSONString(response1);
//            response.getWriter().write(JSON.toJSONString(response1));
//
//        }
//    }
//}
//
//
//       // 解析令牌
////        try {
////            JwtUtils.parseJwt(token.substring(7).trim());
////        }catch (Exception e){
////            }
//////        放行
////        filterChain.doFilter(request, response);
////
////        System.out.println("开始放行");
////    }
////}
//        // 不符合格式时返回null或抛出异常
//
//
//
//////           3获取token
////        String jwt = request.getHeader("Token");
////        System.out.println(jwt);
////
////        System.out.println(request.getHeaderNames());
////           if(jwt==null){
//////               返回登录页面
////          Response response1 = Response.zifinfyiErro(400,"token值不正确");
////               JSON.toJSONString(response1);
////               response.getWriter().write(JSON.toJSONString(response1));
////               return;
////           }
//
////
////// 验证token存在性
////        if (token == null || token.trim().isEmpty()) {
////            Response response1 = Response.zifinfyiErro(401, "未提供有效的Bearer token");
////            response.setContentType("application/json;charset=UTF-8");
////            response.getWriter().write(JSON.toJSONString(response1));
////            return;
////        }