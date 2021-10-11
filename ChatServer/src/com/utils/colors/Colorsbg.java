package com.utils.colors;

public enum Colorsbg {

    ANSI_BG_RED("\u001B[41m"), ANSI_BG_GREEN("\u001B[42m"), ANSI_BG_BLACK("\u001B[40m"), ANSI_BG_YELLOW("\u001B[43m"),
    ANSI_BG_BLUE("\u001B[44m"), ANSI_BG_PURPLE("\u001B[45m"), ANSI_BG_CYAN("\u001B[46m"), ANSI_BG_WHITE("\u001B[47m"),

    ANSI_BRIGHT_BG_RED("\u001B[101m"), ANSI_BRIGHT_BG_GREEN("\u001B[102m"), ANSI_BRIGHT_BG_BLACK("\u001B[100m"),
    ANSI_BRIGHT_BG_YELLOW("\u001B[103m"), ANSI_BRIGHT_BG_BLUE("\u001B[104m"), ANSI_BRIGHT_BG_PURPLE("\u001B[105m"),
    ANSI_BRIGHT_BG_CYAN("\u001B[106m"), ANSI_BRIGHT_BG_WHITE("\u001B[107m");

    private String col;

    private Colorsbg(String color) {
        this.col = color;
    }

    public String getsit() {
        return col;
    }
}
