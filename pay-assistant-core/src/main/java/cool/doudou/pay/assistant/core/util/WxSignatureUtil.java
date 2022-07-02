package cool.doudou.pay.assistant.core.util;

import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.Signature;
import java.util.Base64;
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
     * @param reqBody                 请求数据：POST｜PUT时传入对应值，其他传入空字符串
     * @return 授权字符串
     */
    public static String getAuthorization(String mchId, String certificateSerialNumber, String reqMethod, String reqAbsoluteUrl, String reqBody) {
        String schema = "WECHATPAY2-SHA256-RSA2048";
        long timestamp = System.currentTimeMillis() / 1000;
        String nonceStr = UUID.randomUUID().toString().replaceAll("-", "");

        // 签名值 signatureValue
        String signatureValue = computeSignatureValue(reqMethod, reqAbsoluteUrl, reqBody, timestamp, nonceStr);
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
