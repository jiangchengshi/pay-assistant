package cool.doudou.pay.assistant.core.config;

import cool.doudou.pay.assistant.core.processor.AliNotifyBeanPostProcessor;
import cool.doudou.pay.assistant.core.processor.WxNotifyBeanPostProcessor;
import org.springframework.context.annotation.Bean;

/**
 * PayConfig
 *
 * @author jiangcs
 * @since 2022/2/20
 */
public class PayConfig {
    @Bean
    public WxNotifyBeanPostProcessor wxNotifyBeanPostProcessor() {
        return new WxNotifyBeanPostProcessor();
    }

    @Bean
    public AliNotifyBeanPostProcessor aliNotifyBeanPostProcessor() {
        return new AliNotifyBeanPostProcessor();
    }
}
