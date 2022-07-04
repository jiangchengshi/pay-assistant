package cool.doudou.pay.assistant.core.helper;

import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
     * get 请求
     *
     * @param url     地址
     * @param params  Url参数
     * @param headers Header参数
     * @return 结果
     */
    public String doGet(String url, Map<String, Object> params, Map<String, String> headers) {
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

        Request.Builder builder = new Request.Builder();
        builder.addHeader("Content-Type", "application/json");
        builder.addHeader("Accept", "application/json");
        if (headers != null && headers.keySet().size() > 0) {
            headers.keySet().forEach((key) -> builder.addHeader(key, headers.get(key)));
        }

        Request request = builder.url(url).build();

        System.out.println("url => GET " + url);
        System.out.println("authorization => " + request.headers("Authorization"));
        System.out.println("params => " + params);

        return execute(request);
    }

    /**
     * Post 请求
     *
     * @param url      地址
     * @param params   Url参数
     * @param headers  Header参数
     * @param jsonBody Json数据体
     * @return 结果
     */
    public String doPostJson(String url, Map<String, Object> params, Map<String, String> headers, String jsonBody) {
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

        Request.Builder builder = new Request.Builder();
        builder.addHeader("Content-Type", "application/json");
        builder.addHeader("Accept", "application/json");
        if (headers != null && headers.keySet().size() > 0) {
            headers.keySet().forEach((key) -> builder.addHeader(key, headers.get(key)));
        }
        if (jsonBody == null) {
            jsonBody = "";
        }
        RequestBody requestBody = RequestBody.create(jsonBody, MediaType.parse("application/json; charset=utf-8"));
        Request request = builder.url(url).post(requestBody).build();

        System.out.println("url => POST " + url);
        System.out.println("authorization => " + request.headers("Authorization"));
        System.out.println("params => " + params);
        System.out.println("body => " + jsonBody);

        return execute(request);
    }

    private String execute(Request request) {
        try (Response response = okHttpClient.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String result = Objects.requireNonNull(response.body()).string();
                System.out.println("execute success: " + result);
                return result;
            }
            System.err.println("execute fail: " + response);
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
