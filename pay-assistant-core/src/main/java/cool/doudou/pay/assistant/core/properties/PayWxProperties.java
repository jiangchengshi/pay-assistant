package cool.doudou.pay.assistant.core.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * PayWxProperties
 *
 * @author jiangcs
 * @since 2022/06/23
 */
@Data
@ConfigurationProperties(prefix = "pay.wx")
public class PayWxProperties {
    private String appId;
    private String mchId;
    private String privateKeyPath;
    private String privateKeySerialNumber;
    private String apiKeyV3;
}
