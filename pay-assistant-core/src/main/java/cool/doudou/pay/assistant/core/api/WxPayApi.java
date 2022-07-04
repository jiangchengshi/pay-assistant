package cool.doudou.pay.assistant.core.api;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import cool.doudou.pay.assistant.core.entity.PlaceOrderParam;
import cool.doudou.pay.assistant.core.entity.RefundParam;
import cool.doudou.pay.assistant.core.enums.ReqMethodEnum;
import cool.doudou.pay.assistant.core.helper.HttpHelper;
import cool.doudou.pay.assistant.core.memory.WxPayMem;
import cool.doudou.pay.assistant.core.properties.PayProperties;
import cool.doudou.pay.assistant.core.properties.PayWxProperties;
import cool.doudou.pay.assistant.core.util.AesUtil;
import cool.doudou.pay.assistant.core.util.WxSignatureUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

/**
 * WxPayApi
 *
 * @author jiangcs
 * @since 2022/06/23
 */
@Component
public class WxPayApi {
    private PayProperties payProperties;
    private PayWxProperties payWxProperties;
    private HttpHelper httpHelper;

    private final String wxPayServer = "https://api.mch.weixin.qq.com";

    /**
     * 加载平台证书
     */
    public void loadCertificate() {
        String reqAbsoluteUrl = "/v3/certificates";

        Map<String, String> headers = new HashMap<>(1);
        headers.put("Authorization", WxSignatureUtil.getAuthorization(payWxProperties.getMchId(), payWxProperties.getCertificateSerialNumber(), ReqMethodEnum.GET, reqAbsoluteUrl, null, ""));

        String result = httpHelper.doGet(wxPayServer + reqAbsoluteUrl, null, headers);
        JSONObject resultObj = JSONObject.parseObject(result);
        JSONArray dataArr = resultObj.getJSONArray("data");
        for (int i = 0, len = dataArr.size(); i < len; i++) {
            try {
                JSONObject dataObj = dataArr.getJSONObject(i);
                // 证书序列号
                String certificateSerialNumber = dataObj.getString("serial_no");
                // 加密内容
                JSONObject encryptCertificateObj = dataObj.getJSONObject("encrypt_certificate");
                String nonceStr = encryptCertificateObj.getString("nonce");
                String ciphertextStr = encryptCertificateObj.getString("ciphertext");
                // 证书内容解密
                String associatedDataStr = encryptCertificateObj.getString("associated_data");
                String decryptStr = AesUtil.decrypt(payWxProperties.getApiKeyV3(), associatedDataStr, nonceStr, ciphertextStr);
                // 证书内容转成证书对象
                CertificateFactory cf = CertificateFactory.getInstance("X509");
                X509Certificate x509Certificate = (X509Certificate) cf.generateCertificate(
                        new ByteArrayInputStream(decryptStr.getBytes(StandardCharsets.UTF_8))
                );
                WxPayMem.certificateMap.put(certificateSerialNumber, x509Certificate);
            } catch (Exception e) {
                System.err.println("平台证书加载异常: " + e.getMessage());
            }
        }
    }

    /**
     * 下单
     *
     * @param placeOrderParam 下单参数
     * @return 预支付交易会话标识
     */
    public String place(PlaceOrderParam placeOrderParam) {
        if (placeOrderParam.getOutTradeNo() != null && placeOrderParam.getOutTradeNo().isBlank()) {
            throw new RuntimeException("商户订单号不能为空");
        }
        if (placeOrderParam.getOutTradeNo().length() < 6) {
            throw new RuntimeException("商户订单号长度不能少于6位");
        }
        if (placeOrderParam.getOutTradeNo().length() > 32) {
            throw new RuntimeException("商户订单号长度不能多于32位");
        }

        if (placeOrderParam.getTimeExpire() != null && !placeOrderParam.getTimeExpire().isBlank() && (!placeOrderParam.getTimeExpire().contains("T") || !placeOrderParam.getTimeExpire().contains("+"))) {
            throw new RuntimeException("交易结束时间格式错误");
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("appid", payWxProperties.getAppId());
        jsonObject.put("mchid", payWxProperties.getMchId());
        jsonObject.put("out_trade_no", placeOrderParam.getOutTradeNo());
        jsonObject.put("description", placeOrderParam.getDescription());
        jsonObject.put("time_expire", placeOrderParam.getTimeExpire());
        jsonObject.put("attach", placeOrderParam.getAttach());
        jsonObject.put("notify_url", payProperties.getNotifyServer() + "/pay-notify/wx");
        JSONObject jsonAmount = new JSONObject();
        jsonAmount.put("total", placeOrderParam.getMoney());
        jsonAmount.put("currency", "CNY");
        jsonObject.put("amount", jsonAmount);
        JSONObject jsonPayer = new JSONObject();
        jsonPayer.put("openid", placeOrderParam.getOpenId());
        jsonObject.put("payer", jsonPayer);

        String reqAbsoluteUrl = "/v3/pay/transactions/jsapi";

        Map<String, String> headers = new HashMap<>(1);
        headers.put("Authorization", WxSignatureUtil.getAuthorization(payWxProperties.getMchId(), payWxProperties.getCertificateSerialNumber(), ReqMethodEnum.POST, reqAbsoluteUrl, null, jsonObject.toString()));

        return httpHelper.doPostJson(wxPayServer + reqAbsoluteUrl, null, headers, jsonObject.toJSONString());
    }

    /**
     * 查询
     *
     * @param outTradeNo 商户订单号
     * @return 订单信息
     */
    public String query(String outTradeNo) {
        Map<String, Object> params = new HashMap<>(1);
        params.put("mchid", payWxProperties.getMchId());

        String reqAbsoluteUrl = "/v3/pay/transactions/out-trade-no/" + outTradeNo;

        Map<String, String> headers = new HashMap<>(1);
        headers.put("Authorization", WxSignatureUtil.getAuthorization(payWxProperties.getMchId(), payWxProperties.getCertificateSerialNumber(), ReqMethodEnum.GET, reqAbsoluteUrl, params, ""));

        return httpHelper.doGet(wxPayServer + reqAbsoluteUrl, params, headers);
    }

    /**
     * 关闭
     *
     * @param outTradeNo 商户订单号
     * @return 无内容
     */
    public String close(String outTradeNo) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("mchid", payWxProperties.getMchId());

        String reqAbsoluteUrl = "/v3/pay/transactions/out-trade-no/" + outTradeNo + "/close";

        Map<String, String> headers = new HashMap<>(1);
        headers.put("Authorization", WxSignatureUtil.getAuthorization(payWxProperties.getMchId(), payWxProperties.getCertificateSerialNumber(), ReqMethodEnum.POST, reqAbsoluteUrl, null, jsonObject.toString()));

        return httpHelper.doPostJson(wxPayServer + reqAbsoluteUrl, null, headers, jsonObject.toString());
    }

    /**
     * 退款
     *
     * @param refundParam 退款参数
     * @return 退款信息
     */
    public String refund(RefundParam refundParam) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("out_trade_no", refundParam.getOutTradeNo());
        jsonObject.put("out_refund_no", refundParam.getOutRefundNo());
        jsonObject.put("reason", refundParam.getReason());
        JSONObject jsonAmount = new JSONObject();
        jsonAmount.put("total", refundParam.getMoney());
        jsonAmount.put("currency", "CNY");
        jsonAmount.put("refund", refundParam.getRefundMoney());
        jsonObject.put("amount", jsonAmount);

        String reqAbsoluteUrl = "/v3/refund/domestic/refunds";

        Map<String, String> headers = new HashMap<>(1);
        headers.put("Authorization", WxSignatureUtil.getAuthorization(payWxProperties.getMchId(), payWxProperties.getCertificateSerialNumber(), ReqMethodEnum.POST, reqAbsoluteUrl, null, jsonObject.toString()));

        return httpHelper.doPostJson(wxPayServer + reqAbsoluteUrl, null, headers, jsonObject.toJSONString());
    }

    /**
     * 交易账单
     *
     * @param billDate 账单日期
     * @return 账单下载地址
     */
    public String tradeBill(String billDate) {
        Map<String, Object> params = new HashMap<>(1);
        params.put("bill_date", billDate);

        String reqAbsoluteUrl = "/v3/bill/tradebill";

        Map<String, String> headers = new HashMap<>(1);
        headers.put("Authorization", WxSignatureUtil.getAuthorization(payWxProperties.getMchId(), payWxProperties.getCertificateSerialNumber(), ReqMethodEnum.GET, reqAbsoluteUrl, params, ""));

        return httpHelper.doGet(wxPayServer + reqAbsoluteUrl, params, headers);
    }

    /**
     * 下载账单
     *
     * @param billUrl 账单地址
     * @return 账单信息
     */
    public String downloadBill(String billUrl) {
        Map<String, String> headers = new HashMap<>(1);
        headers.put("Authorization", WxSignatureUtil.getAuthorization(payWxProperties.getMchId(), payWxProperties.getCertificateSerialNumber(), ReqMethodEnum.GET, billUrl, null, ""));

        return httpHelper.doGet(billUrl, null, headers);
    }

    @Autowired
    public void setPayProperties(PayProperties payProperties) {
        this.payProperties = payProperties;
    }

    @Autowired
    public void setPayWxProperties(PayWxProperties payWxProperties) {
        this.payWxProperties = payWxProperties;
    }

    @Autowired
    public void setHttpHelper(HttpHelper httpHelper) {
        this.httpHelper = httpHelper;
    }
}
