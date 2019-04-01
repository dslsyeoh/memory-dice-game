/*
 * Written by Steven Yeoh
 *
 * Copyright (c) 2019.
 */

package com.my.memory.dice.game.utils;

public class TimeConverter
{
    private static final int HOURS = 24;
    private static final int MINUTES = 60;
    private static final int SECONDS = 60;

    public static String byHours(double time)
    {
        if(time > HOURS) throw new UnsupportedOperationException("converter does not support day(s)");
        else
        {
            int hours = (int) time;
            double remainingHours = time - hours;
            return getTime("hours", hours, hoursToSeconds(remainingHours));
        }
    }

    public static String byMinutes(double time)
    {
        if(time >= MINUTES) return toHours(time);
        else
        {
            int minutes = (int) time;
            double remainingMinutes = time - minutes;
            return getTime("minutes", minutes, minutesToSeconds(remainingMinutes));
        }
    }

    public static String bySeconds(double time)
    {
        if(time >= SECONDS) return toMinutes(time);
        return String.format("%d %s", (int)time, "seconds");
    }

    private static String toHours(double time)
    {
        double tempHours = time / MINUTES;
        int hours = (int) tempHours;
        double remainingHours = roundToNearest4Decimal(tempHours - hours);

        return getTime("hours", hours, hoursToSeconds(remainingHours));
    }

    private static String toMinutes(double time)
    {
        double tempMinutes = time / SECONDS;
        if(tempMinutes >= MINUTES) return toHours(tempMinutes);
        else
        {
            int minutes = (int) tempMinutes;
            double remainingMinutes = roundToNearest4Decimal(tempMinutes - minutes);
            return getTime("minutes", minutes, minutesToSeconds(remainingMinutes));
        }
    }

    private static double minutesToSeconds(double minutes)
    {
        return Math.round(minutes * SECONDS);
    }

    private static double hoursToSeconds(double hours)
    {
        return Math.round(hours * MINUTES * SECONDS);
    }

    private static String getTime(String type, int time, double seconds)
    {
        StringBuilder result = new StringBuilder();
        if(time > 0) result.append(String.format("%d %s", time, time > 1 ? type : type.substring(0, type.length() - 1)));
        if(seconds > 0) result.append(String.format(" %s", bySeconds(seconds)));

        return result.toString();
    }

    private static double roundToNearest4Decimal(double value)
    {
        return Math.round(value * 10000.0) / 10000.0;
    }
}
