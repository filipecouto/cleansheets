package csheets.ext.rtc.ui;

import java.awt.event.ActionEvent;
import java.io.IOException;

import csheets.core.Address;
import csheets.ext.rtc.ClientInfo;
import csheets.ext.rtc.RealTimeCollaboration;
import csheets.ext.rtc.RtcShareProperties;
import csheets.ui.ctrl.FocusOwnerAction;
import csheets.ui.ctrl.UIController;

public class ShareAction extends FocusOwnerAction {

    private RealTimeCollaboration extension;
    private UIController uiController;
    private DataListener dataListener;
    private ShareOptionsDialog optDialog;

    public ShareAction(RealTimeCollaboration extension,
	    UIController uiController) {
	this.extension = extension;
	this.uiController = uiController;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

	if (optDialog == null) {
	    optDialog = new ShareOptionsDialog();
	    optDialog.setOnChooseExportListener(new OnChooseExportListener() {

		@Override
		public void onChoosedExport(boolean export) {
		    // selected cells if false , whole spreadsheet if true
		    Address cell1;
		    Address cell2;
		    if (export) {
			cell1 = new Address(1, 1);
			cell2 = new Address(1, 1);
		    } else {
			cell1 = new Address(1, 1);
			cell2 = new Address(1, 1);
		    }
		    String ip = "";
		    ClientInfo server = null;
		    try {
			RtcShareProperties props = new RtcShareProperties();
			props.setRange(cell1, cell2);
			props.setSpreadsheet(0);
			server = extension.createServer(new ClientInfo(
				"Servidor"), props, uiController);
			ip = server.getAddress().toString();
		    } catch (IOException e1) {
			e1.printStackTrace();
		    } finally {
			dataListener.onSendData(server, ip);
		    }
		}
	    });
	    optDialog.setVisible(true);
	} else {
	    optDialog.setVisible(true);
	}
    }

    @Override
    protected String getName() {
	return "Share";
    }

    public void setListener(DataListener dataListener) {
	this.dataListener = dataListener;
    }
}
