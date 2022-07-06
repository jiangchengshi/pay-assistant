package cool.doudou.pay.assistant.boot.starter.controller;

import cool.doudou.pay.assistant.boot.starter.service.PayNotifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
    public void wxPay(@RequestBody String jsonStr, HttpServletRequest request, HttpServletResponse response) {
        payNotifyService.wxPay(jsonStr, request, response);
    }

    @PostMapping("ali")
    public void aliPay(HttpServletRequest request, HttpServletResponse response) {
        payNotifyService.aliPay(request, response);
    }

    @Autowired
    public void setPayNotifyService(PayNotifyService payNotifyService) {
        this.payNotifyService = payNotifyService;
    }
}
