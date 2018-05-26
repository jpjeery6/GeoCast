package jeeryweb.geocast.Constants;

/**
 * Created by Debo#Paul on 5/2/2018.
 */

public class APIEndPoint {
    static public final String lgIn = "http://geocast.in/login.php?mode=android";      //auth
    static public final String migL = "http://geocast.in/migrate.php?mode=android";    //auth
    static public final String reg ="http://geocast.in/register.php?mode=android";     //auth
    static public final String url_uplaod = "http://geocast.in/saveProPic.php?mode=android";    //user
    static public final String updateLoc = "http://geocast.in/updateLoc.php?mode=android";      //Locutil
    static public final String nearbyusers = "http://geocast.in/getUserLocationsRealTime.php?mode=android";   //model
    static public final String sendMsg = "http://geocast.in/uploadMsgUsingPushy.php?mode=android";   //uploadMsg
    static public final String updateFcm = "http://geocast.in/updateFcm.php?mode=android";     //firebase
    static public final String lgot = "http://geocast.in/logout.php?mode=android";  //auth
    static public final String pullMsg = "http://geocast.in/pullMsg.php?mode=android";  //model
    static public final String sendreliable = "http://geocast.in/setReliability.php?mode=android";   //user
    static public final String getPPSummary = "http://geocast.in/getSummary.php?mode=android";       //model
    static public final String sendreliableresconf = "http://geocast.in/acceptDenyReliability.php?mode=android";   //reliablility
    static public final String updatePushy = "http://geocast.in/updatePushy.php?mode=android";    //Puhsy
    static public final String getAllMessages = "http://geocast.in/getAllMessages.php?mode=android";   //model
    static public final String getAllSentMessage = "http://geocast.in/getAllSentMessages.php?mode=android";   //model
    static public final String getReliabilities = "http://geocast.in/getReliabilities.php?mode=android";    //model
    static public final String getReliableConnections="http://geocast.in/getReliableConnections.php?mode=android"; //model
    static public final String msgAck="http://geocast.in/msgAck.php?mode=android";    //

}