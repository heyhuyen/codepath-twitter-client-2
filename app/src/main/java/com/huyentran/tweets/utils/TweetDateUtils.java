package com.huyentran.tweets.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.text.format.DateUtils.DAY_IN_MILLIS;
import static android.text.format.DateUtils.HOUR_IN_MILLIS;
import static android.text.format.DateUtils.MINUTE_IN_MILLIS;
import static android.text.format.DateUtils.SECOND_IN_MILLIS;
import static android.text.format.DateUtils.YEAR_IN_MILLIS;

/**
 * Date utility methods for tweets.
 */
public class TweetDateUtils {
    private static final String TWITTER_FORMAT = "EEE MMM dd HH:mm:ss ZZZZZ yyyy"; //"Mon Apr 01 21:16:23 +0000 2014"
    private static final String DETAIL_DATE_FORMAT = "HH:mm a - dd MMM yy";
    private static final String SECOND_FORMAT = "%ds";
    private static final String MINUTE_FORMAT = "%dm";
    private static final String HOUR_FORMAT = "%dh";
    private static final String MONTH_DAY_FORMAT = "MMM dd";
    private static final String MONTH_DAY_YEAR_FORMAT = "MMM dd yyyy";
    private static final SimpleDateFormat NEAR_DATE_FORMAT = new SimpleDateFormat(MONTH_DAY_FORMAT, Locale.ENGLISH); // within last year
    private static final SimpleDateFormat FAR_DATE_FORMAT = new SimpleDateFormat(MONTH_DAY_YEAR_FORMAT, Locale.ENGLISH); // more than 1 year ago

    /**
     * Returns a formatted string of the time delta between the given date and now.
     * For dates within the last 24 hours,the result will be returned in units of hours, minutes or seconds.
     * For dates beyond 24 hours, the result will return the date formatted with the month and day of month.
     * For dates beyond the past year, the year will also be included.
     *
     * Based off of: https://gist.github.com/nesquena/f786232f5ef72f6e10a7
     *
     * @param thenDate a date in the past
     * @return a formatted human readable string representation of the time difference
     */
    public static String getRelativeTimeAgo(String thenDate) {
        SimpleDateFormat sf = new SimpleDateFormat(TWITTER_FORMAT, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            Date date =  sf.parse(thenDate);
            long millis = System.currentTimeMillis() - date.getTime();
            if (millis < DAY_IN_MILLIS) {
                relativeDate = relativeDuration(millis);
            } else if (millis < YEAR_IN_MILLIS) {
                relativeDate = NEAR_DATE_FORMAT.format(date);
            } else {
                relativeDate = FAR_DATE_FORMAT.format(date);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return relativeDate;
    }

    /**
     * Returns the given millis duration in terms of hours, minutes or seconds,
     * whichever is largest.
     *
     * @param millis the duration in millis
     * @return string formatted duration
     */
    public static String relativeDuration(long millis) {
        if (millis >= HOUR_IN_MILLIS) {
            final int hours = Math.round(millis / HOUR_IN_MILLIS);
            return String.format(Locale.ENGLISH, HOUR_FORMAT, hours);
        } else if (millis >= MINUTE_IN_MILLIS) {
            final int minutes = Math.round(millis / MINUTE_IN_MILLIS);
            return String.format(Locale.ENGLISH, MINUTE_FORMAT, minutes);
        } else {
            final int seconds = Math.round(millis / SECOND_IN_MILLIS);
            return String.format(Locale.ENGLISH, SECOND_FORMAT, seconds);
        }
    }

    public static String getDetailDateFormat(String twitterDate) {
        SimpleDateFormat twitterFormat = new SimpleDateFormat(TWITTER_FORMAT, Locale.ENGLISH);
        twitterFormat.setLenient(true);

        SimpleDateFormat detailFormat = new SimpleDateFormat(DETAIL_DATE_FORMAT, Locale.ENGLISH);
        detailFormat.setLenient(true);

        String result = "";
        try {
            Date date = twitterFormat.parse(twitterDate);
            result = detailFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

}
