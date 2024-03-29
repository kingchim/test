package com.changgou.web.order.controller;

import com.changgou.entity.Result;
import com.changgou.order.feign.OrderFeign;
import com.changgou.order.pojo.Order;
import com.changgou.pay.feign.PayFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequestMapping("/wxpay")
public class PayController {
    @Autowired
    private OrderFeign orderFeign;

    @Autowired
    private PayFeign payFeign;

    //跳转到微信支付二维码页面
    @GetMapping
    public String wxPay(String orderId, Model model) {
        //根据orderid查询订单,如果订单不存在,跳转到错误页面
        Result<Order> orderResult = orderFeign.findById(orderId);
        if (orderResult.getData() == null) {
            return "fail";
        }
        //根据订单的支付状态进行判断,如果不是未支付的订单,跳转到错误页面
        Order order = orderResult.getData();
        if (!"0".equals(order.getPayStatus())) {
            return "fail";
        }
        //基于payFeign调用统计下单接口,并获取返回结果
        Result result = payFeign.nativePay(orderId, order.getPayMoney());
        if (result.getData() == null) {
            return "fail";
        }
        //封装结果数据
        Map payMap = (Map) result.getData();
        payMap.put("orderId", orderId);
        payMap.put("payMoney", order.getPayMoney());

        model.addAllAttributes(payMap);
        return "wxpay";
    }

    //支付成功页面的跳转
    @RequestMapping("/toPaySuccess")
    public String toPaySuccess(Integer payMoney, Model model) {
        model.addAttribute("payMoney", payMoney);
        return "paysuccess";
    }

}
