import org.joda.time.DateTime;

public class master {


    /**
     * @return the current time plus the wanted gap
     */
    private int getTime(){
        return new DateTime().getMinuteOfDay();
    }

    public static void main(String[] args) {

        int id = 0;

        do {
            id++;

        }while(true);

    }
}
