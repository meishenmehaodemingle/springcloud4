package com.qf.dao;

import com.qf.base.BaseMapper;
import com.qf.pojo.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
