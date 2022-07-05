package cool.doudou.pay.assistant.core.api;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import cool.doudou.pay.assistant.core.entity.PlaceOrderParam;
import cool.doudou.pay.assistant.core.entity.RefundParam;
import cool.doudou.pay.assistant.core.helper.HttpHelper;
import cool.doudou.pay.assistant.core.memory.WxPayMem;
import cool.doudou.pay.assistant.core.properties.PayProperties;
import cool.doudou.pay.assistant.core.properties.PayWxProperties;
import cool.doudou.pay.assistant.core.util.AesUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

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

    private final String serverAddress = "https://api.mch.weixin.qq.com";

    /**
     * 加载平台证书
     */
    public void loadCertificate() {
        String reqAbsoluteUrl = "/v3/certificates";

        String result = httpHelper.doGet4Wx(serverAddress, reqAbsoluteUrl, null, payWxProperties.getMchId(), payWxProperties.getCertificateSerialNumber());
        if (!ObjectUtils.isEmpty(result)) {
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

        if (!ObjectUtils.isEmpty(placeOrderParam.getTimeExpire()) && (!placeOrderParam.getTimeExpire().contains("T") || !placeOrderParam.getTimeExpire().contains("+"))) {
            throw new RuntimeException("交易结束时间格式错误");
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("appid", payWxProperties.getAppId());
        jsonObject.put("mchid", payWxProperties.getMchId());
        jsonObject.put("out_trade_no", placeOrderParam.getOutTradeNo());
        jsonObject.put("description", placeOrderParam.getDescription());
        if (!ObjectUtils.isEmpty(placeOrderParam.getTimeExpire())) {
            jsonObject.put("time_expire", placeOrderParam.getTimeExpire());
        }
        if (!ObjectUtils.isEmpty(placeOrderParam.getAttach())) {
            jsonObject.put("attach", placeOrderParam.getAttach());
        }
        jsonObject.put("notify_url", payProperties.getNotifyServer() + "/pay-notify/wx");
        JSONObject jsonAmount = new JSONObject();
        jsonAmount.put("total", placeOrderParam.getMoney());
        jsonAmount.put("currency", "CNY");
        jsonObject.put("amount", jsonAmount);
        JSONObject jsonPayer = new JSONObject();
        jsonPayer.put("openid", placeOrderParam.getUid());
        jsonObject.put("payer", jsonPayer);

        String reqAbsoluteUrl = "/v3/pay/transactions/jsapi";

        return httpHelper.doPost4Wx(serverAddress, reqAbsoluteUrl, jsonObject.toJSONString(), payWxProperties.getMchId(), payWxProperties.getCertificateSerialNumber());
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

        return httpHelper.doGet4Wx(serverAddress, reqAbsoluteUrl, params, payWxProperties.getMchId(), payWxProperties.getCertificateSerialNumber());
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

        return httpHelper.doPost4Wx(serverAddress, reqAbsoluteUrl, jsonObject.toJSONString(), payWxProperties.getMchId(), payWxProperties.getCertificateSerialNumber());
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

        return httpHelper.doPost4Wx(serverAddress, reqAbsoluteUrl, jsonObject.toJSONString(), payWxProperties.getMchId(), payWxProperties.getCertificateSerialNumber());
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

        return httpHelper.doGet4Wx(serverAddress, reqAbsoluteUrl, params, payWxProperties.getMchId(), payWxProperties.getCertificateSerialNumber());
    }

    /**
     * 下载账单
     *
     * @param billUrl 账单地址
     * @return 账单信息
     */
    public String downloadBill(String billUrl) {
        String[] billUrlArr = billUrl.split("\\?");
        String reqAbsoluteUrl = billUrlArr[0].replace(serverAddress, "");

        String[] paramArr = billUrlArr[1].split("=");
        Map<String, Object> params = new HashMap<>(1);
        params.put(paramArr[0], paramArr[1]);

        return httpHelper.doGet4Wx(serverAddress, reqAbsoluteUrl, params, payWxProperties.getMchId(), payWxProperties.getCertificateSerialNumber());
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
