package common;

import exception.IncorrectTaskDateException;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class TimeUtils {

    private static final String DATE_FORMAT = "dd/MM/yyyy HH:mm";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);

    public static OffsetDateTime convertToOffsetDateTime(final String date) {
        try {
            final LocalDateTime datetime = LocalDateTime.parse(date, DATE_TIME_FORMATTER);
            final ZonedDateTime zoned = datetime.atZone(ZoneId.of("Europe/Berlin"));
            return zoned.toOffsetDateTime();
        } catch (DateTimeParseException e) {
            //TODO we should apply some logging class (to not use sout...)
            System.out.printf("The provided date format is incorrect");
            e.printStackTrace();
            throw new IncorrectTaskDateException();
        }
    }
}
