package gov.va.med.imaging.dicom.pmcdimporter;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.swing.EventTableModel;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Utility screen to display the list of configured DICOM Applications entities 
 * this simulator can communicate with.  The constructor does not make the 
 * jFrame visible, it has to be made visible form the creating frame.
 * <p>
 * Sets the following default behavior of the jFrame:
 * <ul>
 * <li> defaultCloseOperation = HIDE
 * <li> title = DICOM Configuration
 * <ul>
 * <p>
 * Created on Mar 29, 2011, 2:52:48 PM
 * 
@author Dee Csipo
 * 
                     
@version     %I%, %G%
 *  
                    
@since       1.0
 
 */
public class DicomConfig extends javax.swing.JFrame implements ComponentListener {
    private DicomEchoDialog mTestDialog;

    /** Creates new form DicomConfig */
    public DicomConfig() {
        // temp stuff to add to dicom config
        mCurrentConfig = new DicomConfigInfo();
        mOriginal = null;
        initComponents();
        // build a JTable
        EditPanel.setVisible(true);
        updateEditPanel(null);
        updateTableView();
        addDocumentListeners();
    }
    private void updateCurrentConfig(DocumentEvent documentEvent){
        
        if (documentEvent.getDocument().getProperty("owner") == AliasName){
            if (!mCurrentConfig.getAlias().equals(AliasName.getText())){
                mCurrentConfig.setAlias(AliasName.getText());
                mCurrentConfig.setChanged(true);
            }
        } else if (documentEvent.getDocument().getProperty("owner") == AETitle){
            if (!mCurrentConfig.getApplicationEntity().equals(AETitle.getText())){
                mCurrentConfig.setApplicationEntity(AETitle.getText());
                mCurrentConfig.setChanged(true);
            }
        } else if (documentEvent.getDocument().getProperty("owner") == CallingAETitle){
            if (!mCurrentConfig.getmCallingAE().equals(CallingAETitle.getText())){
                mCurrentConfig.setmCallingAE(CallingAETitle.getText());
                mCurrentConfig.setChanged(true);
            }
        } else if (documentEvent.getDocument().getProperty("owner") == Host){
            if (!mCurrentConfig.getHost().equals(Host.getText())){
                mCurrentConfig.setHost(Host.getText());
                mCurrentConfig.setChanged(true);
            }
        } else if (documentEvent.getDocument().getProperty("owner") == Port){
            if (!mCurrentConfig.getPort().equals(Port.getText())){
                mCurrentConfig.setPort(Port.getText());
                mCurrentConfig.setChanged(true);
            }
        }
    }
    DocumentListener documentListener = new DocumentListener() {
        @Override
      public void changedUpdate(DocumentEvent documentEvent) {
        updateCurrentConfig(documentEvent);
        checkComplete();
      }
        @Override
      public void insertUpdate(DocumentEvent documentEvent) {
        updateCurrentConfig(documentEvent);
        checkComplete();
      }
        @Override
      public void removeUpdate(DocumentEvent documentEvent) {
        updateCurrentConfig(documentEvent);
        checkComplete();
      }
    };
    private void addDocumentListeners(){
        AliasName.getDocument().addDocumentListener(documentListener);
        AliasName.getDocument().putProperty("owner", AliasName);
        AETitle.getDocument().addDocumentListener(documentListener);
        AETitle.getDocument().putProperty("owner", AETitle);
        CallingAETitle.getDocument().addDocumentListener(documentListener);
        CallingAETitle.getDocument().putProperty("owner", CallingAETitle);
        Host.getDocument().addDocumentListener(documentListener);
        Host.getDocument().putProperty("owner", Host);      
        Port.getDocument().addDocumentListener(documentListener);
        Port.getDocument().putProperty("owner", Port);
    }
    /**
     * Internal function called both by the table entry double click and the
     * edit button action
     */
    private void editEntry(){
        int selectedRow = ApplicationEntities.convertRowIndexToModel(ApplicationEntities.getSelectedRow());
        if (mTableModel != null){
            mOriginal = ConfInfo.findByAlias(mTableModel.getValueAt(selectedRow, 0));
            setCurrentConfig (new DicomConfigInfo(mOriginal));
            if (getCurrentConfig() != null) {
                updateEditPanel(getCurrentConfig());
                updateTableView();
                Test.setEnabled(true);
            }
        }        
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        ApplicationEntities = new javax.swing.JTable();
        DCMConfAdd = new javax.swing.JButton();
        DCMConfClose = new javax.swing.JButton();
        DCMConfDelete = new javax.swing.JButton();
        EditPanel = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        AliasName = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        AETitle = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        CallingAETitle = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        Role = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        Host = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        Port = new javax.swing.JTextField();
        SaveAsBtn = new javax.swing.JButton();
        SaveBtn = new javax.swing.JButton();
        Test = new javax.swing.JButton();
        updateUID = new javax.swing.JCheckBox();

        setTitle("DICOM Configuration");
        setModalExclusionType(java.awt.Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
        });

        ApplicationEntities.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "DICOM AE", "Role", "Host or IP", "Port"
            }
        ));
        ApplicationEntities.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        ApplicationEntities.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ApplicationEntitiesMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(ApplicationEntities);

        DCMConfAdd.setText("Add");
        DCMConfAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DCMConfAddActionPerformed(evt);
            }
        });

        DCMConfClose.setText("Close");
        DCMConfClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DCMConfCloseActionPerformed(evt);
            }
        });

        DCMConfDelete.setText("Delete");
        DCMConfDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DCMConfDeleteActionPerformed(evt);
            }
        });

        EditPanel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel6.setText("Alias Name");

        jLabel1.setText("Called AE Title");

        jLabel5.setText("Calling AE Title");

        jLabel2.setText("Role");

        Role.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "MWL SCP", "STORE SCP" }));
        Role.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RoleActionPerformed(evt);
            }
        });

        jLabel3.setText("Host/IP");

        jLabel4.setText("Port");

        Port.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PortActionPerformed(evt);
            }
        });

        SaveAsBtn.setText("Save As");
        SaveAsBtn.setDefaultCapable(false);
        SaveAsBtn.setEnabled(false);
        SaveAsBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SaveAsBtnActionPerformed(evt);
            }
        });

        SaveBtn.setText("Save");
        SaveBtn.setEnabled(false);
        SaveBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SaveBtnActionPerformed(evt);
            }
        });

        Test.setText("Test");
        Test.setEnabled(false);
        Test.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TestActionPerformed(evt);
            }
        });

        updateUID.setText("Update UIDs");
        updateUID.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateUIDActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout EditPanelLayout = new javax.swing.GroupLayout(EditPanel);
        EditPanel.setLayout(EditPanelLayout);
        EditPanelLayout.setHorizontalGroup(
            EditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(EditPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(EditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addComponent(jLabel1)
                    .addComponent(jLabel5)
                    .addComponent(jLabel4)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(EditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, EditPanelLayout.createSequentialGroup()
                        .addComponent(Port, javax.swing.GroupLayout.DEFAULT_SIZE, 297, Short.MAX_VALUE)
                        .addGap(260, 260, 260))
                    .addComponent(CallingAETitle, javax.swing.GroupLayout.DEFAULT_SIZE, 297, Short.MAX_VALUE)
                    .addComponent(AliasName, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(AETitle, javax.swing.GroupLayout.DEFAULT_SIZE, 297, Short.MAX_VALUE)
                    .addComponent(Host, javax.swing.GroupLayout.DEFAULT_SIZE, 297, Short.MAX_VALUE)
                    .addGroup(EditPanelLayout.createSequentialGroup()
                        .addComponent(Role, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(55, 55, 55)
                        .addComponent(updateUID))))
            .addGroup(EditPanelLayout.createSequentialGroup()
                .addGroup(EditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(EditPanelLayout.createSequentialGroup()
                        .addGap(108, 108, 108)
                        .addComponent(SaveAsBtn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(SaveBtn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Test))
                    .addGroup(EditPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel3)))
                .addContainerGap())
        );

        EditPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {AETitle, AliasName, CallingAETitle, Host, Port});

        EditPanelLayout.setVerticalGroup(
            EditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(EditPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(EditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(AliasName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(EditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(AETitle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(EditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(CallingAETitle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(EditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(Role, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(updateUID))
                .addGap(4, 4, 4)
                .addGroup(EditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(Host, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(EditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Port, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(EditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Test)
                    .addComponent(SaveBtn)
                    .addComponent(SaveAsBtn))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 496, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(DCMConfAdd)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(DCMConfDelete)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(DCMConfClose)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(EditPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 444, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(EditPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, 0, 0, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(DCMConfAdd)
                            .addComponent(DCMConfDelete)
                            .addComponent(DCMConfClose))))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    /**
     * Catching the Mouse clicked event to force an update of the drop list 
     * before displaying
     * 
     * @param evt 
     */
    private void ApplicationEntitiesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ApplicationEntitiesMouseClicked
            editEntry();
    }//GEN-LAST:event_ApplicationEntitiesMouseClicked
    /**
     * Window closing function
     * @param evt 
     */
    private void DCMConfCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DCMConfCloseActionPerformed
        setVisible(false);
    }//GEN-LAST:event_DCMConfCloseActionPerformed
    /**
     * Internal function called from multiple places to update the table view
     * post change of the config info list.
     * 
     */
    private void updateEditPanel(DicomConfigInfo info) {
        if (info !=null) {
            AliasName.setText(info.getAlias());
            AETitle.setText(info.getApplicationEntity());
            CallingAETitle.setText(info.getmCallingAE());
            Host.setText(info.getHost());
            Port.setText(info.getPort());
            Role.setSelectedItem(info.getRole());
            updateUID.setSelected(info.ismUIDupdate());
            info.setChanged(false);
        } else {
            AliasName.setText("");
            AETitle.setText("");
            CallingAETitle.setText("");
            Host.setText("");
            Port.setText("");
            Role.setSelectedIndex(0);         
            updateUID.setSelected(false);
        }
    }    
    private void updateTableView(){
        if (mTableModel == null){
            SortedList sortedTableInfo = new SortedList(mAETitles, null);
            String[] propertyNames = new String[] {"Alias", "Role", "Host", "Port"};
            String[] columnLabels = new String[] {"Alias Name", "Role", "Host", "Port"};
            TableFormat tf = GlazedLists.tableFormat(DicomConfigInfo.class, propertyNames, columnLabels);
            mTableModel = new EventTableModel(sortedTableInfo, tf);
            ApplicationEntities.setModel(mTableModel); 
            ApplicationEntities.setAutoCreateRowSorter(true);
            ApplicationEntities.setAutoResizeMode(4/*AUTO_RESIZE_ALL_COLUMNS*/);
        }
    }
    private void SaveConfigEntries(){
        String msg = ConfInfo.write();
        mCurrentConfig = new DicomConfigInfo();
        mOriginal = null;
        updateEditPanel(null);

    }
    /**
     * Add button was clicked
     * 
     * @param evt 
     */
    private void DCMConfAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DCMConfAddActionPerformed
        mCurrentConfig = new DicomConfigInfo();
        mOriginal = null;
        updateEditPanel(null);
        updateTableView();
    }//GEN-LAST:event_DCMConfAddActionPerformed

    private void DCMConfDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DCMConfDeleteActionPerformed
        int selectedRow = ApplicationEntities.getSelectedRow();
//        if (selectedRow > 0){
            selectedRow = ApplicationEntities.convertRowIndexToModel(selectedRow);
                
            if (mTableModel != null){
                DicomConfigInfo info = ConfInfo.findByAlias(mTableModel.getValueAt(selectedRow, 0));
                if (info != null) {
                    mAETitles.remove(info);
                    String msg = ConfInfo.write();
                    updateEditPanel(null);
                    updateTableView();
                }
            }
//        }
    }//GEN-LAST:event_DCMConfDeleteActionPerformed

    private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
        Logger.getLogger(DicomConfig.class.getName()).log(Level.INFO, null, evt);
    }//GEN-LAST:event_formComponentResized

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        Logger.getLogger(DicomConfig.class.getName()).log(Level.INFO, null, evt);
    }//GEN-LAST:event_formWindowClosed
                                                                                                              
    private void SaveAsBtnActionPerformed(java.awt.event.ActionEvent evt) {                                          
        mAETitles.add(mCurrentConfig);
        mOriginal = null;
        SaveConfigEntries();                                  
    }                                                                                   
                                                                                   
    private void SaveBtnActionPerformed(java.awt.event.ActionEvent evt) { 
        if (mOriginal != null && !mOriginal.contentEquals(mCurrentConfig)){
            mAETitles.remove(mOriginal);
            mAETitles.add(mCurrentConfig);
            mOriginal = null;
        } else {
            mAETitles.add(mCurrentConfig);
            mOriginal = null;
        }
                
        SaveConfigEntries();
    }                                                                               
    private void TestActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TestActionPerformed
        Integer port;         
        mTestDialog = new DicomEchoDialog(new javax.swing.JFrame(), true); 
        mTestDialog.addComponentListener(this);
        mTestDialog.setCalledAETitle(mCurrentConfig.getApplicationEntity());         
        mTestDialog.setCallingAETitle(mCurrentConfig.getmCallingAE());         
        mTestDialog.setHost(mCurrentConfig.getHost());         
        try {             
            port = Integer.parseInt(mCurrentConfig.getPort().trim());         
        } 
        catch (NumberFormatException nfe) {
            this.setCursor(null); 
            return;         
        }         
        mTestDialog.setPort(port);         
        mTestDialog.setVisible(true);     
    }//GEN-LAST:event_TestActionPerformed

    private void RoleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RoleActionPerformed
        mCurrentConfig.setRole(Role.getSelectedItem().toString());
        checkComplete();
    }//GEN-LAST:event_RoleActionPerformed

    private void PortActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PortActionPerformed
        Logger.getLogger(DicomConfig.class.getName()).log(Level.INFO, null, evt);
    }//GEN-LAST:event_PortActionPerformed

    private void updateUIDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateUIDActionPerformed
        // TODO add your handling code here:
        mCurrentConfig.setmUIDupdate(updateUID.isSelected());
    }//GEN-LAST:event_updateUIDActionPerformed
    public void RunIt(){
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                new DicomConfig().setVisible(true);
            }
        });
        
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                new DicomConfig().setVisible(true);
            }
        });
    }

    public EventList<DicomConfigInfo> getConfig(){
        return mAETitles;
    }
    // Custom declarations.
     public void componentHidden(ComponentEvent e) {
        Logger.getLogger(DicomConfig.class.getName()).log(Level.INFO, null, e);
    }

    public void componentMoved(ComponentEvent e) {
        Logger.getLogger(DicomConfig.class.getName()).log(Level.INFO, null, e);
    }

    public void componentResized(ComponentEvent e) {
        Logger.getLogger(DicomConfig.class.getName()).log(Level.INFO, null, e);
    }

    public void componentShown(ComponentEvent e) {
        mTestDialog.doEcho();
    }
    
    private EventTableModel mTableModel;
    private DicomConfigInfoList ConfInfo= DicomConfigInfoList.getInstance();
    private EventList<DicomConfigInfo> mAETitles = DicomConfigInfoList.getObservableInstance();
    private DicomConfigInfo mCurrentConfig;
    private DicomConfigInfo mOriginal;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField AETitle;
    private javax.swing.JTextField AliasName;
    private javax.swing.JTable ApplicationEntities;
    private javax.swing.JTextField CallingAETitle;
    private javax.swing.JButton DCMConfAdd;
    private javax.swing.JButton DCMConfClose;
    private javax.swing.JButton DCMConfDelete;
    private javax.swing.JPanel EditPanel;
    private javax.swing.JTextField Host;
    private javax.swing.JTextField Port;
    private javax.swing.JComboBox Role;
    private javax.swing.JButton SaveAsBtn;
    private javax.swing.JButton SaveBtn;
    private javax.swing.JButton Test;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JCheckBox updateUID;
    // End of variables declaration//GEN-END:variables
    /**
     * Getters and setters
     */
    /**
     * @return the mCurrentConfig
     */
    public DicomConfigInfo getCurrentConfig() {
        return mCurrentConfig;
    }

    /**
     * @param mCurrentConfig the mCurrentConfig to set
     */
    public void setCurrentConfig(DicomConfigInfo mCurrentConfig) {
        this.mCurrentConfig = mCurrentConfig;
    }
    private boolean isComplete(){
        if (mCurrentConfig.isEmpty()) {
            mCurrentConfig.setRole(Role.getSelectedItem().toString());
        }
        if (!mCurrentConfig.isEmpty() && mCurrentConfig.isValid()){
            return true;
        }
        return false;
    }private boolean isContentChanged(){
        if (mOriginal != null){
            if (mOriginal.contentEquals(mCurrentConfig)){
                return false;
            } else {
                return true;
            }
        }else{
            return mCurrentConfig.isChanged();
        }
    }

    private void checkComplete() {
        if (isComplete()){
            if (isContentChanged()){
                SaveBtn.setEnabled(true);
            } else {
                SaveBtn.setEnabled(false);
            }
            Test.setEnabled(true);
            if (mOriginal != null){
                if (!mOriginal.getAlias().equals(mCurrentConfig.getAlias())){
                    SaveAsBtn.setEnabled(true);
                } else {
                    SaveAsBtn.setEnabled(false);                    
                }
            } 
        } else {
            /**
             * this is an edit session see if the content was changed
             */
            SaveBtn.setEnabled(false);
            Test.setEnabled(false);
            SaveAsBtn.setEnabled(false);
                   
        }
    }
}
