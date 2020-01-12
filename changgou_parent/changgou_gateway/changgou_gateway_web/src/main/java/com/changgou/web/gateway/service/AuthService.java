package com.changgou.web.gateway.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    //判断cookie中jti是否存在
    public String getJtiFromCookie(ServerHttpRequest request) {
        HttpCookie cookie = request.getCookies().getFirst("uid");
        if (cookie != null) {
            return cookie.getValue();
        }
        return null;
    }

    //判断redis中令牌是否过期
    public String getTokenFromRedis(String jti) {
        String jwt = stringRedisTemplate.boundValueOps(jti).get();
        return jwt;
    }
}
