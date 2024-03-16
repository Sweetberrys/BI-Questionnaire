package com.questionnaire.mapper;

import com.questionnaire.entity.QuestionItem;
import java.util.List;

/**
 * 操作question_item相关数据接口
 */
public interface QuestionItemMapper {

    /**
     * 新增
     */
    int insert(QuestionItem questionItem);

    /**
     * 删除
     */
    int deleteById(Integer id);

    /**
     * 修改
     */
    int updateById(QuestionItem questionItem);

    /**
     * 根据ID查询
     */
    QuestionItem selectById(Integer id);

    /**
     * 查询所有
     */
    List<QuestionItem> selectAll(QuestionItem questionItem);

    /**
     * 根据questionId查questionItem
     * @param questionId
     * @return
     */
    List<QuestionItem> selectByQuestionId(Integer questionId);

    /**
     * 删除选项内容
     * @param questionId
     */
    void deleteByQuestionId(Integer questionId);
}