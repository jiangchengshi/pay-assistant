package cool.doudou.pay.assistant.boot.starter.helper;

import cool.doudou.pay.assistant.core.api.AliPayApi;
import cool.doudou.pay.assistant.core.api.WxPayApi;
import cool.doudou.pay.assistant.core.entity.PlaceOrderParam;
import cool.doudou.pay.assistant.core.entity.RefundParam;
import cool.doudou.pay.assistant.core.enums.PayModeEnum;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;

/**
 * PayHelper
 *
 * @author jiangcs
 * @since 2022/2/19
 */
public class PayHelper {
    private WxPayApi wxPayApi;
    private AliPayApi aliPayApi;

    /**
     * 下单
     *
     * @param payModeEnum     支付方式
     * @param placeOrderParam 订单参数
     * @return JSON格式结果
     */
    public String place(PayModeEnum payModeEnum, PlaceOrderParam placeOrderParam) {
        switch (payModeEnum) {
            case WX:
                return wxPayApi.place(placeOrderParam);
            case ALI:
                return aliPayApi.place(placeOrderParam);
            default:
                throw new RuntimeException("支付模式匹配失败");
        }
    }

    /**
     * 查询
     *
     * @param payModeEnum 支付方式
     * @param outTradeNo  商户订单号
     * @return JSON格式结果
     */
    public String query(PayModeEnum payModeEnum, String outTradeNo) {
        switch (payModeEnum) {
            case WX:
                return wxPayApi.query(outTradeNo);
            case ALI:
                return aliPayApi.query(outTradeNo);
            default:
                throw new RuntimeException("支付模式匹配失败");
        }
    }

    /**
     * 关闭
     *
     * @param payModeEnum 支付方式
     * @param outTradeNo  商户订单号
     * @return JSON格式结果
     */
    public String close(PayModeEnum payModeEnum, String outTradeNo) {
        switch (payModeEnum) {
            case WX:
                return wxPayApi.close(outTradeNo);
            case ALI:
                return aliPayApi.close(outTradeNo);
            default:
                throw new RuntimeException("支付模式匹配失败");
        }
    }

    /**
     * 退款
     *
     * @param payModeEnum 支付方式
     * @param refundParam 退款参数
     * @return JSON格式结果
     */
    public String refund(PayModeEnum payModeEnum, RefundParam refundParam) {
        switch (payModeEnum) {
            case WX:
                return wxPayApi.refund(refundParam);
            case ALI:
                return aliPayApi.refund(refundParam);
            default:
                throw new RuntimeException("支付模式匹配失败");
        }
    }

    /**
     * 交易账单
     *
     * @param payModeEnum 支付方式
     * @param billDate    账单日期
     * @return JSON格式结果
     */
    public String tradeBill(PayModeEnum payModeEnum, String billDate) {
        switch (payModeEnum) {
            case WX:
                return wxPayApi.tradeBill(billDate);
            case ALI:
                return aliPayApi.tradeBill(billDate);
            default:
                throw new RuntimeException("支付模式匹配失败");
        }
    }

    /**
     * 下载账单
     *
     * @param payModeEnum 支付方式
     * @param billUrl     账单地址
     * @return JSON格式结果
     */
    public ByteArrayInputStream downloadBill(PayModeEnum payModeEnum, String billUrl) {
        switch (payModeEnum) {
            case WX:
                return wxPayApi.downloadBill(billUrl);
            default:
                throw new RuntimeException("支付模式匹配失败");
        }
    }

    @Autowired
    public void setWxPayApi(WxPayApi wxPayApi) {
        this.wxPayApi = wxPayApi;
    }

    @Autowired
    public void setAliPayApi(AliPayApi aliPayApi) {
        this.aliPayApi = aliPayApi;
    }
}
