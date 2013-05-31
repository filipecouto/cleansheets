package csheets.ext.rtc;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * This class provides helper methods for ServerInterface, ClientInterface and
 * Client classes
 * 
 * @author gil_1110484
 */
public abstract class Communicator {
    protected ObjectOutputStream out;
    protected ObjectInputStream in;

    public Communicator() {
    }

    public Communicator(Socket socket) throws IOException {
	setSocket(socket);
    }

    protected void setSocket(Socket socket) throws IOException {
	out = new ObjectOutputStream(socket.getOutputStream());
	in = new ObjectInputStream(socket.getInputStream());
    }

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

    protected RtcMessage getMessage() throws IOException,
	    ClassNotFoundException {
	Object o = in.readObject();
	return (RtcMessage) o;
    }

    protected void sendMessage(RtcMessage message) {
	try {
	    out.writeObject(message);
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    protected abstract void close();
}
