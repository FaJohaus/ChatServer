package com.utils.chatutils;

public class j {

    public static void main(String[] args) {
        rgbChat a = new rgbChat();
        a.setSpeed(0.5f);
        for (int i = 0; i < 100; i++) {

            String c = a.Contiues_RGB("Hallo");

            System.out.println(c + " " + a.getPosition());

        }
    }
}
