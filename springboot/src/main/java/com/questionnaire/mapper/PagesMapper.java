package com.questionnaire.mapper;

import com.questionnaire.entity.Pages;

import java.util.List;

/**
 * 操作pages相关数据接口
 */
public interface PagesMapper {

    /**
     * 新增
     */
    int insert(Pages pages);

    /**
     * 删除
     */
    int deleteById(Integer id);

    /**
     * 修改
     */
    int updateById(Pages pages);

    /**
     * 根据ID查询
     */
    Pages selectById(Integer id);

    /**
     * 查询所有
     */
    List<Pages> selectAll(Pages pages);

    /** 查询所有公开的问卷信息 */
    List<Pages> selectPageByQuestion(Pages pages);
}