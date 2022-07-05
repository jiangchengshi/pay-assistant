package cool.doudou.pay.assistant.core.entity;

import lombok.Data;

import java.math.BigDecimal;

/**
 * PlaceOrderParam
 *
 * @author jiangcs
 * @since 2022/06/30
 */
@Data
public class PlaceOrderParam {
    /**
     * wx: 商户订单号 => 数字、大小写字母_-*且在同一个商户号下唯一 [6,32]
     * ali: 商户订单号 => 字母、数字、下划线且需保证在商户端不重复 [*, 64]
     */
    private String outTradeNo;
    /**
     * wx: 总金额(total), 单位为分
     * ali: 总金额(total_amount), 单位为元 => 精确到小数点后两位，取值范围：[0.01,100000000]
     */
    private BigDecimal money;
    /**
     * wx: 商品描述
     * ali: 订单标题(subject) => 不可使用特殊字符，如 /，=，& 等
     */
    private String description;
    /**
     * wx: 订单失效时间 => 格式为yyyy-MM-DDTHH:mm:ss+TIMEZONE
     * ali: 订单绝对超时时间 => 格式为yyyy-MM-dd HH:mm:ss
     */
    private String timeExpire;
    /**
     * wx: 附加数据 => 在查询API和支付通知中原样返回
     * ali: 订单附加信息(body) => 在异步通知、对账单中原样返回
     */
    private String attach;
    /**
     * wx: 用户标识(openid)
     * ali: 买家支付宝用户ID(buyer_id)
     */
    private String uid;
}
