package fixio.fixprotocol.fields;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

public class DateTimeFormatterWrapper {
    private final String pattern;
    private final ZoneId zoneId;
    private final DateTimeFormatter dateTimeFormatter;
    private final String padding;
    private final int paddingLen;

    public DateTimeFormatterWrapper(String pattern, ZoneId zoneId) {
        this.pattern = pattern;
        this.zoneId = zoneId;
        int idx = pattern.indexOf('\'');
        if (idx > 0) {
            this.dateTimeFormatter = DateTimeFormatter.ofPattern(pattern.substring(0, idx)).withZone(zoneId);
            this.padding = pattern.substring(idx).replaceAll("'", "");
        } else {
            this.dateTimeFormatter = DateTimeFormatter.ofPattern(pattern).withZone(zoneId);
            this.padding = "";
        }
        this.paddingLen = this.padding.length();
    }

    public String getPattern() {
        return pattern;
    }

    public ZoneId getZoneId() {
        return zoneId;
    }

    public DateTimeFormatter getDateTimeFormatter() {
        return dateTimeFormatter;
    }

    public String getPadding() {
        return padding;
    }


    public String format(TemporalAccessor value) {
        return dateTimeFormatter.format(value) + padding;
    }

    public LocalDate parseLocalDate(String timestampString) {
        return LocalDate.parse(timestampString, dateTimeFormatter);
    }

    public LocalTime parseLocalTime(String timestampString) {
        if (timestampString != null) {
            if (paddingLen > 0) {
                int idx = timestampString.length() - paddingLen;
                return LocalTime.parse(timestampString.substring(0, idx), dateTimeFormatter);
            } else {
                return LocalTime.parse(timestampString, dateTimeFormatter);
            }
        }
        return null;
    }

    public ZonedDateTime parseZonedDateTime(String timestampString) {
        if (timestampString != null) {
            if (paddingLen > 0) {
                int idx = timestampString.length() - paddingLen;
                return ZonedDateTime.parse(timestampString.substring(0, idx), dateTimeFormatter);
            } else {
                return ZonedDateTime.parse(timestampString, dateTimeFormatter);
            }
        }
        return null;
    }
}
