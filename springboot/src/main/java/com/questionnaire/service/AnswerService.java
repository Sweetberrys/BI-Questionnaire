package com.questionnaire.service;


import cn.hutool.core.date.DateUtil;
import com.questionnaire.entity.Account;
import com.questionnaire.entity.Answer;
import com.questionnaire.mapper.AnswerMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import javax.annotation.Resource;

import com.questionnaire.utils.TokenUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 业务处理
 **/
@Service
public class AnswerService {

    @Resource
    private AnswerMapper answerMapper;

    /**
     * 新增
     */
    public void add(Answer answer) {
        Account currentUser = TokenUtils.getCurrentUser();

        answer.setCreateTime(DateUtil.now());
        answer.setUserId(currentUser.getId());
        answerMapper.insert(answer);
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        answerMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            answerMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(Answer answer) {
        answerMapper.updateById(answer);
    }

    /**
     * 根据ID查询
     */
    public Answer selectById(Integer id) {
        return answerMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Answer> selectAll(Answer answer) {
        return answerMapper.selectAll(answer);
    }

    /**
     * 分页查询
     */
    public PageInfo<Answer> selectPage(Answer answer, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Answer> list = answerMapper.selectAll(answer);
        return PageInfo.of(list);
    }

    /**
     * 解决多次提交信息
     * @param answerList
     */
    public void addBatch(List<Answer> answerList) {
        for (Answer answer : answerList) {
            this.add(answer);
        }
    }

    /**
     * 根据PageId查answer
     * @param pageId
     */
    public List<Answer> selectByPageId(Integer pageId) {
        return answerMapper.selectByPageId(pageId);
    }
}