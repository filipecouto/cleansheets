package csheets.ext.rtc.ui;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.UnknownHostException;

import csheets.ext.rtc.ClientInfo;
import csheets.ext.rtc.RealTimeCollaboration;
import csheets.ui.ctrl.FocusOwnerAction;
import csheets.ui.ctrl.UIController;

public class ConnectAction extends FocusOwnerAction {

    private RealTimeCollaboration extension;
    private UIController uiController;
    private DataListener dataListener;
    private ConnectionWindow ipDialog;

    public ConnectAction(RealTimeCollaboration extension,
	    UIController uiController) {
	this.extension = extension;
	this.uiController = uiController;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

	if (ipDialog == null) {
	    ipDialog = new ConnectionWindow();
	    ipDialog.setOnIpSelectedListener(new OnIPSelectListener() {
		@Override
		public void onIPSelected(String address, String username) {
		    ClientInfo client = null;
		    String ip = "";
		    try {
			client = extension
				.createClient(new ClientInfo(username),
					address, uiController);
			ip = client.getAddress().toString();
		    } catch (UnknownHostException e1) {
			e1.printStackTrace();
		    } catch (IOException e1) {
			e1.printStackTrace();
		    } finally {
			dataListener.onSendData(client, ip);
		    }
		}
	    });
	    ipDialog.setVisible(true);
	} else {
	    ipDialog.setVisible(true);
	}
    }

    @Override
    protected String getName() {
	// TODO Auto-generated method stub
	return "Connect";
    }

    public void setListener(DataListener dataListener) {
	this.dataListener = dataListener;
    }
}
