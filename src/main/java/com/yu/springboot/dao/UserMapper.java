package com.yu.springboot.dao;

import com.yu.springboot.common.base.GenericDao;
import com.yu.springboot.common.datasource.TargetDataSource;
import com.yu.springboot.model.User;
import org.apache.ibatis.annotations.*;
import org.mapstruct.Mapper;
import java.util.List;
import java.util.Map;

/**
 * @author Feng Yu
 * @version V1.0
 * @date 2017-04-24
 */
@Mapper
@TargetDataSource(name = "ds2")
public interface UserMapper extends GenericDao {

    @Select("SELECT * FROM USER WHERE NAME = #{name}")
    User findByName(@Param("name") String name);

    @Insert("INSERT INTO user(id, name, age, tel) VALUES(#{id}, #{name}, #{age}, #{tel})")
    int insert(User user);

    @Insert("INSERT INTO USER(id, name, age, tel) VALUES(#{id,jdbcType=VARCHAR}, #{name,jdbcType=VARCHAR}, #{age,jdbcType=INTEGER}), #{tel,jdbcType=VARCHAR}")
    int insertByMap(Map<String, Object> map);

    @Update("UPDATE user SET age=#{age}, tel=#{tel} WHERE id =#{id}")
    int update(User user);

    @Delete("DELETE FROM user WHERE id =#{id}")
    int deleteById(@Param("id") String id);

    @Select("SELECT * FROM user WHERE id =#{id}")
    User findById(String id);

    /**
     * 映射结果数据ResultMap,此配置不能复用，其他方法需要时重写
     */
    @Select("select * from user")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "name", column = "name"),
            @Result(property = "age", column = "age"),
            @Result(property = "tel", column = "tel")
    })
    List<User> listAll();

    /**
     * 映射结果为 UserMapper.xml配置的ResultMap，此配置可多方法重用
     */
    @Select("select * from user limit ${startIndex},${pageSize}")
    @ResultMap("myResultMap")
    List<User> pageList(Object paramObj);
}
