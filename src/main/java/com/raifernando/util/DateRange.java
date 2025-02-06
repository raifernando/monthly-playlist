package com.raifernando.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.*;

public class DateRange {
    // Month range in UNIX timestamp
    private long startDate;
    private long endDate;

    public DateRange() {}

    /**
     * Set the month range from the list of arguments.
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
     * Set the start and end UNIX timestamps for the month range of the date provided in the list of arguments.
     * @param args list of arguments
     * @throws Exception if the date is invalid
     */
    public void setDate(String [] args) throws Exception {
        // If lastfm username was passed, the month and year get shifted one time to the right
        int shift = args.length == 3 ? 1 : 0;

        startDate = getUnixTime(getMonthNumber(args[shift]), Integer.parseInt(args[1 + shift]));
        endDate = addOneMonth(startDate);

        if (!validateDate(startDate))
            throw new DateTimeException("Invalid date");
    }

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

    private static long getUnixTime(int month, int year) {
        return LocalDate.of(year, month, 1).atStartOfDay(ZoneId.systemDefault()).toEpochSecond();
    }

    private static long addOneMonth(long time) {
        LocalDate date = LocalDate.ofEpochDay(time / 86400).plusMonths(1);
        return date.atStartOfDay(ZoneId.systemDefault()).toEpochSecond();
    }

    public String getDateAsString() {
        return LocalDate.ofEpochDay(startDate / 86400).getMonth().toString().toLowerCase() + ":"
            + LocalDate.ofEpochDay(startDate / 86400).getYear();
    }

    /**
     * Check whether the date is not in the future and is not before 1970.01.01 (start of the UNIX timestamp)
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

    public long getStartDate() {
        return startDate;
    }

    public long getEndDate() {
        return endDate;
    }
}
