package com.yu.springboot.common.base;

import java.util.List;

/**
 * @author Feng Yu
 * @version V1.0
 * @date 2017-04-25
 */
public abstract class GenericServiceImpl implements GenericService {
	
    /**
     * 定义成抽象方法,由子类实现,完成dao的注入
     *
     * @return GenericDao实现类
     */
    public abstract GenericDao getDao();

    /**
     * 插入对象
     *
     * @param model 对象
     */
    public int insert(GenericVo model) {
        return getDao().insert(model);
    }

    /**
     * 更新对象
     *
     * @param model 对象
     */
    public int update(GenericVo model) {
        return getDao().update(model);
    }

    /**
     * 通过主键, 删除对象
     *
     * @param id 主键
     */
    public int delete(String id) {
        return getDao().deleteById(id);
    }

    /**
     * 通过主键, 查询对象
     *
     * @param id 主键
     * @return
     */
    public GenericVo findById(String id) {
        return getDao().findById(id);
    }

    @Override
    public List<GenericVo> listAll() {
        return getDao().listAll();
    }

	@Override
	public int total(Object paramObj) {
		return getDao().total(paramObj);
	}

	@Override
	public List<GenericVo> pageSearch(Object paramObj) {
		return getDao().pageSearch(paramObj);
	}

	@Override
	public int batchDel(Object[] objArr) {
		return getDao().batchDel(objArr);
	}
}
