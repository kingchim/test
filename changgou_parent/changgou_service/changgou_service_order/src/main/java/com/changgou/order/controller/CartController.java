package com.changgou.order.controller;

import com.changgou.entity.Result;
import com.changgou.entity.StatusCode;
import com.changgou.order.config.TokenDecode;
import com.changgou.order.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/cart")
public class CartController {
    @Autowired
    private CartService cartService;

    @Autowired
    private TokenDecode tokenDecode;


    //    添加购物车
    @GetMapping("/addCart")
    public Result addCart(@RequestParam("skuId") String skuId, @RequestParam("num") Integer num) {
//暂时静态,后续动态获取
//        String username = "itcast";
        String username = tokenDecode.getUserInfo().get("username");
        cartService.addCart(skuId, num, username);
        return new Result(true, StatusCode.OK, "加入购物车成功");
    }

    @GetMapping("/list")
    public Map list() {
//        String username = "itcast";
        String username = tokenDecode.getUserInfo().get("username");
        return cartService.list(username);
    }

}
