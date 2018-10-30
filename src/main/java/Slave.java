/*
 * File         : Slave.java
 * Labo         : Labo_1_Synchronisation_Horloges
 * Project      : PRR_Labo1_Guidoux_Hochet
 * Authors      : Hochet Guillaume 30 octobre 2018
 *                Guidoux Vincent 30 octobre 2018
 *
 * Description  :
 *
 */

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.logging.Logger;

public class Slave extends Thread {

    private Logger log = Logger.getLogger("Slave");
    private InetAddress masterAddress;
    private DatagramSocket socket;
    private Clock slaveClock;
    private Integer k;
    private SlaveListener listener;

    public Slave(String masterAddress, Integer k, Integer retard) {
        try {
            this.listener = new SlaveListener();
            this.masterAddress = InetAddress.getByName(masterAddress);
            this.slaveClock = new Clock("slave",(retard));
            this.socket = new DatagramSocket();
            this.k = k;

            log.info("Slave online");
        } catch (Exception e) {
            log.severe("Failed starting slave socket");
        }
    }

    public void run() {
        while (true) {
            try {

                sleep(random(this.k * 1, this.k * 2));
                delayRequest();
                log.info("CurrentTime: " + slaveClock.getCorrectedTime());

            } catch (InterruptedException e) {
                log.severe("Slave failed sleeping");
            }
        }
    }

    private int random(int min, int max) {
        return (int) (Math.random() * ((max - min) + 1)) + min;
    }

    private String checkPayload() {
        return "swag";
    }

    private void delayRequest() {
        try {
            log.info("delayRequest()");
            // Build request packet, which includes DELAY_REQUEST and a check payload
            String check = checkPayload();
            byte[] request = (Protocol.DELAY_REQUEST + check).getBytes();
            DatagramPacket packetToMaster = new DatagramPacket(request, request.length, masterAddress, Protocol.POINT_TO_POINT);
            long localTimeSentRequest = slaveClock.getCurrentTime(); // Tes //t3
            socket.send(packetToMaster);

            // Wait for server response
            byte[] response = new byte[1024];
            DatagramPacket responsePacket = new DatagramPacket(response, response.length, masterAddress, Protocol.POINT_TO_POINT);
            socket.receive(responsePacket);

            // Parse response, extract milliseconds
            String msg = new String(responsePacket.getData(), 0, responsePacket.getLength());


            if (msg.substring(0, Protocol.DELAY_RESPONSE.length()).equals(Protocol.DELAY_RESPONSE)) {

                String payload = msg.substring(Protocol.DELAY_RESPONSE.length());

                if (payload.startsWith(check)) {
                    long serverRequestArriveTime = Long.valueOf(payload.substring(check.length())); // Tm
                    long delai = (serverRequestArriveTime - localTimeSentRequest); //ds2m
                    slaveClock.setDelai(delai);


                    log.info(" offset found : " + slaveClock.getOffset());
                } else {
                    log.warning("didnt get correct check, sent [" + check + "], got [" + payload + "]");
                }
            } else {
                log.warning("didnt get a valid SERVER_RESPONSE");
            }

        } catch (IOException e) {
            log.severe("Failed during delay_request operation");
        }
    }

    class SlaveListener extends Thread {

        private MulticastSocket multicastSocket;
        byte[] buffer = new byte[1024];
        InetAddress group;

        SlaveListener() {
            try {
                multicastSocket = new MulticastSocket(Protocol.MULTICAST_PORT);
                group = InetAddress.getByName(Protocol.MULTICAST_ADDRESS);
                multicastSocket.joinGroup(group);
                multicastSocket.setInterface(InetAddress.getLocalHost());

                start();

            } catch (IOException e) {
                log.severe("Slave listener failed starting");
            }
        }

        public void run() {

            while (true) try {
                // Receive multicast packet
                log.info("waiting for message");

                DatagramPacket syncPacket = new DatagramPacket(buffer, buffer.length);
                multicastSocket.receive(syncPacket);
                String syncId = "";

                // Note time at which we got sync message
                long syncTime = slaveClock.getCurrentTime();

                // Expect it to be SYNC
                String syncMsg = new String(syncPacket.getData(), 0, syncPacket.getLength());
                if (syncMsg.substring(0, Protocol.SYNC.length()).equals(Protocol.SYNC)) {
                    syncId = (syncMsg.substring(Protocol.SYNC.length()));
                } else {
                    log.warning("got invalid multicast sync: " + syncMsg);
                }

                // Wait for follow_up
                DatagramPacket followupPacket = new DatagramPacket(buffer, buffer.length);
                multicastSocket.receive(followupPacket);
                String followupMsg = new String(followupPacket.getData(), 0, followupPacket.getLength());

                // Expect it to be follow up
                if (followupMsg.startsWith(Protocol.FOLLOW_UP)) {
                    String followupPayload = (followupMsg.substring(Protocol.FOLLOW_UP.length()));
                    String followupId = followupPayload.substring(followupPayload.length() - syncId.length());

                    if (followupId.equals(syncId)) {

                        // Retrieve master time from sync and determine ecart
                        long tSyncMaster = Long.valueOf((followupPayload.substring(0, followupPayload.length() - syncId.length())).trim());
                        long ecart = syncTime - tSyncMaster; //dm2s

                        // Sync clock
                        slaveClock.setEcart(ecart);
                        log.info("multicast got tMaster post followup : " + ecart);
                        if (!Slave.this.isAlive()) {

                            Slave.this.start();
                            delayRequest();
                        }
                    } else {
                        log.warning("multicast follow up id check failed! [" + syncId + "] - [" + followupId + "]: " + followupMsg);
                    }
                } else {
                    log.warning("got invalid multicast followup: " + followupMsg);
                }

            } catch (Exception e) {
                log.severe("Failed receiving a packet : " + e.getMessage());
            }
        }
    }

    public static void main(String... args) {

        Slave slave = new Slave("192.168.43.49", 2000, 1000000000);
    }
}
