package csheets.ext.rtc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * This class provides helper methods for ClientInterface and Client classes
 * especially for sending and receiving data
 * 
 * @author gil_1110484
 */
public abstract class Communicator {
    // After some tests, we found out ObjectInputStream and/or
    // ObjectOutputStream aren't reliable for sending and receiving data over
    // network, therefore we're converting the objects and then sending them in
    // a lower level and passing by the size of each object

    protected DataOutputStream out;
    protected DataInputStream in;

    public Communicator() {
    }

    public Communicator(Socket socket) throws IOException {
	setSocket(socket);
    }

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
	if (message.getMessage() == type) {
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
	    e.printStackTrace();
	}
    }

    protected void setSocket(Socket socket) throws IOException {
	out = new DataOutputStream(socket.getOutputStream());
	in = new DataInputStream(socket.getInputStream());
    }

    private static Object bytesToObject(byte[] bytes) {
	try {
	    return new ObjectInputStream(new ByteArrayInputStream(bytes))
		    .readObject();
	} catch (java.io.IOException e) {
	    e.printStackTrace();
	} catch (java.lang.ClassNotFoundException e) {
	    e.printStackTrace();
	}
	return null;
    }

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
