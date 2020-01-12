package com.changgou.order.service.impl;

import com.changgou.goods.feign.SkuFeign;
import com.changgou.goods.feign.SpuFeign;
import com.changgou.goods.pojo.Sku;
import com.changgou.goods.pojo.Spu;
import com.changgou.order.pojo.OrderItem;
import com.changgou.order.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CartServiceImpl implements CartService {
    private static final String CART = "cart_";

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private SkuFeign skuFeign;

    @Autowired
    private SpuFeign spuFeign;

    //添加购物车
    @Override
    public void addCart(String skuId, Integer num, String username) {
        //1.查询redis中相对应的商品信息
        OrderItem orderItem = (OrderItem) redisTemplate.boundHashOps(CART + username).get(skuId);
        if (orderItem != null) {
            //2.如果当前商品在redis中的存在,则更新商品的数量与价钱
            orderItem.setNum(orderItem.getNum() + num);
            if (orderItem.getNum() <= 0) {
//                商品为0 删除商品
                redisTemplate.boundHashOps(CART + username).delete(skuId);
                return;
            }
            orderItem.setMoney(orderItem.getNum() * orderItem.getPrice());
            orderItem.setPayMoney(orderItem.getNum() * orderItem.getPrice());
        } else {
            //3.如果当前商品在redis中不存在,将商品添加到redis中
            Sku sku = skuFeign.findById(skuId).getData();
            Spu spu = spuFeign.findSpuById(sku.getSpuId()).getData();
            //封装orderItem
            orderItem = this.sku2OrderItem(sku, spu, num);
        }
        //4.将orderitem添加到redis中
        redisTemplate.boundHashOps(CART + username).put(skuId, orderItem);
    }

    //    查询购物车数据
    @Override
    public Map list(String username) {
        Map map = new HashMap<>();
        List<OrderItem> orderItemList = redisTemplate.boundHashOps(CART + username).values();
        map.put("orderItemList", orderItemList);
//        总价，数量
        int totalNum = 0;
        int totalMoney = 0;

        for (OrderItem orderItem : orderItemList) {
            totalNum += orderItem.getNum();
            totalMoney += orderItem.getMoney();
        }
        map.put("totalNum", totalNum);
        map.put("totalMoney", totalMoney);
        return map;
    }

    private OrderItem sku2OrderItem(Sku sku, Spu spu, Integer num) {
        OrderItem orderItem = new OrderItem();
        orderItem.setSpuId(spu.getId());
        orderItem.setSkuId(sku.getId());
        orderItem.setNum(num);
        orderItem.setName(sku.getName());
        orderItem.setPrice(sku.getPrice());
        orderItem.setMoney(orderItem.getPrice() * num);
        orderItem.setPayMoney(orderItem.getPrice() * num);
        orderItem.setImage(sku.getImage());
        orderItem.setWeight(sku.getWeight() * num);


//        是分类信息
        orderItem.setCategoryId1(spu.getCategory1Id());
        orderItem.setCategoryId2(spu.getCategory2Id());
        orderItem.setCategoryId3(spu.getCategory3Id());
        return orderItem;
    }


}
