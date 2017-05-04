package com.yu.springboot.common.base;

import java.util.List;

/**
 * @author Feng Yu
 * @version V1.0
 * @date 2017-04-25
 */
public interface GenericService {

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
    int delete(String id);

    /**
     * 通过主键, 查询对象
     *
     * @param id 主键
     * @return model 对象
     */
    GenericVo findById(String id);

    /**
     * 查询多个对象
     *
     * @return 对象集合
     */
    List<GenericVo> listAll();
    
    /**
     *查询总数
     * @param paramObj
     * @return
     */
	int total(Object paramObj);
    
    /**
     * 分页查询
     * @param paramObj
     * @return
     */
	List<GenericVo> pageSearch(Object paramObj);
	
	/**
	 * 批量删除
	 * @param objArr
	 * @return
	 */
	int batchDel(Object[] objArr);
}
