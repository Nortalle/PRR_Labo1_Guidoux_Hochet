import org.joda.time.DateTime;

public class Clock {

    private long retard = 0;

    private long delai = 0;
    private long ecart = 0; //dm2s

    private long offset = 0;

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
        return getCurrentTime() + retard + ecart;
    }

    public void setDelai(long delai) {
        this.delai = delai;
        updateOffset();
    }

    public long getEcart() {
        return ecart;
    }

    public void setEcart(long ecart) {
        this.ecart = ecart;
        updateOffset();
    }

    public long getDelai() {
        return delai;
    }

    private void updateOffset(){

        long d = (ecart + delai) / 2;
        this.offset = ecart - d;
    }

    public long getOffset() {
        return offset;
    }

}
