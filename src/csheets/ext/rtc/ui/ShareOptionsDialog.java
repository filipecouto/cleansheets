package csheets.ext.rtc.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.plaf.basic.BasicBorders.RadioButtonBorder;

public class ShareOptionsDialog extends JDialog {

    private JRadioButton selectWhole;
    private JRadioButton selectSelected;
    private JButton buttonAccept;
    private JButton buttonCancel;
    private onChooseExportListener listener;
    private ButtonGroup group;
    
    public ShareOptionsDialog() {
	super((JFrame) null, "Options of share", true);

	// creation of buttongroup

	setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
	group = new ButtonGroup();
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
		ShareOptionsDialog.this.listener.onChoosedExport(choice);
		ShareOptionsDialog.this.setVisible(false);
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
	add(panel);
	setSize(400, 200);

	pack();
    }
    
    public void setOnChooseExportListener(onChooseExportListener listener) {
	this.listener = listener;
    }
}
