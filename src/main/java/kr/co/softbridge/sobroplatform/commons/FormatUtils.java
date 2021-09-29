package kr.co.softbridge.sobroplatform.commons;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 포맷 유틸
 */
public class FormatUtils {

    public static class PATTERNS {
        public static String NORMAL = "YYYY-MM-dd";
        public static String WITH_TIME = "YYYY-MM-dd'T'HH:mm:ss";
    }

    /**
     * ISO 8601 -> datetime 형식 날짜 변환
     * @param datetime
     * @return datetime 형식 일자
     * 2021-01-01T13:20:30 --> 2021-01-01 13:20:30
     */
    public static String ISO8601ToDatetime (String datetime) {
        return datetime.replace("T", " ");
    }

    /**
     * datetime -> ISO 8601 형식 날짜 변환
     * @param datetime
     * @return ISO 8601 형식 일자
     * 2021-01-01 13:20:30 --> 2021-01-01T13:20:30
     */
    public static String DatetimeToISO8601 (String datetime) {
        return datetime.replace(" ", "T");
    }

    /**
     * date 문자열 변환
     * @param localDateTime
     * @param pattern - 변환 패턴
     *                "YYYY-MM-dd"
     *                "YYYY-MM-ddTHH:mm:ss
     * @return
     */
    public static String dateToStringByPattern(LocalDateTime localDateTime, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return localDateTime.format(formatter);
    }

    /**
     * date 문자열 변환
     * @param localDate
     * @param pattern - 변환 패턴
     *                "YYYY-MM-dd"
     *                "YYYY-MM-ddTHH:mm:ss
     * @return
     */
    public static String dateToStringByPattern(LocalDate localDate, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return localDate.format(formatter);
    }
}
