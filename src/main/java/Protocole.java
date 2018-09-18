public class Protocole {

    public static final String SYNC = "SYNC";
    public static final String FOLLOW_UP = "FOLLOW_UP";

    public static String toSYNC(int id) {
        return SYNC + " " + id;
    }

    public static String toFOLLOW_UP(int masterTime, int id) {
        return FOLLOW_UP + " " + masterTime + " " + id;
    }

    public static String fromSYNC(String msg){
        return msg.split(" ")[1];
    }

    public static String[] fromFOLLOW_UP(String msg) {
        String tab[] = msg.split(" ");

        return tab;
    }
}
