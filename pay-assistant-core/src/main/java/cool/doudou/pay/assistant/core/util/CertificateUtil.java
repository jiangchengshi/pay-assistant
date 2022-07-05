package cool.doudou.pay.assistant.core.util;

import cool.doudou.pay.assistant.core.memory.AliPayMem;
import cool.doudou.pay.assistant.core.memory.WxPayMem;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

/**
 * CertificateUtil
 *
 * @author jiangcs
 * @since 2022/07/02
 */
public class CertificateUtil {
    /**
     * 加载 密钥文件  wx
     *
     * @param file 文件
     */
    public static void loadWxPrivateKey(File file) {
        try {
            WxPayMem.privateKey = loadPrivateKey(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            throw new RuntimeException("密钥文件不存在", e);
        }
    }

    /**
     * 加载 密钥文件 ali
     *
     * @param file 文件
     */
    public static void loadAliPrivateKey(File file) {
        try {
            AliPayMem.privateKey = loadPrivateKey(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            throw new RuntimeException("密钥文件不存在", e);
        }
    }

    /**
     * 加载密钥文件
     *
     * @param inputStream 文件流
     * @return 密钥
     */
    public static PrivateKey loadPrivateKey(InputStream inputStream) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(2048);
        byte[] byteArr = new byte[1024];

        // 加载 密钥 文件
        try {
            int readLength = inputStream.read(byteArr);
            while (readLength != -1) {
                byteArrayOutputStream.write(byteArr, 0, readLength);

                readLength = inputStream.read(byteArr);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("密钥文件读取异常", e);
        }
        String privateKeyStr = byteArrayOutputStream.toString(StandardCharsets.UTF_8);

        // 密钥字符串 加密
        try {
            privateKeyStr = privateKeyStr
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s+", "");
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyStr)));
        } catch (Exception e) {
            throw new RuntimeException("密钥字符串加密异常", e);
        }
    }
}
