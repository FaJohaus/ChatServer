package com.utils.chatutils;

import java.util.Arrays;

import com.utils.colors.*;

public class rgbChat {

    public static String rgbf(String in) {
        String out = "";
        int c = Colorss.values().length;
        int g = 0;
        for (int i = 0; i < in.length(); i++) {

            if (g == c - 1) {
                g = 0;
            }

            out += Colorss.values()[g].getsit() + in.charAt(i);
            g++;
        }

        return out;
    }

    public static String rgbb(String in) {
        String out = "";
        int c = Colorsbg.values().length;
        int g = 0;
        for (int i = 0; i < in.length(); i++) {

            if (g == c - 1) {
                g = 0;
            }

            out += Colorsbg.values()[g].getsit() + in.charAt(i);
            g++;
        }

        return out;
    }

    // public static String rgba(String in) {
    // String out = "";

    // int c = 0;
    // int e = 0;
    // int ce = Colorsbg.values().length;
    // int ee = Colorss.values().length;

    // for (int i = 0; i < in.length(); i++) {
    // if (c == ce - 1) {
    // c = 0;
    // }
    // if (e == ee - 1) {
    // e = 0;
    // }

    // out += Colorsbg.values()[c].getsit() + Colorss.values()[e].getsit() +
    // in.charAt(i);

    // }

    // return out;
    // }

    public static void main(String[] args) {
        System.out.println(rgbb("Hallo du kek ich mag dich nicht"));
        System.out.println(rgbf("Hallo du kek ich mag dich nicht"));
    }
}
