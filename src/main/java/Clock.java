/*
 * File         : Clock.java
 * Labo         : Labo_1_Synchronisation_Horloges
 * Project      : PRR_Labo1_Guidoux_Hochet
 * Authors      : Hochet Guillaume 30 octobre 2018
 *                Guidoux Vincent 30 octobre 2018
 *
 * Description  : We implemented a clock with some attribute to handle
 *                delay, gaps, given delay from other pc(retard) etc We used french for the variables to be consistent
 *                with the lab. It also give a GUI to display the clock
 * Source       : https://www.javacodex.com/Swing/Digital-Clock
 *                https://fr.wikipedia.org/wiki/Precision_Time_Protocol#D%C3%A9lai_aller
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Clock {

    private String name = "";

    private long retard = 0;

    private long delai = 0; //ds2m
    private long ecart = 0; //dm2s

    private long offset = 0;

    /**
     * constructs a clock with a given delay and a name
     *
     * @param name   : name of the clock
     * @param retard : given delay, to test the algorithm
     */
    public Clock(String name, long retard) {
        this.name = name;
        this.retard = retard;
        ClockLabel dateLable = new ClockLabel("date");
        ClockLabel timeLable = new ClockLabel("time");
        ClockLabel dayLable = new ClockLabel("day");

        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame f = new JFrame(name);
        f.setSize(300, 150);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setLayout(new GridLayout(3, 1));

        f.add(dateLable);
        f.add(timeLable);
        f.add(dayLable);

        f.getContentPane().setBackground(Color.black);

        f.setVisible(true);
    }

    /**
     * Constructs a clock with a given name and 0 delay at the starts
     *
     * @param name
     */
    public Clock(String name) {
        this(name, 0);
    }

    /**
     * @return the local time of the slave
     */
    public long getCurrentTime() {
        return System.currentTimeMillis() + retard;
    }

    /**
     * @return the corrected time with the master
     */
    public long getCorrectedTime() {
        return getCurrentTime() - offset;
    }

    /**
     * set the delay, it's the difference that occurs between the slave and the master
     * based on the emission and transmission.
     *
     * @param delai : delay calculated at the second part.
     */
    public synchronized void setDelai(long delai) {
        this.delai = delai;
        updateOffset();
    }

    /**
     * set the gap between the slave and the master.
     *
     * @param ecart : gap between the slave and the master
     */
    public synchronized void setEcart(long ecart) {
        this.ecart = ecart;
        updateOffset();
    }

    /**
     * Update the offset
     */
    private synchronized void updateOffset() {
        //d = (dm2s + ds2m) / 2
        long d = (ecart + delai) / 2;
        //Δt = dm2s - d
        this.offset = ecart - d;
    }

    /**
     * @return the difference between slave and master
     */
    public long getOffset() {
        return offset;
    }

    /**
     * A clock label that display the date, not implemented by us.
     */
    class ClockLabel extends JLabel implements ActionListener {

        final String type;
        SimpleDateFormat sdf;

        public ClockLabel(String type) {
            this.type = type;
            setForeground(Color.green);

            if ("date".equals(type)) {
                sdf = new SimpleDateFormat("  MMMM dd yyyy");
                setFont(new Font("sans-serif", Font.PLAIN, 12));
                setHorizontalAlignment(SwingConstants.LEFT);

            } else if ("time".equals(type)) {
                sdf = new SimpleDateFormat("hh:mm:ss a");
                setFont(new Font("sans-serif", Font.PLAIN, 40));
                setHorizontalAlignment(SwingConstants.CENTER);

            } else if ("day".equals(type)) {
                sdf = new SimpleDateFormat("EEEE  ");
                setFont(new Font("sans-serif", Font.PLAIN, 16));
                setHorizontalAlignment(SwingConstants.RIGHT);

            } else {
                sdf = new SimpleDateFormat();

            }

            Timer t = new Timer(50, this);
            t.start();
        }

        public void actionPerformed(ActionEvent ae) {
            //We just change that line
            Date d = new Date(getCorrectedTime());
            setText(sdf.format(d));
        }
    }
}
