package cool.doudou.pay.assistant.core.util;

import cool.doudou.pay.assistant.core.memory.WxPayMem;

import java.nio.charset.StandardCharsets;
import java.security.Signature;
import java.util.Base64;

/**
 * RsaUtil
 *
 * @author jiangcs
 * @since 2022/07/04
 */
public class RsaUtil {
    /**
     * 加密
     *
     * @param plaintext 明文
     * @return Base64加密的密文
     */
    public static String encrypt(String plaintext) {
        // 签名值
        try {
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(WxPayMem.privateKey);
            signature.update(plaintext.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(signature.sign());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
