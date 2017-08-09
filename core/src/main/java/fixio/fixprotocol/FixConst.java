package fixio.fixprotocol;

import fixio.fixprotocol.fields.DateTimeFormatterWrapper;

import java.time.ZoneId;
import java.time.ZoneOffset;

public interface FixConst {
    ZoneId DEFAULT_ZONE_ID = ZoneOffset.UTC;
    enum TimeStampPrecision {
        SECONDS, MILLIS, MICROS, NANOS, PICOS
    }
    String DATE_PATTERN  = "yyyyMMdd";
    //
    String TIME_PATTERN_SECONDS  = "HH:mm:ss";
    String TIME_PATTERN_MILLIS   = "HH:mm:ss.SSS";
    String TIME_PATTERN_MICROS   = "HH:mm:ss.SSSSSS";
    String TIME_PATTERN_NANOS    = "HH:mm:ss.nnnnnnnnn";
    String TIME_PATTERN_PICOS    = "HH:mm:ss.nnnnnnnnn'000'"; // not supported by java.time at the moment, use nanos and pad '000'
    //
    String DATE_TIME_PATTERN_SECONDS  = DATE_PATTERN+"-"+TIME_PATTERN_SECONDS;
    String DATE_TIME_PATTERN_MILLIS   = DATE_PATTERN+"-"+TIME_PATTERN_MILLIS;
    String DATE_TIME_PATTERN_MICROS   = DATE_PATTERN+"-"+TIME_PATTERN_MICROS;
    String DATE_TIME_PATTERN_NANOS    = DATE_PATTERN+"-"+TIME_PATTERN_NANOS;
    String DATE_TIME_PATTERN_PICOS    = DATE_PATTERN+"-"+TIME_PATTERN_PICOS;
    //
    int TIME_PATTERN_SECONDS_LENGTH= TIME_PATTERN_SECONDS.length();
    int TIME_PATTERN_MILLIS_LENGTH = TIME_PATTERN_MILLIS.length();
    int TIME_PATTERN_MICROS_LENGTH = TIME_PATTERN_MICROS.length();
    int TIME_PATTERN_NANOS_LENGTH  = TIME_PATTERN_NANOS.length();
    int TIME_PATTERN_PICOS_LENGTH  = TIME_PATTERN_PICOS.length();
    //
    int DATE_TIME_PATTERN_SECONDS_LENGTH= DATE_TIME_PATTERN_SECONDS.length();
    int DATE_TIME_PATTERN_MILLIS_LENGTH = DATE_TIME_PATTERN_MILLIS.length();
    int DATE_TIME_PATTERN_MICROS_LENGTH = DATE_TIME_PATTERN_MICROS.length();
    int DATE_TIME_PATTERN_NANOS_LENGTH  = DATE_TIME_PATTERN_NANOS.length();
    int DATE_TIME_PATTERN_PICOS_LENGTH  = DATE_TIME_PATTERN_PICOS.length();
    //
    DateTimeFormatterWrapper DATE_FORMATTER = new DateTimeFormatterWrapper(DATE_PATTERN, DEFAULT_ZONE_ID);
    //
    DateTimeFormatterWrapper TIME_FORMATTER_SECONDS = new DateTimeFormatterWrapper(TIME_PATTERN_SECONDS,DEFAULT_ZONE_ID);
    DateTimeFormatterWrapper TIME_FORMATTER_MILLIS  = new DateTimeFormatterWrapper(TIME_PATTERN_MILLIS,DEFAULT_ZONE_ID);
    DateTimeFormatterWrapper TIME_FORMATTER_MICROS  = new DateTimeFormatterWrapper(TIME_PATTERN_MICROS,DEFAULT_ZONE_ID);
    DateTimeFormatterWrapper TIME_FORMATTER_NANOS   = new DateTimeFormatterWrapper(TIME_PATTERN_NANOS,DEFAULT_ZONE_ID);
    DateTimeFormatterWrapper TIME_FORMATTER_PICOS   = new DateTimeFormatterWrapper(TIME_PATTERN_PICOS,DEFAULT_ZONE_ID);
    //
    DateTimeFormatterWrapper DATE_TIME_FORMATTER_SECONDS= new DateTimeFormatterWrapper(DATE_TIME_PATTERN_SECONDS,DEFAULT_ZONE_ID);
    DateTimeFormatterWrapper DATE_TIME_FORMATTER_MILLIS = new DateTimeFormatterWrapper(DATE_TIME_PATTERN_MILLIS,DEFAULT_ZONE_ID);
    DateTimeFormatterWrapper DATE_TIME_FORMATTER_MICROS = new DateTimeFormatterWrapper(DATE_TIME_PATTERN_MICROS,DEFAULT_ZONE_ID);
    DateTimeFormatterWrapper DATE_TIME_FORMATTER_NANOS  = new DateTimeFormatterWrapper(DATE_TIME_PATTERN_NANOS,DEFAULT_ZONE_ID);
    DateTimeFormatterWrapper DATE_TIME_FORMATTER_PICOS  = new DateTimeFormatterWrapper(DATE_TIME_PATTERN_PICOS,DEFAULT_ZONE_ID);
}
