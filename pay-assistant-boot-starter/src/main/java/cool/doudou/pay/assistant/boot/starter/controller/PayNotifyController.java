package cool.doudou.pay.assistant.boot.starter.controller;

import cool.doudou.pay.assistant.boot.starter.PayNotifyService;
import cool.doudou.pay.assistant.core.enums.PayModeEnum;
import cool.doudou.pay.assistant.core.factory.ConcurrentMapFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * PayNotifyController
 *
 * @author jiangcs
 * @since 2022/06/30
 */
@RequestMapping("pay-notify")
@RestController
public class PayNotifyController {
    private PayNotifyService payNotifyService;

    @PostMapping("wx")
    public void wxPay(String jsonStr) {
        payNotifyService.wxPay(jsonStr);
    }

    @PostMapping("ali")
    public void aliPay(String jsonStr) {
        payNotifyService.aliPay(jsonStr);
    }

    @Autowired
    public void setPayNotifyService(PayNotifyService payNotifyService) {
        this.payNotifyService = payNotifyService;
    }
}
