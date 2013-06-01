package csheets.ext.rtc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import csheets.ext.rtc.messages.MessageTypes;
import csheets.ext.rtc.messages.RtcMessage;

/**
 * This class provides helper methods for ClientInterface and Client classes
 * especially for sending and receiving data
 * 
 * <i>After some tests, we found out ObjectInputStream and/or ObjectOutputStream
 * aren't reliable for sending and receiving data over network, therefore we're
 * converting the objects and then sending them in a lower level and passing by
 * the size of each object</i>
 * 
 * @author gil_1110484
 */
public abstract class Communicator {
    protected DataOutputStream out;
    protected DataInputStream in;

    /**
     * Default constructor
     */
    public Communicator() {
    }

    /**
     * Constructor, gets a socket to open the streams for communication
     * 
     * @param socket
     *            the socket from which the stream will be open
     * @throws IOException
     *             may fail when getting the streams from the socket
     */
    public Communicator(Socket socket) throws IOException {
	setSocket(socket);
    }

    /**
     * Closes this Communicator, classes should implement this method in order
     * to do any needed clean up and close the streams (by closing the socket)
     */
    protected abstract void close();

    /**
     * Waits for a RtcMessage from the host
     * 
     * @return the RtcMessage received from the host
     */
    protected RtcMessage getMessage() throws IOException,
	    ClassNotFoundException {
	int size = in.readInt();
	byte[] bytes = new byte[size];
	in.readFully(bytes);
	Object o = bytesToObject(bytes);
	return (RtcMessage) o;
    }

    /**
     * Waits for a RtcMessage from the host with the specified type. If the
     * received message doesn't match the expected type, connection will be
     * closed.
     * 
     * @param type
     *            the expected type
     * @return the received RtcMessage or null if failed
     */
    protected RtcMessage getMessageOrFail(MessageTypes type)
	    throws IOException, ClassNotFoundException {
	final RtcMessage message = getMessage();
	if (message.getMessageType() == type) {
	    return message;
	} else {
	    // something is wrong, we better disconnect
	    close();
	    return null;
	}
    }

    /**
     * Sends a RtcMessage to the host
     * 
     * @param message
     *            the RtcMessage to send
     */
    protected void sendMessage(RtcMessage message) {
	try {
	    byte[] bytes = objectToBytes(message);
	    out.writeInt(bytes.length);
	    out.write(bytes);
	} catch (IOException e) {
	}
    }

    /**
     * Gets a socket to open the streams, there's no need to call this method if
     * the constructor already took the socket
     * 
     * @param socket
     *            the socket from which the stream will be open
     * @throws IOException
     *             may fail when getting the streams from the socket
     */
    protected void setSocket(Socket socket) throws IOException {
	out = new DataOutputStream(socket.getOutputStream());
	in = new DataInputStream(socket.getInputStream());
    }

    /**
     * Converts received bytes into a real Object
     * 
     * @param bytes
     *            an array of bytes containing the serialized object
     * @return the converted object from bytes
     */
    private static Object bytesToObject(byte[] bytes) {
	try {
	    return new ObjectInputStream(new ByteArrayInputStream(bytes))
		    .readObject();
	} catch (java.io.IOException e) {
	} catch (java.lang.ClassNotFoundException e) {
	    e.printStackTrace();
	}
	return null;
    }

    /**
     * Converts a Serializable Object into a byte array
     * 
     * @param object
     *            the object to convert
     * @return an array of bytes with the converted object
     */
    private static byte[] objectToBytes(Object object) {
	ByteArrayOutputStream bytes = new ByteArrayOutputStream();
	try {
	    ObjectOutputStream out = new ObjectOutputStream(bytes);
	    out.writeObject(object);
	} catch (java.io.IOException e) {
	    e.printStackTrace();
	}
	return bytes.toByteArray();
    }
}
