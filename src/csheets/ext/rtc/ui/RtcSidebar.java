package csheets.ext.rtc.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.IOException;
import java.net.UnknownHostException;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

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

    private RealTimeCollaboration extension;

    public RtcSidebar(RealTimeCollaboration extension,
	    UIController uiController, ShareAction shareAction,
	    ConnectAction connectAction) {
	this.extension = extension;
	setName("Real Time Colaboration");
	setLayout(new BorderLayout());
	JPanel buttonPanel = new JPanel();
	buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

	bShare = new JButton(shareAction);

	bShare.setAlignmentX(JComponent.CENTER_ALIGNMENT);
	buttonPanel.add(bShare);
	bConnect = new JButton(connectAction);

	bConnect.setAlignmentX(JComponent.CENTER_ALIGNMENT);
	ipAddress = new JLabel();
	ipAddress.setText("Not connected");
	ipAddress.setAlignmentX(JComponent.CENTER_ALIGNMENT);
	buttonPanel.add(bConnect);
	buttonPanel.add(ipAddress);

	bDisconnect = new JButton("Disconnect");
	bDisconnect.setAlignmentX(JComponent.CENTER_ALIGNMENT);
	bDisconnect.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent e) {
		// ipAddress.setText("Not connected");
		// bShare.setVisible(true);
		// bConnect.setVisible(true);
		// bDisconnect.setVisible(false);
		RtcSidebar.this.extension.disconnect();
	    }
	});
	buttonPanel.add(bDisconnect);
	bDisconnect.setVisible(false);
	bShare.setSize(100, 30);
	bConnect.setSize(100, 30);

	adapter = new ClientsListAdapter();
	JList<ClientInfo> list = new JList<ClientInfo>(adapter);
	list.setCellRenderer(new ClientInfoListRenderer());
	list.setBorder(BorderFactory.createLineBorder(Color.GRAY));
	add(list, BorderLayout.CENTER);
	add(buttonPanel, BorderLayout.SOUTH);
    }

    public void updateUsersList(final ClientInfo[] clients) {
	SwingUtilities.invokeLater(new Runnable() {
	    public void run() {
		adapter.update(clients);
	    }
	});
    }

    public void onConnection(String address) {
	if (ipAddress != null) {
	    ipAddress.setText("Connected: " + address);
	    bDisconnect.setVisible(true);
	    bShare.setVisible(false);
	    bConnect.setVisible(false);
	}
    }

    public void onDisconnected() {
	ipAddress.setText("Not connected");
	bShare.setVisible(true);
	bConnect.setVisible(true);
	bDisconnect.setVisible(false);
    }
}
