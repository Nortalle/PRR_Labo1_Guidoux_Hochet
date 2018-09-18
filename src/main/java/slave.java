import org.joda.time.DateTime;

public class slave {

    int givenGap;
    int gap;
    int delay;

    /**
     * @return the current time plus the wanted gap
     */
    private int getTime(){
        return new DateTime().getMinuteOfDay() + givenGap;
    }

    public static void main(String[] args) {


    }
}
