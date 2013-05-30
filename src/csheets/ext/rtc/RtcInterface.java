package csheets.ext.rtc;

/**
 * This interface is the bridge between clients and server
 * 
 * @author gil_1110484
 */
public interface RtcInterface extends RtcListener {
    static int PORT = 12345;

    /**
     * Used by each side of the communication, another RtcListener must be
     * implemented in order to react to events on the other side
     * 
     * @param listener
     *            another implementation of RtcListener
     */
    public void setListener(RtcListener listener);
}
