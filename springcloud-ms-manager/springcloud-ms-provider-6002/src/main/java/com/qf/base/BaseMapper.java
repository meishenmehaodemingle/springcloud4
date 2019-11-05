package com.qf.base;

import com.qf.pojo.User;
import tk.mybatis.mapper.common.ConditionMapper;
import tk.mybatis.mapper.common.ExampleMapper;
import tk.mybatis.mapper.common.IdsMapper;
import tk.mybatis.mapper.common.MySqlMapper;

public interface BaseMapper<T> extends MySqlMapper<T>, ExampleMapper<T>,
        tk.mybatis.mapper.common.BaseMapper<T>,
        IdsMapper<T>, ConditionMapper<T> {
}
