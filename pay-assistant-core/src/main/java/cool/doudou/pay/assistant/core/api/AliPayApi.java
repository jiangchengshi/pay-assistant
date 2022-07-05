package cool.doudou.pay.assistant.core.api;

import com.alibaba.fastjson2.JSONObject;
import cool.doudou.pay.assistant.core.entity.PlaceOrderParam;
import cool.doudou.pay.assistant.core.helper.HttpHelper;
import cool.doudou.pay.assistant.core.properties.PayAliProperties;
import cool.doudou.pay.assistant.core.properties.PayProperties;
import cool.doudou.pay.assistant.core.signer.AliSigner;
import cool.doudou.pay.assistant.core.util.ComUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
    private HttpHelper httpHelper;

    private final String serverAddress = "https://openapi.alipaydev.com/gateway.do";

    public String place(PlaceOrderParam placeOrderParam) {
        if (placeOrderParam.getOutTradeNo().isBlank()) {
            throw new RuntimeException("商户订单号不能为空");
        }
        if (placeOrderParam.getOutTradeNo().length() > 64) {
            throw new RuntimeException("商户订单号长度不能超多64位");
        }
        if (!ObjectUtils.isEmpty(placeOrderParam.getTimeExpire()) && placeOrderParam.getTimeExpire().length() != 19) {
            throw new RuntimeException("交易超时时间格式错误");
        }

        // 公共参数
        Map<String, String> publicParamMap = new HashMap<>(8);
        publicParamMap.put("app_id", payAliProperties.getAppId());
        publicParamMap.put("method", "alipay.trade.create");
        publicParamMap.put("charset", "utf-8");
        publicParamMap.put("sign_type", "RSA2");
        publicParamMap.put("timestamp", ComUtil.formatDate(new Date()));
        publicParamMap.put("version", "1.0");
        publicParamMap.put("notify_url", payProperties.getNotifyServer() + "/pay-notify/ali");

        // 业务参数
        Map<String, String> bizParamMap = new HashMap<>(1);
        JSONObject apiParamObj = new JSONObject();
        apiParamObj.put("out_trade_no", placeOrderParam.getOutTradeNo());
        apiParamObj.put("total_amount", placeOrderParam.getMoney());
        apiParamObj.put("subject", placeOrderParam.getDescription());
        if (!ObjectUtils.isEmpty(placeOrderParam.getUid())) {
            apiParamObj.put("buyer_id", placeOrderParam.getUid());
        }
        if (!ObjectUtils.isEmpty(placeOrderParam.getAttach())) {
            apiParamObj.put("body", placeOrderParam.getAttach());
        }
        if (!ObjectUtils.isEmpty(placeOrderParam.getTimeExpire())) {
            apiParamObj.put("time_expire", placeOrderParam.getTimeExpire());
        }
        bizParamMap.put("biz_content", apiParamObj.toJSONString());

        // 签名
        publicParamMap.put("sign", AliSigner.getSign(publicParamMap, bizParamMap));

        return httpHelper.doPost4Ali(serverAddress, publicParamMap, bizParamMap);
    }

    @Autowired
    public void setPayProperties(PayProperties payProperties) {
        this.payProperties = payProperties;
    }

    @Autowired
    public void setPayAliProperties(PayAliProperties payAliProperties) {
        this.payAliProperties = payAliProperties;
    }

    @Autowired
    public void setHttpHelper(HttpHelper httpHelper) {
        this.httpHelper = httpHelper;
    }
}
