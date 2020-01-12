package com.changgou.web.gateway.filter;


import com.changgou.web.gateway.service.AuthService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AuthFilter implements GlobalFilter, Ordered {

    private static final String LOGIN_URL = "http://localhost:8001/api/oauth/toLogin";

    @Autowired
    private AuthService authService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //获取当前请求对象
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        String path = request.getURI().getPath();
        if ("/api/oauth/login".equals(path) || !UrlFilter.hasAuthorize(path)) {
            //放行
            return chain.filter(exchange);
        }

        //判断cookie上是否存在jti
        String jti = authService.getJtiFromCookie(request);
        if (StringUtils.isEmpty(jti)) {
//            拒绝访问,直接跳转
            /*response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();*/

            return this.toLoginPage(LOGIN_URL + "?FROM=" + request.getURI().getPath(), exchange);
        }
        //判断redis中token是否存在
        String token = authService.getTokenFromRedis(jti);
        if (StringUtils.isEmpty(token)) {
//            拒绝访问,直接跳转
//            response.setStatusCode(HttpStatus.UNAUTHORIZED);
//            return response.setComplete();
            return this.toLoginPage(LOGIN_URL, exchange);
        }

        //校验通过,请求头增强，放行
        request.mutate().header("Authorization", "Bearer " + token);
        return chain.filter(exchange);
    }

    private Mono<Void> toLoginPage(String loginUrl, ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.SEE_OTHER);
        response.getHeaders().set("Location", loginUrl);
        return response.setComplete();
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
