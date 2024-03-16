package com.questionnaire.service;


import cn.hutool.core.date.DateUtil;
import com.questionnaire.common.enums.QuestionTypeEnum;
import com.questionnaire.common.enums.RoleEnum;
import com.questionnaire.entity.Account;
import com.questionnaire.entity.Question;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.questionnaire.entity.QuestionItem;
import com.questionnaire.mapper.QuestionMapper;
import com.questionnaire.utils.TokenUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * 题目表业务处理
 **/
@Service
public class QuestionService {

    @Resource
    private QuestionMapper questionMapper;


    @Resource
    QuestionItemService questionItemService;

    /**
     * 新增
     */
    public void add(Question question) {
        //新增时进行判断
        Account currentUser = TokenUtils.getCurrentUser();
        //判断是否是用户，若是用户，则记录下来ID
        if(RoleEnum.USER.name().equals(currentUser.getRole())){
            question.setUserId(currentUser.getId());
        }

        //设置创建时间
        question.setCreateTime(DateUtil.now());
        questionMapper.insert(question);
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        questionMapper.deleteById(id);

        //删除题目选项
        questionItemService.deleteByQuestionId(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            this.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(Question question) {
        questionMapper.updateById(question);
    }

    /**
     * 根据ID查询
     */
    public Question selectById(Integer id) {
        return questionMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Question> selectAll(Question question) {
        return questionMapper.selectAll(question);
    }

    /**
     * 分页查询
     */
    public PageInfo<Question> selectPage(Question question, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Question> list = questionMapper.selectAll(question);
        return PageInfo.of(list);
    }

    /** 根据问卷Id查问卷题目
     *
     * @param pageId
     * @return
     */
    public List<Question> selectByPageId(Integer pageId) {
        List<Question> questionList = questionMapper.selectByPageId(pageId);
        for (Question question : questionList) {
            List<QuestionItem> questionItemList = questionItemService.selectByQuestionId(question.getId());
            question.setQuestionItemList(questionItemList);
        }
        return questionList;
    }


    /**
     * 用户新建题目
     * @param question
     */
    @Transactional
    public void addForUser(Question question) {
        this.add(question);
        Integer questionId = question.getId();

        //添加单选/多选的默认2选项
        if(QuestionTypeEnum.SINGLE.getValue().equals(question.getType())
                || QuestionTypeEnum.MULTIPLE.getValue().equals(question.getType())){
            QuestionItem questionItem1 = new QuestionItem();
            questionItem1.setQuestionId(questionId);
            questionItemService.add(questionItem1);


            QuestionItem questionItem2 = new QuestionItem();
            questionItem2.setQuestionId(questionId);
            questionItemService.add(questionItem2);
        }

    }

    /**
     * 根据问卷ID去删除
     * @param id
     */
    public void deleteByPageId(Integer pageId) {

        //查出问卷ID
        List<Question> questionList = this.selectByPageId(pageId);
        //根据问卷ID删除
        questionMapper.deleteByPageId(pageId);

        //删除题目选项
        for (Question question : questionList) {
            questionItemService.deleteByQuestionId(question.getId());

        }
    }
}