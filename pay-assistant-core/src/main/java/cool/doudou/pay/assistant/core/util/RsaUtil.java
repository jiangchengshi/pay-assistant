package cool.doudou.pay.assistant.core.util;

import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;

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
     * @param plaintext  明文
     * @param privateKey 密钥
     * @return 密文字节数组
     */
    public static byte[] encrypt(String plaintext, PrivateKey privateKey) {
        // 签名值
        try {
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(privateKey);
            signature.update(plaintext.getBytes(StandardCharsets.UTF_8));
            return signature.sign();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 验签
     *
     * @param ciphertext   密文
     * @param publicKey    公钥
     * @param signatureStr 待验证字符串
     * @return true-成功；false-失败
     */
    public static boolean verify(String ciphertext, PublicKey publicKey, String signatureStr) {
        // 签名值
        try {
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initVerify(publicKey);
            signature.update(ciphertext.getBytes(StandardCharsets.UTF_8));
            return signature.verify(signatureStr.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
