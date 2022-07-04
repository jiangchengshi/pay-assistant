package cool.doudou.pay.assistant.core.util;

import cool.doudou.pay.assistant.core.enums.ReqMethodEnum;

import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.Signature;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

/**
 * WxSignatureUtil
 *
 * @author jiangcs
 * @since 2022/07/01
 */
public class WxSignatureUtil {
    private static PrivateKey privateKey;

    /**
     * 获取 授权信息
     *
     * @param mchId                   商户Id
     * @param certificateSerialNumber 商户API证书序列号
     * @param reqMethod               请求方法：GET｜POST｜PUT｜DELETE
     * @param reqAbsoluteUrl          请求地址
     * @param reqParam                请求参数
     * @param reqBody                 请求Body数据：POST｜PUT时传入对应值，其他传入空字符串
     * @return 授权字符串
     */
    public static String getAuthorization(String mchId, String certificateSerialNumber, ReqMethodEnum reqMethod, String reqAbsoluteUrl, Map<String, Object> reqParam, String reqBody) {
        String schema = "WECHATPAY2-SHA256-RSA2048";
        long timestamp = System.currentTimeMillis() / 1000;
        String nonceStr = UUID.randomUUID().toString().replaceAll("-", "");

        StringBuilder sbParam = new StringBuilder();
        if (reqParam != null) {
            for (String key : reqParam.keySet()) {
                if (sbParam.length() <= 0) {
                    sbParam.append("?");
                } else {
                    sbParam.append("&");
                }
                sbParam.append(key).append("=").append(reqParam.get(key));
            }
        }
        reqAbsoluteUrl += sbParam;

        // 签名值 signatureValue
        String signatureValue = computeSignatureValue(reqMethod.name(), reqAbsoluteUrl, reqBody, timestamp, nonceStr);
        // 签名信息 signatureInfo
        String sbSignatureInfo = "mchid=\"" + mchId + "\"," +
                "serial_no=\"" + certificateSerialNumber + "\"," +
                "nonce_str=\"" + nonceStr + "\"," +
                "timestamp=\"" + timestamp + "\"," +
                "signature=\"" + signatureValue + "\"";
        // Authorization: 认证类型 签名信息
        return schema + " " + sbSignatureInfo;
    }

    private static String computeSignatureValue(String reqMethod, String reqAbsoluteUrl, String reqBody, long timestamp, String nonceStr) {
        // 构造签名字符串
        StringBuilder sbSignatureValue = new StringBuilder();
        sbSignatureValue.append(reqMethod).append("\n")
                .append(reqAbsoluteUrl).append("\n")
                // 时间戳
                .append(timestamp).append("\n")
                // 随机串
                .append(nonceStr).append("\n")
                .append(reqBody).append("\n");

        // 签名值
        try {
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(privateKey);
            signature.update(sbSignatureValue.toString().getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(signature.sign());
        } catch (Exception e) {
            throw new RuntimeException("签名值计算异常", e);
        }
    }

    public static void setPrivateKey(PrivateKey privateKey) {
        WxSignatureUtil.privateKey = privateKey;
    }
}
