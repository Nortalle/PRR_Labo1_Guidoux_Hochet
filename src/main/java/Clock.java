/*
 * File         : Clock.java
 * Labo         : Labo_1_Synchronisation_Horloges
 * Project      : PRR_Labo1_Guidoux_Hochet
 * Authors      : Hochet Guillaume 30 octobre 2018
 *                Guidoux Vincent 30 octobre 2018
 *
 * Description  : We implemented a clock with some attribute to handle
 *                delay, gaps... etc We used french for the variables to be consistent with
 *                the lab
 *
 */

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
        this.offset = -(ecart - d);
    }

    public long getOffset() {
        return offset;
    }

}
