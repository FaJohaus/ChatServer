package com.utils.chatutils;

import com.utils.colors.Colorsbg;
import com.utils.colors.Colorss;

public class rgbChat {

    // Variables

    private Colorss[] forgroundColor = null;
    private double speed = 1;
    private boolean active = true;
    private double position = 0.0d;

    // ----------------------------------------------------------------

    // Constructors

    public rgbChat() {

        forgroundColor = Colorss.values();

    }

    public rgbChat(int speed) {
        this.speed = speed;
    }

    public rgbChat(boolean active) {
        this.active = active;
    }

    public rgbChat(int speed, boolean active) {
        this.speed = speed;
        this.active = active;
    }

    public rgbChat(Colorss[] forgroundColor, int speed, boolean active) {
        this.forgroundColor = forgroundColor;

        this.speed = speed;
        this.active = active;
    }

    // ----------------------------------------------------------------

    /**
     * @param in is the String wich is going to be rbgt
     * @return returns a String with background color escape sequences
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
     * @return returns a string with background color escape sequences
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

    // ----------------------------------------------------------------
    // methods -------------------------------

    public String Contiues_RGB(String s) {

        if (active) {

            if (position > forgroundColor.length - 1)
                position = 0;

            s = forgroundColor[(int) position].getsit() + s;

            position += speed;

            return s;
        }

        else
            return s;

    }

    public Colorss[] getForgroundColor() {
        return forgroundColor;
    }

    public void setForgroundColor(Colorss[] forgroundColor) {
        this.forgroundColor = forgroundColor;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public double getPosition() {
        return position;
    }

    public void setPosition(double position) {
        this.position = position;
    }

    // ----------------------------------------------------------------
    // getters and
    // setters----------------------------------------------------------------

    // ----------------------------------------------------------------
    // Statics ----------------------------------------------------------------

}
