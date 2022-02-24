/*
 * Created on Dec 27, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gov.va.med.imaging.dicom.utilities.view.imageinfo;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.tree.TreePath;

import com.sun.misc.treetable.JTreeTable;

import gov.va.med.imaging.dicom.utilities.exceptions.DataCreationException;
import gov.va.med.imaging.dicom.utilities.exceptions.DicomFileException;
import gov.va.med.imaging.dicom.utilities.exceptions.PixelDataException;
import gov.va.med.imaging.dicom.utilities.exceptions.ValidationException;
import gov.va.med.imaging.dicom.utilities.model.DicomImageInfoModel;
import gov.va.med.imaging.dicom.utilities.model.IDicomInformation;
import gov.va.med.imaging.dicom.utilities.model.SimpleFileSystemModel;

/**
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 *
 *
 * @author William Peterson
 *
 */
public class DicomImageInfo extends JFrame {

    private JTable histogramTable;
    private JTreeTable fileSystemComponent;
    private SimpleFileSystemModel fileSystemModel;
    private JScrollPane directoryScrollPane;
    private JLabel fileDateLabel;
    private JLabel fileSizeLabel;
    private JLabel fileLabel;
    private JMenuItem preferencesMenuItem;
    private JMenuItem aboutMenuItem;
    private JMenuItem archiveNewFilesMenuItem;
    private JMenuItem exitMenuItem;
    private JMenu helpMenu;
    private JMenu toolsMenu;
    private JMenu fileMenu;
    private JMenuBar menuBar;
    private JTextArea iodArea;
    private JScrollPane iodValidationScrollPane;
    private JHistogramPanel histogramPanel;
    private JTable headerTable;
    private JScrollPane headerScrollPane;
    private JScrollPane histogramScrollPane;
    private JTabbedPane tabbedPane;
    private JSplitPane splitPane;
    private JFrame parent;
    private DicomImageInfoModel guiModel;
    
    private File currentDicomFile;
    private IDicomInformation dicomObjectInfo;
    
    /** Path created with. */
    protected String             path;
    /** Row the is being reloaded. */
    protected int selectedRow;
    /** TreePath being reloaded. */
    protected TreePath selectedPath;


    
    private final static String VERSION = "1.0";
    
    /**
     * Launch the application
     * @param args
     */
    /**
    public static void main(String args[]) {
        try {
            SwingUtilities.invokeLater(new Runnable(){
                public void run(){
                    new DicomImageInfo();
                }
            });
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    **/

    /**
     * Create the frame
     */
    public DicomImageInfo() {
        super();
        
        //DicomImageInfo frame = new DicomImageInfo();
        setTitle("DICOM Image Information");
        setName("parent");
        setSize(1200, 800);
        setResizable(false);
        //FIXME The SplitPane object is not following the size of the main frame.  Modify
        //  app to have all panes follow the frame in size.
        getContentPane().setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        parent = (JFrame)this;
        guiModel = new DicomImageInfoModel();

        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                if(parent != null){
                    //Dimension parentSize = parent.getSize();
                    //splitPane.setSize((int)parentSize.getWidth()-10, (int)parentSize.getHeight()-10);
                }
            }
        });

        
        splitPane = new JSplitPane();
        splitPane.setDividerSize(7);
        splitPane.setDividerLocation(400);
        splitPane.setBounds(0, 32, 1154, 683);
        getContentPane().add(splitPane);

        splitPane.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                if(splitPane != null){
                    //Dimension rightPaneSize = splitPane.getRightComponent().getSize();
                    //tabbedPane.setSize(rightPaneSize);
                
                    //Dimension leftPaneSize = splitPane.getLeftComponent().getSize();
                    //directoryScrollPane.setSize(leftPaneSize);
                }
            }
        });

        
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("", Font.BOLD, 14));
        splitPane.setRightComponent(tabbedPane);

        
        headerScrollPane = new JScrollPane();
        tabbedPane.addTab("Header", null, headerScrollPane, null);

        histogramPanel = new JHistogramPanel();
        histogramPanel.setLayout(null);
        tabbedPane.addTab("Histogram", null, histogramPanel, null);

        histogramScrollPane = new JScrollPane();
        tabbedPane.addTab("Histogram Table", null, histogramScrollPane, null);
        
        
        iodArea = new JTextArea("No Information.");
        iodValidationScrollPane = new JScrollPane(iodArea);
        tabbedPane.addTab("IOD Validation", null, iodValidationScrollPane, null);


        // Setting up Directory in Left viewport.
        
        //File start = new File(System.getProperty("user.home"));
        fileSystemModel = new SimpleFileSystemModel();
        fileSystemComponent = new JTreeTable(fileSystemModel);
        
        directoryScrollPane = new JScrollPane(fileSystemComponent);
        splitPane.setLeftComponent(directoryScrollPane);
        //directoryScrollPane.setViewportView(fileSystemComponent);

        
        //Temp code to track column names.
        int count = fileSystemComponent.getColumnModel().getColumnCount();
        for(int i=0; i<count; i++){
            String columnName = fileSystemComponent.getColumnModel().getColumn(i).getHeaderValue().toString();
            //System.out.println("column "+i+" represents "+columnName);
        }//end of temp code.
        
        fileSystemComponent.getColumnModel().getColumn(1).setCellRenderer
        (new NumberTableCellRenderer());
        
        fileSystemComponent.getColumnModel().getColumn(2).setCellRenderer
        (new DateTableCellRenderer());


        Reloader rl = new Reloader();
        fileSystemComponent.getTree().addTreeExpansionListener(rl);
        fileSystemComponent.getTree().addTreeSelectionListener(rl);
        //path = guiModel.getInitialDirectory();
        
        menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        fileMenu = new JMenu();
        fileMenu.setText("File");
        menuBar.add(fileMenu);

        exitMenuItem = new JMenuItem();
        exitMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String actionStr = e.getActionCommand();
                if(actionStr.equals("Exit")) System.exit(0);
            }
        });
        exitMenuItem.setText("Exit");
        fileMenu.add(exitMenuItem);

        toolsMenu = new JMenu();
        toolsMenu.setText("Tools");
        menuBar.add(toolsMenu);

        archiveNewFilesMenuItem = new JMenuItem();
        archiveNewFilesMenuItem.setText("Archive New Files");
        toolsMenu.add(archiveNewFilesMenuItem);

        preferencesMenuItem = new JMenuItem();
        preferencesMenuItem.setText("Preferences");
        toolsMenu.add(preferencesMenuItem);

        helpMenu = new JMenu();
        helpMenu.setText("Help");
        menuBar.add(helpMenu);

        aboutMenuItem = new JMenuItem();
        aboutMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String actionStr = e.getActionCommand();
                if(actionStr.equals("About")){
                    String messageString = "Dicom Image Information \n" +
                            "Version: "+VERSION+"\n" +
                            "Created for Veterans Affairs \n" +
                            "by William Peterson, Kinetix Medical Resources, Inc. \n" +
                            "Copyrighted 2007 \n";
                           
                    JOptionPane.showMessageDialog(parent, messageString, "About", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        aboutMenuItem.setText("About");
        helpMenu.add(aboutMenuItem);

        fileLabel = new JLabel();
        fileLabel.setFont(new Font("", Font.PLAIN, 14));
        fileLabel.setText("File:");
        fileLabel.setBounds(250, 10, 327, 15);
        getContentPane().add(fileLabel);

        fileSizeLabel = new JLabel();
        fileSizeLabel.setFont(new Font("", Font.PLAIN, 14));
        fileSizeLabel.setText("File Size:");
        fileSizeLabel.setBounds(598, 10, 202, 15);
        getContentPane().add(fileSizeLabel);

        fileDateLabel = new JLabel();
        fileDateLabel.setFont(new Font("", Font.PLAIN, 14));
        fileDateLabel.setText("File Date:");
        fileDateLabel.setBounds(837, 10, 267, 15);
        getContentPane().add(fileDateLabel);
        
        setVisible(true);

    }
    
    /**
     * A renderer that will give an indicator when a cell is being reloaded.
     */
    class NumberTableCellRenderer extends DefaultTableCellRenderer {
        /** Makes sure the number of displayed in an internationalized
         * manner. */
        protected NumberFormat       formatter;
        /** Row that is currently being painted. */
        protected int                lastRow;
    

        NumberTableCellRenderer() {
            setHorizontalAlignment(JLabel.RIGHT);
            formatter = NumberFormat.getInstance();
        }

        /**
         * Invoked as part of DefaultTableCellRenderers implemention. Sets
         * the text of the Cell.
         */
        public void setValue(Object value) { 

            setText((value == null) ? "---" : formatter.format(value)); 
        }

        /**
         * Returns this.
         */
        public Component getTableCellRendererComponent(JTable table,
                Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected,
                        hasFocus, row, column);
            lastRow = row;
            return this;
        }
    }

    /**
     * A renderer that will give an indicator when a cell is being reloaded.
     */
    class DateTableCellRenderer extends DefaultTableCellRenderer {
        /** Makes sure the number of displayed in an internationalized
         * manner. */
        protected NumberFormat       formatter;
        /** Row that is currently being painted. */
        protected int                lastRow;
    

        DateTableCellRenderer() {
            setHorizontalAlignment(JLabel.RIGHT);
            formatter = NumberFormat.getInstance();
        }

        /**
         * Invoked as part of DefaultTableCellRenderers implemention. Sets
         * the text of the Cell.
         */
        public void setValue(Object value) { 

            if(value == null){
                setText("---");
            }
            else{
                DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
                String date = df.format((Date)value);
                setText(date);
            }
        }

        /**
         * Returns this.
         */
        public Component getTableCellRendererComponent(JTable table,
                Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected,
                        hasFocus, row, column);
            lastRow = row;
            return this;
        }
    }
    
    
    /**
     * Reloader is the ActionListener used in the Timer. In response to
     * the timer updating it will reset the reloadRow/reloadPath and
     * generate the necessary event so that the display will update. It
     * also implements the TreeExpansionListener so that if the tree is
     * altered while loading the reloadRow is updated accordingly.
     */
    class Reloader implements ActionListener, TreeExpansionListener, TreeSelectionListener{
    public void actionPerformed(ActionEvent ae) {
        System.out.println("ActionEvent was fired.");
    }

    /**
     * Generates and update event for the specified row. FileSystemModel2
     * could do this, but it would not know when the row has changed
     * as a result of expanding/collapsing nodes in the tree.
     */
    protected void generateChangeEvent(int row) {
        System.out.println("generate Change Event was invoked.");
        if (row != -1) {
            AbstractTableModel tModel = (AbstractTableModel)fileSystemComponent.getModel();
    
            tModel.fireTableChanged(new TableModelEvent(tModel, row, row, 1));
        }
    }

    //
    // TreeExpansionListener
    //

    /**
     * Invoked when the tree has expanded.
     */
    public void treeExpanded(TreeExpansionEvent te) {
        System.out.println("Tree Expansion was fired.");
    }

    /**
     * Invoked when the tree has collapsed.
     */
    public void treeCollapsed(TreeExpansionEvent te) {
        System.out.println("Tree Collapsion was fired.");
    }

    /**
     * Updates the reloadRow and path, this does not genernate a
     * change event.
     */
    protected void updateRow() {
        System.out.println("updateRow method was invoked.");        
    }
 
    public void valueChanged(TreeSelectionEvent tse){
        //FIXME Find out why this fires multiple times when I select a node.  It should only
        //  first once.
        System.out.println("Tree Selection was fired.");
        
        File tempFile = fileSystemModel.createFilePath(tse.getPath());
        if(currentDicomFile != tempFile){
            currentDicomFile = tempFile;
            if(currentDicomFile != null){
                if(currentDicomFile.isFile() && (currentDicomFile.toString().toLowerCase().endsWith(".dcm"))){
                    //Fill out the labels above the Split Pane.
                    //Change color to RED while loading data.
                    fileLabel.setForeground(Color.RED);
                    fileLabel.setText("File: "+currentDicomFile.getName());
                    fileSizeLabel.setForeground(Color.RED);
                    fileSizeLabel.setText("File Size: "+Long.toString(currentDicomFile.length())+" Bytes");
                    DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
                    String date = df.format(new Date(currentDicomFile.lastModified()));
                    fileDateLabel.setForeground(Color.RED);
                    fileDateLabel.setText("File Date: "+ date);
                    
                    //Use the filename and get the IOD information.
                    try{
                        dicomObjectInfo = dicomObjectInfo.getinstance();
                        dicomObjectInfo.openFile(currentDicomFile);

                        //***Put together information for the Header Tab.
                        try{
                            Vector header = dicomObjectInfo.getMetaDataHeaderNames();
                            Vector data = dicomObjectInfo.getMetaData();
                            headerTable = new JTable(data, header);
                            headerScrollPane.setViewportView(headerTable);
                        }
                        catch(DataCreationException dce){
                            System.out.println("Threw DataCreationException.");
                            System.out.println(dce.getMessage());
                            String message = "Could not successfully read MetaData: \n"
                                +dce.getMessage();
                            JOptionPane.showMessageDialog(splitPane, 
                                    message,
                                    "Threw DataCreationException", JOptionPane.ERROR_MESSAGE);
                            dce.printStackTrace();
                            headerTable = new JTable(0,0);
                            headerScrollPane.setViewportView(headerTable);
                        }

                        //***put together information for the IOD Validation Tab.
                        try{
                            String results;
                            results = dicomObjectInfo.validateIOD();
                            //iodArea.setLineWrap(true);
                            iodArea.setText(results);
                        }
                        catch(ValidationException ve){
                            System.out.println("Threw ValidationException.");
                            System.out.println(ve.getMessage());
                            String message = "Could not successfully validate MetaData \n"
                                +ve.getMessage();
                            JOptionPane.showMessageDialog(splitPane, 
                                    message,
                                    "Threw ValidationException", JOptionPane.ERROR_MESSAGE);
                            ve.printStackTrace();
                            iodArea.setText("Could not validate MetaData.");
                        }

                        //***Put together information for the Histogram Tab.
                        try{
                        //histogramPanel = new JHistogramPanel();
                        histogramPanel.setHistogramData(dicomObjectInfo.getHistogram());
                        histogramPanel.repaint();
                        
                        Vector histogramHeader = dicomObjectInfo.getHistogramTableNames();
                        Vector histogramData = dicomObjectInfo.getHistogramTable();
                        histogramTable = new JTable(histogramData, histogramHeader);
                        histogramScrollPane.setViewportView(histogramTable);
                        }
                        catch(PixelDataException pde){
                            System.out.println("Threw PixelDataException.");
                            System.out.println(pde.getMessage());
                            String message = "Could not successfully read Pixel Data: \n"
                                +pde.getMessage();
                            JOptionPane.showMessageDialog(splitPane, 
                                    message,
                                    "Threw PixelDataException", JOptionPane.ERROR_MESSAGE);
                            pde.printStackTrace();
                            histogramTable = new JTable(0,0);
                            histogramScrollPane.setViewportView(histogramTable);
                            histogramPanel.setHistogramData(null);
                            histogramPanel.repaint();
                        }
                        
                        //Change color to BLACK when all panes are loaded.
                        fileLabel.setForeground(Color.BLACK);
                        fileSizeLabel.setForeground(Color.BLACK);
                        fileDateLabel.setForeground(Color.BLACK);
                    }
                    catch(DicomFileException dfe){
                        System.out.println("Threw DicomFileException.");
                        String message = "Could not successfully open DICOM File: \n"
                            +dfe.getMessage();
                        System.out.println(dfe.getMessage());
                        JOptionPane.showMessageDialog(splitPane, 
                                message,
                                "Threw DicomFileException", JOptionPane.ERROR_MESSAGE);
                        dfe.printStackTrace();
                        headerTable = new JTable(0,0);
                        headerScrollPane.setViewportView(headerTable);
                        iodArea.setText("Could not validate MetaData.");
                        histogramTable = new JTable(0,0);
                        histogramScrollPane.setViewportView(histogramTable);
                        histogramPanel.setHistogramData(null);
                        histogramPanel.repaint();
                    }
                }
            }
        }
    }
    }
}
