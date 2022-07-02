package cool.doudou.pay.assistant.core.api;

import com.alibaba.fastjson2.JSONObject;
import cool.doudou.pay.assistant.core.entity.PlaceOrderParam;
import cool.doudou.pay.assistant.core.helper.HttpHelper;
import cool.doudou.pay.assistant.core.properties.PayProperties;
import cool.doudou.pay.assistant.core.properties.PayWxProperties;
import cool.doudou.pay.assistant.core.util.WxSignatureUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

    /**
     * 下单
     *
     * @param placeOrderParam
     * @return
     */
    public boolean place(PlaceOrderParam placeOrderParam) {
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

        Map<String, String> headers = new HashMap<>(1);
        headers.put("Authorization", WxSignatureUtil.getAuthorization(payWxProperties.getMchId(), payWxProperties.getCertificateSerialNumber(), "POST", "/v3/pay/transactions/jsapi", jsonObject.toString()));

        String result = httpHelper.doPostJson("https://api.mch.weixin.qq.com/v3/pay/transactions/jsapi", null, headers, jsonObject.toJSONString());
        return result != null;
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
