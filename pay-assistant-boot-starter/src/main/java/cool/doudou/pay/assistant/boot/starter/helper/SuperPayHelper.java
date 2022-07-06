package cool.doudou.pay.assistant.boot.starter.helper;

import cool.doudou.pay.assistant.core.enums.PayModeEnum;
import cool.doudou.pay.assistant.core.properties.PayProperties;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;

/**
 * SuperPayHelper
 *
 * @author jiangcs
 * @since 2022/07/06
 */
public class SuperPayHelper {
    private PayProperties payProperties;

    protected void support(PayModeEnum payModeEnum) {
        if (!Arrays.asList(payProperties.getModes()).contains(payModeEnum.code())) {
            throw new RuntimeException(payModeEnum.note() + "支付未配置");
        }
    }

    @Autowired
    public void setPayProperties(PayProperties payProperties) {
        this.payProperties = payProperties;
    }
}
