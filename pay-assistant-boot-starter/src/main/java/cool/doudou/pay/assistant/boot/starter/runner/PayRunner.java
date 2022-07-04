package cool.doudou.pay.assistant.boot.starter.runner;

import cool.doudou.pay.assistant.core.api.WxPayApi;
import cool.doudou.pay.assistant.core.properties.PayWxProperties;
import cool.doudou.pay.assistant.core.util.PemUtil;
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
    private WxPayApi wxPayApi;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 加载商户密钥文件
        PemUtil.loadPrivateKey(new File(payWxProperties.getCertificatePath()));

        // 加载平台证书
        wxPayApi.loadCertificate();
    }

    @Autowired
    public void setPayWxProperties(PayWxProperties payWxProperties) {
        this.payWxProperties = payWxProperties;
    }

    @Autowired
    public void setWxPayApi(WxPayApi wxPayApi) {
        this.wxPayApi = wxPayApi;
    }
}
