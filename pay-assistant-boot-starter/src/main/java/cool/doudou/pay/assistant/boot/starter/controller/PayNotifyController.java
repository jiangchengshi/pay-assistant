package cool.doudou.pay.assistant.boot.starter.controller;

import cool.doudou.pay.assistant.core.factory.ConcurrentMapFactory;
import cool.doudou.pay.assistant.core.enums.PayModeEnum;
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
    @PostMapping("wx")
    public void wxPay(String jsonStr) {
        ConcurrentMapFactory.get(PayModeEnum.WX).accept(jsonStr);
    }

    @PostMapping("ali")
    public void aliPay(String jsonStr) {
        ConcurrentMapFactory.get(PayModeEnum.ALI).accept(jsonStr);
    }
}
