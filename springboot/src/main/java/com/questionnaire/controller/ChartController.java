package com.questionnaire.controller;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.PageInfo;
import com.questionnaire.common.Result;
import com.questionnaire.common.enums.ChartStatusTypeEnum;
import com.questionnaire.common.enums.QuestionTypeEnum;
import com.questionnaire.common.enums.ResultCodeEnum;
import com.questionnaire.entity.Account;
import com.questionnaire.entity.Chart;
import com.questionnaire.entity.dto.ChartRequest;
import com.questionnaire.entity.dto.ChartTableRequest;
import com.questionnaire.exception.CustomException;
import com.questionnaire.service.ChartService;
import com.questionnaire.service.RedisLimiterManager;
import com.questionnaire.utils.ExcelUtils;
import com.questionnaire.utils.TokenUtils;
import com.questionnaire.utils.YuApiUtils;
import io.netty.util.concurrent.CompleteFuture;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * AI图表前端操作接口
 **/

@RestController
@RequestMapping("/chart")
@Slf4j
public class ChartController {

    @Resource
    private ChartService chartService;

    @Resource
    private YuApiUtils yuApiUtils;

    @Resource
    private RedisLimiterManager redisLimiterManager;

    @Resource
    private ThreadPoolExecutor threadPoolExecutor;

    /**
     * 文件保存到数据库
     */
    @PostMapping("/gen")
    public Result genChartByAi(@RequestBody ChartRequest chartRequest) {
        System.out.println("---------------");
        System.out.println("answerCount=" + chartRequest.getAnswerCount());
        System.out.println("---------------");

        //获取目标、图表名称、图表类型
        List<String> questionGoalNameType = chartRequest.getQuestionGoalNameType();
        String questionChartGoal = questionGoalNameType.get(0);
        String questionChartName = questionGoalNameType.get(1);
        String questionChartType = questionGoalNameType.get(2);


        System.out.println("选择的图表类型" + questionChartType);
        Map<String, Object> answerCounts = chartRequest.getAnswerCount();
        Object o = answerCounts.get("answerCount");
        //答题人数
        int questionNum = ((Integer) o).intValue();
        Object o1 = answerCounts.get("questionList");
        List<Map<String, Object>> questionList = (List<Map<String, Object>>) o1;
        System.out.println("questionList=" + questionList);

        //拼接的信息
        String questionName = "";
        String questionType = "";
        String questionItemContent = "";
        Integer questionCount = 0;


        Account currentUser = TokenUtils.getCurrentUser();
        // 校验
        if ((StringUtils.isNotBlank(questionChartName) && questionChartName.length() > 200)
                && !StringUtils.isBlank(questionChartGoal) && StringUtils.isBlank(questionChartType)) {
            throw new RuntimeException("格式有误");
        }
        // 无需Prompt，直接调用现有模型
        // 模型ID
        long biModelId = 1763730658075037697L;

        // 构造用户输入,设置预设信息
        StringBuilder userInput = new StringBuilder();

//        String chartInfo=
//                "分析需求:" +questionChartGoal+
//                        "图表类型:"+questionChartType+
//                        "\n原始数据:\n" +
//                        "题目,题目类型,题目选项,作答选项,该选项作答人数\n" +
//                        "该问卷有效作答人数:"+questionNum+"人";
        userInput.append("分析需求:").append("\n");
        userInput.append(questionChartGoal).append("\n");
        userInput.append("图表类型:").append(questionChartType);
        userInput.append("\n该问卷有效作答人数:").append(questionNum);
        userInput.append("\n原始数据:\n题目,题目类型,题目选项,作答选项,该选项作答人数");
        // 拼接分析目标

        // 遍历问题列表
        for (Map<String, Object> question : questionList) {
            //获取题目
            questionName = (String) question.get("name");
            //获取题目类型
            questionType = (String) question.get("type");
            //拼接题目和题目类型
            userInput.append("\n").append(questionName).append(",");
            userInput.append(questionType).append(",");


            System.out.println("Question Name: " + questionName);
            System.out.println("Question Type: " + questionType);

            // 获取并处理questionItemList
            Object itemListObj = question.get("questionItemList");
            if (itemListObj instanceof List<?>) {
                List<Map<String, Object>> itemList = (List<Map<String, Object>>) itemListObj;
                for (Map<String, Object> item : itemList) {
                    //获取选项名称
                    questionItemContent = (String) item.get("content");
                    //获取选择该选项的人数
                    questionCount = (Integer) item.get("count");
                    if (questionType.equals("填空题")) {
                        questionCount = 1;
                    }
                    userInput.append(questionItemContent).append("(").append(questionCount).append(")");

                    System.out.println("Item Content: " + questionItemContent);
                    System.out.println("Item Count: " + questionCount);
                }
            }
        }

        // 调用AI

        String questionData = "数据为：" + userInput;
        userInput.append("\n我还需要明确的数据分析结论，同时需要你可以对数据的结果作出一定程度的预测、越详细越好");
        String chartResult = yuApiUtils.doChat(biModelId, userInput.toString());
        System.out.println("============================");
        System.out.println(userInput);
        String[] splits = chartResult.split("【【【【【");
//        if (splits.length < 3) {
//            throw new RuntimeException("AI 生成错误");
//        }
        // 去掉换行、空格等
        for (int i = 0; i < splits.length; i++) {
            System.out.println("splits[" + i + "]" + splits[i]);
        }
        //完整的splits()
        System.out.println("splits.length=" + splits.length);

        String genChart = splits[1].trim();
        String genResult = splits[2].trim();
//
//        String genChart = splits[1];
//        String genResult = splits[2];


        // 插入数据到数据库
        Chart newChart = new Chart();
        try {
            newChart.setGoal(questionChartGoal);
            newChart.setChartData(questionData);
            newChart.setName(questionChartName);
            newChart.setChartType(questionChartType);
            newChart.setGenChart(genChart);
            newChart.setGenResult(genResult);
            newChart.setUserId(currentUser.getId());
            String now = DateUtil.now();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date = null;
            date = sdf.parse(now);
            newChart.setCreateTime(date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        chartService.add(newChart);
        System.out.println(newChart);
        // 返回到前端

        List<String> genStr = new ArrayList<>();
        genStr.add(genChart);
        genStr.add(genResult);
        System.out.println("--------------------");
        for (String s : genStr) {
            System.out.println(s);
        }
        System.out.println("--------------------");


        return Result.success(genStr);
    }


    private void handlerChartUpdateError(Chart newChart, String execMessage) {
        newChart.setStatus(String.valueOf(ChartStatusTypeEnum.FAILED));
        newChart.setExecMessage(execMessage);
        chartService.updateById(newChart);
        if (!String.valueOf(ChartStatusTypeEnum.FAILED).equals(newChart.getStatus())) {
            System.out.println("更新图表失败状态失败" + newChart.getId() + "," + execMessage);
        }
        System.out.println("更新图表失败后的状态="+newChart.getStatus());
    }

    /**
     * 异步化
     *
     * @param fileName
     * @param chartTableRequest
     * @return
     */
    @PostMapping("/createTable/async")
    public Result createTableByAiForAsync(@RequestParam String fileName, @RequestBody ChartTableRequest chartTableRequest) {

        System.out.println("fileName=" + fileName);
        File file = new File("D:\\毕设项目\\111\\BI-questionnaire\\springboot\\src\\main\\resources\\excel\\" + fileName);

        String csvStr = "";
        try {
            MockMultipartFile multipartFile = null;
            //new一个文件转为csv格式
            multipartFile = new MockMultipartFile(file.getName(), new FileInputStream(file));
            //校验文件
            long size = multipartFile.getSize();

            final long TEN_MB = 10 * 1024 * 1024L;
            if (size > TEN_MB) {
                return Result.error(ResultCodeEnum.FILE_MORE);
            }

            csvStr = ExcelUtils.excelToCsv(multipartFile);
            //如果是空表
            if (csvStr == null) {
                return Result.error(ResultCodeEnum.NULL_EXCEL);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(csvStr);


        //todo 需要修改ChartTableRequest类
        List<String> goalNameType = chartTableRequest.getGoalNameType();

        //获取目标、图表名称、图表类型
        String chartGoal = goalNameType.get(0);
        String chartName = goalNameType.get(1);
        String chartType = goalNameType.get(2);

        System.out.println("分析目标" + chartGoal);
        System.out.println("图表名称" + chartName);
        System.out.println("图表类型" + chartType);

        // 校验
        if ((StringUtils.isNotBlank(chartName) && chartName.length() > 200)
                && !StringUtils.isBlank(chartGoal) && StringUtils.isBlank(chartType)) {
            throw new RuntimeException("格式有误");
        }

        //在校验后，进行限流
        Account currentUser = TokenUtils.getCurrentUser();
        //根据限流的粒度，每个用户之间一个限流器，且添加"createTableByAi_"，就会让每个方法之间不会冲突
        redisLimiterManager.doRateLimit("createTableByAi_" + currentUser.getId());

        // 无需Prompt，直接调用现有模型
        // 模型ID
        long biModelId = 1766830106405720065L;

        // 构造用户输入,设置预设信息
        StringBuilder userInput = new StringBuilder();

        userInput.append("分析需求:").append("\n");
        userInput.append(chartGoal).append("\n");
        userInput.append("图表类型:").append(chartType);
        // 拼接分析目标
        userInput.append("\n原始数据:\n").append(csvStr);

        String tableData = userInput + "";
        System.out.println(tableData);
        userInput.append("\n我还需要明确的数据分析结论，同时需要你可以对数据的结果作出一定程度的预测、越详细越好");


        /**
         * 加入任务队列
         */
        // 插入数据到数据库
        Chart newChart = new Chart();
        try {
            newChart.setGoal(chartGoal);
            newChart.setChartData(tableData);
            newChart.setName(chartName);
            newChart.setChartType(chartType);
            newChart.setStatus(String.valueOf(ChartStatusTypeEnum.WAIT));
            newChart.setUserId(currentUser.getId());
            String now = DateUtil.now();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date = null;
            date = sdf.parse(now);
            newChart.setCreateTime(date);
            //因为后面需要使用到newChart的id，默认id自增，所以这里先插入，方便后面获取
            chartService.add(newChart);

            System.out.println("newChart=" + newChart);
        } catch (Exception e) {
            e.printStackTrace();
        }


        List<String> genStr = new ArrayList<>();


        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            System.out.println("==========================异步任务执行中==========================");

            //先修改任务状态为执行中，减少重复执行的风险，同时让用户知道执行状态
            //更新执行中状态

            newChart.setStatus(String.valueOf(ChartStatusTypeEnum.RUNNING));
            System.out.println("==========================更新执行中状态==========================");
            //将数据查询出来进行对比
            try {
                chartService.updateById(newChart);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println(String.valueOf(ChartStatusTypeEnum.RUNNING).equals(newChart.getStatus()));
            if (!String.valueOf(ChartStatusTypeEnum.RUNNING).equals(newChart.getStatus())) {
                handlerChartUpdateError(newChart, "更新图表执行中状态失败");
                System.out.println("==========================更新图表执行中状态失败==========================");
                return;
            }
            System.out.println("更新图表执行中后的状态="+newChart.getStatus());

            // 调用AI
            String chartResult = yuApiUtils.doChat(biModelId, userInput.toString());
            System.out.println("==========================调用AI==========================");
            String[] splits = chartResult.split("【【【【【");
            if (splits.length < 3) {
                handlerChartUpdateError(newChart, String.valueOf(ResultCodeEnum.AI_GENERATE_FAIL));
            }
            // 去掉换行、空格等
            for (int i = 0; i < splits.length; i++) {
                System.out.println("splits[" + i + "]" + splits[i]);
            }
            //完整的splits()
            System.out.println("splits.length=" + splits.length);

            String genChart = splits[1].trim();
            String genResult = splits[2].trim();


            //更新成功状态

            newChart.setGenChart(genChart);
            newChart.setGenResult(genResult);
            newChart.setStatus(String.valueOf(ChartStatusTypeEnum.SUCCEED));
            //更新状态为成功状态
            chartService.updateById(newChart);
            if (!String.valueOf(ChartStatusTypeEnum.SUCCEED).equals(newChart.getStatus())) {
                handlerChartUpdateError(newChart, "更新图表成功状态失败");
                System.out.println("==========================更新图表成功状态失败==========================");
                return;
            }
            System.out.println("更新图表成功后的状态="+newChart.getStatus());

            chartService.add(newChart);
            System.out.println(newChart);
            // 返回到前端
            genStr.add(genChart);
            genStr.add(genResult);

        }, threadPoolExecutor);

        future.thenAccept(unused -> {
            System.out.println("异步任务已完成");
        });
        return Result.success(genStr);
    }


    /**
     * 同步化
     *
     * @param fileName
     * @param chartTableRequest
     * @return
     */
    @PostMapping("/createTable")
    public Result createTableByAi(@RequestParam String fileName, @RequestBody ChartTableRequest chartTableRequest) {

        System.out.println("fileName=" + fileName);
        File file = new File("D:\\毕设项目\\111\\BI-questionnaire\\springboot\\src\\main\\resources\\excel\\" + fileName);

        String csvStr = "";
        try {
            MockMultipartFile multipartFile = null;
            //new一个文件转为csv格式
            multipartFile = new MockMultipartFile(file.getName(), new FileInputStream(file));
            //校验文件
            String originalFilename = multipartFile.getOriginalFilename();
            long size = multipartFile.getSize();

            final long TEN_MB = 10 * 1024 * 1024L;
            if (size > TEN_MB) {
                return Result.error(ResultCodeEnum.FILE_MORE);
            }

            csvStr = ExcelUtils.excelToCsv(multipartFile);
            //如果是空表
            if (csvStr == null) {
                return Result.error(ResultCodeEnum.NULL_EXCEL);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(csvStr);


        //todo 需要修改ChartTableRequest类
        List<String> goalNameType = chartTableRequest.getGoalNameType();

        //获取目标、图表名称、图表类型
        String chartGoal = goalNameType.get(0);
        String chartName = goalNameType.get(1);
        String chartType = goalNameType.get(2);

        System.out.println("分析目标" + chartGoal);
        System.out.println("图表名称" + chartName);
        System.out.println("图表类型" + chartType);

        // 校验
        if ((StringUtils.isNotBlank(chartName) && chartName.length() > 200)
                && !StringUtils.isBlank(chartGoal) && StringUtils.isBlank(chartType)) {
            throw new RuntimeException("格式有误");
        }

        //在校验后，进行限流
        Account currentUser = TokenUtils.getCurrentUser();
        //根据限流的粒度，每个用户之间一个限流器，且添加"createTableByAi_"，就会让每个方法之间不会冲突
        redisLimiterManager.doRateLimit("createTableByAi_" + currentUser.getId());

        // 无需Prompt，直接调用现有模型
        // 模型ID
        long biModelId = 1766830106405720065L;

        // 构造用户输入,设置预设信息
        StringBuilder userInput = new StringBuilder();

        userInput.append("分析需求:").append("\n");
        userInput.append(chartGoal).append("\n");
        userInput.append("图表类型:").append(chartType);
        // 拼接分析目标
        userInput.append("\n原始数据:\n").append(csvStr);

        String tableData = userInput + "";
        System.out.println(tableData);
        userInput.append("\n我还需要明确的数据分析结论，同时需要你可以对数据的结果作出一定程度的预测、越详细越好");


        // 调用AI
        String chartResult = yuApiUtils.doChat(biModelId, userInput.toString());
        System.out.println("============================");
        System.out.println(userInput);
        String[] splits = chartResult.split("【【【【【");
        if (splits.length < 3) {
            throw new CustomException(ResultCodeEnum.AI_GENERATE_FAIL);
        }
        // 去掉换行、空格等
        for (int i = 0; i < splits.length; i++) {
            System.out.println("splits[" + i + "]" + splits[i]);
        }
        //完整的splits()
        System.out.println("splits.length=" + splits.length);

        String genChart = splits[1].trim();
        String genResult = splits[2].trim();


        // 插入数据到数据库
        Chart newChart = new Chart();
        try {
            newChart.setGoal(chartGoal);
            newChart.setChartData(tableData);
            newChart.setName(chartName);
            newChart.setChartType(chartType);
            newChart.setGenChart(genChart);
            newChart.setGenResult(genResult);
            newChart.setUserId(currentUser.getId());
            String now = DateUtil.now();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date = null;
            date = sdf.parse(now);
            newChart.setCreateTime(date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        chartService.add(newChart);
        System.out.println(newChart);
        List<String> genStr = new ArrayList<>();

        // 返回到前端
        genStr.add(genChart);
        genStr.add(genResult);


        return Result.success(genStr);
    }

    /**
     * 新增
     */
    @PostMapping("/add")
    public Result add(@RequestBody Chart chart) {
        chartService.add(chart);
        return Result.success();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete/{id}")
    public Result deleteById(@PathVariable Integer id) {
        chartService.deleteById(id);
        return Result.success();
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/delete/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        chartService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    public Result updateById(@RequestBody Chart chart) {
        chartService.updateById(chart);
        return Result.success();
    }

    /**
     * 根据ID查询
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        Chart chart = chartService.selectById(id);
        return Result.success(chart);
    }

    /**
     * 查询所有
     */
    @GetMapping("/selectAll")
    public Result selectAll(Chart chart) {
        List<Chart> list = chartService.selectAll(chart);
        return Result.success(list);
    }

    /**
     * 分页查询
     */
    @GetMapping("/selectPage")
    public Result selectPage(Chart chart,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<Chart> page = chartService.selectPage(chart, pageNum, pageSize);
        System.out.println(page);
        return Result.success(page);
    }

}