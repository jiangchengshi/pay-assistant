package cool.doudou.pay.assistant.core.entity;

import lombok.Data;

import java.math.BigDecimal;

/**
 * RefundParam
 *
 * @author jiangcs
 * @since 2022/06/30
 */
@Data
public class RefundParam {
    /**
     * /**
     * wx: 商户订单号 => 数字、大小写字母_-*且在同一个商户号下唯一 [6,32]
     * ali: 商户订单号 => 字母、数字、下划线且需保证在商户端不重复 [*, 64]
     */
    private String outTradeNo;
    /**
     * wx: 商户退款单号 => 商户系统内部唯一，只能是数字、大小写字母_-|*@
     * ali: 退款请求号(out_request_no) => 标识一次退款请求，需要保证在交易号下唯一
     */
    private String outRefundNo;
    /**
     * wx: 退款原因
     * ali: 退款原因(refund_reason)
     */
    private String reason;
    /**
     * wx: 总金额，单位为分
     * ali: 无
     */
    private BigDecimal money;
    /**
     * wx: 退款金额，单位为分
     * ali: 退款金额，单位为元(refund_amount)
     */
    private BigDecimal refundMoney;
}
