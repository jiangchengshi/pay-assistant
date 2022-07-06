package cool.doudou.pay.assistant.core.util;

import cool.doudou.pay.assistant.core.memory.AliPayMem;
import cool.doudou.pay.assistant.core.memory.WxPayMem;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
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
     * @param privateFilePath 私钥文件
     */
    public static void loadWxSecretKey(String privateFilePath) {
        try {
            WxPayMem.privateKey = loadPrivateKey(new FileInputStream(privateFilePath));
        } catch (FileNotFoundException e) {
            throw new RuntimeException("密钥文件不存在", e);
        }
    }

    /**
     * 加载 密钥文件 ali
     *
     * @param privateFilePath 私钥文件
     * @param publicFilePath  公钥文件
     */
    public static void loadAliSecretKey(String privateFilePath, String publicFilePath) {
        try {
            AliPayMem.privateKey = loadPrivateKey(new FileInputStream(privateFilePath));
            AliPayMem.publicKey = loadPublicKey(new FileInputStream(publicFilePath));
        } catch (FileNotFoundException e) {
            throw new RuntimeException("密钥文件不存在", e);
        }
    }

    /**
     * 加载私钥文件
     *
     * @param inputStream 文件流
     * @return 私钥
     */
    public static PrivateKey loadPrivateKey(FileInputStream inputStream) {
        String privateKeyStr = loadFileContent(inputStream);

        try {
            privateKeyStr = privateKeyStr.replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s+", "");
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyStr)));
        } catch (Exception e) {
            throw new RuntimeException("密钥字符串加密异常", e);
        }
    }

    /**
     * 加载公钥文件
     *
     * @param inputStream 文件流
     * @return 公钥
     */
    private static PublicKey loadPublicKey(FileInputStream inputStream) {
        String publicKeyStr = loadFileContent(inputStream);

        try {
            publicKeyStr = publicKeyStr.replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s+", "");
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyStr)));
        } catch (Exception e) {
            throw new RuntimeException("密钥字符串加密异常", e);
        }
    }

    private static String loadFileContent(InputStream inputStream) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(2048);
        byte[] byteArr = new byte[1024];

        try {
            // 加载 密钥 文件
            int readLength = inputStream.read(byteArr);
            while (readLength != -1) {
                byteArrayOutputStream.write(byteArr, 0, readLength);

                readLength = inputStream.read(byteArr);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("密钥文件读取异常", e);
        }

        return byteArrayOutputStream.toString(StandardCharsets.UTF_8);
    }
}
