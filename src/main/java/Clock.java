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
 * Source       : https://www.javacodex.com/Swing/Digital-Clock
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

    private long delai = 0;
    private long ecart = 0; //dm2s

    private long offset = 0;

    public Clock(String name, long retard) {
        this.name = name;
        this.retard = retard;
        ClockLabel dateLable = new ClockLabel("date");
        ClockLabel timeLable = new ClockLabel("time");
        ClockLabel dayLable = new ClockLabel("day");

        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame f = new JFrame(name);
        f.setSize(300,150);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setLayout(new GridLayout(3, 1));

        f.add(dateLable);
        f.add(timeLable);
        f.add(dayLable);

        f.getContentPane().setBackground(Color.black);

        f.setVisible(true);
    }

    public Clock(String name) {
        this(name,0);
    }

    public long getCurrentTime() {
        return System.currentTimeMillis() + retard;
    }

    public long getCorrectedTime() {
        return getCurrentTime() - offset;
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
            Date d = new Date(getCorrectedTime());
            setText(sdf.format(d));
        }
    }
}
