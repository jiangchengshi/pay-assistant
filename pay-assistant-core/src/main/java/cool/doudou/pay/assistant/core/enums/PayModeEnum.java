package cool.doudou.pay.assistant.core.enums;

/**
 * PayModeEnum
 *
 * @author jiangcs
 * @since 2022/06/30
 */
public enum PayModeEnum {
    WX("wx", "微信"), ALI("ali", "支付宝");

    final String code;
    final String note;

    PayModeEnum(String code, String note) {
        this.code = code;
        this.note = note;
    }

    public String code() {
        return code;
    }

    public String note() {
        return note;
    }
}
