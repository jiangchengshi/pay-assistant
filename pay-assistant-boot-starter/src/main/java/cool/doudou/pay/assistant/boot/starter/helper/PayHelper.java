package cool.doudou.pay.assistant.boot.starter.helper;

import cool.doudou.pay.assistant.core.PayModeEnum;
import cool.doudou.pay.assistant.core.api.AliPayApi;
import cool.doudou.pay.assistant.core.api.WxPayApi;
import cool.doudou.pay.assistant.core.entity.PlaceOrderParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

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
     * @param placeOrderParam 下单参数
     */
    public void place(PayModeEnum payModeEnum, PlaceOrderParam placeOrderParam) {
        switch (payModeEnum) {
            case WX:
                wxPayApi.place(placeOrderParam);
                break;
            case ALI:
                aliPayApi.place(placeOrderParam);
                break;
            default:
                throw new RuntimeException("支付模式匹配失败");
        }
    }

    public void refund() {

    }

    public void close() {

    }

    public void query() {

    }

    public void bill() {

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
