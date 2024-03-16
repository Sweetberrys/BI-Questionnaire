package com.questionnaire.service;

import cn.hutool.core.date.DateUtil;
import com.questionnaire.common.enums.RoleEnum;
import com.questionnaire.entity.Account;
import com.questionnaire.entity.QuestionItem;
import com.questionnaire.mapper.QuestionItemMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.questionnaire.utils.TokenUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * 题目内容表业务处理
 **/
@Service
public class QuestionItemService {

    @Resource
    private QuestionItemMapper questionItemMapper;

    /**
     * 新增
     */
    public void add(QuestionItem questionItem) {

        //新增时进行判断
        Account currentUser = TokenUtils.getCurrentUser();
        //判断是否是用户，若是用户，则记录下来ID
        if(RoleEnum.USER.name().equals(currentUser.getRole())){
            questionItem.setUserId(currentUser.getId());
        }

        //设置创建时间
        questionItem.setCreateTime(DateUtil.now());
        questionItemMapper.insert(questionItem);
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        questionItemMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            questionItemMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(QuestionItem questionItem) {
        questionItemMapper.updateById(questionItem);
    }

    /**
     * 根据ID查询
     */
    public QuestionItem selectById(Integer id) {
        return questionItemMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<QuestionItem> selectAll(QuestionItem questionItem) {
        return questionItemMapper.selectAll(questionItem);
    }

    /**
     * 分页查询
     */
    public PageInfo<QuestionItem> selectPage(QuestionItem questionItem, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<QuestionItem> list = questionItemMapper.selectAll(questionItem);
        return PageInfo.of(list);
    }

    /**
     * 通过questionId查question
     * @param id
     */
    public List<QuestionItem> selectByQuestionId(Integer questionId) {
        return questionItemMapper.selectByQuestionId(questionId);
    }

    /**
     * 删除问卷选项
     */
    public void deleteByQuestionId(Integer questionId) {
        questionItemMapper.deleteByQuestionId(questionId);
    }
}