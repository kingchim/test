package com.changgou.pay.service.impl;


import com.github.wxpay.sdk.WXPay;
import com.changgou.pay.service.WXPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class WXPayServiceImpl implements WXPayService {

    @Autowired
    private WXPay wxPay;

    @Value("${wxpay.notify_url}")
    private String notify_url;

    @Override
    public Map nativePay(String orderId, Integer money) {
        try {
            //请求封装参数
            Map<String, String> map = new HashMap<>();
            map.put("body", "畅购");
            map.put("out_trade_no", orderId);

            BigDecimal payMoney = new BigDecimal("0.01");
            BigDecimal fen = payMoney.multiply(new BigDecimal("100"));//1.00
            fen = fen.setScale(0, BigDecimal.ROUND_UP);
            map.put("total_fee", String.valueOf(fen));

            map.put("spbill_create_ip", "127.0.0.1");
            map.put("notify_url", notify_url);
            map.put("trade_type", "NATIVE");

            //基于wxpay完成统一下单接口的调用,并获取返回结果
            Map<String, String> result = wxPay.unifiedOrder(map);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Map queryOrder(String orderId) {
        return null;
    }
}
