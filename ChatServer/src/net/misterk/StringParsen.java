package net.misterk;

public class StringParsen {
    public static void erkennungBefehl() {
        String eingabe = "!nick tom";
        if (eingabe.startsWith("!")){
            System.out.println("Befehl erkannt!");
            //welcherBefehl(eingabe);
        } else {
            System.out.println("Kein Befehel erkannt");
        }
    }

    public static void welcherBefehl(){
        // Reguläre Ausdrücke, RegEx
        String eingabe =  "!sendTo Steffen Was geht ab?";
        String patternSendTo = "^!sendTo ([A-Za-z]+) (.*)";
        String patternNick = "^!nick ([A-Za-z]+)";
        String patternColour = "^!colour ([A-Za-z]+) (.*)";

        if(eingabe.matches(patternSendTo)){
            // führe auf dem Server sendTo(user, msg); aus
            System.out.println("sendto");
        } else if(eingabe.matches(patternNick)){
            // führe auf dem Server renameUser(user, newName); aus
            System.out.println("renameuser");
        } else if(eingabe.matches(patternColour)){
            // ändere im Client die Farbe auf "colour"
            System.out.println("colour");
        } else {
            // sende die Eingabe an alle
            System.out.println("sendtoall");
        }
    }

    public static void main(String[] args) {
        welcherBefehl();
    }

}
