package com.questionnaire.service;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.questionnaire.entity.Chart;
import com.questionnaire.mapper.ChartMapper;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;

/**
 * AI图表表业务处理
 **/
@Service
public class ChartService {

    @Resource
    private ChartMapper chartMapper;

    /**
     * 新增
     */
    public void add(Chart chart) {
        chartMapper.insert(chart);

    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        chartMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            chartMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(Chart chart) {
        chartMapper.updateById(chart);
    }

    /**
     * 根据ID查询
     */
    public Chart selectById(Integer id) {
        return chartMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Chart> selectAll(Chart chart) {
        return chartMapper.selectAll(chart);
    }

    /**
     * 分页查询
     */
    public PageInfo<Chart> selectPage(Chart chart, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Chart> list = chartMapper.selectAll(chart);
        return PageInfo.of(list);
    }

}