package cool.doudou.pay.assistant.core.helper;

import cool.doudou.pay.assistant.core.enums.RestfulMethodEnum;
import cool.doudou.pay.assistant.core.signer.AliSigner;
import cool.doudou.pay.assistant.core.signer.WxSigner;
import cool.doudou.pay.assistant.core.util.ComUtil;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;

import java.io.ByteArrayInputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * HttpHelper
 *
 * @author jiangcs
 * @since 2022/06/30
 */
public class HttpHelper {
    private OkHttpClient okHttpClient;

    /**
     * get 请求 微信
     *
     * @param serverAddress          服务器地址
     * @param reqAbsoluteUrl         请求API地址
     * @param params                 Query参数
     * @param mchId                  商户ID
     * @param privateKeySerialNumber 商户密钥证书序列号
     * @return 结果
     */
    public String doGet4Wx(String serverAddress, String reqAbsoluteUrl, Map<String, Object> params, String mchId, String privateKeySerialNumber) {
        String url = serverAddress + reqAbsoluteUrl;

        Request.Builder builder = new Request.Builder();
        // Header
        builder.addHeader("Content-Type", "application/json");
        builder.addHeader("Accept", "application/json");
        builder.addHeader("Authorization", WxSigner.getAuthorization(mchId, privateKeySerialNumber, RestfulMethodEnum.GET, reqAbsoluteUrl, params, ""));
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
        System.out.println("params => " + params);

        return execute(request);
    }

    /**
     * getInputStream 请求 微信
     *
     * @param serverAddress          服务器地址
     * @param reqAbsoluteUrl         请求API地址
     * @param params                 Query参数
     * @param mchId                  商户ID
     * @param privateKeySerialNumber 商户密钥证书序列号
     * @return 字节数组输入流
     */
    public ByteArrayInputStream doGetInputStream4Wx(String serverAddress, String reqAbsoluteUrl, Map<String, Object> params, String mchId, String privateKeySerialNumber) {
        String url = serverAddress + reqAbsoluteUrl;

        Request.Builder builder = new Request.Builder();
        // Header
        builder.addHeader("Content-Type", "application/json");
        builder.addHeader("Accept", "application/json");
        builder.addHeader("Authorization", WxSigner.getAuthorization(mchId, privateKeySerialNumber, RestfulMethodEnum.GET, reqAbsoluteUrl, params, ""));
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
        System.out.println("params => " + params);

        return executeInputStream(request);
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
        builder.addHeader("Authorization", WxSigner.getAuthorization(mchId, certificateSerialNumber, RestfulMethodEnum.POST, reqAbsoluteUrl, null, jsonBody));
        // Body
        if (jsonBody == null) {
            jsonBody = "";
        }
        RequestBody requestBody = RequestBody.create(jsonBody, MediaType.parse("application/json; charset=utf-8"));
        Request request = builder.url(url).post(requestBody).build();

        System.out.println("===========HTTP START==========");
        System.out.println("url => POST[wx] " + url);
        System.out.println("body => " + jsonBody);

        return execute(request);
    }

    /**
     * Post 请求 支付宝
     *
     * @param url                 网关地址
     * @param method              接口名称
     * @param params              业务参数
     * @param appId               应用ID
     * @param notifyServerAddress 异步通知服务地址
     * @return 结果
     */
    public String doPost4Ali(String url, String method, Map<String, String> params, String appId, String notifyServerAddress) {
        // 公共参数
        Map<String, String> publicParamMap = new HashMap<>(8);
        publicParamMap.put("app_id", appId);
        publicParamMap.put("method", method);
        publicParamMap.put("charset", "utf-8");
        publicParamMap.put("sign_type", "RSA2");
        publicParamMap.put("timestamp", ComUtil.formatDate(new Date()));
        publicParamMap.put("version", "1.0");
        publicParamMap.put("notify_url", notifyServerAddress + "/pay-notify/ali");
        // 签名
        publicParamMap.put("sign", AliSigner.getSign(publicParamMap, params));

        Request.Builder builder = new Request.Builder();
        // Query
        StringBuilder sbPublicParam = new StringBuilder();
        for (String key : publicParamMap.keySet()) {
            if (sbPublicParam.length() <= 0) {
                sbPublicParam.append("?");
            } else {
                sbPublicParam.append("&");
            }
            sbPublicParam.append(key).append("=").append(URLEncoder.encode(publicParamMap.get(key), StandardCharsets.UTF_8));
        }
        url += sbPublicParam;
        // Body
        FormBody.Builder formBuilder = new FormBody.Builder();
        if (params != null && params.keySet().size() > 0) {
            for (String key : params.keySet()) {
                formBuilder.add(key, params.get(key));
            }
        }
        RequestBody requestBody = formBuilder.build();
        Request request = builder.url(url).post(requestBody).build();

        System.out.println("===========HTTP START==========");
        System.out.println("url => POST[ali] " + url);
        System.out.println("method => " + method);
        System.out.println("params => " + params);

        return execute(request);
    }

    private String execute(Request request) {
        try (Response response = okHttpClient.newCall(request).execute()) {
            if (response.isSuccessful()) {
                ResponseBody responseBody = response.body();
                if (!ObjectUtils.isEmpty(responseBody)) {
                    String result = responseBody.string();
                    System.out.println("execute success: " + result);
                    System.out.println("===========HTTP END==========");
                    return result;
                }
            }
            System.err.println("execute fail: " + response);
            System.out.println("===========HTTP END==========");
        } catch (Exception e) {
            System.err.println("execute exception: ");
            e.printStackTrace();
            System.out.println("===========HTTP END==========");
        }
        return null;
    }

    private ByteArrayInputStream executeInputStream(Request request) {
        try (Response response = okHttpClient.newCall(request).execute()) {
            if (response.isSuccessful()) {
                ResponseBody responseBody = response.body();
                if (!ObjectUtils.isEmpty(responseBody)) {
                    System.out.println("execute success: " + responseBody.contentType() + " => " + responseBody.contentLength());
                    System.out.println("===========HTTP END==========");
                    return new ByteArrayInputStream(responseBody.bytes());
                }
            }
            System.err.println("execute fail: " + response);
            System.out.println("===========HTTP END==========");
        } catch (Exception e) {
            System.err.println("execute exception: ");
            e.printStackTrace();
            System.out.println("===========HTTP END==========");
        }
        return null;
    }

    @Autowired
    public void setOkHttpClient(OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
    }
}
