package cool.doudou.pay.assistant.boot.starter.runner;

import cool.doudou.pay.assistant.core.api.WxPayApi;
import cool.doudou.pay.assistant.core.properties.PayAliProperties;
import cool.doudou.pay.assistant.core.properties.PayWxProperties;
import cool.doudou.pay.assistant.core.util.CertificateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * PayRunner
 *
 * @author jiangcs
 * @since 2022/07/02
 */
@Component
public class PayRunner implements ApplicationRunner {
    private PayWxProperties payWxProperties;
    private WxPayApi wxPayApi;
    private PayAliProperties payAliProperties;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 加载微信商户密钥文件
        CertificateUtil.loadWxSecretKey(payWxProperties.getPrivateKeyPath());

        // 加载微信平台证书
        wxPayApi.loadPlatformCertificate();

        // 加载支付宝商户密钥文件
        CertificateUtil.loadAliSecretKey(payAliProperties.getPrivateKeyPath(), payAliProperties.getPublicKeyPath());
    }

    @Autowired
    public void setPayWxProperties(PayWxProperties payWxProperties) {
        this.payWxProperties = payWxProperties;
    }

    @Autowired
    public void setWxPayApi(WxPayApi wxPayApi) {
        this.wxPayApi = wxPayApi;
    }

    @Autowired
    public void setPayAliProperties(PayAliProperties payAliProperties) {
        this.payAliProperties = payAliProperties;
    }
}
