package com.questionnaire.service;

import cn.hutool.core.date.DateUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.questionnaire.common.enums.RoleEnum;
import com.questionnaire.entity.Account;
import com.questionnaire.entity.Pages;
import com.questionnaire.entity.Question;
import com.questionnaire.entity.QuestionItem;
import com.questionnaire.mapper.PagesMapper;
import com.questionnaire.utils.TokenUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * 问卷表业务处理
 **/
@Service
public class PagesService {

    @Resource
    private PagesMapper pagesMapper;

    @Resource
    QuestionService questionService;

    @Resource
    QuestionItemService questionItemService;


    /**
     * 新增
     */
    public void add(Pages pages) {
        //新增时进行判断
        Account currentUser = TokenUtils.getCurrentUser();
        //判断是否是用户，若是用户，则记录下来ID
        if(RoleEnum.USER.name().equals(currentUser.getRole())){
            pages.setUserId(currentUser.getId());
        }

        //设置创建时间
        pages.setCreateTime(DateUtil.today());
        pagesMapper.insert(pages);


    }

    /**
     * 删除
     */
    @Transactional
    public void deleteById(Integer id) {
        Pages pages = this.selectById(id);
        pagesMapper.deleteById(id);
        Account currentUser = TokenUtils.getCurrentUser();

        questionService.deleteByPageId(id);

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
    public void updateById(Pages pages) {
        pagesMapper.updateById(pages);

        Account currentUser = TokenUtils.getCurrentUser();


    }

    /**
     * 根据ID查询
     */
    public Pages selectById(Integer id) {
        return pagesMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Pages> selectAll(Pages pages) {
        return pagesMapper.selectAll(pages);
    }

    /**
     * 分页查询
     */
    public PageInfo<Pages> selectPage(Pages pages, Integer pageNum, Integer pageSize) {
        //进行筛选
        Account currentUser = TokenUtils.getCurrentUser();
        //如果是用户的话，需要筛选出当前用户自己的问卷信息
        if(RoleEnum.USER.name().equals(currentUser.getRole())){
            pages.setUserId(currentUser.getId());
        }
        PageHelper.startPage(pageNum, pageSize);
        List<Pages> list = pagesMapper.selectAll(pages);
        return PageInfo.of(list);
    }

    /**
     * 复制模板
     * @param pageId
     */
    @Transactional
    public Integer copy(Integer pageId) {
        //拿到当前被拷贝的问卷内容
        Pages pages = this.selectById(pageId);
        pages.setCount(pages.getCount()+1);
        //更新使用次数
        this.updateById(pages);

        Pages newPage = new Pages();
        newPage.setName(pages.getName()+"-复制");
        //复制后的问卷使用者应该为新的用户ID
        Account currentUser = TokenUtils.getCurrentUser();
//        newPage.setUserId(currentUser.getId());
        newPage.setDescr(pages.getDescr());
        newPage.setImg(pages.getImg());
        this.add(newPage);


        //拿到当前问卷关联的所有题目
        List<Question> questionList = questionService.selectByPageId(pageId);
        List<QuestionItem> questionItemList=null;
        for (Question question : questionList) {
            //清除原先的主键
            question.setId(null);
            //设置题目的问卷ID
            question.setPageId(newPage.getId());
            questionService.add(question);

            questionItemList = question.getQuestionItemList();
            for (QuestionItem questionItem : questionItemList) {
                questionItem.setId(null);
                questionItem.setQuestionId(question.getId());
                questionItemService.add(questionItem);
            }
        }
        return newPage.getId();
    }

    /**
     * 分页查询
     */
    public PageInfo<Pages> selectPageByQuestion(Pages pages, Integer pageNum, Integer pageSize) {
        //进行筛选
//        Account currentUser = TokenUtils.getCurrentUser();
        //如果是用户的话，需要筛选出当前用户自己的问卷信息
//        if(RoleEnum.USER.name().equals(currentUser.getRole())){
//            pages.setUserId(currentUser.getId());
//        }
        PageHelper.startPage(pageNum, pageSize);
        List<Pages> list = pagesMapper.selectPageByQuestion(pages);
        return PageInfo.of(list);
    }
}