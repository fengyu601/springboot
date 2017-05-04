package com.yu.springboot.controller;

import com.yu.springboot.common.base.GenericService;
import com.yu.springboot.common.error.ErrorReportException;
import com.yu.springboot.model.User;
import com.yu.springboot.service.UserService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Feng Yu
 * @version V1.0
 * @date 2017-04-12
 */
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    //@Qualifier("UserService")
    private UserService userService;

    @RequestMapping(value="/listAll", method= RequestMethod.GET)
    public String listAll(Model model) {
        List list = userService.listAll();
        //
        model.addAttribute("list", list);
        return "user/list";
    }

    @RequestMapping(value="/pageList", method= RequestMethod.GET)
    public String pageList(@RequestParam(value = "start") String start, @RequestParam(value = "limit") String limit,Model model) {
        Map<String, String> paraMap = new HashMap();
        paraMap.put("startIndex", start);
        paraMap.put("pageSize", limit);
        List list = userService.pageSearch(paraMap);
        //
        model.addAttribute("list", list);
        return "user/list";
    }

    @RequestMapping(value="/total", method= RequestMethod.GET)
    @ResponseBody
    public int total(@RequestParam(value = "name", required = false) String name, @RequestParam(value = "age", required = false) String age, @RequestParam(value = "tel", required = false) String tel) {
        Map<String, String> paraMap = new HashMap();
        if(StringUtils.hasText(name)){
            paraMap.put("name", name);
        }
        if(StringUtils.hasText(age)){
            paraMap.put("age", age);
        }
        if(StringUtils.hasText(tel)){
            paraMap.put("tel", tel);
        }
        int total = userService.total(paraMap);
        //
        return total;
    }

    @RequestMapping(value = "/getByName", method = RequestMethod.GET)
    public ModelAndView getByName(@RequestParam(value = "name", required = true) String name) {
        ModelAndView mav = new ModelAndView("user/read");
        User user = userService.findByName(name);
        mav.addObject(user);
        return mav;
    }

    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    @ResponseBody
    public String delete(@RequestParam(value = "oid", required = true) String id) {
        userService.delete(id);
        return "success";
    }

    @RequestMapping(value="/save", method= RequestMethod.POST)
    @ApiOperation(value="新增用户")
    @ApiImplicitParam(name = "user", value = "用户对象user", required = true, dataType = "User")
    @ResponseBody
    public String save(@Valid @RequestBody User user) {
        userService.insert(user);
        int total =  userService.total(null);
        return String.valueOf(total);
    }

    @RequestMapping(value="/update", method= RequestMethod.POST)
    @ResponseBody
    public String update(@Valid @RequestBody User user) {
        userService.update(user);
        return "success";
    }

    @RequestMapping(value="/myException", method= RequestMethod.GET)
    public String myException(){
        throw new ErrorReportException("-9999", "My Exception.");
    }

}
