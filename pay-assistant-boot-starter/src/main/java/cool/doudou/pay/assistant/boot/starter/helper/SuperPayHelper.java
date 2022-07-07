package cool.doudou.pay.assistant.boot.starter.helper;

import cool.doudou.pay.assistant.core.enums.PayModeEnum;
import cool.doudou.pay.assistant.core.properties.PayProperties;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * SuperPayHelper
 *
 * @author jiangcs
 * @since 2022/07/06
 */
public class SuperPayHelper {
    private PayProperties payProperties;

    /**
     * 判断 是否支持方式
     *
     * @param payModeEnum 支付方式
     */
    protected void support(PayModeEnum payModeEnum) {
        if (!payProperties.getModes().contains(payModeEnum.code())) {
            throw new RuntimeException("支付方式[" + payModeEnum.note() + "]参数未配置");
        }
    }

    @Autowired
    public void setPayProperties(PayProperties payProperties) {
        this.payProperties = payProperties;
    }
}
