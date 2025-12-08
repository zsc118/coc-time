package com.example.coctime;

import java.util.Calendar;

public class Time implements Comparable<Time> {
    public int data;

    /**
     * 构造函数
     *
     * @param year   公元纪年 - 2025 (20+)
     * @param month  0 ~ 11 (4 bit, 16 - 19)
     * @param day    1 ~ 31 (5 bit, 11 - 15)
     * @param hour   0 ~ 23 (5 bit, 6 - 10)
     * @param minute 0 ~ 59 (6 bit, 0 - 5)
     */
    public Time(byte year, byte month, byte day, byte hour, byte minute) {
        data = year << 20 | month << 16 | day << 11 | hour << 6 | minute;
    }

    public Time(int data) {
        this.data = data;
    }

    public byte year() {
        return (byte) (data >> 20);
    }

    public byte month() {
        return (byte) (data >> 16 & 0x0f);
    }

    public byte day() {
        return (byte) (data >> 11 & 0x1f);
    }

    public byte hour() {
        return (byte) (data >> 6 & 0x1f);
    }

    public byte minute() {
        return (byte) (data & 0x3f);
    }

    Calendar toCalendar() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year() + 2025, month(), day(), hour(), minute(), 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    // 从 1970 年 1 月 1 日 00:00:00 开始的毫秒时间戳
    public long toMillis() {
        return toCalendar().getTimeInMillis();
    }

    public String format() {
        byte M = (byte) (month() + 1), d = day(), h = hour(), m = minute();
        return String.valueOf(new char[]{(char) (M / 10 + '0'), (char) (M % 10 + '0'), '月', (char) (d / 10 + '0'), (char) (d % 10 + '0'), '日', ' ', (char) (h / 10 + '0'), (char) (h % 10 + '0'), ':', (char) (m / 10 + '0'), (char) (m % 10 + '0')});
    }

    Time(Calendar calendar) {
        set(calendar);
    }

    void set(Calendar calendar) {
        data = calendar.get(Calendar.YEAR) - 2025 << 20 | calendar.get(Calendar.MONTH) << 16 | calendar.get(Calendar.DAY_OF_MONTH) << 11 | calendar.get(Calendar.HOUR_OF_DAY) << 6 | calendar.get(Calendar.MINUTE);
    }

    public void set(long millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        set(calendar);
    }

    public Time plus(int minute) {
        set(toMillis() + minute * 60000L);
        return this;
    }

    public Time minus(int minute) {
        set(toMillis() - minute * 60000L);
        return this;
    }

    // t = "ddhhmm"
    public static Time getByLag(String t) {
        if (t == null || t.length() != 6) return null;
        char[] a = t.toCharArray();
        if (!Character.isDigit(a[0]) || !Character.isDigit(a[1]) || !Character.isDigit(a[2]) || !Character.isDigit(a[3]) || !Character.isDigit(a[4]) || !Character.isDigit(a[5]))
            return null;
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.setTimeInMillis(calendar.getTimeInMillis() + ((((a[0] - '0') * 10 + a[1] - '0') * 24 + (a[2] - '0') * 10 + a[3] - '0') * 60 + (a[4] - '0') * 10 + a[5] - '0') * 60000L);
        return new Time(calendar);
    }

    public static Time getByLag(int minutes) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Calendar.getInstance().getTimeInMillis()+minutes*60000L);
        return new Time(calendar);
    }

    public static Time getCurrent() {
        return new Time(Calendar.getInstance());
    }

    public static int minutesBetween(Time t1, Time t2) {
        return (int) ((t2.toMillis() - t1.toMillis()) / 60000L);
    }

    @Override
    public boolean equals(Object o) {
        return o != null && getClass() == o.getClass() && data == ((Time) o).data;
    }

    @Override
    public int hashCode() {
        return data;
    }

    @Override
    public int compareTo(Time time) {
        return Integer.compare(data, time.data);
    }
}
