package com.example.jintfocusstart_28115;

import android.content.SharedPreferences;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public class WorkWithDate {
    public static void saveDate(SharedPreferences.Editor e) {
        ZonedDateTime today = ZonedDateTime.now(ZoneId.of("UTC"));
        ZonedDateTime dayUpdate = ZonedDateTime.of(
                today.getYear(),
                today.getMonthValue(),
                today.getDayOfMonth(),
                9,
                0,
                0,
                0,
                ZoneId.of("UTC")
        );

        if (today.isAfter(dayUpdate)) {
            dayUpdate = dayUpdate.plusDays(1);
        }

        e.putInt("year", dayUpdate.getYear());
        e.putInt("month", dayUpdate.getMonthValue());
        e.putInt("day", dayUpdate.getDayOfMonth());
        e.putInt("hour", dayUpdate.getHour());
        e.apply();
    }

    public static boolean checkNeedUpdate(SharedPreferences sharedPreferences) {
        ZonedDateTime today = ZonedDateTime.now(ZoneId.of("UTC"));
        ZonedDateTime dayUpdate = ZonedDateTime.of(
                sharedPreferences.getInt("year", 1998),
                sharedPreferences.getInt("month", 7),
                sharedPreferences.getInt("day", 30),
                sharedPreferences.getInt("hour", 11),
                0, 0, 0, ZoneId.of("UTC")
        );

        return today.isAfter(dayUpdate);
    }
}
