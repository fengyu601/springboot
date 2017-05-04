package com.yu.springboot.model;

import com.yu.springboot.common.base.GenericVo;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Pattern;

/**
 * @author Feng Yu
 * @version V1.0
 * @date 2017-04-12
 */
public class User implements GenericVo{
    private String id;

    @NotBlank(message = "坐席名不能为空！")
    private String name;

    private int age;

    @NotBlank(message = "电话号码不能为空！")
    @Pattern(regexp = "1[\\d]{10}|0[\\d]{9,11}", message = "电话号码格式错误!")
    private String tel;

    @Override
    public String getOid() {
        return this.id;
    }

    @Override
    public void setOid(String oid) {
        this.id = oid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }
}
