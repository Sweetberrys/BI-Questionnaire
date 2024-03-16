package com.questionnaire.controller;


import cn.hutool.core.io.FileUtil;
import com.questionnaire.common.Result;
import com.questionnaire.common.enums.ResultCodeEnum;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;

import java.util.List;
import java.util.UUID;


@RestController
@CrossOrigin
@RequestMapping("/fileOperation")
public class FileOperationController {

    //文件上传路径
    @Value("${download.filePath}")
    private String filePath;

    /**
     * 文件上传
     *
     * @param fileName
     * @return
     */
    @PostMapping("/upload")
    public Result uploadFile(@RequestParam("file") MultipartFile fileName) {
        try {
            // 关联文件
            File file = new File(filePath);
            if (!file.exists()) {
                file.mkdirs();
            }
            //校验文件后缀
            String suffix = FileUtil.getSuffix(fileName.getOriginalFilename());
            //图片
//            final List<String> validFileSuffixList= Arrays.asList("png","jpg","svg","webp","jpeg");
            //文件
            final List<String> validFileSuffixList= Arrays.asList("xls","xlsx");
            if(!validFileSuffixList.contains(suffix)){
                return Result.error(ResultCodeEnum.FILE_NAME_NO);
            }
            //校验文件名长度
            if(fileName.getOriginalFilename().length() >100){
                return Result.error(ResultCodeEnum.FILE_NAME_MORE);
            }
            String newFileName = generateNewFileName(fileName.getOriginalFilename());
            FileOutputStream writer = new FileOutputStream(new File(file, newFileName));
            writer.write(fileName.getBytes());
            writer.flush();
            writer.close();
            return Result.success(newFileName);
        } catch (Exception e) {
            return Result.error(ResultCodeEnum.DATA_IMPORT_ERROR);
        }
    }

    /**
     *  生成新的文件名
     */
    private String generateNewFileName(String originalFileName) {
        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String newFileName = UUID.randomUUID().toString() +"_"+originalFileName;
        return newFileName;
    }

}
