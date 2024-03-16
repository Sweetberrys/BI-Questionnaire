package com.questionnaire.controller;


import com.questionnaire.common.Result;
import com.questionnaire.entity.QuestionItem;
import com.questionnaire.service.QuestionItemService;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;

/**
 * 题目内容表前端操作接口
 **/

@RestController
@RequestMapping("/questionItem")
public class QuestionItemController {

    @Resource
    private QuestionItemService questionItemService;

    /**
     * 新增
     */
    @PostMapping("/add")
    public Result add(@RequestBody QuestionItem questionItem) {
        questionItemService.add(questionItem);
        return Result.success();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete/{id}")
    public Result deleteById(@PathVariable Integer id) {
        questionItemService.deleteById(id);
        return Result.success();
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/delete/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        questionItemService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    public Result updateById(@RequestBody QuestionItem questionItem) {
        questionItemService.updateById(questionItem);
        return Result.success();
    }

    /**
     * 根据ID查询
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        QuestionItem questionItem = questionItemService.selectById(id);
        return Result.success(questionItem);
    }

    /**
     * 查询所有
     */
    @GetMapping("/selectAll")
    public Result selectAll(QuestionItem questionItem) {
        List<QuestionItem> list = questionItemService.selectAll(questionItem);
        return Result.success(list);
    }

    /**
     * 分页查询
     */
    @GetMapping("/selectPage")
    public Result selectPage(QuestionItem questionItem,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<QuestionItem> page = questionItemService.selectPage(questionItem, pageNum, pageSize);
        return Result.success(page);
    }

}