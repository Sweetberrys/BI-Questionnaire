package com.questionnaire.controller;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.questionnaire.common.Result;
import com.questionnaire.common.enums.QuestionTypeEnum;
import com.questionnaire.common.enums.ResultCodeEnum;
import com.questionnaire.common.enums.RoleEnum;
import com.questionnaire.entity.Account;
import com.questionnaire.entity.Answer;
import com.questionnaire.entity.Question;
import com.questionnaire.entity.QuestionItem;
import com.questionnaire.service.*;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 基础前端接口
 */

@RestController
public class WebController {

    @Resource
    private AdminService adminService;
    @Resource
    private UserService userService;

    @Resource
    QuestionService questionService;

    @Resource
    AnswerService answerService;

    @GetMapping("/")
    public Result hello() {
        return Result.success("访问成功");
    }

    /**
     * 登录
     */
    @PostMapping("/login")
    public Result login(@RequestBody Account account) {
        if (ObjectUtil.isEmpty(account.getUsername()) || ObjectUtil.isEmpty(account.getPassword())
                || ObjectUtil.isEmpty(account.getRole())) {
            return Result.error(ResultCodeEnum.PARAM_LOST_ERROR);
        }
        if (RoleEnum.ADMIN.name().equals(account.getRole())) {
            account = adminService.login(account);
        } else if (RoleEnum.USER.name().equals(account.getRole())) {
            account = userService.login(account);
        }
        return Result.success(account);
    }

    /**
     * 注册
     */
    @PostMapping("/register")
    public Result register(@RequestBody Account account) {
        if (StrUtil.isBlank(account.getUsername()) || StrUtil.isBlank(account.getPassword())
                || ObjectUtil.isEmpty(account.getRole())) {
            return Result.error(ResultCodeEnum.PARAM_LOST_ERROR);
        }
        if (RoleEnum.ADMIN.name().equals(account.getRole())) {
            adminService.register(account);
        } else if (RoleEnum.USER.name().equals(account.getRole())) {
            userService.register(account);
        }
        return Result.success();
    }

    /**
     * 修改密码
     */
    @PutMapping("/updatePassword")
    public Result updatePassword(@RequestBody Account account) {
        if (StrUtil.isBlank(account.getUsername()) || StrUtil.isBlank(account.getPassword())
                || ObjectUtil.isEmpty(account.getNewPassword())) {
            return Result.error(ResultCodeEnum.PARAM_LOST_ERROR);
        }
        if (RoleEnum.ADMIN.name().equals(account.getRole())) {
            adminService.updatePassword(account);
        } else if (RoleEnum.USER.name().equals(account.getRole())) {
            userService.updatePassword(account);
        }
        return Result.success();
    }

    /**
     * 数据统计
     */

    @GetMapping("/pageCount")
    public Result pageCount(@RequestParam Integer pageId) {

        Dict pageCount = Dict.create();

        List<Answer> answerList = answerService.selectByPageId(pageId);

        //用户提交问卷的数据
        long answerCount = answerList.stream().map(Answer::getNo).distinct().count();
        pageCount.set("answerCount", answerCount);


        List<Question> questionList = questionService.selectByPageId(pageId);
        //仅限于单选和多选
        for (Question question : questionList) {
            List<String> questionAnswerList =
                    answerList.stream().filter(answer -> answer.getQuestionId().equals(question.getId())).
                            map(Answer::getContent).collect(Collectors.toList());
            question.setCount(questionAnswerList.size());
            if (question.getType().equals(QuestionTypeEnum.SINGLE.getValue())
                    || question.getType().equals(QuestionTypeEnum.MULTIPLE.getValue())) {

                List<QuestionItem> questionItemList = question.getQuestionItemList();


                for (QuestionItem questionItem : questionItemList) {
                    //统计当前选项有多少回答
                    long count = questionAnswerList.stream().filter(content -> content.contains(questionItem.getContent())).count();
                    questionItem.setCount(count);
                    //统计当前选项的比例,
                    int percentage = BigDecimal.valueOf(count).divide(BigDecimal.valueOf(questionAnswerList.size()),
                           2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).intValue();
                    questionItem.setPercentage(percentage);
                }
            }else{
                List<QuestionItem> questionItemList = questionAnswerList.stream().map(content -> {
                    QuestionItem questionItem = new QuestionItem();
                    questionItem.setContent(content);
                    return questionItem;
                }).collect(Collectors.toList());
                //设置填空题的选项集合
                question.setQuestionItemList(questionItemList);
            }
        }

        pageCount.set("questionList", questionList);

        return Result.success(pageCount);
    }

}
