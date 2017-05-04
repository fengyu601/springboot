package com.yu.springboot.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author Feng Yu
 * @version V1.0
 * @date 2017/4/13 0013
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = UserController.class)
//@AutoConfigureMockMvc
public class UserControllerTest {
    private static final Logger log = LoggerFactory.getLogger(UserControllerTest.class);
    private MockMvc mvc;

    @Before
    public void setUp() throws Exception {
        mvc = MockMvcBuilders.standaloneSetup(new UserController()).build();
    }

    @Test
    public void test() throws Exception {
        String reqBody = "{\"id\":\"1\",\"name\":\"Feng Yu\",\"age\":\"1\",\"tel\":\"13581663023\"}";
        //
        RequestBuilder request= post("/user/getNameRest")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(reqBody);
        //
        mvc.perform(request)//执行请求
                //.andExpect(handler().handlerType(UserController.class)) //验证执行的控制器类型
                //.andExpect(handler().methodName("create")) //验证执行的控制器方法名
                //.andExpect(model().hasNoErrors()) //验证页面没有错误
                //.andExpect(model().attributeExists("user")) //验证存储模型数据
                //.andExpect(view().name("user/view")) //验证viewName
                //.andExpect(forwardedUrl("/WEB-INF/jsp/user/view.jsp"))//验证视图渲染时forward到的jsp
                .andExpect(status().isOk())//验证状态码
                //.andExpect(content().string(equalTo("pageList")))//验证响应报文字符串
                //.andDo(MockMvcResultHandlers.print())//控制台打印
                .andDo(print());//控制台打印
    }

    @Test
    public void testMyException(){
        RequestBuilder request = post("/user/myException").contentType(MediaType.APPLICATION_JSON_UTF8);
        try {
            mvc.perform(request).andDo(print());
        } catch (Exception e) {
            log.error("error",e);
        }
    }
}
