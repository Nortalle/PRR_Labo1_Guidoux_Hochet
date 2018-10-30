/*
 * File         : Protocol.java
 * Labo         : Labo_1_Synchronisation_Horloges
 * Project      : PRR_Labo1_Guidoux_Hochet
 * Authors      : Hochet Guillaume 30 octobre 2018
 *                Guidoux Vincent 30 octobre 2018
 *
 * Description  : This is the protocol used by the Slave and the Master
 *
 */

public class Protocol {

    //Messages should be as short as possible. (Strings do not
    //should not be a substitute for numbers!) You will need to work with bytes.
    //So we made "SYNC" and "FOLLOW_UP", if there wasn't the phrase : (Strings do not
    //should not be a substitute for numbers!) We coded : byte SYNC = 0, FOLLOW_UP = 1;
    //For the step 1 (SYNC/FOLLOW_UP)
    static final Integer MULTICAST_PORT     = 4445;
    static final String MULTICAST_ADDRESS   = "228.5.6.7";
    static final String SYNC                = "SYNC";
    static final String FOLLOW_UP           = "FOLLOW_UP";
    //For the step 2 (Delay calcul)
    static final Integer POINT_TO_POINT     = 4443;
    static final String DELAY_REQUEST       = "DELAY_REQUEST";
    static final String DELAY_RESPONSE      = "DELAY_RESPONSE";
}
