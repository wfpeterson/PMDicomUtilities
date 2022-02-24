/*
 * Created on Jun 13, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gov.va.med.imaging.dicom.utilities.view.reconstitution;

/**
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 *
 *
 * @author William Peterson
 *
 */
/**
public class ReconstitutionFrame extends JFrame {

    private JCheckBox shadowGroups;
    private JCheckBox deleteOriginal;
    private JTextField directoryField;
    private JButton selectFiles;
    private JButton exit;
    private JButton startRecon;
    //private JCheckBox scrubVADataBox;
    private JCheckBox autoModeBox;
    private ReconstitutionControl reconControl;
    private String lastFolderUsed = ".";
    
    public static void main(String args[]) {
        try {
            ReconstitutionFrame frame = new ReconstitutionFrame();
            frame.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ReconstitutionFrame() {
        super();
        reconControl = new ReconstitutionControl();
        setTitle("DICOM File Reconstitution");
        setBounds(100, 100, 600, 271);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //try{
        //    UIManager.setLookAndFeel(
        //            "com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        //}
        //catch(Exception e){
            //leave it alone then.
        //}
        getContentPane().setLayout(null);

        autoModeBox = new JCheckBox();
        autoModeBox.setSelected(reconControl.isAutoMode());
        autoModeBox.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if(autoModeBox.isSelected()){
                    //scrubControl.setAutoMode(false);
                    //autoModeBox.setSelected(false);
                }
                else{
                    reconControl.setAutoMode(true);
                    autoModeBox.setSelected(true);
                }
            }
        });
        autoModeBox.setText("AutoMode");
        autoModeBox.setBounds(205, 76, 120, 30);
        getContentPane().add(autoModeBox);

        //scrubVADataBox = new JCheckBox();
        //scrubVADataBox.setSelected(scrubControl.isScrubVAOnly());
        //scrubVADataBox.addMouseListener(new MouseAdapter() {
            //public void mouseClicked(MouseEvent e) {
            //    if(scrubVADataBox.isSelected()){
            //        //scrubControl.setScrubVAOnly(false);
            //        //scrubVADataBox.setSelected(false);
            //    }
            //    else{
            //        scrubControl.setScrubVAOnly(true);
            //        scrubVADataBox.setSelected(true);
            //    }
            //}
        //});
        //scrubVADataBox.addActionListener(new ActionListener() {
          //  public void actionPerformed(ActionEvent e) {
          //      //
          //  }
        //});
        //scrubVADataBox.setText("Scrub VA Data Only");
        //scrubVADataBox.setBounds(203, 116, 163, 30);
        //getContentPane().add(scrubVADataBox);

        startRecon = new JButton();
        startRecon.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                try{
                    if(directoryField.getText().equals("")){
                        System.out.println("Reconstitution directory: "+directoryField.getText());
                        JOptionPane.showMessageDialog(null, "No directory to Reconstitute.", "Folder Error",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                    else{
                        System.out.println("Starting reconstitution process.");
                        reconControl.setDirectory(directoryField.getText());
                        reconControl.reconstituteFiles(new File(directoryField.getText()));
                        System.out.println("Successfully reconstituted directory: "+directoryField.getText());
                        JOptionPane.showMessageDialog(null, "Successfully reconstituted folder.", 
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                }
                catch(DicomFileException dfe){
                    JOptionPane.showMessageDialog(null, "Did not successfully access or create" +
                            " .DCM files.", "File Error", JOptionPane.ERROR_MESSAGE);
                }
                catch(DataCreationException dce){
                    JOptionPane.showMessageDialog(null, "Did not successfully generate reconstituted " +
                            "data.", "File Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        startRecon.setText("Reconstitute");
        startRecon.setBounds(32, 92, 139, 30);
        getContentPane().add(startRecon);

        exit = new JButton();
        exit.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
               System.exit(0);
            }
        });
        exit.setText("Exit");
        exit.setBounds(33, 156, 139, 30);
        getContentPane().add(exit);

        selectFiles = new JButton();
        selectFiles.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                JFileChooser fileDialog = new JFileChooser();
                //fileDialog.setFileFilter(new DCMFilter());
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

        deleteOriginal = new JCheckBox();
        deleteOriginal.setSelected(reconControl.isDeleteOriginal());
        deleteOriginal.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if(deleteOriginal.isSelected()){
                    reconControl.setDeleteOriginal(true);
                    //deleteOriginal.setSelected(false);
                }
                else{
                    reconControl.setDeleteOriginal(false);
                    //deleteOriginal.setSelected(true);
                }
                
            }
        });
        deleteOriginal.setText("Delete original DICOM File");
        deleteOriginal.setBounds(203, 155, 198, 30);
        getContentPane().add(deleteOriginal);

        shadowGroups = new JCheckBox();
        shadowGroups.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if(shadowGroups.isSelected()){
                    reconControl.setRemovePrivateAttributes(true);
                }
                else{
                    reconControl.setRemovePrivateAttributes(false);
                }
            }
        });
        shadowGroups.setText("Remove Private Attributes");
        shadowGroups.setBounds(392, 77, 162, 30);
        getContentPane().add(shadowGroups);
        //
    }
}
**/
