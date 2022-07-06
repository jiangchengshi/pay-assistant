package cool.doudou.pay.assistant.core.enums;

/**
 * AliInterfaceEnum
 *
 * @author jiangcs
 * @since 2022/07/06
 */
public enum AliInterfaceEnum {
    /**
     * 统一收单线下交易查询
     */
    TRADE_CREATE("alipay.trade.create"),
    /**
     * 统一收单交易创建接口
     */
    TRADE_QUERY("alipay.trade.query"),
    /**
     * 统一收单交易退款接口
     */
    TRADE_REFUND("alipay.trade.refund"),
    /**
     * 统一收单交易关闭接口
     */
    TRADE_CLOSE("alipay.trade.close"),
    /**
     * 查询对账单下载地址
     */
    TRADE_BILL_QUERY("alipay.data.dataservice.bill.downloadurl.query");

    private final String method;

    AliInterfaceEnum(String method) {
        this.method = method;
    }

    public String method() {
        return method;
    }
}
