package com.utils;

import java.util.Arrays;

/**
 * cmdUtil
 */

public class cmdUtil {

    /**
     * 
     * @param a
     * @return
     */
    public static String cmd_RemovertoString(String[] a) {

        String[] res = Arrays.copyOfRange(a, 1, a.length - 1);
        String resa = String.join(" ", res);
        return resa;
    }

    /**
     * 
     * @param in
     * @param start
     * @param end
     * @return
     */
    public static String cmd_RemovertoString(String[] in, int start, int end) {

        String[] res = Arrays.copyOfRange(in, start, in.length - 1);
        String resa = String.join(" ", res);
        return resa;
    }

    /**
     * 
     * @param in
     * @return
     */
    public static String[] cmd_Remover(String[] in) {

        String[] res = Arrays.copyOfRange(in, 0, in.length - 1);

        return res;
    }

    /**
     * 
     * @param in
     * @param start
     * @param end
     * @return
     */
    public static String[] cmd_Remover(String[] in, int start, int end) {

        String[] res = Arrays.copyOfRange(in, start, in.length - 1);

        return res;
    }
}