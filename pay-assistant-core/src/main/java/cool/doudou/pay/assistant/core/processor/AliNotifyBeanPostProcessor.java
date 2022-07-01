package cool.doudou.pay.assistant.core.processor;

import cool.doudou.pay.assistant.annotation.AliPayNotify;
import cool.doudou.pay.assistant.core.ConcurrentMapFactory;
import cool.doudou.pay.assistant.core.PayModeEnum;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.util.Arrays;

/**
 * AliNotifyBeanPostProcessor
 *
 * @author jiangcs
 * @since 2022/2/20
 */
@AllArgsConstructor
public class AliNotifyBeanPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Arrays.stream(bean.getClass().getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(AliPayNotify.class))
                .forEach(method -> ConcurrentMapFactory.add(PayModeEnum.ALI, (data) -> {
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
