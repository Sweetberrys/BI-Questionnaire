package com.questionnaire.mapper;

import com.questionnaire.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* @author 16690
* @description 针对表【user(管理员)】的数据库操作Mapper
* @createDate 2024-02-28 16:30:10
* @Entity com.questionnaire.entity.User
*/
public interface UserMapper {

    List<User> selectAll(User user);

    void insert(User user);

    void deleteById(Integer id);

    User selectById(Integer id);

    void updateById(User user);

    @Select("select * from user where username = #{username}")
    User selectByUsername(String username);
}




