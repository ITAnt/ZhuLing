package com.itant.zhuling.tool;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Jason on 2017/3/26.
 */

public class DateTool {
    /**
     * from yyyy-MM-dd HH:mm:ss to MM-dd HH:mm
     */
    public static String formatDate(String before) {
        String after;
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    .parse(before);
            after = new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault()).format(date);
        } catch (ParseException e) {
            return before;
        }
        return after;
    }
}
