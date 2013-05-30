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

public class RtcMessage implements Serializable {
    private static final long serialVersionUID = -1845883260814635491L;

    public static final int MESSAGE_DISCONNECT = 0, MESSAGE_INFO = 1,
	    MESSAGE_EVENT_CELLSELECTED = 4, MESSAGE_EVENT_CELLCHANGED = 8,
	    MESSAGE_GET_CELL = 16;

    private byte[] from;
    private int message;
    private Serializable argument;

    public RtcMessage(InetAddress from, int message, Serializable argument) {
	this.from = from.getAddress();
	this.message = message;
	this.argument = argument;
    }

    public int getMessage() {
	return message;
    }

    public Serializable getArgument() {
	return argument;
    }

    public byte[] getSenderAddress() {
	return from;
    }
}
