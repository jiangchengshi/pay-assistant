package cool.doudou.pay.assistant.core;

import cool.doudou.pay.assistant.core.api.WxPayApi;
import cool.doudou.pay.assistant.core.entity.PlaceOrderParam;
import cool.doudou.pay.assistant.core.entity.RefundParam;
import cool.doudou.pay.assistant.core.helper.HttpHelper;
import cool.doudou.pay.assistant.core.properties.PayProperties;
import cool.doudou.pay.assistant.core.properties.PayWxProperties;
import cool.doudou.pay.assistant.core.util.CertificateUtil;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * WxPayTest
 *
 * @author jiangcs
 * @since 2022/06/30
 */
public class WxPayTest {
    @Test
    public void place() {
        try {
            WxPayApi wxPayApi = initContext();

            PlaceOrderParam placeOrderParam = new PlaceOrderParam();
            placeOrderParam.setOutTradeNo("1217752501201407033233368399");
            placeOrderParam.setDescription("test");
            placeOrderParam.setTimeExpire("2022-07-02T16:10:00+08:00");
            placeOrderParam.setAttach("订单测试");
            placeOrderParam.setMoney(BigDecimal.valueOf(200));
            placeOrderParam.setUid("o4GgauInH_RCEdvrrNGrntXDuXXX");

            assertNotNull(wxPayApi.place(placeOrderParam));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void query() {
        try {
            WxPayApi wxPayApi = initContext();

            assertNotNull(wxPayApi.query("44_2126281063_5504"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void close() {
        try {
            WxPayApi wxPayApi = initContext();

            assertNotNull(wxPayApi.close("44_2126281063_5504"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void refund() {
        try {
            WxPayApi wxPayApi = initContext();

            RefundParam refundParam = new RefundParam();
            refundParam.setOutTradeNo("1217752501201407033233368018");
            refundParam.setOutRefundNo("1217752501201407033233368018");
            refundParam.setReason("测试退款");
            refundParam.setMoney(BigDecimal.valueOf(100));
            refundParam.setRefundMoney(BigDecimal.valueOf(100));
            assertNotNull(wxPayApi.refund(refundParam));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void tradeBill() {
        try {
            WxPayApi wxPayApi = initContext();

            assertNotNull(wxPayApi.tradeBill("2022-07-01"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void downloadBill() {
        try {
            WxPayApi wxPayApi = initContext();

            assertNotNull(wxPayApi.downloadBill("https://api.mch.weixin.qq.com/v3/billdownload/file?token=6XIv5TUPto7pByrTQKhd6kwvyKLG2uY2wMMR8cNXqaA_Cv_isgaUtBzp4QtiozLO"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private WxPayApi initContext() {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("application-context.xml");

        WxPayApi wxPayApi = new WxPayApi();
        wxPayApi.setPayProperties(applicationContext.getBean("payProperties", PayProperties.class));
        PayWxProperties payWxProperties = applicationContext.getBean("payWxProperties", PayWxProperties.class);
        wxPayApi.setPayWxProperties(payWxProperties);

        HttpHelper httpHelper = applicationContext.getBean("httpHelper", HttpHelper.class);
        httpHelper.setOkHttpClient(new OkHttpClient());
        wxPayApi.setHttpHelper(httpHelper);

        CertificateUtil.loadWxPrivateKey(new File(payWxProperties.getCertificatePath()));

        wxPayApi.loadCertificate();

        return wxPayApi;
    }
}
