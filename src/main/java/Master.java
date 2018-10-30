/*
 * File         : Master.java
 * Labo         : Labo_1_Synchronisation_Horloges
 * Project      : PRR_Labo1_Guidoux_Hochet
 * Authors      : Hochet Guillaume 30 octobre 2018
 *                Guidoux Vincent 30 octobre 2018
 *
 * Description  : Implements a master that dialog with slaves with UDP to sync them with the
 *                master's clock.
 *
 * Source       : https://fr.wikipedia.org/wiki/Precision_Time_Protocol#D%C3%A9lai_aller
 *
 */

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.logging.Logger;

public class Master extends Thread {

    private Clock masterClock = new Clock("master");
    private MulticastSocket multicastSocket;
    private InetAddress multicastAddress;
    private DatagramSocket masterSocket;

    private Integer id = 0;
    private Integer k;

    private Logger log = Logger.getLogger("Master");

    /**
     * @param k : delay between multicast
     */
    public Master(Integer k) {

        this.k = k;

        try {
            multicastAddress = InetAddress.getByName(Protocol.MULTICAST_ADDRESS);
            masterSocket = new DatagramSocket(Protocol.POINT_TO_POINT);
            multicastSocket = new MulticastSocket();
            multicastSocket.setInterface(InetAddress.getLocalHost());
            multicastSocket.joinGroup(multicastAddress);
            start(); //starts the first part of the protocol to sync slaves (SYNC/FOLLOW_UP)

            // listen to packets from slaves
            MasterListener listener = new MasterListener();
            listener.start();

            log.info("Master online");
        } catch (IOException e) {
            log.severe("Failed starting master socket");
        }
    }

    /**
     * Threads that runs the first part of the protocol to sync slaves (SYNC/FOLLOW_UP)
     */
    public void run() {

        long tMaitre;

        while(true) {
            try {
                this.id += 1;
                tMaitre = masterClock.getCurrentTime();

                // Send sync
                this.sendMulticast(Protocol.SYNC + this.id);

                // Send follow_up
                // Network is supposed without failures, thus slaves can get back tMaitre by removing
                // FOLLOW_UP and previously given id
                this.sendMulticast(Protocol.FOLLOW_UP + tMaitre + this.id);

                sleep(this.k);

            } catch(IOException e) {
                log.severe("Failed sending multicast payload");
            } catch (InterruptedException e) {
                log.severe("sleep failed!");
            }
        }
    }

    /**
     * Send the given payload to a multicast group
     *
     * @param payload       : payload to send to the multicast group
     * @throws IOException  : if something bad happen during the multicasting
     */
    private void sendMulticast(String payload) throws IOException {

        // Build sync payload
        byte[] buf = payload.getBytes();
        DatagramPacket packet = new DatagramPacket(buf, buf.length, multicastAddress, Protocol.MULTICAST_PORT);
        log.info("Sending multicast payload " + payload);
        multicastSocket.send(packet);
    }

    /**
     * Threads needed to the second part of the protocol
     */
    class MasterListener extends Thread {

        private byte[] buffer = new byte[1024];

        MasterListener() {
            log.info("Master listener listening");
        }

        /**
         * second part of the protocol IEEE 1588
         */
        public void run() {
            while(true) {
                try {
                    // Wait te receive DELAY_REQUEST
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    masterSocket.receive(packet);
                    long tMaster = masterClock.getCurrentTime();
                    String msg = new String(packet.getData(), 0, packet.getLength());

                    // Got a delay request
                    // Verify that it's actually a delay request, and isolate checking payload
                    if(msg.substring(0, Protocol.DELAY_REQUEST.length()).equals(Protocol.DELAY_REQUEST)) {

                        // Extract slave check payload
                        String checkPayload = msg.substring(Protocol.DELAY_REQUEST.length());

                        if(checkPayload.isEmpty())
                            log.warning("Got delay_request without check payload");

                        // get slave information
                        InetAddress slaveAddress = packet.getAddress();
                        Integer slavePort = packet.getPort();

                        //Send back delay_response which contains DELAY_RESPONSE + (given check payload) + (received milliseconds)
                        byte[] response = (Protocol.DELAY_RESPONSE + checkPayload + tMaster).getBytes();
                        DatagramPacket slavePacket = new DatagramPacket(response, response.length, slaveAddress, slavePort);
                        masterSocket.send(slavePacket);
                    }

                } catch(IOException e) {
                    log.severe("failed receiving message");
                }
            }
        }
    }

    public static void main(String[] args) {
        Master master = new Master(2000);
    }
}
