package cool.doudou.pay.assistant.core.memory;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WxPayMem
 *
 * @author jiangcs
 * @since 2022/07/04
 */
public class WxPayMem {
    /**
     * 商户证书私钥
     */
    public static PrivateKey privateKey;

    /**
     * 平台证书
     */
    public static Map<String, X509Certificate> certificateMap = new ConcurrentHashMap<>();
}
