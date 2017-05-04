package com.yu.springboot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Feng Yu
 * @version V1.0
 * @date 2017-04-12
 */

@Controller
public class HelloController {

    @RequestMapping(value = "/hello", method= RequestMethod.GET)
    public String HelloWord(){
        return "hello";
    }
}
