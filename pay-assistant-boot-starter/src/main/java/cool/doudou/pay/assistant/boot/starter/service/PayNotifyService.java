package cool.doudou.pay.assistant.boot.starter.service;

import com.alibaba.fastjson2.JSONObject;
import cool.doudou.pay.assistant.boot.starter.util.RespUtil;
import cool.doudou.pay.assistant.core.enums.PayModeEnum;
import cool.doudou.pay.assistant.core.factory.ConcurrentMapFactory;
import cool.doudou.pay.assistant.core.memory.WxPayMem;
import cool.doudou.pay.assistant.core.properties.PayWxProperties;
import cool.doudou.pay.assistant.core.signer.AliSigner;
import cool.doudou.pay.assistant.core.signer.WxSigner;
import cool.doudou.pay.assistant.core.util.AesUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.cert.X509Certificate;
import java.util.Base64;
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

    /**
     * 微信支付通知
     *
     * @param jsonStr  通知参数
     * @param request  请求句柄
     * @param response 响应句柄
     */
    public void wxPay(String jsonStr, HttpServletRequest request, HttpServletResponse response) {
        try {
            String timestamp = request.getHeader("Wechatpay-Timestamp");
            String nonce = request.getHeader("Wechatpay-Nonce");
            String signature = request.getHeader("Wechatpay-Signature");
            String serial = request.getHeader("Wechatpay-Serial");
            X509Certificate x509Certificate = WxPayMem.certificateMap.get(serial);
            boolean verifyFlag = WxSigner.verify(x509Certificate, timestamp, nonce, jsonStr, signature);
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

            RespUtil.writeSuccess(response, "");
        } catch (Exception e) {
            e.printStackTrace();

            JSONObject resultObj = new JSONObject();
            resultObj.put("code", "FAIL");
            resultObj.put("message", e.getMessage());
            RespUtil.writeFail(response, resultObj.toJSONString());
        }
    }

    /**
     * 支付宝支付通知
     *
     * @param request  请求句柄
     * @param response 响应句柄
     */
    public void aliPay(HttpServletRequest request, HttpServletResponse response) {
        try {
            Map<String, String> paramMap = request.getParameterMap();
            // 除去 sign、sign_type 两个参数
            String signatureStr = new String(Base64.getDecoder().decode(paramMap.remove("sign")));
            paramMap.remove("sign_type");

            boolean verifyFlag = AliSigner.verify(paramMap, signatureStr);
            if (!verifyFlag) {
                throw new RuntimeException("签名验证失败");
            }

            ConcurrentMapFactory.get(PayModeEnum.ALI).accept(JSONObject.toJSONString(paramMap));

            RespUtil.writeSuccess(response, "success");
        } catch (Exception e) {
            e.printStackTrace();

            RespUtil.writeFail(response, "fail");
        }
    }

    @Autowired
    public void setPayWxProperties(PayWxProperties payWxProperties) {
        this.payWxProperties = payWxProperties;
    }
}
