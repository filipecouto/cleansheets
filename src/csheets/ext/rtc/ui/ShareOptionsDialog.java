package csheets.ext.rtc.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.plaf.basic.BasicBorders.RadioButtonBorder;

public class ShareOptionsDialog extends JDialog {

    private JRadioButton selectWhole;
    private JRadioButton selectSelected;
    private JButton buttonAccept;
    private JButton buttonCancel;
    private OnChooseExportListener listener;
    private ButtonGroup group;
    private JTextField userName;
    private JTextField connectionPort;
    
    public ShareOptionsDialog() {
	super((JFrame) null, "Options of share", true);

	// creation of buttongroup

	setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
	group = new ButtonGroup();
	userName= new JTextField();
	userName.setText("Insert username");
	userName.addFocusListener(new FocusListener() {
	    
	    @Override
	    public void focusLost(FocusEvent arg0) {
	    }
	    
	    @Override
	    public void focusGained(FocusEvent arg0) {
		userName.selectAll();
	    }
	});
	
	connectionPort = new JTextField();
	connectionPort.setText("Insert port for connection");
	connectionPort.addFocusListener(new FocusListener() {
	    
	    @Override
	    public void focusLost(FocusEvent arg0) {
		// TODO Auto-generated method stub
		
	    }
	    
	    @Override
	    public void focusGained(FocusEvent arg0) {	
		connectionPort.selectAll();
	    }
	});
	
	selectWhole = new JRadioButton();
	selectWhole.setText("Export whole spreadsheet");

	selectSelected = new JRadioButton();
	selectSelected.setText("Export selected cells");
	selectSelected.setSelected(true);
	
	group.add(selectWhole);
	group.add(selectSelected);

	JPanel panel = new JPanel();

	panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

	buttonAccept = new JButton();
	buttonAccept.setText("Accept");
	buttonAccept.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent arg0) {
		/**
		 * true for whole , false for selected
		 */
		boolean choice = true;
		if(ShareOptionsDialog.this.selectSelected.isSelected()) {
		    choice = false;
		} else if(ShareOptionsDialog.this.selectWhole.isSelected()){
		    choice = true;
		}
		int port = Integer.valueOf(ShareOptionsDialog.this.connectionPort.getText());
		String userName = ShareOptionsDialog.this.userName.getText();
		ShareOptionsDialog.this.listener.onChoosedExport(choice, userName, port);
	    }
	});

	buttonCancel = new JButton();
	buttonCancel.setText("Cancel");
	buttonCancel.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent e) {
		ShareOptionsDialog.this.setVisible(false);
	    }
	});
	
	panel.add(buttonAccept);
	panel.add(buttonCancel);
	add(selectSelected);
	add(selectWhole);
	add(userName);
	add(connectionPort);
	add(panel);
	setSize(400, 200);
	setLocationRelativeTo(null);

	pack();
    }
    
    public void setOnChooseExportListener(OnChooseExportListener listener) {
	this.listener = listener;
    }
}
