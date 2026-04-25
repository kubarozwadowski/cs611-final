package models;

public class Date implements Comparable<Date> {
    private final int year;
    private final int month;
    private final int day;
    private final Integer hour;
    private final Integer minute;

    public Date() {
        this(0, 1, 1, null, null);
    }

    public Date(int year, int month, int day) {
        this(year, month, day, null, null);
    }

    public Date(int year, int month, int day, Integer hour, Integer minute) {
        validateDateParts(year, month, day, hour, minute);
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public Integer getHour() {
        return hour;
    }

    public Integer getMinute() {
        return minute;
    }

    @Override
    public int compareTo(Date other) {
        if (other == null) {
            return 1;
        }

        int yearComparison = Integer.compare(year, other.year);
        if (yearComparison != 0) {
            return yearComparison;
        }

        int monthComparison = Integer.compare(month, other.month);
        if (monthComparison != 0) {
            return monthComparison;
        }

        int dayComparison = Integer.compare(day, other.day);
        if (dayComparison != 0) {
            return dayComparison;
        }

        int thisHour = hour == null ? -1 : hour;
        int otherHour = other.hour == null ? -1 : other.hour;
        int hourComparison = Integer.compare(thisHour, otherHour);
        if (hourComparison != 0) {
            return hourComparison;
        }

        int thisMinute = minute == null ? -1 : minute;
        int otherMinute = other.minute == null ? -1 : other.minute;
        return Integer.compare(thisMinute, otherMinute);
    }

    @Override
    public String toString() {
        String dateString = String.format("%04d-%02d-%02d", year, month, day);
        if (hour == null || minute == null) {
            return dateString;
        }
        return dateString + " " + String.format("%02d:%02d", hour, minute);
    }

    private void validateDateParts(int year, int month, int day, Integer hour, Integer minute) {
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Month must be between 1 and 12.");
        }

        if (day < 1 || day > 31) {
            throw new IllegalArgumentException("Day must be between 1 and 31.");
        }

        if ((hour == null) != (minute == null)) {
            throw new IllegalArgumentException("Hour and minute must both be provided together.");
        }

        if (hour != null && (hour < 0 || hour > 23)) {
            throw new IllegalArgumentException("Hour must be between 0 and 23.");
        }

        if (minute != null && (minute < 0 || minute > 59)) {
            throw new IllegalArgumentException("Minute must be between 0 and 59.");
        }
    }
}
