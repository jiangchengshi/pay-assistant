package cool.doudou.pay.assistant.core.util;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * AesUtil
 *
 * @author jiangcs
 * @since 2022/07/04
 */
public class AesUtil {
    /**
     * 解密
     *
     * @param aesKey            AES Key
     * @param associatedDataStr 附加数据包
     * @param nonceStr          随机字符串
     * @param ciphertext        加密内容
     * @return 解密内容
     */
    public static String decrypt(String aesKey, String associatedDataStr, String nonceStr, String ciphertext) {
        try {
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

            SecretKeySpec secretKeySpec = new SecretKeySpec(aesKey.getBytes(StandardCharsets.UTF_8), "AES");
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(128, nonceStr.getBytes(StandardCharsets.UTF_8));

            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, gcmParameterSpec);
            cipher.updateAAD(associatedDataStr.getBytes(StandardCharsets.UTF_8));

            return new String(cipher.doFinal(Base64.getDecoder().decode(ciphertext)), StandardCharsets.UTF_8);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new IllegalStateException(e);
        } catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
            throw new IllegalArgumentException(e);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }
}
