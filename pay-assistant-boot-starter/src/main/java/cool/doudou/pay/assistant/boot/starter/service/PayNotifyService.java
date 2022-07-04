package cool.doudou.pay.assistant.boot.starter.service;

import com.alibaba.fastjson2.JSONObject;
import cool.doudou.pay.assistant.core.enums.PayModeEnum;
import cool.doudou.pay.assistant.core.factory.ConcurrentMapFactory;
import cool.doudou.pay.assistant.core.memory.WxPayMem;
import cool.doudou.pay.assistant.core.properties.PayWxProperties;
import cool.doudou.pay.assistant.core.util.AesUtil;
import cool.doudou.pay.assistant.core.util.WxSignatureUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

/**
 * PayNotifyService
 *
 * @author jiangcs
 * @since 2022/07/04
 */
@Service
public class PayNotifyService {
    private PayWxProperties payWxProperties;

    public Map<String, Object> wxPay(HttpServletRequest request, String jsonStr) {
        Map<String, Object> map = new HashMap<>(2);

        try {
            String timestamp = request.getHeader("Wechatpay-Timestamp");
            String nonce = request.getHeader("Wechatpay-Nonce");
            String signature = request.getHeader("Wechatpay-Signature");
            String serial = request.getHeader("Wechatpay-Serial");
            X509Certificate x509Certificate = WxPayMem.certificateMap.get(serial);
            boolean verifyFlag = WxSignatureUtil.certificateVerify(x509Certificate, timestamp, nonce, jsonStr, signature);
            if (!verifyFlag) {
                throw new RuntimeException("签名验证失败");
            }

            JSONObject bodyObj = JSONObject.parseObject(jsonStr);
            JSONObject resourceObj = bodyObj.getJSONObject("resource");
            String ciphertextStr = resourceObj.getString("ciphertext");
            String associatedDataStr = resourceObj.getString("associated_data");
            String nonceStr = resourceObj.getString("nonce");
            String decryptStr = AesUtil.decrypt(payWxProperties.getApiKeyV3(), associatedDataStr, nonceStr, ciphertextStr);

            ConcurrentMapFactory.get(PayModeEnum.WX).accept(decryptStr);

            map.put("code", "SUCCESS");
            map.put("message", "ok");
        } catch (Exception e) {
            map.put("code", "FAIL");
            map.put("message", e.getMessage());
        }
        return map;
    }

    public void aliPay(String jsonStr) {
        ConcurrentMapFactory.get(PayModeEnum.ALI).accept(jsonStr);
    }

    @Autowired
    public void setPayWxProperties(PayWxProperties payWxProperties) {
        this.payWxProperties = payWxProperties;
    }
}
