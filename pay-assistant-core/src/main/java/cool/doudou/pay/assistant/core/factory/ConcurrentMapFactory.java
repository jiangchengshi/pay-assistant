package cool.doudou.pay.assistant.core.factory;

import cool.doudou.pay.assistant.core.enums.PayModeEnum;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * ConcurrentMapFactory
 *
 * @author jiangcs
 * @since 2022/2/19
 */
public class ConcurrentMapFactory {
    private static final Map<PayModeEnum, Consumer<String>> PRODUCER_MAP = new ConcurrentHashMap<>();

    public static Consumer<String> get(PayModeEnum mode) {
        return PRODUCER_MAP.get(mode);
    }

    public static void add(PayModeEnum mode, Consumer<String> consumer) {
        PRODUCER_MAP.put(mode, consumer);
    }
}
