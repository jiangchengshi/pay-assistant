package cool.doudou.pay.assistant.core.enums;

/**
 * PayModeEnum
 *
 * @author jiangcs
 * @since 2022/06/30
 */
public enum PayModeEnum {
    WX("wx"), ALI("ali");

    final String name;

    PayModeEnum(String name) {
        this.name = name;
    }
}
