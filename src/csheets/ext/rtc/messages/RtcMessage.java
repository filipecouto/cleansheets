package csheets.ext.rtc.messages;

import java.io.Serializable;
import java.net.InetAddress;


/**
 * This class is the main communication mean, instances are sent across the
 * connection in order to communicate with the others.
 * 
 * @author gil_1110484
 */
public class RtcMessage implements Serializable {
    private static final long serialVersionUID = -1845883260814635491L;

    private byte[] from;
    private MessageTypes type;
    private Serializable argument;

    public RtcMessage(InetAddress from, MessageTypes message,
	    Serializable argument) {
	if (from != null)
	    this.from = from.getAddress();
	this.type = message;
	this.argument = argument;
    }

    /**
     * Gets the message type of this message
     * 
     * @return a MessageType, the argument will depend on the type
     */
    public MessageTypes getMessageType() {
	return type;
    }

    /**
     * Gets the argument of this message. This argument will depend on the type
     * of the message. This argument may be a ClientInfo, a RemoteCell, and so
     * on...
     * 
     * @return any object, corresponding to the type of the message
     */
    public Serializable getArgument() {
	return argument;
    }

    /**
     * Gets the address of who sent this message
     * 
     * @return the address
     */
    public byte[] getSenderAddress() {
	return from;
    }
}
