package gov.va.med.imaging.dicom.utilities.view.general.metadata;

import java.awt.Font;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;

import gov.va.med.imaging.dicom.utilities.information.MetaDataInfo;
import gov.va.med.imaging.dicom.utilities.model.IFolderActionProgress;
import gov.va.med.imaging.dicom.utilities.model.IMetaDataInfoControl;
import javax.swing.JProgressBar;

public class MetaDataFrame extends JFrame implements IFolderActionProgress {

	private static final long serialVersionUID = 4758477794665251596L;
	private JPanel contentPane;
	private JTextField txtFolder;
	private JButton btnSelectFolder;
	private JTextArea txtTextArea;
	private JScrollPane scrollPane;
	private JButton btnStart;
	private JCheckBox chckbxRecursive;
	private JProgressBar progressBar;
	private IMetaDataInfoControl metaDataControl;
    private String lastFolderUsed = ".";



	/**
	 * Create the frame.
	 */
	public MetaDataFrame(IMetaDataInfoControl metaDataControl) {
		super();
		this.metaDataControl = metaDataControl;
        this.metaDataControl.setFolderAction(this);
		setTitle("MetaData Information");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 900, 746);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		txtFolder = new JTextField();
		txtFolder.setFont(new Font("Dialog", Font.PLAIN, 12));
		txtFolder.setBounds(163, 39, 374, 24);
		contentPane.add(txtFolder);
		
		chckbxRecursive = new JCheckBox("Recursive");
		chckbxRecursive.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				metaDataControl.changeRecursiveCheck();
			}
		});
		chckbxRecursive.setBounds(163, 72, 113, 25);
		contentPane.add(chckbxRecursive);
		
		btnStart = new JButton("Start");
		btnStart.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
                if(txtFolder.getText().equals("")){
                    JOptionPane.showMessageDialog(null, "No folder selected.", "Folder Error",
                            JOptionPane.INFORMATION_MESSAGE);
                }
                else{
                	startReadingMetaData(new File(txtFolder.getText()));
                }
			}
		});
		btnStart.setBounds(12, 115, 97, 25);
		contentPane.add(btnStart);
		
		btnSelectFolder = new JButton("Select Folder");
		btnSelectFolder.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
                JFileChooser fileDialog = new JFileChooser();
                fileDialog.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                fileDialog.setCurrentDirectory(new File(lastFolderUsed));
                fileDialog.setMultiSelectionEnabled(false);
                fileDialog.showOpenDialog(e.getComponent());
                txtFolder.setText(fileDialog.getSelectedFile().getPath());
                lastFolderUsed = fileDialog.getSelectedFile().getPath();
			}
		});
		btnSelectFolder.setBounds(12, 39, 123, 25);
		contentPane.add(btnSelectFolder);
		
		scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 164, 858, 522);
		contentPane.add(scrollPane);
		
		txtTextArea = new JTextArea();
		txtTextArea.setEditable(false);
		scrollPane.setViewportView(txtTextArea);
		
		progressBar = new JProgressBar();
		progressBar.setBounds(163, 126, 374, 14);
        progressBar.setMinimum(0);
        progressBar.setValue(0);
		contentPane.add(progressBar);
	}
	
	private void startReadingMetaData(File startingPath){
		
    	SwingWorker<List<MetaDataInfo>, Void> worker = new SwingWorker<List<MetaDataInfo>, Void>(){
    		
			@Override
			protected List<MetaDataInfo> doInBackground() throws Exception {
				
				return metaDataControl.getFileMetaDataCollection(startingPath);
			}
    		
			
    		@Override
    		protected void done(){
    			try {
					List<MetaDataInfo> metaDataCollection = get();
                    if(metaDataCollection.isEmpty()){
                    	JOptionPane.showMessageDialog(null, "Did not successfully get DICOM metadata." +
                            " necessary DICOM object files. Review log file.", "File Error", JOptionPane.ERROR_MESSAGE);
                    }
                    else{
                        //JOptionPane.showMessageDialog(null, "Successfully retrieved DICOM metadata.", 
                        //        "Success",
                        //        JOptionPane.INFORMATION_MESSAGE);
                    	StringBuffer buffer = new StringBuffer();
                    	Iterator<MetaDataInfo> iter = metaDataCollection.iterator();
                    	while(iter.hasNext()){
                    		MetaDataInfo temp = (MetaDataInfo)iter.next();
                    		buffer.append(temp.getFilename()+"\n");
                    		buffer.append(temp.getSopClass()+"\n");
                    		buffer.append(temp.getTransferSyntax()+"\n");
                    		buffer.append(temp.getManufacturer()+"\n");
                    		buffer.append(temp.getModel()+"\n");
                    		buffer.append(temp.getMfgVersion()+"\n");
                    		buffer.append(temp.getSourceAETitle()+"\n");
                    		buffer.append("\n");
                    	}
                    	txtTextArea.setText(buffer.toString());
                    }
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
    			progressBar.setValue(0);
    		}
    	};
    	worker.execute();
    }

	@Override
	public void progressUpdate() {
		progressBar.setMaximum(metaDataControl.getTotalFilesInFolder());
		progressBar.setValue(metaDataControl.getCurrentFileInFolder());		
	}
}
