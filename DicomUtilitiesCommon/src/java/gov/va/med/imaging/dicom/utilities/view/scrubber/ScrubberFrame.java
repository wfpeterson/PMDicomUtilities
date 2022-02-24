/*
 * Created on Jun 13, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gov.va.med.imaging.dicom.utilities.view.scrubber;

import java.awt.Font;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.concurrent.ExecutionException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.UIManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import gov.va.med.imaging.dicom.utilities.model.IFolderActionProgress;
import gov.va.med.imaging.dicom.utilities.model.IScrubControl;

/**
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 *
 *
 * @author William Peterson
 *
 */
public class ScrubberFrame extends JFrame implements IFolderActionProgress{

    /**
	 * 
	 */
    private static final Logger logger = LogManager.getLogger(ScrubberFrame.class);

	private static final long serialVersionUID = 45188385100624231L;
	private JCheckBox shadowGroups;
    private JCheckBox deleteOriginal;
    private JCheckBox grp6000Box;
    private JCheckBox chckbxRecursive;
    private JTextField directoryField;
    private JButton selectFiles;
    private JButton exit;
    private JButton startScrub;
    private JButton openLog;
    private JProgressBar progressBar;
    private IScrubControl scrubControl;
    private String lastFolderUsed = ".";
    private JCheckBox chckbxGoldDbImages;
    private JCheckBox chckbxExplicitVrOutput;
    

    public ScrubberFrame(IScrubControl scrubControl) {
        super();
        //FIXME-Add Console window to the GUI.
        //	I want it to display either files or directories that failed.
        logger.debug("Scrubber starting.");
        this.scrubControl = scrubControl;
        this.scrubControl.setFolderAction(this);
        setTitle("VA DICOM and Text File Scrubber");
        setBounds(100, 100, 625, 316);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        try{
            UIManager.setLookAndFeel(
                    "com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        }
        catch(Exception e){
            //leave it alone then.
        }
        getContentPane().setLayout(null);

        //FIXME-Add some type of spinning something to activity is happening.
        startScrub = new JButton();
        startScrub.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if(directoryField.getText().equals("")){
                    JOptionPane.showMessageDialog(null, "No directory to scrub.", "Folder Error",
                            JOptionPane.INFORMATION_MESSAGE);
                }
                else{
                	startScrubbing();
                }
            }
        });
        startScrub.setText("Start Scrubbing");
        startScrub.setBounds(32, 92, 139, 30);
        getContentPane().add(startScrub);

        exit = new JButton();
        exit.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
            	//scrubControl.closeControl();
            	logger.debug("Scrubber closing.");
            	dispose();
            	System.exit(0);
            }
        });
        exit.setText("Exit");
        exit.setBounds(32, 216, 139, 30);
        getContentPane().add(exit);

        selectFiles = new JButton();
        selectFiles.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                JFileChooser fileDialog = new JFileChooser();
                fileDialog.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                fileDialog.setCurrentDirectory(new File(lastFolderUsed));
                fileDialog.setMultiSelectionEnabled(false);
                fileDialog.showOpenDialog(e.getComponent());
                directoryField.setText(fileDialog.getSelectedFile().getPath());
                lastFolderUsed = fileDialog.getSelectedFile().getPath();
            }
        });
        selectFiles.setText("Select Directory");
        selectFiles.setBounds(33, 28, 139, 30);
        getContentPane().add(selectFiles);

        directoryField = new JTextField();
        directoryField.setFont(new Font("", Font.PLAIN, 12));
        directoryField.setBounds(200, 31, 374, 24);
        getContentPane().add(directoryField);

        grp6000Box = new JCheckBox();
        grp6000Box.setToolTipText("This will remove Overlay data located in Group 60xx.");
        grp6000Box.setSelected(scrubControl.isRemoveGrp6000());
        grp6000Box.addItemListener(new ItemListener() {
        	public void itemStateChanged(ItemEvent e) {
                scrubControl.changeRemoveGrp6000();
        	}
        });
        grp6000Box.setText("Remove Group 6000 Overlay");
        grp6000Box.setBounds(203, 112, 198, 30);
        getContentPane().add(grp6000Box);

        deleteOriginal = new JCheckBox();
        deleteOriginal.setToolTipText("This will delete the original DICOM file after creating the scrubbed version.");
        deleteOriginal.setSelected(scrubControl.isDeleteOriginal());
        deleteOriginal.addItemListener(new ItemListener() {
        	public void itemStateChanged(ItemEvent e) {
                scrubControl.changeDeleteOriginal();
        	}
        });
        deleteOriginal.setText("Delete original DICOM File");
        deleteOriginal.setBounds(203, 155, 198, 30);
        getContentPane().add(deleteOriginal);

        shadowGroups = new JCheckBox();
        shadowGroups.setToolTipText("This will remove all information with an Odd Group value.");
        shadowGroups.setSelected(scrubControl.isRemovePrivateAttributes());
        shadowGroups.addItemListener(new ItemListener() {
        	public void itemStateChanged(ItemEvent e) {
                scrubControl.changeRemovePrivateAttributes();
        	}
        });
        shadowGroups.setText("Remove Private Attributes");
        shadowGroups.setBounds(203, 77, 180, 30);
        getContentPane().add(shadowGroups);
        
        chckbxRecursive = new JCheckBox();
        chckbxRecursive.setToolTipText("This will go beyond the selected folder.  It will scrub any DICOM files located in subfolders.");
        chckbxRecursive.setSelected(scrubControl.isRecursiveCheck());
        chckbxRecursive.addItemListener(new ItemListener() {
        	public void itemStateChanged(ItemEvent e) {
    			scrubControl.changeRecursiveCheck();
        	}
        });
        chckbxRecursive.setText("Recursive");
        chckbxRecursive.setBounds(429, 80, 139, 25);
        getContentPane().add(chckbxRecursive);
 
        chckbxGoldDbImages = new JCheckBox("GOLD DB Images");
        chckbxGoldDbImages.setToolTipText("This does not overwrite the Patient Name and Patient ID fields, yet scrubs rest of the DICOM Header.");
        chckbxGoldDbImages.setSelected(scrubControl.isGoldDBImages());
        chckbxGoldDbImages.addItemListener(new ItemListener() {
        	public void itemStateChanged(ItemEvent e) {
    			scrubControl.changeGoldDBImages();
        	}
        });
        chckbxGoldDbImages.setBounds(429, 117, 145, 25);
        getContentPane().add(chckbxGoldDbImages);
        
        openLog = new JButton("Open Log");
        openLog.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent e) {
        		JFrame LogFrame = new JFrame();
        		new LogFrame();
        	}
        });
        openLog.setToolTipText("View files that failed to scrubbed.");
        openLog.setBounds(32, 155, 139, 30);
        getContentPane().add(openLog);
        
        progressBar = new JProgressBar();
        progressBar.addPropertyChangeListener(new PropertyChangeListener() {
        	public void propertyChange(PropertyChangeEvent evt) {
        		
        	}
        });
        progressBar.setBounds(200, 216, 300, 24);
        progressBar.setMinimum(0);
        progressBar.setValue(0);
        getContentPane().add(progressBar);
        
        chckbxExplicitVrOutput = new JCheckBox("Explicit VR Output");
        chckbxExplicitVrOutput.addItemListener(new ItemListener() {
        	public void itemStateChanged(ItemEvent e) {
        		scrubControl.changeExplicitVRCheck();
        	}
        });
        chckbxExplicitVrOutput.setBounds(429, 158, 145, 25);
        chckbxExplicitVrOutput.setEnabled(false);
        getContentPane().add(chckbxExplicitVrOutput);
        
        setVisible(true);        
    }

    private void startScrubbing(){
    	
    	SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>(){
    		
			@Override
			protected Boolean doInBackground() throws Exception {
				scrubControl.openLog();
				scrubControl.writeLog("...new Scrub run...\r\n");
				Boolean isFailures = false;
				isFailures = scrubControl.scrubFiles(new File(directoryField.getText()));
                logger.debug("Returned from scrubbing.");
				scrubControl.writeLog("...Scrub finished...\r\n");
				return isFailures;
			}
    		
			
    		@Override
    		protected void done(){
    			try {
					Boolean isFailures = get();
                    if(isFailures){
                    	JOptionPane.showMessageDialog(null, "Did not successfully scrub" +
                            " necessary DICOM object files. Review log file.", "File Error", JOptionPane.ERROR_MESSAGE);
                    }
                    else{
                        JOptionPane.showMessageDialog(null, "Successfully scrubbed folders.", 
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE);                    	
                    }
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
    			progressBar.setValue(0);
    			scrubControl.closeLog();
    		}
    	};
    	worker.execute();
    }

	@Override
	public void progressUpdate() {
		progressBar.setMaximum(scrubControl.getTotalFilesInFolder());
		progressBar.setValue(scrubControl.getCurrentFileInFolder());
	}
}
