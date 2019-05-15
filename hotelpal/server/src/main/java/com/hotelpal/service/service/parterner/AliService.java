package com.hotelpal.service.service.parterner;

import com.alibaba.fastjson.JSON;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.hotelpal.service.common.exception.ServiceException;

import java.util.Map;

/**
 * @author Redx
 * @since 2019/5/14 22:30
 */
public class AliService {
    private AliService(){}
    //LTAILciQ7Jrf0Vwm
    //3dNDY8nBeJaIQJb7saRwuFrkdvE7Vm

    @SuppressWarnings("unchecked")
    public static void sendSms(String phone, String code) {
        DefaultProfile profile = DefaultProfile.getProfile("default", "LTAILciQ7Jrf0Vwm", "3dNDY8nBeJaIQJb7saRwuFrkdvE7Vm");
        IAcsClient client = new DefaultAcsClient(profile);

        CommonRequest request = new CommonRequest();
        request.setMethod(MethodType.POST);
        request.setDomain("dysmsapi.aliyuncs.com");
        request.setVersion("2017-05-25");
        request.setAction("SendSms");
        request.putQueryParameter("PhoneNumbers", phone);
        request.putQueryParameter("SignName", "酒店邦成长营");
        request.putQueryParameter("TemplateCode", "SMS_165410504");
        request.putQueryParameter("TemplateParam", "{\"code\":\"" + code + "\"}");
        try {
            CommonResponse response = client.getCommonResponse(request);
            if (response != null && !response.getHttpResponse().isSuccess()) {
                throw new ServiceException((String) JSON.parseObject(response.getData(), Map.class).get("Message"));
            }
        } catch (ClientException e) {
            throw new ServiceException(e.getErrMsg());
        }
    }

}
