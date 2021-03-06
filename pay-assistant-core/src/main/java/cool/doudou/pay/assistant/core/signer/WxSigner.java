package cool.doudou.pay.assistant.core.signer;

import cool.doudou.pay.assistant.core.enums.RestfulMethodEnum;
import cool.doudou.pay.assistant.core.memory.WxPayMem;
import cool.doudou.pay.assistant.core.util.RsaUtil;

import java.nio.charset.StandardCharsets;
import java.security.Signature;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

/**
 * 微信签名者
 *
 * @author jiangcs
 * @since 2022/07/01
 */
public class WxSigner {
    /**
     * 获取 授权信息
     *
     * @param mchId                  商户Id
     * @param privateKeySerialNumber 商户API密钥证书序列号
     * @param reqMethod              请求方法：GET｜POST｜PUT｜DELETE
     * @param reqAbsoluteUrl         请求地址
     * @param reqParam               请求参数
     * @param reqBody                请求Body数据：POST｜PUT时传入对应值，其他传入空字符串
     * @return 授权字符串
     */
    public static String getAuthorization(String mchId, String privateKeySerialNumber, RestfulMethodEnum reqMethod, String reqAbsoluteUrl, Map<String, Object> reqParam, String reqBody) {
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
                "serial_no=\"" + privateKeySerialNumber + "\"," +
                "nonce_str=\"" + nonceStr + "\"," +
                "timestamp=\"" + timestamp + "\"," +
                "signature=\"" + signatureValue + "\"";
        // Authorization: 认证类型 签名信息
        return schema + " " + sbSignatureInfo;
    }

    /**
     * 计算签名值
     *
     * @param reqMethod      请求方法
     * @param reqAbsoluteUrl 请求Url
     * @param reqBody        请求Body数据
     * @param timestamp      时间戳
     * @param nonceStr       随机字符串
     * @return 签名值
     */
    private static String computeSignatureValue(String reqMethod, String reqAbsoluteUrl, String reqBody, long timestamp, String nonceStr) {
        // 构造签名字符串
        String sbSignatureValue = reqMethod + "\n" +
                reqAbsoluteUrl + "\n" +
                timestamp + "\n" +
                nonceStr + "\n" +
                reqBody + "\n";

        // 加密
        byte[] encryptArr = RsaUtil.encrypt(sbSignatureValue, WxPayMem.privateKey);

        // Base64
        return Base64.getEncoder().encodeToString(encryptArr);
    }

    /**
     * 证书验证
     *
     * @param certificate  平台证书
     * @param timestamp    时间戳
     * @param nonceStr     随机字符串
     * @param jsonStr      请求Body数据
     * @param signatureStr 签名字符串
     * @return true-验证通过；false-验证失败
     */
    public static boolean verify(X509Certificate certificate, String timestamp, String nonceStr, String jsonStr, String signatureStr) {
        try {
            // 构造签名字符串
            String sbSignatureValue = timestamp + "\n" +
                    nonceStr + "\n" +
                    jsonStr + "\n";

            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initVerify(certificate);
            signature.update(sbSignatureValue.getBytes(StandardCharsets.UTF_8));
            return signature.verify(Base64.getDecoder().decode(signatureStr));
        } catch (Exception e) {
            throw new RuntimeException("证书验证异常", e);
        }
    }
}
