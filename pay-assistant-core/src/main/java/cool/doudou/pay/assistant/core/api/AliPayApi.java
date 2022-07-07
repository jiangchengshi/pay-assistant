package cool.doudou.pay.assistant.core.api;

import com.alibaba.fastjson2.JSONObject;
import cool.doudou.pay.assistant.core.entity.PlaceOrderParam;
import cool.doudou.pay.assistant.core.entity.RefundParam;
import cool.doudou.pay.assistant.core.enums.AliInterfaceEnum;
import cool.doudou.pay.assistant.core.helper.HttpHelper;
import cool.doudou.pay.assistant.core.properties.PayAliProperties;
import cool.doudou.pay.assistant.core.properties.PayProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * AliPayApi
 *
 * @author jiangcs
 * @since 2022/06/23
 */
public class AliPayApi {
    private PayProperties payProperties;
    private PayAliProperties payAliProperties;
    private HttpHelper httpHelper;

    public String place(PlaceOrderParam placeOrderParam) {
        if (ObjectUtils.isEmpty(placeOrderParam.getOutTradeNo())) {
            throw new RuntimeException("商户订单号不能为空");
        }
        if (placeOrderParam.getOutTradeNo().length() > 64) {
            throw new RuntimeException("商户订单号长度不能超多64位");
        }
        if (!ObjectUtils.isEmpty(placeOrderParam.getTimeExpire()) && placeOrderParam.getTimeExpire().length() != 19) {
            throw new RuntimeException("交易超时时间格式错误");
        }

        // 业务参数
        Map<String, String> params = new HashMap<>(1);
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
        params.put("biz_content", apiParamObj.toJSONString());

        return httpHelper.doPost4Ali(payAliProperties.getServerAddress(), AliInterfaceEnum.TRADE_CREATE.method(), params, payAliProperties.getAppId(), payProperties.getNotifyServerAddress());
    }

    public String query(String outTradeNo) {
        if (ObjectUtils.isEmpty(outTradeNo)) {
            throw new RuntimeException("商户订单号不能为空");
        }

        // 业务参数
        Map<String, String> params = new HashMap<>(1);
        JSONObject apiParamObj = new JSONObject();
        apiParamObj.put("out_trade_no", outTradeNo);
        params.put("biz_content", apiParamObj.toJSONString());

        return httpHelper.doPost4Ali(payAliProperties.getServerAddress(), AliInterfaceEnum.TRADE_QUERY.method(), params, payAliProperties.getAppId(), payProperties.getNotifyServerAddress());
    }

    public String close(String outTradeNo) {
        if (ObjectUtils.isEmpty(outTradeNo)) {
            throw new RuntimeException("商户订单号不能为空");
        }

        // 业务参数
        Map<String, String> params = new HashMap<>(1);
        JSONObject apiParamObj = new JSONObject();
        apiParamObj.put("out_trade_no", outTradeNo);
        params.put("biz_content", apiParamObj.toJSONString());

        return httpHelper.doPost4Ali(payAliProperties.getServerAddress(), AliInterfaceEnum.TRADE_CLOSE.method(), params, payAliProperties.getAppId(), payProperties.getNotifyServerAddress());
    }

    public String refund(RefundParam refundParam) {
        if (ObjectUtils.isEmpty(refundParam.getOutTradeNo())) {
            throw new RuntimeException("商户订单号不能为空");
        }

        // 业务参数
        Map<String, String> params = new HashMap<>(1);
        JSONObject apiParamObj = new JSONObject();
        apiParamObj.put("out_trade_no", refundParam.getOutTradeNo());
        apiParamObj.put("refund_amount", refundParam.getRefundMoney());
        if (!ObjectUtils.isEmpty(refundParam.getReason())) {
            apiParamObj.put("refund_reason", refundParam.getReason());
        }
        if (!ObjectUtils.isEmpty(refundParam.getOutRefundNo())) {
            apiParamObj.put("out_request_no", refundParam.getOutRefundNo());
        }
        params.put("biz_content", apiParamObj.toJSONString());

        return httpHelper.doPost4Ali(payAliProperties.getServerAddress(), AliInterfaceEnum.TRADE_REFUND.method(), params, payAliProperties.getAppId(), payProperties.getNotifyServerAddress());
    }

    /**
     * 交易账单
     *
     * @param billDate 账单日期（日账单格式为yyyy-MM-dd；月账单格式为yyyy-MM）
     * @return 账单下载地址
     */
    public String tradeBill(String billDate) {
        if (ObjectUtils.isEmpty(billDate)) {
            throw new RuntimeException("账单日期不能为空");
        }

        Map<String, String> params = new HashMap<>(1);
        JSONObject apiParamObj = new JSONObject();
        apiParamObj.put("bill_type", "trade");
        apiParamObj.put("bill_date", billDate);
        params.put("biz_content", apiParamObj.toJSONString());

        return httpHelper.doPost4Ali(payAliProperties.getServerAddress(), AliInterfaceEnum.TRADE_BILL_QUERY.method(), params, payAliProperties.getAppId(), payProperties.getNotifyServerAddress());
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
