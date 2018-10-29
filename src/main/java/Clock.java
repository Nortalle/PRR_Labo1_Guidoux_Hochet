import org.joda.time.DateTime;

public class Clock {

    long delay_init_ms;
    long delay_ms;


    public Clock(int delay_init_ms) {
        this.delay_init_ms = delay_init_ms;
    }

    public long getTime(){
        return System.currentTimeMillis() + this.delay_init_ms;
    }

    public void setDelay(long delay_ms) {
        this.delay_ms = delay_ms;
    }
}
