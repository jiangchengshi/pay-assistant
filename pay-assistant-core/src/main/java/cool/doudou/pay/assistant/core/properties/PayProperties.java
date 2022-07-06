package cool.doudou.pay.assistant.core.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * PayProperties
 *
 * @author jiangcs
 * @since 2022/06/23
 */
@Data
@ConfigurationProperties(prefix = "pay")
public class PayProperties {
    private String[] modes;
    private String notifyServerAddress;
}
