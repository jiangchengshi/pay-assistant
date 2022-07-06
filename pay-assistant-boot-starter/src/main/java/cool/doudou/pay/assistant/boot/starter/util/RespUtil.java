package cool.doudou.pay.assistant.boot.starter.util;

import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;

/**
 * RespUtil
 *
 * @author jiangcs
 * @since 2022/07/06
 */
public class RespUtil {
    public static void writeSuccess(HttpServletResponse response, String message) {
        write(response, HttpStatus.OK, message);
    }

    public static void writeFail(HttpServletResponse response, String message) {
        write(response, HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

    private static void write(HttpServletResponse response, HttpStatus httpStatus, String message) {
        try {
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setStatus(httpStatus.value());
            response.getWriter().write(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
