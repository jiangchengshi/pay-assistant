package cool.doudou.pay.assistant.core.signer;

import cool.doudou.pay.assistant.core.memory.AliPayMem;
import cool.doudou.pay.assistant.core.util.ComUtil;
import cool.doudou.pay.assistant.core.util.RsaUtil;

import java.util.*;

/**
 * 支付宝签名者
 *
 * @author jiangcs
 * @since 2022/07/05
 */
public class AliSigner {
    /**
     * 获取 签名
     *
     * @param publicParamMap 公共参数
     * @param bizParamMap    业务参数
     * @return 签名字符串
     */
    public static String getSign(Map<String, String> publicParamMap, Map<String, String> bizParamMap) {
        // 参数Key
        Map<String, String> paramMap = new TreeMap<>();
        paramMap.putAll(publicParamMap);
        paramMap.putAll(bizParamMap);
        List<String> keyList = new ArrayList<>(paramMap.keySet());

        // 参数Key排序
        ComUtil.asciiSortAsc(keyList);

        // 计算签名值
        return computeSignatureValue(keyList, paramMap);
    }

    /**
     * 计算签名值
     *
     * @param keyList  ascii排序key集合
     * @param paramMap 请求参数
     * @return 签名值
     */
    private static String computeSignatureValue(List<String> keyList, Map<String, String> paramMap) {
        StringBuilder sbSignatureValue = new StringBuilder();
        for (String key : keyList) {
            if (sbSignatureValue.length() > 0) {
                sbSignatureValue.append("&");
            }
            sbSignatureValue.append(key).append("=").append(paramMap.get(key));
        }

        // 加密
        byte[] encryptArr = RsaUtil.encrypt(sbSignatureValue.toString(), AliPayMem.privateKey);

        // Base64
        return Base64.getEncoder().encodeToString(encryptArr);
    }
}
