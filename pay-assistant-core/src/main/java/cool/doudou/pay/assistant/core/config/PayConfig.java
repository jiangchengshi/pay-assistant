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

import java.util.Arrays;

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
    public void initSecretKey(PayProperties payProperties, PayWxProperties payWxProperties, PayAliProperties payAliProperties, WxPayApi wxPayApi) {
        if (Arrays.asList(payProperties.getModes()).contains(PayModeEnum.WX.code())) {
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

        if (Arrays.asList(payProperties.getModes()).contains(PayModeEnum.ALI.code())) {
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
