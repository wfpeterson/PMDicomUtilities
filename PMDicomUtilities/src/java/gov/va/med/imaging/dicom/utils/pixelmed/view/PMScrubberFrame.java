package gov.va.med.imaging.dicom.utils.pixelmed.view;

import javax.swing.SwingUtilities;

import gov.va.med.imaging.dicom.utilities.view.scrubber.ScrubberFrame;
import gov.va.med.imaging.dicom.utils.pixelmed.model.ScrubControl;

public class PMScrubberFrame {

	public PMScrubberFrame() {
		// TODO Auto-generated constructor stub
	}
	
    public static void main(String args[]) {
        try {
            SwingUtilities.invokeLater(new Runnable(){
                public void run(){
                    new ScrubberFrame(new ScrubControl());
                }
            });
        } 
        catch (Exception e) {
            System.out.print("failed to create GUI thread.");
            System.out.print(e);
        }
    }


}
