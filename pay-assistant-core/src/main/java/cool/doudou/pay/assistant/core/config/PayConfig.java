package cool.doudou.pay.assistant.core.config;

import cool.doudou.pay.assistant.core.api.AliPayApi;
import cool.doudou.pay.assistant.core.api.WxPayApi;
import cool.doudou.pay.assistant.core.enums.PayModeEnum;
import cool.doudou.pay.assistant.core.helper.HttpHelper;
import cool.doudou.pay.assistant.core.processor.AliNotifyBeanPostProcessor;
import cool.doudou.pay.assistant.core.processor.WxNotifyBeanPostProcessor;
import cool.doudou.pay.assistant.core.properties.PayAliProperties;
import cool.doudou.pay.assistant.core.properties.PayProperties;
import cool.doudou.pay.assistant.core.properties.PayWxProperties;
import cool.doudou.pay.assistant.core.util.CertificateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.util.ObjectUtils;

/**
 * PayConfig
 *
 * @author jiangcs
 * @since 2022/2/20
 */
public class PayConfig {
    @Bean
    public WxPayApi wxPayApi() {
        return new WxPayApi();
    }

    @Bean
    public AliPayApi aliPayApi() {
        return new AliPayApi();
    }

    @Bean
    public HttpHelper httpHelper() {
        return new HttpHelper();
    }

    @Bean
    public WxNotifyBeanPostProcessor wxNotifyBeanPostProcessor() {
        return new WxNotifyBeanPostProcessor();
    }

    @Bean
    public AliNotifyBeanPostProcessor aliNotifyBeanPostProcessor() {
        return new AliNotifyBeanPostProcessor();
    }

    @Autowired
    public void init(PayProperties payProperties, PayWxProperties payWxProperties, PayAliProperties payAliProperties, WxPayApi wxPayApi) {
        if (ObjectUtils.isEmpty(payProperties.getModes())) {
            throw new RuntimeException("支付模式参数未配置");
        }

        if (payProperties.getModes().contains(PayModeEnum.WX.code())) {
            if (ObjectUtils.isEmpty(payWxProperties.getServerAddress()) || ObjectUtils.isEmpty(payWxProperties.getAppId())
                    || ObjectUtils.isEmpty(payWxProperties.getMchId()) || ObjectUtils.isEmpty(payWxProperties.getPrivateKeyPath())
                    || ObjectUtils.isEmpty(payWxProperties.getPrivateKeySerialNumber()) || ObjectUtils.isEmpty(payWxProperties.getApiKeyV3())) {
                throw new RuntimeException("微信支付参数配置缺失");
            }

            try {
                // 加载微信商户密钥文件
                CertificateUtil.loadWxSecretKey(payWxProperties.getPrivateKeyPath());
                // 加载微信平台证书
                wxPayApi.loadPlatformCertificate();
            } catch (Exception e) {
                System.err.println("加载微信密钥异常: ");
                e.printStackTrace();
            }
        }

        if (payProperties.getModes().contains(PayModeEnum.ALI.code())) {
            if (ObjectUtils.isEmpty(payAliProperties.getServerAddress()) || ObjectUtils.isEmpty(payAliProperties.getAppId())
                    || ObjectUtils.isEmpty(payAliProperties.getPrivateKeyPath()) || ObjectUtils.isEmpty(payAliProperties.getPublicKeyPath())) {
                throw new RuntimeException("支付宝支付参数配置缺失");
            }

            try {
                // 加载支付宝商户密钥文件
                CertificateUtil.loadAliSecretKey(payAliProperties.getPrivateKeyPath(), payAliProperties.getPublicKeyPath());
            } catch (Exception e) {
                System.err.println("加载支付宝密钥异常: ");
                e.printStackTrace();
            }
        }
    }
}
