package com.yu.springboot.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/sdkproxy")
public class EUCPServerController {

    @RequestMapping(value = "/sendsms.action")
    public String sendSMS(HttpServletRequest request){
        Map map = request.getParameterMap();
        String res = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><response><error>0</error><message>发送成功</message></response>";
        return res;
    }

    @RequestMapping(value = "/getreport.action")
    public String getReport(){
        String res = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<response>" +
                "<error>0</error>" +
                "<message>" +
                "<srctermid>110</srctermid>" +
                "<submitDate>20170417000000</submitDate>" +
                "<receiveDate>20170417122334</receiveDate>" +
                "<addSerial></addSerial>" +
                "<addSerialRev></addSerialRev>" +
                "<state>-9000</state>" +
                "<seqid>00083628942533347826378652141504</seqid>" +
                "</message>" +
                "<message>" +
                "<srctermid>120</srctermid>" +
                "<submitDate>20170417000000</submitDate>" +
                "<receiveDate>20170417122334</receiveDate>" +
                "<addSerial></addSerial>" +
                "<addSerialRev></addSerialRev>" +
                "<state>-9000</state>" +
                "<seqid>58940855871174046675637871764694</seqid>" +
                "</message>" +
                "</response>";
        return res;
    }
}
