package cool.doudou.pay.assistant.core;

import cool.doudou.pay.assistant.core.api.AliPayApi;
import cool.doudou.pay.assistant.core.entity.PlaceOrderParam;
import cool.doudou.pay.assistant.core.entity.RefundParam;
import cool.doudou.pay.assistant.core.helper.HttpHelper;
import cool.doudou.pay.assistant.core.properties.PayAliProperties;
import cool.doudou.pay.assistant.core.properties.PayProperties;
import cool.doudou.pay.assistant.core.util.CertificateUtil;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * AliPayTest
 *
 * @author jiangcs
 * @since 2022/07/05
 */
public class AliPayTest {
    @Test
    public void place() {
        try {
            AliPayApi aliPayApi = initContext();

            PlaceOrderParam placeOrderParam = new PlaceOrderParam();
            placeOrderParam.setOutTradeNo("9917752501201407033233360003");
            placeOrderParam.setDescription("test");
            placeOrderParam.setTimeExpire("2022-07-06 09:58:00");
            placeOrderParam.setAttach("16g");
            placeOrderParam.setMoney(BigDecimal.valueOf(200));
            placeOrderParam.setUid("2088622987242457");

            assertNotNull(aliPayApi.place(placeOrderParam));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void query() {
        try {
            AliPayApi aliPayApi = initContext();

            assertNotNull(aliPayApi.query("9917752501201407033233360002"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void close() {
        try {
            AliPayApi aliPayApi = initContext();

            assertNotNull(aliPayApi.close("9917752501201407033233360002"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void refund() {
        try {
            AliPayApi aliPayApi = initContext();

            RefundParam refundParam = new RefundParam();
            refundParam.setOutTradeNo("9917752501201407033233360003");
            refundParam.setOutRefundNo("1217752501201407033233368018");
            refundParam.setReason("测试退款");
            refundParam.setRefundMoney(BigDecimal.valueOf(100));
            assertNotNull(aliPayApi.refund(refundParam));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void tradeBill() {
        try {
            AliPayApi aliPayApi = initContext();

            assertNotNull(aliPayApi.tradeBill("2022-07-05"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private AliPayApi initContext() {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("application-context.xml");

        AliPayApi aliPayApi = new AliPayApi();
        aliPayApi.setPayProperties(applicationContext.getBean("payProperties", PayProperties.class));
        PayAliProperties payAliProperties = applicationContext.getBean("payAliProperties", PayAliProperties.class);
        aliPayApi.setPayAliProperties(payAliProperties);

        HttpHelper httpHelper = applicationContext.getBean("httpHelper", HttpHelper.class);
        httpHelper.setOkHttpClient(new OkHttpClient());
        aliPayApi.setHttpHelper(httpHelper);

        CertificateUtil.loadAliSecretKey(payAliProperties.getPrivateKeyPath(), payAliProperties.getPublicKeyPath());

        return aliPayApi;
    }
}
