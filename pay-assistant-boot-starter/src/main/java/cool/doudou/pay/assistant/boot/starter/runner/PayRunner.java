package cool.doudou.pay.assistant.boot.starter.runner;

import cool.doudou.pay.assistant.core.properties.PayWxProperties;
import cool.doudou.pay.assistant.core.util.PemUtil;
import cool.doudou.pay.assistant.core.util.WxSignatureUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * PayRunner
 *
 * @author jiangcs
 * @since 2022/07/02
 */
@Component
public class PayRunner implements ApplicationRunner {
    private PayWxProperties payWxProperties;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 加载微信支付密钥文件
        WxSignatureUtil.setPrivateKey(PemUtil.loadPrivateKey(new File(payWxProperties.getCertificatePath())));
    }

    @Autowired
    public void setPayWxProperties(PayWxProperties payWxProperties) {
        this.payWxProperties = payWxProperties;
    }
}
