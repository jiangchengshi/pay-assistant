package cool.doudou.pay.assistant.core;

import cool.doudou.pay.assistant.core.api.WxPayApi;
import cool.doudou.pay.assistant.core.entity.PlaceOrderParam;
import cool.doudou.pay.assistant.core.helper.HttpHelper;
import cool.doudou.pay.assistant.core.properties.PayProperties;
import cool.doudou.pay.assistant.core.properties.PayWxProperties;
import cool.doudou.pay.assistant.core.util.PemUtil;
import cool.doudou.pay.assistant.core.util.WxSignatureUtil;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * PayTest
 *
 * @author jiangcs
 * @since 2022/06/30
 */
public class PayTest {
    @Test
    public void wxPay() {
        try {
            ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("application-context.xml");

            WxPayApi wxPayApi = new WxPayApi();
            wxPayApi.setPayProperties(applicationContext.getBean("payProperties", PayProperties.class));
            PayWxProperties payWxProperties = applicationContext.getBean("payWxProperties", PayWxProperties.class);
            wxPayApi.setPayWxProperties(payWxProperties);

            WxSignatureUtil.setPrivateKey(PemUtil.loadPrivateKey(new File(payWxProperties.getCertificatePath())));

            HttpHelper httpHelper = applicationContext.getBean("httpHelper", HttpHelper.class);
            httpHelper.setOkHttpClient(new OkHttpClient());
            wxPayApi.setHttpHelper(httpHelper);

            PlaceOrderParam placeOrderParam = new PlaceOrderParam();
            placeOrderParam.setOutTradeNo("SN123456789");
            placeOrderParam.setDescription("test");
            placeOrderParam.setTimeExpire("2022-06-30T22:12:00+08:00:00");
            placeOrderParam.setAttach("订单测试");
            placeOrderParam.setMoney(BigDecimal.valueOf(200));
            placeOrderParam.setOpenId("wx000010000100001");

            assertTrue(wxPayApi.place(placeOrderParam));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
