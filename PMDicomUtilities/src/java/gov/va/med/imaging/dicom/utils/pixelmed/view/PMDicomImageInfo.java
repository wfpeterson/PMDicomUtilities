package gov.va.med.imaging.dicom.utils.pixelmed.view;

import javax.swing.SwingUtilities;

import gov.va.med.imaging.dicom.utilities.model.IDicomInformation;
import gov.va.med.imaging.dicom.utilities.view.imageinfo.DicomImageInfo;
import gov.va.med.imaging.dicom.utils.pixelmed.model.DicomInformationImpl;

public class PMDicomImageInfo {

	public PMDicomImageInfo() {
		// TODO Auto-generated constructor stub
	}
	
    /**
     * Launch the application
     * @param args
     */
    public static void main(String args[]) {
        try {
            SwingUtilities.invokeLater(new Runnable(){
                public void run(){
                	//IDicomInformation info = new DicomInformationImpl();
                    new DicomImageInfo();
                }
            });
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }


}
