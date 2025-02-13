package com.raifernando.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.*;

/**
 * The {@link DateRange} class stores a date range in UNIX timestamps.
 */
public class DateRange {
    // Month range in UNIX timestamp
    private long startDate;
    private long endDate;

    /**
     * Sets the month range based on the list of arguments.
     * @param args list of arguments
     * @throws DateTimeException if the date is invalid
     */
    public DateRange(String [] args) throws DateTimeException {
        try {
            setDate(args);
        } catch (Exception e) {
            throw new DateTimeException("The date provided is invalid");
        }
    }

    /**
     * Sets the start and end UNIX timestamps for the month range based on the date provided in the list of arguments.
     * @param args list of arguments
     * @throws Exception if the date is invalid
     */
    public void setDate(String [] args) throws Exception {
        // If last.fm username was passed, the month and year get shifted one time to the right
        int shift = args.length == 3 ? 1 : 0;

        startDate = getUnixTime(getMonthNumber(args[shift]), Integer.parseInt(args[1 + shift]));
        endDate = addOneMonth(startDate);

        if (!validateDate(startDate))
            throw new DateTimeException("Invalid date");
    }

    /**
     * Retrieves the month number from a {@link String} containing the month name or abbreviation.
     * @param month the month as a {@link String}
     * @return an integer with the month number
     * @throws ParseException if the month name is invalid
     */
    private static int getMonthNumber(String month) throws ParseException {
        if (month.length() <= 2)
            return Integer.parseInt(month);

        Date date;
        if (month.length() == 3)
            date = new SimpleDateFormat("MMM", Locale.ENGLISH).parse(month);
        else
            date = new SimpleDateFormat("MMMM", Locale.ENGLISH).parse(month);

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return (cal.get(Calendar.MONTH) + 1);
    }

    /**
     * Retrieves the UNIX timestamp for the first day of the month and year.
     * The time is from the start of the day, at the system's default timezone.
     * @param month value of the month
     * @param year value ogf the year
     * @return a long with the unix timestamp
     */
    private static long getUnixTime(int month, int year) {
        return LocalDate.of(year, month, 1).atStartOfDay(ZoneId.systemDefault()).toEpochSecond();
    }

    /**
     * Adds one month to the provided UNIX timestamp.
     * The time is from the start of the day, at the system's default timezone.
     * @param time UNIX timestamp
     * @return a long with the UNIX timestamp of the next month
     */
    private static long addOneMonth(long time) {
        LocalDate date = LocalDate.ofEpochDay(time / 86400).plusMonths(1);
        return date.atStartOfDay(ZoneId.systemDefault()).toEpochSecond();
    }

    /**
     * Returns a {@link String} in the format month:YYYY. The month is from the {@link #startDate}.
     * @return the string
     */
    public String getDateAsString() {
        return LocalDate.ofEpochDay(startDate / 86400).getMonth().toString().toLowerCase() + ":"
            + LocalDate.ofEpochDay(startDate / 86400).getYear();
    }

    /**
     * Checks whether the date is not in the future and is not before 1970.01.01 (the start of the UNIX timestamp)
     * @param date date in unix timestamp
     * @return true if the date is valid; false if invalid
     */
    private boolean validateDate(long date) {
        LocalDate todaysDate = LocalDate.now(ZoneId.systemDefault());
        LocalDate inputDate = Instant.ofEpochSecond(date)
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        return inputDate.isBefore(todaysDate) && (date > 0);
    }

    /**
     * Returns a {@link String} in the format YYYY-MM-DD:YYYY-MM-DD of the date range.
     * @return the string
     */
    public String getFullRange() {
        // The endDate is the start of the next month. Subtracting 1 day gets the last day of the current month.
        long startEpochDay = startDate / 86400, endEpochDay = (endDate / 86400) - 1;
        String firstDay = String.format("%d-%02d-%02d",
                LocalDate.ofEpochDay(startEpochDay).getYear(),
                LocalDate.ofEpochDay(startEpochDay).getMonthValue(),
                LocalDate.ofEpochDay(startEpochDay).getDayOfMonth()
        );

        String lastDay = String.format("%d-%02d-%02d",
                LocalDate.ofEpochDay(endEpochDay).getYear(),
                LocalDate.ofEpochDay(endEpochDay).getMonthValue(),
                LocalDate.ofEpochDay(endEpochDay).getDayOfMonth()
        );

        return firstDay + ":" + lastDay;
    }


    public long getStartDate() {
        return startDate;
    }

    public long getEndDate() {
        return endDate;
    }
}
