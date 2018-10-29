import org.joda.time.DateTime;

public class Clock {

    private long retard = 0;
    private long delai = 0;
    private long ecart = 0;

    public Clock(long retard) {
        this.retard = retard;
    }

    public Clock() {
        this(0);
    }

    public long getCurrentTime() {
        return System.currentTimeMillis() + retard;
    }

    public long getCorrectedTime() {
        return getCurrentTime() + delai + ecart;
    }

    public void setDelai(long delai) {
        this.delai = delai;
    }

    public long getEcart() {
        return ecart;
    }

    public void setEcart(long ecart) {
        this.ecart = ecart;
    }
}
