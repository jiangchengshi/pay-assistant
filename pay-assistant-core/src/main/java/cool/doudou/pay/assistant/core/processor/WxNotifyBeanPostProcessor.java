package cool.doudou.pay.assistant.core.processor;

import cool.doudou.pay.assistant.annotation.WxPayNotify;
import cool.doudou.pay.assistant.core.factory.ConcurrentMapFactory;
import cool.doudou.pay.assistant.core.enums.PayModeEnum;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.util.Arrays;

/**
 * WxNotifyBeanPostProcessor
 *
 * @author jiangcs
 * @since 2022/2/20
 */
@AllArgsConstructor
public class WxNotifyBeanPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Arrays.stream(bean.getClass().getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(WxPayNotify.class))
                .forEach(method -> ConcurrentMapFactory.add(PayModeEnum.WX, (data) -> {
                    try {
                        method.setAccessible(true);
                        method.invoke(bean, data);
                    } catch (Exception e) {
                        throw new RuntimeException("bean[" + bean + "].method[" + method + "]invoke exception: ", e);
                    }
                }));
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}
