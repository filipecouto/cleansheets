package csheets.ext.rtc;

import java.io.IOException;
import java.util.ArrayList;

import csheets.core.Address;
import csheets.ext.Extension;
import csheets.ext.rtc.messages.RemoteCell;
import csheets.ext.rtc.ui.RtcSharingProperties;
import csheets.ext.rtc.ui.RtcUI;
import csheets.ui.ctrl.EditEvent;
import csheets.ui.ctrl.EditListener;
import csheets.ui.ctrl.UIController;
import csheets.ui.ext.UIExtension;

/**
 * This extension lets the user share his/her workbook with other CleanSheets
 * users and real-time collaboration (many people can be working on the same
 * workbook at the same time).
 * 
 * Currently, it is able to share one spreadsheet (however the structure is
 * ready to support for any amount of spreadsheets) or a range of cells in one
 * spreadsheet.
 * 
 * Once disconnected, the user can use that shared spreadsheet or piece of it
 * separately from the source.
 * 
 * @author gil_1110484; Rita Nogueira
 */
public class RealTimeCollaboration extends Extension {

	private RtcEventsResponder responder;
	private ClientInfo identity;
	private ArrayList<RtcCommunicator> communicators = new ArrayList<RtcCommunicator>();
	private RtcUI rtcUI;
	private MulticastServer multserver;

	private boolean isOwner;

	public RealTimeCollaboration() {
		super("Real Time Collaboration");
	}

	/**
	 * Tells the side bar to refresh the list of connected users
	 * 
	 * @param com
	 */
	public void updateUsersList(RtcCommunicator com) {
		rtcUI.updateUsersList(com.getConnectedUsers());
	}

	/**
	 * Tells the side bar to refresh the list of shares
	 */
	public void updateServersList() {
		RtcCommunicator[] array = new RtcCommunicator[communicators.size()];
		communicators.toArray(array);
		rtcUI.updateServersList(array);
	}

	/**
	 * Creates a server for sharing on this instance
	 * 
	 * @param client
	 *           the identity of this user
	 * @param port
	 *           the port where this server will be listening
	 * @param properties
	 *           sharing options, for now controls whether a cell is shared or
	 *           not
	 * @param uiController
	 *           uiController needed for the RtcResponder in charge of sending
	 *           feedback to the GUI
	 * @return the identity, may contain the address
	 * @throws IOException
	 */
	public ClientInfo createServer(ClientInfo client, int port,
			RtcSharingProperties properties, UIController uiController)
			throws IOException {
		isOwner = true;
		properties.setOwner(true);
		ServerInterface server = new ServerInterface(client, port, properties,
				uiController);
		identity = server.getServerInfo();
		server.setListener(new RtcEventsResponder(server, uiController, this));
		server.start();
		communicators.add(server);

		if (multserver == null) {
			multserver = new MulticastServer(communicators);
		}
		return identity;
	}

	/**
	 * Creates a client to connect to a shared workbook
	 * 
	 * @param client
	 *           the identity of this user
	 * @param ipAddress
	 *           the address of the server to connect to
	 * @param port
	 *           the port where the server will be listening
	 * @param uiController
	 *           uiController needed for the RtcResponder in charge of sending
	 *           feedback to the GUI
	 * @return the identity, may contain the address
	 * @throws IOException
	 */
	public ClientInfo createClient(ClientInfo client, String ipAddress,
			int port, UIController uiController) throws IOException {
		isOwner = false;
		identity = client;
		ClientInterface communicator = new ClientInterface(ipAddress, port,
				identity);
		communicator.setListener(new RtcEventsResponder(communicator,
				uiController, this));
		communicators.add(communicator);
		communicator.start();
		return identity;
	}

	/**
	 * Called from the GUI when the users stops the connection, this method will
	 * tell the communication interface to stop (and maybe warn any connected
	 * user)
	 */
	public void disconnect() {
		for (RtcCommunicator com : communicators) {
			com.onDisconnected(null);
		}
		communicators.removeAll(communicators);
		updateServersList();
	}

	/**
	 * Called when the connection is stopped, by user request or any other reason
	 */
	public void onDisconnected() {
		// TODO refactoring this method
		rtcUI.onDisconnected();
	}

	/**
	 * Called when connection is established
	 */
	public void onConnected() {
		rtcUI.onConnected(identity.getAddress().getHostAddress());
	}

	/**
	 * Called when an exception is thrown while trying to connect, this method
	 * will communicate with the UI in order to warn the user about the issue
	 * 
	 * @param e
	 *           the thrown exception
	 */
	public void onConnectionFailed(Exception e) {
		rtcUI.onConnectionFailed(e);
	}

	/**
	 * Says whether there is currently a connection (either this is a client or a
	 * server)
	 * 
	 * @return true if it is connected, false otherwise
	 */
	public boolean isConnected() {
		if (communicators.isEmpty()) {
			return false;
		}
		for (RtcCommunicator com : communicators) {
			if (!com.isConnected()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Says whether this instance represents the real owner of the workbook.
	 * Usually determined by who is the server.
	 * 
	 * @return true if it is, false otherwise
	 */
	public boolean isOwner() {
		return isOwner;
	}

	/**
	 * Asks the RtcShareProperties if this address is shared
	 * 
	 * @param address
	 *           the address to check
	 * @return true if this address is shared, false otherwise
	 */
	public boolean isShared(Address address) {
		if (responder == null) {
			return false;
		} else {
			final RtcSharingProperties props = responder.getShareProperties();
			return props == null ? false : props.isInsideRange(address);
		}
	}

	@Override
	public UIExtension getUIExtension(final UIController uiController) {
		if (rtcUI == null) {
			uiController.addEditListener(new EditListener() {
				@Override
				public void workbookModified(EditEvent event) {
					for (RtcCommunicator com : communicators) {
						if (uiController.getActiveSpreadsheet() == uiController
								.getActiveWorkbook().getSpreadsheet(0)) {
							com.onCellChanged(null,
									new RemoteCell(uiController.getActiveCell()));
						}
					}
				}
			});
			// TODO let the others see what's selected
			// uiController.addSelectionListener(new SelectionListener() {
			// @Override
			// public void selectionChanged(SelectionEvent event) {
			// if (uiController.getActiveCell() == null) {
			// return;
			// }
			// if (communicator != null) {
			// communicator.onCellSelected(null, uiController
			// .getActiveCell().getAddress());
			// }
			// }
			// });
			rtcUI = new RtcUI(this, uiController);
		}
		return rtcUI;
	}

	/**
     * 
     */
	public void onError(Object error) {
		rtcUI.onError(error);
	}
}
