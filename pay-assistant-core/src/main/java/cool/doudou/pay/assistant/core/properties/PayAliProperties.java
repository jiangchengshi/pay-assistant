package cool.doudou.pay.assistant.core.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * PayAliProperties
 *
 * @author jiangcs
 * @since 2022/06/23
 */
@Data
@ConfigurationProperties(prefix = "pay.ali")
public class PayAliProperties {
    private String serverAddress;
    private String appId;
    private String privateKeyPath;
    private String publicKeyPath;
}
