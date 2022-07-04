package cool.doudou.pay.assistant.boot.starter;

import cool.doudou.pay.assistant.core.enums.PayModeEnum;
import cool.doudou.pay.assistant.core.factory.ConcurrentMapFactory;
import org.springframework.stereotype.Service;

/**
 * PayNotifyService
 *
 * @author jiangcs
 * @since 2022/07/04
 */
@Service
public class PayNotifyService {
    public void wxPay(String jsonStr) {
        ConcurrentMapFactory.get(PayModeEnum.WX).accept(jsonStr);
    }

    public void aliPay(String jsonStr) {
        ConcurrentMapFactory.get(PayModeEnum.ALI).accept(jsonStr);
    }
}
