import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.logging.Logger;

public class Slave extends Thread {

    private Logger log = Logger.getLogger("Slave");
    private InetAddress masterAddress;
    private Integer masterPort;
    private DatagramSocket socket;
    private Clock slaveClock;
    private Integer k;
    private SlaveListener listener;

    public Slave(String masterAddress, Integer masterPort, Integer k, Integer secondDuration) {
        try {
            this.listener = new SlaveListener();
            this.masterAddress = InetAddress.getByName(masterAddress);
            this.masterPort = masterPort;
            this.slaveClock = new Clock(secondDuration);
            this.socket = new DatagramSocket();
            this.k = k;

            log.info("Slave online");
        } catch (Exception e) {
            log.severe("Failed starting slave socket");
        }
    }

    public void run() {
        while(true) {
            try {

                sleep(random(this.k*4, this.k*60));
                delayRequest();

            } catch (InterruptedException e) {
                log.severe("Slave failed sleeping");
            }
        }
    }

    private int random(int min, int max) {
        return (int)(Math.random() * ((max - min) + 1)) + min;
    }

    private String checkPayload() {
        return "swag";
    }

    private void delayRequest() {
        try {
            // Build request packet, which includes DELAY_REQUEST and a check payload
            String check = checkPayload();
            byte[] request = (Protocol.DELAY_REQUEST + check).getBytes();
            DatagramPacket packetToMaster = new DatagramPacket(request, request.length, masterAddress, masterPort);
            long localTimeSentRequest = slaveClock.getTime(); // Tes
            socket.send(packetToMaster);

            // Wait for server response
            byte[] response = new byte[1024];
            DatagramPacket responsePacket = new DatagramPacket(response, response.length);
            socket.receive(responsePacket);

            // Parse response, extract milliseconds
            String msg = new String(responsePacket.getData());

            if(msg.substring(0, Protocol.DELAY_RESPONSE.length()).equals(Protocol.DELAY_RESPONSE)) {

                String payload = msg.substring(Protocol.DELAY_RESPONSE.length());
                String receivedCheck = payload.substring(0, check.length());

                if(receivedCheck.equals(check)) {
                    Integer serverRequestArriveTime = Integer.valueOf(payload.substring(check.length())); // Tm
                    Integer delai = (serverRequestArriveTime - localTimeSentRequest) / 2;
                    slaveClock.setDelay(delai);
                }
                else {
                    log.warning("didnt get correct check, sent [" + check + "], got [" + receivedCheck + "]");
                }
            }
            else {
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
            while(true) {
                try {
                    // Receive multicast packet
                    log.info("waiting for message");

                    DatagramPacket syncPacket = new DatagramPacket(buffer, buffer.length);
                    multicastSocket.receive(syncPacket);
                    String syncId = "";

                    // Note time at which we got sync message
                    long syncTime = slaveClock.getTime();

                    // Expect it to be SYNC
                    String syncMsg = new String(syncPacket.getData());
                    if(syncMsg.substring(0, Protocol.SYNC.length()).equals(Protocol.SYNC)) {
                        syncId = syncMsg.substring(Protocol.SYNC.length());
                    }
                    else {
                        log.warning("got invalid multicast sync: " + syncMsg);
                    }

                    // Wait for follow_up
                    DatagramPacket followupPacket = new DatagramPacket(buffer, buffer.length);
                    multicastSocket.receive(followupPacket);
                    String followupMsg = new String(followupPacket.getData());


                    // Expect it to be follow up
                    if(followupMsg.substring(0, Protocol.FOLLOW_UP.length()).equals(Protocol.FOLLOW_UP)) {
                        String followupPayload = followupMsg.substring(Protocol.FOLLOW_UP.length());
                        String followupId = followupPayload.substring(0, syncId.length());

                        if(followupId.equals(syncId)) {

                            // Retrieve master time from sync and determine ecart
                            long tSyncMaster = Integer.valueOf(followupPayload.substring(followupId.length()));
                            long ecart = Math.abs(tSyncMaster - syncTime);

                            // Sync clock
                            slaveClock.setEcart(ecart);
                            log.info("multicast got tMaster post followup");

                        }
                        else {
                            log.warning("multicast follow up id check failed! ["+syncId+"] - ["+followupId+"]: " + followupMsg);
                        }
                    }
                    else {
                        log.warning("got invalid multicast followup: " + followupMsg);
                    }

                } catch (Exception e) {
                    log.severe("Failed receiving a packet");
                }
            }
        }
    }
}
