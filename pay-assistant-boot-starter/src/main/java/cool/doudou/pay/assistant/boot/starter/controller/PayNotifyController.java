package cool.doudou.pay.assistant.boot.starter.controller;

import cool.doudou.pay.assistant.boot.starter.service.PayNotifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

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
    public Map<String, Object> wxPay(HttpServletRequest request, @RequestBody String jsonStr) {
        return payNotifyService.wxPay(request, jsonStr);
    }

    @PostMapping("ali")
    public void aliPay(@RequestBody String jsonStr) {
        payNotifyService.aliPay(jsonStr);
    }

    @Autowired
    public void setPayNotifyService(PayNotifyService payNotifyService) {
        this.payNotifyService = payNotifyService;
    }
}
