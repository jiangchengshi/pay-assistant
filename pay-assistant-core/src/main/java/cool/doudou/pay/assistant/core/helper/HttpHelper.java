package cool.doudou.pay.assistant.core.helper;

import com.alibaba.fastjson2.JSONObject;
import cool.doudou.pay.assistant.core.enums.ReqMethodEnum;
import cool.doudou.pay.assistant.core.signer.WxSigner;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

/**
 * HttpHelper
 *
 * @author jiangcs
 * @since 2022/06/30
 */
@Component
public class HttpHelper {
    private OkHttpClient okHttpClient;

    /**
     * get 请求 微信
     *
     * @param serverAddress           服务器地址
     * @param reqAbsoluteUrl          请求API地址
     * @param params                  Query参数
     * @param mchId                   商户ID
     * @param certificateSerialNumber 商户证书序列号
     * @return 结果
     */
    public String doGet4Wx(String serverAddress, String reqAbsoluteUrl, Map<String, Object> params, String mchId, String certificateSerialNumber) {
        String url = serverAddress + reqAbsoluteUrl;

        Request.Builder builder = new Request.Builder();
        // Header
        builder.addHeader("Content-Type", "application/json");
        builder.addHeader("Accept", "application/json");
        builder.addHeader("Authorization", WxSigner.getAuthorization(mchId, certificateSerialNumber, ReqMethodEnum.GET, reqAbsoluteUrl, params, ""));
        // Query
        StringBuilder sbParam = new StringBuilder();
        if (params != null && params.keySet().size() > 0) {
            for (String key : params.keySet()) {
                if (sbParam.length() <= 0) {
                    sbParam.append("?").append(key).append("=").append(params.get(key));
                } else {
                    sbParam.append("&").append(key).append("=").append(params.get(key));
                }
            }
        }
        url += sbParam;
        Request request = builder.url(url).build();

        System.out.println("===========HTTP START==========");
        System.out.println("url => GET[wx] " + url);
        System.out.println("headers => " + JSONObject.toJSONString(request.headers()));
        System.out.println("params => " + params);

        return execute(request);
    }

    /**
     * Post 请求 微信
     *
     * @param serverAddress           服务器地址
     * @param reqAbsoluteUrl          请求API地址
     * @param jsonBody                Body参数
     * @param mchId                   商户ID
     * @param certificateSerialNumber 商户证书序列号
     * @return 结果
     */
    public String doPost4Wx(String serverAddress, String reqAbsoluteUrl, String jsonBody, String mchId, String certificateSerialNumber) {
        String url = serverAddress + reqAbsoluteUrl;

        Request.Builder builder = new Request.Builder();
        // Header
        builder.addHeader("Authorization", WxSigner.getAuthorization(mchId, certificateSerialNumber, ReqMethodEnum.POST, reqAbsoluteUrl, null, jsonBody));
        // Body
        if (jsonBody == null) {
            jsonBody = "";
        }
        RequestBody requestBody = RequestBody.create(jsonBody, MediaType.parse("application/json; charset=utf-8"));
        Request request = builder.url(url).post(requestBody).build();

        System.out.println("===========HTTP START==========");
        System.out.println("url => POST[wx] " + url);
        System.out.println("headers => " + JSONObject.toJSONString(request.headers()));
        System.out.println("body => " + jsonBody);

        return execute(request);
    }

    /**
     * Post 请求 支付宝
     *
     * @param url            网关地址
     * @param publicParamMap 公共参数
     * @param bizParamMap    业务参数
     * @return 结果
     */
    public String doPost4Ali(String url, Map<String, String> publicParamMap, Map<String, String> bizParamMap) {
        Request.Builder builder = new Request.Builder();

        // Query
        StringBuilder sbPublicParam = new StringBuilder();
        if (publicParamMap != null && publicParamMap.keySet().size() > 0) {
            for (String key : publicParamMap.keySet()) {
                if (sbPublicParam.length() <= 0) {
                    sbPublicParam.append("?");
                } else {
                    sbPublicParam.append("&");
                }
                sbPublicParam.append(key).append("=").append(URLEncoder.encode(publicParamMap.get(key), StandardCharsets.UTF_8));
            }
        }
        url += sbPublicParam;

        // Body
        FormBody.Builder formBuilder = new FormBody.Builder();
        if (bizParamMap != null && bizParamMap.keySet().size() > 0) {
            for (String key : bizParamMap.keySet()) {
                formBuilder.add(key, bizParamMap.get(key));
            }
        }
        RequestBody requestBody = formBuilder.build();
        Request request = builder.url(url).post(requestBody).build();

        System.out.println("===========HTTP START==========");
        System.out.println("url => POST[ali] " + url);
        System.out.println("public-params => " + JSONObject.toJSONString(publicParamMap));
        System.out.println("biz-params => " + JSONObject.toJSONString(bizParamMap));

        return execute(request);
    }

    private String execute(Request request) {
        try (Response response = okHttpClient.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String result = Objects.requireNonNull(response.body()).string();
                System.out.println("execute success: " + result);
                System.out.println("===========HTTP END==========");
                return result;
            }
            System.err.println("execute fail: " + response);
            System.out.println("===========HTTP END==========");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Autowired
    public void setOkHttpClient(OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
    }
}
