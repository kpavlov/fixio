package fixio.fixprotocol;

import fixio.fixprotocol.fields.DateTimeFormatterWrapper;

import java.time.ZoneId;
import java.time.ZoneOffset;

public class FixConst {

    public static final ZoneId DEFAULT_ZONE_ID = ZoneOffset.UTC;

    public static final String DATE_PATTERN = "yyyyMMdd";
    //
    public static final String TIME_PATTERN_SECONDS = "HH:mm:ss";
    public static final String TIME_PATTERN_MILLIS = "HH:mm:ss.SSS";
    public static final String TIME_PATTERN_MICROS = "HH:mm:ss.SSSSSS";
    public static final String TIME_PATTERN_NANOS = "HH:mm:ss.nnnnnnnnn";
    public static final String TIME_PATTERN_PICOS = "HH:mm:ss.nnnnnnnnn'000'"; // not supported by java.time at the moment, use nanos and pad '000'
    //
    public static final String DATE_TIME_PATTERN_SECONDS = DATE_PATTERN + "-" + TIME_PATTERN_SECONDS;
    public static final String DATE_TIME_PATTERN_MILLIS = DATE_PATTERN + "-" + TIME_PATTERN_MILLIS;
    public static final String DATE_TIME_PATTERN_MICROS = DATE_PATTERN + "-" + TIME_PATTERN_MICROS;
    public static final String DATE_TIME_PATTERN_NANOS = DATE_PATTERN + "-" + TIME_PATTERN_NANOS;
    public static final String DATE_TIME_PATTERN_PICOS = DATE_PATTERN + "-" + TIME_PATTERN_PICOS;
    //
    public static final int TIME_PATTERN_SECONDS_LENGTH = TIME_PATTERN_SECONDS.length();
    public static final int TIME_PATTERN_MILLIS_LENGTH = TIME_PATTERN_MILLIS.length();
    public static final int TIME_PATTERN_MICROS_LENGTH = TIME_PATTERN_MICROS.length();
    public static final int TIME_PATTERN_NANOS_LENGTH = TIME_PATTERN_NANOS.length();
    public static final int TIME_PATTERN_PICOS_LENGTH = TIME_PATTERN_PICOS.length();
    //
    public static final int DATE_TIME_PATTERN_SECONDS_LENGTH = DATE_TIME_PATTERN_SECONDS.length();
    public static final int DATE_TIME_PATTERN_MILLIS_LENGTH = DATE_TIME_PATTERN_MILLIS.length();
    public static final int DATE_TIME_PATTERN_MICROS_LENGTH = DATE_TIME_PATTERN_MICROS.length();
    public static final int DATE_TIME_PATTERN_NANOS_LENGTH = DATE_TIME_PATTERN_NANOS.length();
    public static final int DATE_TIME_PATTERN_PICOS_LENGTH = DATE_TIME_PATTERN_PICOS.length();
    //
    public static final DateTimeFormatterWrapper DATE_FORMATTER = new DateTimeFormatterWrapper(DATE_PATTERN, DEFAULT_ZONE_ID);
    //
    public static final DateTimeFormatterWrapper TIME_FORMATTER_SECONDS = new DateTimeFormatterWrapper(TIME_PATTERN_SECONDS, DEFAULT_ZONE_ID);
    public static final DateTimeFormatterWrapper TIME_FORMATTER_MILLIS = new DateTimeFormatterWrapper(TIME_PATTERN_MILLIS, DEFAULT_ZONE_ID);
    public static final DateTimeFormatterWrapper TIME_FORMATTER_MICROS = new DateTimeFormatterWrapper(TIME_PATTERN_MICROS, DEFAULT_ZONE_ID);
    public static final DateTimeFormatterWrapper TIME_FORMATTER_NANOS = new DateTimeFormatterWrapper(TIME_PATTERN_NANOS, DEFAULT_ZONE_ID);
    public static final DateTimeFormatterWrapper TIME_FORMATTER_PICOS = new DateTimeFormatterWrapper(TIME_PATTERN_PICOS, DEFAULT_ZONE_ID);
    //
    public static final DateTimeFormatterWrapper DATE_TIME_FORMATTER_SECONDS = new DateTimeFormatterWrapper(DATE_TIME_PATTERN_SECONDS, DEFAULT_ZONE_ID);
    public static final DateTimeFormatterWrapper DATE_TIME_FORMATTER_MILLIS = new DateTimeFormatterWrapper(DATE_TIME_PATTERN_MILLIS, DEFAULT_ZONE_ID);
    public static final DateTimeFormatterWrapper DATE_TIME_FORMATTER_MICROS = new DateTimeFormatterWrapper(DATE_TIME_PATTERN_MICROS, DEFAULT_ZONE_ID);
    public static final DateTimeFormatterWrapper DATE_TIME_FORMATTER_NANOS = new DateTimeFormatterWrapper(DATE_TIME_PATTERN_NANOS, DEFAULT_ZONE_ID);
    public static final DateTimeFormatterWrapper DATE_TIME_FORMATTER_PICOS = new DateTimeFormatterWrapper(DATE_TIME_PATTERN_PICOS, DEFAULT_ZONE_ID);

    private FixConst() {
        // to prevent instatiation
    }

    public enum TimeStampPrecision {
        SECONDS,
        MILLIS,
        MICROS,
        NANOS,
        PICOS
    }

}
