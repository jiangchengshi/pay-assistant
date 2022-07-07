package cool.doudou.pay.assistant.boot.starter.auto.configuration;

import cool.doudou.pay.assistant.boot.starter.helper.PayHelper;
import cool.doudou.pay.assistant.core.config.OkHttpConfig;
import cool.doudou.pay.assistant.core.config.PayConfig;
import cool.doudou.pay.assistant.core.properties.PayAliProperties;
import cool.doudou.pay.assistant.core.properties.PayProperties;
import cool.doudou.pay.assistant.core.properties.PayWxProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * PayAutoConfiguration
 *
 * @author jiangcs
 * @since 2022/2/19
 */
@EnableConfigurationProperties({PayProperties.class, PayWxProperties.class, PayAliProperties.class})
@Import({PayConfig.class, OkHttpConfig.class})
@Configuration
public class PayAutoConfiguration {
    @ConditionalOnMissingBean(PayHelper.class)
    @Bean
    public PayHelper payHelper() {
        return new PayHelper();
    }
}
