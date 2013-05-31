package csheets.ext.rtc.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.IOException;
import java.net.UnknownHostException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;

import csheets.core.Address;
import csheets.ext.rtc.ClientInfo;
import csheets.ext.rtc.ClientInterface;
import csheets.ext.rtc.RealTimeCollaboration;
import csheets.ext.rtc.RtcEventsResponder;
import csheets.ext.rtc.ServerInterface;
import csheets.ui.ctrl.UIController;

public class RtcSidebar extends JPanel {

    private JButton bShare;
    private JButton bConnect;
    private JButton bDisconnect;
    private JLabel ipAddress;
    private ClientsListAdapter adapter;

    public RtcSidebar(final RealTimeCollaboration extension,
	    final UIController uiController) {
	setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	JPanel buttonPanel = new JPanel();
	buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
	
	bShare = new JButton("Share");
	bShare.setAlignmentX(JComponent.CENTER_ALIGNMENT);
	buttonPanel.add(bShare);
	bShare.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent e) {
		
		ShareOptionsDialog optDialog = new ShareOptionsDialog();
		optDialog.setOnChooseExportListener(new onChooseExportListener() {
		    
		    @Override
		    public void onChoosedExport(boolean export) {
			// selected cells if false , whole spreadsheet if true
			Address cells[] = new Address[2];
			Address cell1;
			Address cell2;
			if(export) {
			    cell1 = new Address(1,1);
			    cell2 = new Address(1,1);
			} else {
			    cell1 = new Address(1,1);
			    cell2 = new Address(1,1);
			}
			cells[0] = cell1;
			cells[1] = cell2;
			String ip = "";
			try {
			    ip = extension
				    .createServer(new ClientInfo("Servidor"),
					    uiController, cells).getAddress().toString();
			} catch (IOException e1) {
			    e1.printStackTrace();
			} finally {
			    ipAddress.setText("Your ip address: " + ip);
			    bShare.setVisible(false);
			    bConnect.setVisible(false);
			    bDisconnect.setVisible(true);
			}
		    }
		});
		/*String ip = "";
		*/
		optDialog.setVisible(true);
	    }
	});
	bConnect = new JButton("Connect");
	bConnect.setAlignmentX(JComponent.CENTER_ALIGNMENT);
	ipAddress = new JLabel();
	ipAddress.setText("Not connected");
	ipAddress.setAlignmentX(JComponent.CENTER_ALIGNMENT);
	buttonPanel.add(bConnect);
	// add(ipAddress);
	bConnect.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent e) {

		ConnectionWindow ipDialog = new ConnectionWindow();
		ipDialog.setOnIpSelectedListener(new OnIPSelectListener() {
		    @Override
		    public void onIPSelected(String address, String username) {
			try {
			    extension.createClient(new ClientInfo(username),
				    address, uiController);
			} catch (UnknownHostException e1) {
			    e1.printStackTrace();
			} catch (IOException e1) {
			    e1.printStackTrace();
			} finally {
			    ipAddress.setText("Your ip address: " + address);
			    bShare.setVisible(false);
			    bConnect.setVisible(false);
			    bDisconnect.setVisible(true);
			}
		    }
		});
		ipDialog.setVisible(true);
	    }
	});
	bDisconnect = new JButton("Disconnect");
	bDisconnect.setAlignmentX(JComponent.CENTER_ALIGNMENT);
	bDisconnect.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		ipAddress.setText("Not connected");
		bShare.setVisible(true);
		bConnect.setVisible(true);
		bDisconnect.setVisible(false);

	    }
	});
	buttonPanel.add(bDisconnect);

	adapter = new ClientsListAdapter();
	JList<ClientInfo> list = new JList<ClientInfo>(adapter);
	list.setCellRenderer(new ClientInfoListRenderer());
	add(list);
	add(ipAddress);
	add(buttonPanel);
    }
}
