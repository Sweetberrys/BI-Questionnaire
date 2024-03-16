package com.questionnaire.utils;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
public class ExcelUtils {
    public static String excelToCsv(MultipartFile multipartFile){
        //这里是最后一步，将转为CSV格式的数据进行拼接
        StringBuilder stringBuilder = new StringBuilder();
        //利用Spring下面的方法获取Resources下面的文件
        try {
            //再利用EasyExcel将文件读为字符串
            List<LinkedHashMap<Integer,String>> list = EasyExcel.read(multipartFile.getInputStream())
                    .excelType(ExcelTypeEnum.XLSX)
                    .sheet()
                    .headRowNumber(0)
                    .doReadSync();
            //如果数据为空
            if (CollUtil.isEmpty(list)){
                return "";
            }
            //过滤掉读取数据的时候表格删除不干净有null的表格数据
            //解决思路：读取表头数据过滤
            LinkedHashMap<Integer, String> heardMap = list.get(0);
            //此时就是过滤后的表头数据
            List<String> heardList = heardMap.values().stream().filter(StringUtils::isNotBlank).collect(Collectors.toList());
            log.info("表头数据：{}",heardList);
            //将表头添加进来，并且换行
            stringBuilder.append( StringUtils.join(heardList, ",")).append("\n");
            //读取表格里面的数据,除去表头从第一项开始
            for (int i = 1; i < list.size(); i++) {
                LinkedHashMap<Integer, String> dataMap = list.get(i);
                //todo 第一条list的时候将表头的数据放在建表语句中也就是列，别的数据都当作insert语句插入到表中
                if (i == 1){
                    //这里就要利用时间戳进行创建表语句
                }
                List<String> dataList = dataMap.values().stream().filter(StringUtils::isNotBlank).collect(Collectors.toList());
                log.info("表数据：{}",dataList);
                //将表格中的数据一行一行的添加进来，并且换行
                stringBuilder.append( StringUtils.join(dataList, ",")).append("\n");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return stringBuilder.toString();
    }

}
