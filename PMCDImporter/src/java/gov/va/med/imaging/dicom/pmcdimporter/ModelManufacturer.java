/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ModelManufacturer.java
 *
 * Created on Sep 19, 2011, 11:26:43 PM
 */

package gov.va.med.imaging.dicom.pmcdimporter;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.swing.EventTableModel;

/**
 *
 * @author dcsipo
 */
public class ModelManufacturer extends javax.swing.JFrame {

    /**
     * Local properties
     *
     */
    private EventList<ModelManufacturerEntry> mManufModelNotificationList = ModelManufacturerList.getObservableInstance();
    private ModelManufacturerList mManufacturerModelList = ModelManufacturerList.getInstance();
    private ModelManufacturerList mParseList = ModelManufacturerList.getInstance();
    private ModelManufacturerEntry mCurrentEntry;
    private ModelManufacturerEntry mOriginal;
    private EventTableModel mTableModel;
    /**
     * Private local routines
     * 
     */
    private void editEntry(){
       int selectedRow = ManufacturerModelTable.convertRowIndexToModel(ManufacturerModelTable.getSelectedRow());
        if (mTableModel != null){
            mOriginal = mManufacturerModelList.findByAlias(mTableModel.getValueAt(selectedRow, 0));
            if (mOriginal != null && !mOriginal.getAlias().equals("<original>")) {
                setmCurrentEntry (new ModelManufacturerEntry(mOriginal));
                updateEditPanel(getmCurrentEntry());
                updateTableView();
                SaveBtn.setEnabled(false);
                SaveAs.setEnabled(false);
           }
        }        
    }
    private void updateEditPanel(ModelManufacturerEntry info) {
        if (info !=null) {
            AliasName.setText(info.getAlias());
            ManufacturerEntry.setText(info.getManufacturer());
            ModelEntry.setText(info.getModel());
            ModalitySelect.setSelectedItem(info.getModality());
        } else {
            AliasName.setText("<original>");
            ManufacturerEntry.setText("<original>");
            ModelEntry.setText("<original>");
            ModalitySelect.setSelectedIndex(0);
        }
    }    
    private void SaveConfigEntries(){
        String msg = mManufacturerModelList.write();
        mCurrentEntry = new ModelManufacturerEntry();
        mOriginal = null;
        updateEditPanel(null);
    }
 
    /** Creates new form ModelManufacturer */
    public ModelManufacturer() {
        mCurrentEntry = new ModelManufacturerEntry();
        mOriginal = null;
        // build a JTable
        initComponents();
        EditPanel.setVisible(true);
        updateEditPanel(null);       
        updateTableView();
    }
    private boolean isComplete(){
        if (getmCurrentEntry().getAlias().equalsIgnoreCase("<original>") || getmCurrentEntry().getAlias().equalsIgnoreCase("")){
            return false;
        }
        getmCurrentEntry().setModality(ModalitySelect.getSelectedItem().toString());
        if (!mCurrentEntry.isEmpty() && getmCurrentEntry().isValid()){
            return true;
        }
        return false;
    }
    private void checkComplete(){
        if (isComplete()){
            SaveBtn.setEnabled(true);
            if (mOriginal != null && !mParseList.isUnique(getmCurrentEntry())){
                SaveAs.setEnabled(false);
            } else {
                SaveAs.setEnabled(true);
            }
        } else {
            SaveBtn.setEnabled(false);
            SaveAs.setEnabled(false);
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
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        ManufacturerModelTable = new javax.swing.JTable();
        CloseBtn = new javax.swing.JButton();
        DeletBtn = new javax.swing.JButton();
        EditPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        AliasName = new javax.swing.JTextField();
        ManufacturerEntry = new javax.swing.JTextField();
        ModelEntry = new javax.swing.JTextField();
        ModalitySelect = new javax.swing.JComboBox();
        SaveBtn = new javax.swing.JButton();
        SaveAs = new javax.swing.JButton();

        setTitle("Model & Manufacturer");

        jPanel1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        ManufacturerModelTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Alias Name", "Manufacturer", "Model", "Modality"
            }
        ));
        ManufacturerModelTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ManufacturerModelTableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(ManufacturerModelTable);

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(9, 9, 9)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 551, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(20, 20, 20)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 278, Short.MAX_VALUE)
                .addContainerGap())
        );

        CloseBtn.setText("Close");
        CloseBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CloseBtnActionPerformed(evt);
            }
        });

        DeletBtn.setText("Delete");
        DeletBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DeletBtnActionPerformed(evt);
            }
        });

        EditPanel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel1.setText("Manufacturer");

        jLabel2.setText("Model");

        jLabel3.setText("Modality");

        jLabel4.setText("Alias Label");

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${mCurrentEntry.alias}"), AliasName, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        AliasName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                AliasNameKeyReleased(evt);
            }
        });

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${mCurrentEntry.manufacturer}"), ManufacturerEntry, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        ManufacturerEntry.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                ManufacturerEntryKeyReleased(evt);
            }
        });

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${mCurrentEntry.model}"), ModelEntry, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        ModelEntry.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                ModelEntryKeyReleased(evt);
            }
        });

        ModalitySelect.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "<original>", "AR", "AU", "BDUS", "BI", "BMD", "CR", "CT", "DG", "DOC", "DX", "ECG", "EPS", "ES", "FID", "GM", "HC", "HD", "IO", "IOL", "IVOCT", "IVUS", "KER", "KO", "LEN", "LS", "MG", "MR", "NM", "OAM", "OCT", "OP", "OPM", "OPT", "OPV", "OT", "PLAN", "PR", "PT", "PX", "REG", "RESP", "RF", "RG", "RTDOSE", "RTIMAGE", "RTPLAN", "RTRECORD", "RTSTRUCT", "SEG", "SM", "SMR", "SR", "SRF", "TG", "US", "VA", "XA", "XC" }));
        ModalitySelect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ModalitySelectActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout EditPanelLayout = new org.jdesktop.layout.GroupLayout(EditPanel);
        EditPanel.setLayout(EditPanelLayout);
        EditPanelLayout.setHorizontalGroup(
            EditPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(EditPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(EditPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel1)
                    .add(jLabel4)
                    .add(jLabel2)
                    .add(jLabel3))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(EditPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(ModelEntry, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 293, Short.MAX_VALUE)
                    .add(ManufacturerEntry, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 293, Short.MAX_VALUE)
                    .add(AliasName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 293, Short.MAX_VALUE)
                    .add(ModalitySelect, 0, 293, Short.MAX_VALUE))
                .addContainerGap())
        );
        EditPanelLayout.setVerticalGroup(
            EditPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(EditPanelLayout.createSequentialGroup()
                .add(8, 8, 8)
                .add(EditPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(AliasName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel4))
                .add(2, 2, 2)
                .add(EditPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(ManufacturerEntry, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel1))
                .add(2, 2, 2)
                .add(EditPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(ModelEntry, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel2))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(EditPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel3)
                    .add(ModalitySelect, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(187, Short.MAX_VALUE))
        );

        SaveBtn.setText("Save");
        SaveBtn.setEnabled(false);
        SaveBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SaveBtnActionPerformed(evt);
            }
        });

        SaveAs.setText("Save As");
        SaveAs.setEnabled(false);
        SaveAs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SaveAsActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(18, 18, 18)
                        .add(EditPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(52, Short.MAX_VALUE))
                    .add(layout.createSequentialGroup()
                        .add(CloseBtn, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 295, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(SaveAs, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 233, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(SaveBtn, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 227, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(DeletBtn, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 282, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(41, 41, 41))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(EditPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .add(18, 18, 18)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(CloseBtn)
                    .add(SaveAs)
                    .add(DeletBtn)
                    .add(SaveBtn))
                .add(17, 17, 17))
        );

        bindingGroup.bind();

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void CloseBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CloseBtnActionPerformed
        setVisible(false);
    }//GEN-LAST:event_CloseBtnActionPerformed

    private void DeletBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DeletBtnActionPerformed
        int selectedRow = ManufacturerModelTable.getSelectedRow();
         if (selectedRow > 0){
            selectedRow = ManufacturerModelTable.convertRowIndexToModel(selectedRow);
            if (mTableModel != null){
                ModelManufacturerEntry info = mManufacturerModelList.findByAlias(mTableModel.getValueAt(selectedRow, 0));
                if (info != null) {
                    if (!info.getAlias().equals("<original>")) {
                        mManufModelNotificationList.remove(info);
                        String msg = mManufacturerModelList.write();
                        mCurrentEntry = new ModelManufacturerEntry("<original>", "<original>", "<original>", "<original>");
                        updateEditPanel(null);
                        updateTableView();
                        SaveBtn.setEnabled(false);
                        SaveAs.setEnabled(false);
                    }
                }
            }
         }
    }//GEN-LAST:event_DeletBtnActionPerformed

    private void ManufacturerModelTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ManufacturerModelTableMouseClicked
        editEntry();
    }//GEN-LAST:event_ManufacturerModelTableMouseClicked

    private void AliasNameKeyReleased(java.awt.event.KeyEvent evt) {                                              
	checkComplete();    
    }                                     

    private void ManufacturerEntryKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ManufacturerEntryKeyReleased
        checkComplete();
    }//GEN-LAST:event_ManufacturerEntryKeyReleased

    private void ModelEntryKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ModelEntryKeyReleased
        checkComplete();
    }//GEN-LAST:event_ModelEntryKeyReleased

    private void SaveBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SaveBtnActionPerformed
        if (mCurrentEntry != null){
            if (!mCurrentEntry.getAlias().equalsIgnoreCase("<original>")){
                if (mOriginal != null && !mOriginal.contentEquals(mCurrentEntry)){
                    mManufModelNotificationList.remove(mOriginal);
                    mManufModelNotificationList.add(mCurrentEntry);
                    mOriginal = null;
                } else {
                    if (mParseList.isUnique(mCurrentEntry)) {
                        mManufModelNotificationList.add(mCurrentEntry);
                        mOriginal = null;
                    }
                }
                SaveConfigEntries();
            }
        }
    }//GEN-LAST:event_SaveBtnActionPerformed

    private void SaveAsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SaveAsActionPerformed
        if (mCurrentEntry != null){
            if (!mCurrentEntry.getAlias().equalsIgnoreCase("<original>") && 
                mOriginal != null && !mOriginal.contentEquals(mCurrentEntry)){
                    mManufModelNotificationList.add(mCurrentEntry);
                    mOriginal = null;
                    SaveConfigEntries(); 
            }
        }
     }//GEN-LAST:event_SaveAsActionPerformed

    private void ModalitySelectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ModalitySelectActionPerformed
        if (mCurrentEntry != null){
            checkComplete();
        }
    }//GEN-LAST:event_ModalitySelectActionPerformed

    private void updateTableView(){
     if (mTableModel == null){
            SortedList sortedTableInfo = new SortedList(mManufModelNotificationList, null);
            String[] propertyNames = new String[] {"Alias", "Manufacturer", "Model", "Modality"};
            String[] columnLabels = new String[] {"Alias Name", "Manufacturer", "Model", "Modality"};
            TableFormat tf = GlazedLists.tableFormat(ModelManufacturerEntry.class, propertyNames, columnLabels);
            mTableModel = new EventTableModel(sortedTableInfo, tf);
            ManufacturerModelTable.setModel(mTableModel);  
            ManufacturerModelTable.setAutoCreateRowSorter(true);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new ModelManufacturer().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField AliasName;
    private javax.swing.JButton CloseBtn;
    private javax.swing.JButton DeletBtn;
    private javax.swing.JPanel EditPanel;
    private javax.swing.JTextField ManufacturerEntry;
    private javax.swing.JTable ManufacturerModelTable;
    private javax.swing.JComboBox ModalitySelect;
    private javax.swing.JTextField ModelEntry;
    private javax.swing.JButton SaveAs;
    private javax.swing.JButton SaveBtn;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    /**
     * @return the mCurrentEntry
     */
    public ModelManufacturerEntry getmCurrentEntry() {
        return mCurrentEntry;
    }

    /**
     * @param mCurrentEntry the mCurrentEntry to set
     */
    public void setmCurrentEntry(ModelManufacturerEntry _mCurrentEntry) {
        mCurrentEntry = _mCurrentEntry;
    }
}