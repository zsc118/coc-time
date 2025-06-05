package com.example.coctime;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@RequiresApi(api = Build.VERSION_CODES.O)
public class Item implements Comparable<Item>, Serializable {
    private static final long serialVersionUID = 1L;
    public static final byte ACC_EPSILON = 0;
    public static final byte ACC_DELTA = 1;
    public static final String[] TYPE_NAME = new String[]{"家乡建筑", "家乡科技", "夜世界", "其它"};
    public static final String[] ACCOUNT_NAME = new String[]{"epsilon", "delta"};
    public static final byte TYPE_HOME_BUILDING = 0;
    public static final byte TYPE_HOME_LAB = 1;
    public static final byte TYPE_NIGHT = 2;
    public static final byte TYPE_OTHER = 3;
    static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM月dd日 HH:mm");

    public Item(byte account, String project, String time, byte type) {
        this.account = account;
        this.project = project;
        this.type = type;
        setDate(time);
    }

    public Item(byte account, String project, LocalDateTime time, byte type) {
        this.account = account;
        this.project = project;
        this.type = type;
        this.time = time;
    }

    public byte account, type;
    public String project;
    public LocalDateTime time;

    public static LocalDateTime str2date(String t) {
        if (t == null||t.length()!=6) return null;
        char[] a = t.toCharArray();
        return Character.isDigit(a[0]) && Character.isDigit(a[1]) && Character.isDigit(a[2]) && Character.isDigit(a[3]) && Character.isDigit(a[4]) && Character.isDigit(a[5]) ? LocalDateTime.now(ZoneId.systemDefault()).plusDays((a[0] - '0') * 10 + a[1] - '0').plusHours((a[2] - '0') * 10 + a[3] - '0').plusMinutes((a[4] - '0') * 10 + a[5] - '0') : null;
    }

    public void setDate(String t) {
        time = str2date(t);
    }

    public String getTimeStr() {
        return time.format(formatter);
    }

    public String getText() {
        return ACCOUNT_NAME[account] + "：" + project + " (" + TYPE_NAME[type] + ")";
    }

    @Override
    public int compareTo(Item item) {
        return time.compareTo(item.time);
    }
}