package csheets.ext.rtc;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import csheets.core.Address;
import csheets.core.Cell;
import csheets.core.Workbook;
import csheets.ext.rtc.messages.RemoteCell;
import csheets.ext.rtc.messages.RemoteSpreadsheet;
import csheets.ext.rtc.messages.RemoteWorkbook;

/**
 * This class will be the client-side bridge of the connection (therefore the
 * name "Interface")
 * 
 * @author gil_1110484
 */
public class ClientInterface extends Communicator implements RtcCommunicator {
    private ClientInfo info;
    private Socket server;
    private RtcListener listener;

    private ClientInfo[] otherUsers;

    public ClientInterface(String address, ClientInfo clientInfo)
	    throws UnknownHostException, IOException {
	server = new Socket(address, PORT);
	setSocket(server);
	info = clientInfo;

	new Thread(new Runnable() {
	    @Override
	    public void run() {
		try {
		    RtcMessage message;

		    // wait for the list of already connected users
		    if ((message = getMessageOrFail(MessageTypes.infoList)) != null) {
			otherUsers = (ClientInfo[]) message.getArgument();
		    } else {
			return;
		    }

		    // the server needs to know who we are
		    sendMessage(new RtcMessage(info.getAddress(),
			    MessageTypes.info, info));

		    if ((message = getMessageOrFail(MessageTypes.workbook)) != null) {
			Workbook wb = ((RemoteWorkbook) message.getArgument())
				.getWorkbook();
			if (wb.getSpreadsheetCount() > 0) {
			    sendMessage(new RtcMessage(info.getAddress(),
				    MessageTypes.getSpreadsheet, 0));
			    if ((message = getMessageOrFail(MessageTypes.spreadsheet)) != null) {
				RemoteSpreadsheet sheet = (RemoteSpreadsheet) message
					.getArgument();
				sheet.getSpreadsheet(wb);
				sendMessage(new RtcMessage(info.getAddress(),
					MessageTypes.getCells, new Address[] {
						new Address(0, 0),
						new Address(sheet
							.getColumnCount(),
							sheet.getRowCount()) }));
			    } else {
				return;
			    }
			} else {
			    // TODO support more than one spreadsheet, protocol
			    // and interfaces are ready for it
			    // TODO what if the workbook is empty?
			}
			listener.onWorkbookReceived(info, wb);
		    } else {
			return;
		    }

		    while (true) {
			message = getMessage();
			switch (message.getMessage()) {
			case eventCellChanged:
			    final RemoteCell c = (RemoteCell) message
				    .getArgument();
			    listener.onCellChanged(null, c);
			    break;
			case eventCellSelected:
			    final Address a = (Address) message.getArgument();
			    listener.onCellSelected(null, a);
			    break;
			case cells:
			    final RemoteCell[] cells = (RemoteCell[]) message
				    .getArgument();
			    listener.onCellsReceived(null, cells);
			    break;
			case disconnect:
			    listener.onDisconnected(info);
			    server.close();
			    return;
			}
		    }

		    // in.close();
		} catch (IOException e) {
		    e.printStackTrace();
		} catch (ClassNotFoundException e) {
		    e.printStackTrace();
		}
	    }
	}).start();
    }

    @Override
    public void onConnected(ClientInfo client) {

    }

    @Override
    public void onCellSelected(ClientInfo source, Address address) {
	sendMessage(new RtcMessage(server.getInetAddress(),
		MessageTypes.eventCellSelected, address));
    }

    @Override
    public void onCellChanged(ClientInfo source, Cell cell) {
	sendMessage(new RtcMessage(server.getInetAddress(),
		MessageTypes.eventCellChanged, new RemoteCell(cell)));
    }

    @Override
    public void onDisconnected(ClientInfo client) {
    }

    @Override
    public void setListener(RtcListener listener) {
	this.listener = listener;
    }

    @Override
    public ClientInfo[] getConnectedUsers() {
	return otherUsers;
    }

    @Override
    protected void close() {
	try {
	    server.close();
	    listener.onDisconnected(null);
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }
}
