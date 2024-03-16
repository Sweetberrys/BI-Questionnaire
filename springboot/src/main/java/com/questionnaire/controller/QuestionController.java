package com.questionnaire.controller;

import com.questionnaire.common.Result;
import com.questionnaire.entity.Question;
import com.questionnaire.service.QuestionService;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;

/**
 * 题目表前端操作接口
 **/

@RestController
@RequestMapping("/question")
public class QuestionController {

    @Resource
    private QuestionService questionService;

    /**
     * 新增
     */
    @PostMapping("/add")
    public Result add(@RequestBody Question question) {
        questionService.add(question);
        return Result.success();
    }
    /**
     * 新增题目单选/多选/填空并默认一个选项
     */
    @PostMapping("/addForUser")
    public Result addForUser(@RequestBody Question question) {
        questionService.addForUser(question);
        return Result.success();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete/{id}")
    public Result deleteById(@PathVariable Integer id) {
        questionService.deleteById(id);
        return Result.success();
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/delete/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        questionService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    public Result updateById(@RequestBody Question question) {
        questionService.updateById(question);
        return Result.success();
    }

    /**
     * 根据ID查询
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        Question question = questionService.selectById(id);
        return Result.success(question);
    }

    /**
     * 查询所有
     */
    @GetMapping("/selectAll")
    public Result selectAll(Question question) {
        List<Question> list = questionService.selectAll(question);
        return Result.success(list);
    }

    /**
     * 分页查询
     */
    @GetMapping("/selectPage")
    public Result selectPage(Question question,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<Question> page = questionService.selectPage(question, pageNum, pageSize);
        return Result.success(page);
    }

    /**
     * 根据问卷ID查询所有的题目信息
     */
    @GetMapping("/selectByPageId")
    public Result selectByPageId(@RequestParam Integer pageId) {
        List<Question> pageList = questionService.selectByPageId(pageId);
        return Result.success(pageList);
    }

}