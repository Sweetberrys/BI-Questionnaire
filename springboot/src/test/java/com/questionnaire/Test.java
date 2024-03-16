package com.questionnaire;

import com.questionnaire.utils.YuApiUtils;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class Test {

    @Resource
    private YuApiUtils yuApiUtils;

    @org.junit.jupiter.api.Test
    void doChat() {
        yuApiUtils.doChat(自己的模型ID,"" +
                "分析需求：分析网站用户的增长情况\n" +
                "原始数据：日期,用户数\n" +
                "1号,10\n" +
                "2号,20\n" +
                "3号,30\n");

    }
}
