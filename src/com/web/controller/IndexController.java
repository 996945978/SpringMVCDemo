package com.web.controller;

import com.web.anno.RequestMapping;
import com.web.pojo.Order;

public class IndexController {
    @RequestMapping(value = "/login" )
    public void login(Order order) {

        System.out.println(order.toString());
    }

}
