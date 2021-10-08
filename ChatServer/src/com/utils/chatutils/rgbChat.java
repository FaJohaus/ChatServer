package com.utils.chatutils;

import java.util.Arrays;

import com.utils.colors.*;

public class rgbChat {

    /**
     * @param in is the String wich is going to be rbgt
     * @return retruns a string with differt colors
     */
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

    /**
     * 
     * @param in
     * @return
     */
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

    public static void main(String[] args) {
        System.out.println(rgbb("Hallo du kek ich mag dich nicht"));
        System.out.println(rgbf("Hallo du kek ich mag dich nicht"));
    }
}
