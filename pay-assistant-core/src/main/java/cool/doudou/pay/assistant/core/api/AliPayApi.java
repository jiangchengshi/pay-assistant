package cool.doudou.pay.assistant.core.api;

import com.alibaba.fastjson2.JSONObject;
import cool.doudou.pay.assistant.core.entity.PlaceOrderParam;
import cool.doudou.pay.assistant.core.properties.PayAliProperties;
import cool.doudou.pay.assistant.core.properties.PayProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * AliPayApi
 *
 * @author jiangcs
 * @since 2022/06/23
 */
@Component
public class AliPayApi {
    private PayProperties payProperties;
    private PayAliProperties payAliProperties;

    public void place(PlaceOrderParam placeOrderParam) {
        if (placeOrderParam.getOutTradeNo().isBlank()) {
            throw new RuntimeException("商户订单号不能为空");
        }
        if (placeOrderParam.getOutTradeNo().length() > 64) {
            throw new RuntimeException("商户订单号长度不能超多64位");
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("app_id", payAliProperties.getAppId());
        jsonObject.put("method", "alipay.trade.create");
        jsonObject.put("format", "JSON");
        jsonObject.put("charset", "utf-8");
        jsonObject.put("sign_type", "RSA2");
        jsonObject.put("sign", "");
        jsonObject.put("timestamp", "");
        jsonObject.put("version", "1.0");
        jsonObject.put("notify_url", payProperties.getNotifyServer() + "/pay-notify/ali");
        JSONObject jsonBiz = new JSONObject();
        jsonBiz.put("out_trade_no", placeOrderParam.getOutTradeNo());
        jsonBiz.put("total_amount", placeOrderParam.getMoney());
        jsonBiz.put("subject", placeOrderParam.getDescription());
        jsonBiz.put("buyer_id", placeOrderParam.getOpenId());
        jsonBiz.put("body", placeOrderParam.getAttach());
        jsonBiz.put("time_expire", placeOrderParam.getTimeExpire());
        jsonObject.put("biz_content", jsonBiz);
    }

    @Autowired
    public void setPayProperties(PayProperties payProperties) {
        this.payProperties = payProperties;
    }

    @Autowired
    public void setPayAliProperties(PayAliProperties payAliProperties) {
        this.payAliProperties = payAliProperties;
    }
}
