package com.utils;

import java.util.regex.Pattern;

import org.apache.commons.lang3.RegExUtils;

/**
 * stringUtil
 */
public class stringUtil {

    public static String cmd_Remover(String[] a) {
        final Pattern pat  = Pattern.compile("/[a-zA-Z]+ ")
        
        
        String b = String.join(" ", a);


        b.replaceAll("/[a-zA-Z]+", "");

        return "";
    }
}