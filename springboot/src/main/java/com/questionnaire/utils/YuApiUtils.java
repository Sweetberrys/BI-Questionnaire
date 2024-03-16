package com.questionnaire.utils;

import com.yupi.yucongming.dev.client.YuCongMingClient;
import com.yupi.yucongming.dev.common.BaseResponse;
import com.yupi.yucongming.dev.model.DevChatRequest;
import com.yupi.yucongming.dev.model.DevChatResponse;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class YuApiUtils {
    @Resource
    private YuCongMingClient yuCongMingClient;

    public String doChat(Long biModelId,String message){
        DevChatRequest devChatRequest = new DevChatRequest();
        devChatRequest.setModelId(biModelId); //模型参数
        devChatRequest.setMessage(message);

        BaseResponse<DevChatResponse> baseResponse = yuCongMingClient.doChat(devChatRequest);
        System.out.println(baseResponse);
        return baseResponse.getData().getContent();
    }


}
