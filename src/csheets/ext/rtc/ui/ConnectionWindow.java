package csheets.ext.rtc.ui;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ConnectionWindow extends JDialog {

    private JTextField ipAddress;
    private JButton buttonCancel;
    private JButton buttonAccept;
    private OnIPSelectListener listener;
    private JPanel panelButtons;

    public ConnectionWindow(RtcSidebar sidebar) {
	super((JFrame) null, "Connection", true);

	setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

	JPanel panel = new JPanel();
	panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

	// creates textfield for insertion
	ipAddress = new JTextField();
	ipAddress.setText("INSERT IP ADDRESS OR URL");
	ipAddress.addFocusListener(new FocusListener() {
	    @Override
	    public void focusLost(FocusEvent arg0) {
		// TODO Auto-generated method stub

	    }

	    @Override
	    public void focusGained(FocusEvent arg0) {
		ipAddress.selectAll();
	    }
	});
	
	buttonAccept = new JButton("Accept");
	buttonAccept.addActionListener(new ActionListener() {
	    
	    @Override
	    public void actionPerformed(ActionEvent arg0) {
		listener.onIPSelected(ipAddress.getText());
	    }
	});
	
	buttonCancel = new JButton("Cancel");
	buttonCancel.addActionListener(new ActionListener() {
	    
	    @Override
	    public void actionPerformed(ActionEvent arg0) {
		listener.onIPSelected("CANCELED");
	    }
	});
	panel.add(ipAddress);
	panelButtons = new JPanel();
	panelButtons.setLayout(new BoxLayout(panelButtons, BoxLayout.X_AXIS));
	panelButtons.add(buttonAccept);
	panelButtons.add(buttonCancel);
	panel.add(panelButtons);
	setVisible(true);
    }
    
    public void setOnIpSelectedListener(OnIPSelectListener listener) {
	this.listener = listener;
    }
}
