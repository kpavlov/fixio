package fixio.fixprotocol;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public interface FixConst {
    enum TimeStampPrecision {
        SECONDS, MILLIS, MICROS, NANOS
    }
    String DATE_PATTERN  = "yyyyMMdd";
    //
    String TIME_PATTERN_SECONDS  = "HH:mm:ss";
    String TIME_PATTERN_MILLIS   = "HH:mm:ss.SSS";
    String TIME_PATTERN_MICROS   = "HH:mm:ss.SSSSSS";
    String TIME_PATTERN_NANOS    = "HH:mm:ss.SSSSSSSSS";
    //
    String DATE_TIME_PATTERN_SECONDS  = DATE_PATTERN+"-"+TIME_PATTERN_SECONDS;
    String DATE_TIME_PATTERN_MILLIS   = DATE_PATTERN+"-"+TIME_PATTERN_MILLIS;
    String DATE_TIME_PATTERN_MICROS   = DATE_PATTERN+"-"+TIME_PATTERN_MICROS;
    String DATE_TIME_PATTERN_NANOS    = DATE_PATTERN+"-"+TIME_PATTERN_NANOS;
    //
    DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN).withZone(ZoneId.of("UTC"));
    //
    DateTimeFormatter TIME_FORMATTER_SECONDS = DateTimeFormatter.ofPattern(TIME_PATTERN_SECONDS).withZone(ZoneId.of("UTC"));
    DateTimeFormatter TIME_FORMATTER_MILLIS  = DateTimeFormatter.ofPattern(TIME_PATTERN_MILLIS).withZone(ZoneId.of("UTC"));
    DateTimeFormatter TIME_FORMATTER_MICROS  = DateTimeFormatter.ofPattern(TIME_PATTERN_MICROS).withZone(ZoneId.of("UTC"));
    DateTimeFormatter TIME_FORMATTER_NANOS   = DateTimeFormatter.ofPattern(TIME_PATTERN_NANOS).withZone(ZoneId.of("UTC"));
    //
    DateTimeFormatter DATE_TIME_FORMATTER_SECONDS = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN_SECONDS).withZone(ZoneId.of("UTC"));
    DateTimeFormatter DATE_TIME_FORMATTER_MILLIS  = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN_MILLIS).withZone(ZoneId.of("UTC"));
    DateTimeFormatter DATE_TIME_FORMATTER_MICROS  = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN_MICROS).withZone(ZoneId.of("UTC"));
    DateTimeFormatter DATE_TIME_FORMATTER_NANOS   = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN_NANOS).withZone(ZoneId.of("UTC"));
}
