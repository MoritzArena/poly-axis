package io.polyaxis.api.utils.time;

import io.polyaxis.api.utils.misc.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.TimeZone;

public class TimeUtil {
    private final static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final static Logger logger = LoggerFactory.getLogger(TimeUtil.class);

    /**
     * Convert LocalDateTime to Date.
     *
     * @param localDateTime the LocalDateTime to convert
     * @return the converted Date object
     */
    public static Date localDateTimeToDate(LocalDateTime localDateTime) {
        ZonedDateTime zdt = localDateTime.atZone(TimeZone.getTimeZone("GMT+08:00").toZoneId());
        return Date.from(zdt.toInstant());
    }

    /**
     * Convert Date to LocalDateTime.
     *
     * @param date the Date to convert
     * @return the converted LocalDateTime object
     */
    public static LocalDateTime dateToLocalDateTime(Date date) {
        Instant instant = date.toInstant();
        return instant.atZone(TimeZone.getTimeZone("GMT+08:00").toZoneId()).toLocalDateTime();
    }

    /**
     * check date string is valid yyyy-MM-dd HH:mm:ss format.
     *
     * @param dateStr datetime string, e.g. "2023-10-01 12:00:00"
     * @return true if valid, false otherwise
     */
    public static boolean isValid(String dateStr) {
        try {
            DATE_TIME_FORMATTER.parse(dateStr);
        } catch (DateTimeParseException e) {
            return false;
        }
        return true;
    }

    /**
     * check date string is not valid yyyy-MM-dd HH:mm:ss format.
     *
     * @param dateStr datetime string, e.g. "2023-10-01 12:00:00"
     * @return true if not valid, false if valid
     */
    public static boolean isNotValid(String dateStr) {
        return !isValid(dateStr);
    }


    /**
     * format ISO_LOCAL_DATE_TIME to "yyyy-MM-dd HH:mm:ss" format.
     *
     * @param dateTimeStr ISO_LOCAL_DATE_TIME string, e.g. "2023-10-01T12:00:00"
     * @return formatted date string in "yyyy-MM-dd HH:mm:ss" format, e.g. "2023-10-01 12:00:00"
     */
    public static String formatDate(String dateTimeStr) {
        if (StringUtils.contains(dateTimeStr, "T")) {
            var dateTime = LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            return dateTime.format(DATE_TIME_FORMATTER);
        }
        return dateTimeStr;
    }

    /**
     * Get current date and time in "yyyy-MM-dd HH:mm:ss" format and shanghai timezone.
     *
     * @return current date and time as a string
     */
    public static String getNowDateTime() {
        return DATE_TIME_FORMATTER.format(LocalDateTime.now(ZoneId.of("Asia/Shanghai")));
    }

    public static int compareStrDateTime(String datetime1, String datetime2) {
        return LocalDateTime.parse(datetime1 == null ? "1970-01-01 00:00:01" : datetime1, DATE_TIME_FORMATTER)
                .compareTo(LocalDateTime.parse(datetime2 == null ? "1970-01-01 00:00:01" : datetime2, DATE_TIME_FORMATTER));
    }

    /**
     * parse datetime string to date
     *
     * @param dateTime datetime string
     * @return date
     */
    public static Date parseDatetime(String dateTime) {
        return Date.from(LocalDateTime.parse(dateTime, DATE_TIME_FORMATTER).atZone(ZoneId.of("Asia/Shanghai")).toInstant());
    }

    /**
     * parse date to string
     *
     * @param date date
     * @return date string
     */
    public static String parseDate2String(Date date) {
        return DATE_TIME_FORMATTER.format(date.toInstant().atZone(ZoneId.of("Asia/Shanghai")).toLocalDateTime());
    }

}
