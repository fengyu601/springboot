package com.yu.springboot.common.base;

import java.util.List;

/**
 * @author Feng Yu
 * @version V1.0
 * @date 2017-04-25
 */
public interface GenericDao{

    /**
     * 插入对象
     *
     * @param model 对象
     */
    int insert(GenericVo model);

    /**
     * 更新对象
     *
     * @param model 对象
     */
    int update(GenericVo model);

    /**
     * 通过主键, 删除对象
     *
     * @param id 主键
     */
    int deleteById(String id);

    /**
     * 通过主键, 查询对象
     *
     * @param id 主键
     * @return
     */
    GenericVo findById(String id);
    
    /**
     * 查询所有
     * @return
     */
    List listAll();
    
    /**
     * 查询总数
     * @param paramObj
     * @return
     */
	int total(Object paramObj);
    
    /**
     * 分页查询
     * @param paramObj
     * @return
     */
	List pageSearch(Object paramObj);
	
	/**
	 * 批量删除
	 * @param objArr
	 * @return
	 */
	int batchDel(Object[] objArr);

}
