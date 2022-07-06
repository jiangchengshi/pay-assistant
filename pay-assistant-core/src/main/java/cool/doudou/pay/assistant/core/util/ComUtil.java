package cool.doudou.pay.assistant.core.util;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * ComUtil
 *
 * @author jiangcs
 * @since 2022/07/05
 */
public class ComUtil {
    /**
     * 日期格式化
     *
     * @param date 日期
     * @return 日期字符串
     */
    public static String formatDate(Date date) {
        return formatDate(date, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 日期格式化
     *
     * @param date    日期
     * @param pattern 日期格式
     * @return 日期字符串
     */
    public static String formatDate(Date date, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }

    /**
     * ASCII值 升序
     *
     * @param dataList 数据集合
     */
    public static void asciiSortAsc(List<String> dataList) {
        Collections.sort(dataList);
    }
}
