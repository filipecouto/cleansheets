package csheets.ext.rtc;

/**
 @startuml
 title Communication

 box "Client Side" #54d1ff
 boundary "CleanSheets Client"
 end box
 box "Server Side" #ffaf32
 boundary Server
 participant Client
 participant "other:Client"
 end box

 activate Server
 "CleanSheets Client" --> Server: connects
 activate "CleanSheets Client"
 Server -> Client: onClientConnected
 activate Client
 'Client -> Client: wait for ClientInfo
 "CleanSheets Client" -> Client: ClientInfo
 'Client -> Client: save ClientInfo
 "CleanSheets Client" <-- Client: WorkbookInfo
 "CleanSheets Client" --> Client: Get SpreadsheetInfo
 "CleanSheets Client" <-- Client: SpreadsheetInfo
 loop as much as needed
 "CleanSheets Client" --> Client: Get Cell
 "CleanSheets Client" <-- Client: Cell
 end
 ...
 "CleanSheets Client" -> Client: onCellSelected
 Client -> Server: onCellSelected
 activate Server
 Server -> "other:Client": onCellSelected
 deactivate Server
 ...
 "CleanSheets Client" -> Client: onCellEdited
 Client -> Server: onCellEdited
 activate Server
 Server -> "other:Client": onCellEdited
 deactivate Server
 ...
 "CleanSheets Client" -> Client: disconnects
 Client -> Server: onClientDisconnected
 activate Server
 deactivate Server
 destroy Client

 deactivate Client
 deactivate Server
 deactivate "CleanSheets Client"

 @enduml
 */

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
