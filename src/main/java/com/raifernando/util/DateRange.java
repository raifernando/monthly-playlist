package com.raifernando.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.*;

public class DateRange {
    private long startDate;
    private long endDate;

    public DateRange() {}

    public void setDate(String [] args) throws ParseException {
        // If lastfm username was passed, the month and year get shifted one time to the right
        int shift = args.length == 3 ? 1 : 0;

        startDate = getUnixTime(getMonthNumber(args[shift]), Integer.parseInt(args[1 + shift]));
        endDate = addOneMonth(startDate);
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

    public long getStartDate() {
        return startDate;
    }

    public long getEndDate() {
        return endDate;
    }
}
