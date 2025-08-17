//package com.example.demo.unitls;
//
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//
//
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
//
//public class jwt {
//    private static long AnotherTime= 1000L*60*5;
//    private static String secretKey= "yuejaintao";
//    public static String generateJwt(Map<String, Object> map) {
//        String jwt = Jwts.builder()
//                .signWith(SignatureAlgorithm.HS256, secretKey)
//                .setClaims(map)
//                .setExpiration(new Date(System.currentTimeMillis() + AnotherTime))
//                .compact();
//
//        System.out.println(jwt);
//
//        return jwt;
//    }
//    public static void main(String[] args) {
//        Map<String, Object> map  = new HashMap< >( );
//
//        map.put("username", "lihua");
//        map.put("password", "123456");
//        map.put("id", "1");
//        map.put("email", "lihua@gmail.com");
//
//        String jwt = jwt.generateJwt( map );
//        System.out.println("生成的JWT令牌：" + jwt);
//    }
//
//
//}
