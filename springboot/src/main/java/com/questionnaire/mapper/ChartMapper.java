package com.questionnaire.mapper;

import com.questionnaire.entity.Chart;

import java.util.List;


/**
 * 操作AI图表相关数据接口
 */
public interface ChartMapper {

    /**
     * 新增
     */
    int insert(Chart chart);

    /**
     * 删除
     */
    int deleteById(Integer id);

    /**
     * 修改
     */
    int updateById(Chart chart);


    /**
     * 根据ID查询
     */
    Chart selectById(Integer id);

    /**
     * 查询所有
     */
    List<Chart> selectAll(Chart chart);

}



