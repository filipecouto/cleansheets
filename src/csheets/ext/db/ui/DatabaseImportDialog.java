package csheets.ext.db.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.text.BadLocationException;

import csheets.core.Cell;
import csheets.core.Spreadsheet;
import csheets.core.Workbook;
import csheets.core.formula.compiler.FormulaCompilationException;
import csheets.ext.db.DatabaseImportController;
import csheets.ext.db.DatabaseInterface;
import csheets.ext.db.DatabaseExtension;
import csheets.ui.sheet.SpreadsheetTable;
import javax.swing.SwingUtilities;

/*
 * 
 * @author Filipe Silva & Filipe Couto
 */

public class DatabaseImportDialog extends JDialog {

    private JFileChooser fileChooser;
    private JTextField url;
    private JTextField username;
    private JTextField password;
    private JComboBox<String> format;
    private JComboBox<String> tables;
    private JRadioButton currentSheet;
    private JRadioButton newSheet;
    private JButton browse;
    private String databaseName;

    private JPanel panelButtons;

    private DatabaseExtension extension;

    private SpreadsheetTable table;
    
    private boolean importToCurrentSheet;

    public DatabaseImportDialog(DatabaseExtension extension,
	    SpreadsheetTable table) {
	super((JFrame) null, "Import from database", true);

	this.extension = extension;
	this.table = table;

	getContentPane().setLayout(
		new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

	add(createOptionsPanel());
	add(createButtonsPanel());

	pack();
	setLocationRelativeTo(null);
    }

    private JPanel createButtonsPanel() {
	panelButtons = new JPanel();
	panelButtons.setLayout(new BoxLayout(panelButtons, BoxLayout.X_AXIS));
	JButton cancel = new JButton("Cancel");
	cancel.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent e) {
		setVisible(false);
	    }
	});
	panelButtons.add(cancel);

	JButton ok = new JButton("Import");
	ok.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent e) {
		importDatabase();
	    }
	});
	panelButtons.add(ok);
	return panelButtons;
    }

    /**
     * enables buttons depending on parameter
     * 
     * @param value
     */
    public void enableButtons(boolean value) {
	panelButtons.getComponent(0).setEnabled(value);
	panelButtons.getComponent(1).setEnabled(value);
    }

    /**
     * Creates a Dialog that is used to retrieve the exportation data from the
     * user in order to proceed to the exportation
     */
    private void importDatabase() {
	panelButtons.getComponent(0).setEnabled(false);
	panelButtons.getComponent(1).setEnabled(false);
	// force the GUI to redraw the window so the label can be seen
	revalidate();
	repaint();
	Thread importThread = new Thread(new Runnable() {
	    DatabaseImportController importController;

	    @Override
	    public void run() {
		if (importController == null) {
		    // controller
		    importController = new DatabaseImportController();
		    // driver
		    importController.setDriver(extension.getAvailableDrivers()
			    .get(format.getSelectedIndex()));
		    // URL
		    String dbUrl = url.getText();
		    importController.setDatabase(dbUrl);
		    // ----
                    // table name
		    importController.setTableName(tables.getSelectedItem()
			    .toString());
                    // spreadsheet
		    importController.setSpreadsheet(table.getSpreadsheet());
                    // cell
		    importController.setCell(table.getSelectedCell());
                    // importToCurrentSheet decides if it's to import into the current sheet or into a new one
                    importController.setImportToCurrentSheet(importToCurrentSheet);
		}
		try {
		    final String [][] info = importController.importDatabase();
                    SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            //Read matrix and put data into spreadsheet
                            int i=0,j=0;
                            int cellCol,cellRow;
                            Cell cell = table.getSelectedCell();
                            Spreadsheet sheet = table.getSpreadsheet();
                            // Import into a new sheet
                            if(!importToCurrentSheet){
                                Workbook workbook = sheet.getWorkbook();
                                workbook.addSpreadsheet(info);
                                workbook.getSpreadsheet(workbook.getSpreadsheetCount()-1).setTitle(tables.getSelectedItem()
			    .toString()); // Doesn't set the title graphically
                                System.out.println(workbook.getSpreadsheet(workbook.getSpreadsheetCount()-1).getTitle());
                                //TODO get focus on the new sheet
                            }
                            // Import into the current sheet starting at the selected cell
                            else{ //TODO verify if its going to overlap cells
                                cellCol=cell.getAddress().getColumn();
                                cellRow=cell.getAddress().getRow();
                                try{
                                    for(i=0;i<info.length;i++){
                                        for(j=0;j<info[0].length;j++){
                                            sheet.getCell(cellCol+j, cellRow+i).setContent(info[i][j].toString());
                                        }
                                    }
                                }catch(FormulaCompilationException e){
                                    System.out.println(e);
                                }
                            }
                        }
                    });
		} catch (Exception e) {
		    System.out.println(e);
                    e.printStackTrace();
		}
		enableButtons(true);
		setVisible(false);
	    }
	});
	importThread.start();
    }

    private JPanel createOptionsPanel() {
	JPanel options = new JPanel();
	GroupLayout layout = new GroupLayout(options);
	options.setLayout(layout);
	layout.setAutoCreateGaps(true);
	layout.setAutoCreateContainerGaps(true);

	final JLabel lUrl = new JLabel("URL");
	final JLabel lExport = new JLabel("Import");
	final JLabel lFormat = new JLabel("Driver");
	final JLabel lUserName = new JLabel("Username");
	final JLabel lPassword = new JLabel("Password");
	final JLabel lTables = new JLabel("Tables");

	JPanel urlBrowse = new JPanel();
	urlBrowse.setLayout(new BoxLayout(urlBrowse, BoxLayout.X_AXIS));

	browse = new JButton("Browse");
	browse.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent arg0) {
		DatabaseImportDialog.this.fileChooser = new JFileChooser();
		DatabaseImportDialog.this.fileChooser
			.setMultiSelectionEnabled(false);
		// Checks if the required driver needs a folder or a file to
		// work with
		if (!DatabaseImportDialog.this.extension
			.getAvailableDrivers()
			.get(DatabaseImportDialog.this.format
				.getSelectedIndex()).requiresFile()) {
		    DatabaseImportDialog.this.fileChooser
			    .setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		}
		int status = DatabaseImportDialog.this.fileChooser.showDialog(
			DatabaseImportDialog.this, "Choose File");
		if (status == JFileChooser.APPROVE_OPTION) {

		    DatabaseImportDialog.this.url
			    .setText(DatabaseImportDialog.this.fileChooser
				    .getSelectedFile().getAbsolutePath());
		    updateComboBox(url.getText());
		} else {
		    DatabaseImportDialog.this.url
			    .setText("No file was selected");
		}
	    }
	});

	url = new JTextField();
	url.setText("");
	url.addFocusListener(new FocusListener() {
	    @Override
	    public void focusLost(FocusEvent arg0) {
	    }

	    @Override
	    public void focusGained(FocusEvent arg0) {
		url.selectAll();
	    }
	});

	url.addKeyListener(new KeyListener() {

	    @Override
	    public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub

	    }

	    @Override
	    public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub

	    }

	    @Override
	    public void keyPressed(KeyEvent e) {
		if (e.getKeyChar() == KeyEvent.VK_ENTER) {
		    updateComboBox(url.getText());
		}

	    }
	});

	urlBrowse.add(url);
	urlBrowse.add(browse);

	final List<DatabaseInterface> availableDrivers = extension
		.getAvailableDrivers();

	String[] availableDriverNames = new String[availableDrivers.size()];
	for (int i = 0; i < availableDriverNames.length; i++) {
	    availableDriverNames[i] = availableDrivers.get(i).getName();
	}

	TableNamesAdapter adapter = new TableNamesAdapter();
	tables = new JComboBox<String>();
	tables.setModel(adapter);

	format = new JComboBox<String>(availableDriverNames);
	format.addItemListener(new ItemListener() {
	    @Override
	    public void itemStateChanged(ItemEvent e) {
		onFormatSelected(extension.getAvailableDrivers().get(
			format.getSelectedIndex()));
	    }
	});

	JPanel importPanel = new JPanel();
	importPanel.setLayout(new BoxLayout(importPanel, BoxLayout.Y_AXIS));

	newSheet = new JRadioButton("Create a new sheet");
        newSheet.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                importToCurrentSheet=false;
                newSheet.setSelected(!importToCurrentSheet);
                currentSheet.setSelected(importToCurrentSheet);
            }
        });
	importPanel.add(newSheet);
	currentSheet = new JRadioButton("To current sheet");
        currentSheet.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                importToCurrentSheet=true;
                newSheet.setSelected(!importToCurrentSheet);
                currentSheet.setSelected(importToCurrentSheet);
            }
        });
	importPanel.add(currentSheet);

	username = new JTextField();
	username.setEnabled(false);

	password = new JTextField();
	password.setEnabled(false);

	layout.setHorizontalGroup(layout
		.createParallelGroup(GroupLayout.Alignment.LEADING)
		.addGroup(
			layout.createSequentialGroup().addComponent(lUrl)
				.addComponent(urlBrowse))
		.addGroup(
			layout.createSequentialGroup().addComponent(lFormat)
				.addComponent(format))
		.addGroup(
			layout.createSequentialGroup().addComponent(lTables)
				.addComponent(tables))
		.addGroup(
			layout.createSequentialGroup().addComponent(lExport)
				.addComponent(importPanel))
		.addGroup(
			layout.createSequentialGroup().addComponent(lUserName)
				.addComponent(username))
		.addGroup(
			layout.createSequentialGroup().addComponent(lPassword)
				.addComponent(password)));
	layout.setVerticalGroup(layout
		.createSequentialGroup()
		.addGroup(
			layout.createParallelGroup(
				GroupLayout.Alignment.BASELINE)
				.addComponent(lUrl).addComponent(urlBrowse))
		.addGroup(
			layout.createParallelGroup(
				GroupLayout.Alignment.BASELINE)
				.addComponent(lFormat).addComponent(format))
		.addGroup(
			layout.createParallelGroup(
				GroupLayout.Alignment.BASELINE)
				.addComponent(lTables).addComponent(tables))
		.addGroup(
			layout.createParallelGroup(
				GroupLayout.Alignment.BASELINE)
				.addComponent(lExport)
				.addComponent(importPanel))
		.addGroup(
			layout.createParallelGroup(
				GroupLayout.Alignment.BASELINE)
				.addComponent(lUserName).addComponent(username))
		.addGroup(
			layout.createParallelGroup(
				GroupLayout.Alignment.BASELINE)
				.addComponent(lPassword).addComponent(password)));

	ButtonGroup group = new ButtonGroup();
	group.add(currentSheet);
	group.add(newSheet);

	// prepare GUI for this driver
	if (availableDrivers.size() != 0) {
	    onFormatSelected(availableDrivers.get(0));
	}

	return options;
    }

    private void onFormatSelected(DatabaseInterface driver) {
	username.setEnabled(driver.requiresUsername());
	password.setEnabled(driver.requiresUsername());
    }

    private void updateComboBox(String database) {
	DatabaseImportController controller = new DatabaseImportController();
	controller.setDatabase(database);
	controller.setDriver(extension.getAvailableDrivers().get(
		format.getSelectedIndex()));
	List<String> values = controller.getTables();
	TableNamesAdapter adapter = (TableNamesAdapter) tables.getModel();
	adapter.update(values);
    }

    public void prepareDialog(SpreadsheetTable table) {
	this.table = table;
        importToCurrentSheet=false;
	newSheet.setSelected(!importToCurrentSheet);
        currentSheet.setSelected(importToCurrentSheet);
    }
}
