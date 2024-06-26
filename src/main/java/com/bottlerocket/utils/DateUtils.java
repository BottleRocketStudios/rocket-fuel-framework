package com.bottlerocket.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Keep all framework reporting and things that care about the date/time on the same page
 */
public class DateUtils {
    private static String dateOffset;

    /**
     * The first time this is called, it will set the date. From here, it will return the same each time it is called
     *
     * @return Current date if never called before, or the date it was first called
     */
    public static synchronized String getFirstDateRun() {
        if(dateOffset == null || dateOffset.equals("")) {
            DateFormat dateFormat = new SimpleDateFormat("MMMM_yy/d/HH_mm_ss_a");
            Calendar cal = Calendar.getInstance();
            dateOffset = dateFormat.format(cal.getTime());
        }

        return dateOffset;
    }
}
