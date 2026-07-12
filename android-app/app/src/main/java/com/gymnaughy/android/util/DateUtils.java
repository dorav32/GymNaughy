package com.gymnaughy.android.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public final class DateUtils {

    private static final String ISO_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    private DateUtils() {
    }

    public static Date parseIso(String iso) {
        if (iso == null) {
            return null;
        }
        SimpleDateFormat format = new SimpleDateFormat(ISO_PATTERN, Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            return format.parse(iso);
        } catch (ParseException e) {
            return null;
        }
    }

    public static boolean isSameCalendarDay(Date a, Date b) {
        if (a == null || b == null) {
            return false;
        }
        java.util.Calendar calA = java.util.Calendar.getInstance();
        java.util.Calendar calB = java.util.Calendar.getInstance();
        calA.setTime(a);
        calB.setTime(b);
        return calA.get(java.util.Calendar.YEAR) == calB.get(java.util.Calendar.YEAR)
                && calA.get(java.util.Calendar.DAY_OF_YEAR) == calB.get(java.util.Calendar.DAY_OF_YEAR);
    }

    public static String formatFriendly(Date date) {
        if (date == null) {
            return "--";
        }
        return new SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(date);
    }
}
