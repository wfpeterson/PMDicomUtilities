/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * DicomDirectoryBrowserDialog.java
 *
 * Created on May 2, 2013, 11:10:39 AM
 */
package gov.va.med.imaging.dicom.pmcdimporter;

import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.DicomDirectoryBrowser;
import com.pixelmed.dicom.DicomException;
import java.awt.Cursor;
import java.io.File;
import java.util.Vector;

/**
 *
 * @author dcsipo
 */
public class DicomDirectoryBrowserDialog extends javax.swing.JDialog {
    private Vector selectedFiles;
    private String DicomDirFile;
    protected File savedImagesFolder;
    protected String lastDirectoryPath;
    private boolean mCancelled = false;
    private DicomDirBrowserFrame tree;
    
    boolean isCancelled() {
        return mCancelled;
    }
    
    private class DicomDirBrowserFrame extends DicomDirectoryBrowser{
 		/**
		 * @param	list
		 * @param	imagePanel
		 * @param	referenceImagePanelForImages
		 * @param	referenceImagePanelForSpectra
		 * @exception	DicomException
		 */
		public DicomDirBrowserFrame(AttributeList list) throws DicomException {
                    super(list,lastDirectoryPath,dicomdirTreeScrollPane,
                            scrollPaneOfCurrentAttributes
                          );
		}

		/**
		 * @param	paths
		 */
        @Override
		protected void doSomethingWithSelectedFiles(Vector paths) {
			setSelectedFiles(paths);
		}

		/**
		 */
        @Override
		protected void doSomethingMoreWithWhateverWasSelected() {
System.err.println("DicomImageViewer.OurDicomDirectoryBrowser.doSomethingMoreWithWhateverWasSelected():");

		}

	}


    
    public void Execute(String DcmDir){
        DicomDirFile = DcmDir;
        try {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            AttributeList list = new AttributeList();
            final String parentFilePath = new File(DicomDirFile).getParent();
            list.read(DicomDirFile);
            tree = new DicomDirBrowserFrame(list);
        }
        catch (Exception e){
            
            System.err.println(e);
            e.printStackTrace(System.err);
        }
        this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        setVisible(true);
    }

    /** Creates new form DicomDirectoryBrowserDialog */
    public DicomDirectoryBrowserDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButtonOK = new javax.swing.JButton();
        jButtonCancel = new javax.swing.JButton();
        dicomdirTreeScrollPane = new javax.swing.JScrollPane();
        scrollPaneOfCurrentAttributes = new javax.swing.JScrollPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jButtonOK.setText("Ok");
        jButtonOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOKActionPerformed(evt);
            }
        });

        jButtonCancel.setText("Cancel");
        jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(10, 10, 10)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(scrollPaneOfCurrentAttributes, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 621, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(235, 235, 235)
                        .add(jButtonOK)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(jButtonCancel))
                    .add(dicomdirTreeScrollPane))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(dicomdirTreeScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 480, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(scrollPaneOfCurrentAttributes, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 105, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jButtonOK)
                    .add(jButtonCancel)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOKActionPerformed
 
        setVisible(false);
    }//GEN-LAST:event_jButtonOKActionPerformed

    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
        mCancelled = true;
        setVisible(false);
    }//GEN-LAST:event_jButtonCancelActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(DicomDirectoryBrowserDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DicomDirectoryBrowserDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DicomDirectoryBrowserDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DicomDirectoryBrowserDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                DicomDirectoryBrowserDialog dialog = new DicomDirectoryBrowserDialog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {

                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane dicomdirTreeScrollPane;
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonOK;
    private javax.swing.JScrollPane scrollPaneOfCurrentAttributes;
    // End of variables declaration//GEN-END:variables

    /**
     * @return the selectedFiles
     */
    public Vector getSelectedFiles() {
        return selectedFiles;
    }

    /**
     * @param selectedFiles the selectedFiles to set
     */
    public void setSelectedFiles(Vector selectedFiles) {
        this.selectedFiles = selectedFiles;
    }

    /**
     * @return the DicomDirFile
     */
    public String getDicomDirFile() {
        return DicomDirFile;
    }

    /**
     * @param DicomDirFile the DicomDirFile to set
     */
    public void setDicomDirFile(String DicomDirFile) {
        this.DicomDirFile = DicomDirFile;
    }
}
