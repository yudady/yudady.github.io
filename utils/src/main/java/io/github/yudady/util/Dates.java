package io.github.yudady.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import javax.annotation.Nullable;

/**
 * @author Chris
 */
public class Dates {
    public static LocalDate chinaLocalDate(ZonedDateTime dateTime) {
        return dateTime.withZoneSameInstant(ZoneIds.ASIA_TAIPEI).toLocalDate();
    }

    public static LocalTime chinaLocalTime(ZonedDateTime time) {
        return time.withZoneSameInstant(ZoneIds.ASIA_TAIPEI).toLocalTime();
    }

    public static ZonedDateTime chinaStartOfToday() {
        return ZonedDateTime.now().withZoneSameInstant(ZoneIds.ASIA_TAIPEI).with(LocalTime.MIN);
    }

    public static ZonedDateTime chinaEndOfToday() {
        return ZonedDateTime.now().withZoneSameInstant(ZoneIds.ASIA_TAIPEI).with(LocalTime.MAX);
    }

    public static ZonedDateTime chinaStartOfTheDay(LocalDate date) {
        return ZonedDateTime.of(date, LocalTime.MIN, ZoneIds.ASIA_TAIPEI);
    }

    public static ZonedDateTime chinaStartOfTheDay(ZonedDateTime dateTime) {
        return dateTime.withZoneSameInstant(ZoneIds.ASIA_TAIPEI).with(LocalTime.MIN);
    }

    public static ZonedDateTime chinaEndOfTheDay(LocalDate date) {
        return ZonedDateTime.of(date, LocalTime.MAX, ZoneIds.ASIA_TAIPEI);
    }

    public static ZonedDateTime chinaEndOfTheDay(ZonedDateTime dateTime) {
        return dateTime.withZoneSameInstant(ZoneIds.ASIA_TAIPEI).with(LocalTime.MAX);
    }

    public static LocalDate localDate(String isoString) {
        return LocalDate.parse(isoString, DateTimeFormatter.ISO_LOCAL_DATE);
    }

    public static LocalTime localTime(String isoString) {
        return LocalTime.parse(isoString, DateTimeFormatter.ISO_LOCAL_TIME);
    }

    public static ZonedDateTime zonedDateTime(@Nullable String isoString) {
        if (isoString == null) return null;
        return ZonedDateTime.parse(isoString, DateTimeFormatter.ISO_ZONED_DATE_TIME);
    }

    public static ZonedDateTime chinaZonedDateTime(ZonedDateTime dateTime) {
        return dateTime.withZoneSameInstant(ZoneIds.ASIA_TAIPEI);
    }

    public static ZonedDateTime chinaZonedDateTime(LocalDate date, Integer hour) {
        return ZonedDateTime.of(date, LocalTime.of(hour, 0), ZoneIds.ASIA_TAIPEI);
    }

    public static String isoString(LocalDate date) {
        return date.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    public static String isoString(LocalTime time) {
        return time.format(DateTimeFormatter.ISO_LOCAL_TIME);
    }

    public static String isoString(ZonedDateTime time) {
        return time.format(DateTimeFormatter.ISO_ZONED_DATE_TIME);
    }

    public static LocalDate chinaLocalDateOfToday() {
        return chinaLocalDate(ZonedDateTime.now());
    }

    public static ZonedDateTime chinaNow() {
        return ZonedDateTime.now().withZoneSameInstant(ZoneIds.ASIA_TAIPEI);
    }

    public static ZonedDateTime startOfTheDay(LocalDate date) {
        return date.atStartOfDay(ZoneIds.ASIA_TAIPEI).withZoneSameInstant(ZoneOffset.UTC);
    }

    // safari & chrome will act differently if LocalTime.MAX, eg. 23:59:59:999999 -> 00:00:00:000000 (Safari)
    public static ZonedDateTime endOfTheDay(LocalDate date) {
        return date.atStartOfDay(ZoneIds.ASIA_TAIPEI).with(LocalTime.MAX).withZoneSameInstant(ZoneOffset.UTC).withNano(0);
    }

    public static ZonedDateTime startOfTheWeek(LocalDate date) {
        return startOfTheDay(date.with(DayOfWeek.MONDAY));
    }

    public static ZonedDateTime endOfTheWeek(LocalDate date) {
        return endOfTheDay(date.with(DayOfWeek.SUNDAY));
    }

    public static ZonedDateTime startOfTheMonth(LocalDate date) {
        return startOfTheDay(date.withDayOfMonth(1));
    }

    public static ZonedDateTime endOfTheMonth(LocalDate date) {
        return endOfTheDay(date.withDayOfMonth(date.lengthOfMonth()));
    }

    public static String chinaTodayDateString() {
        return chinaLocalDateOfToday().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    }

    public static int daysBetween(ZonedDateTime start, ZonedDateTime end) {
        // (end - start) in days
        return Math.toIntExact(ChronoUnit.DAYS.between(start, end));
    }

    static class ZoneIds {
        public static final ZoneId UTC = ZoneId.of("UTC");
        public static final ZoneId ASIA_TAIPEI = ZoneId.of("Asia/Taipei");
    }

}
