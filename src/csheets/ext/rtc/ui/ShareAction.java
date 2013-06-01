package csheets.ext.rtc.ui;

import java.awt.event.ActionEvent;
import java.io.IOException;

import csheets.core.Address;
import csheets.core.Cell;
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
		public void onChoosedExport(boolean export, String name, int port) {
		    // selected cells if false , whole spreadsheet if true
		    RtcShareProperties props = new RtcShareProperties();
		    Address cell1;
		    Address cell2;
		    if (export) {
			props.setAcceptWholeSpreadsheet(true);
		    } else {
			Cell[][] cells = focusOwner.getSelectedCells();
			cell1 = cells[0][0].getAddress();
			cell2 = cells[cells.length - 1][cells[0].length - 1]
				.getAddress();
			props.setRange(cell1, cell2);
		    }
		    String ip = "";
		    ClientInfo server = null;
		    try {
			props.setSpreadsheet(0);
			server = extension.createServer(new ClientInfo(
				name), props, uiController);
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
