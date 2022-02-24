package gov.va.med.imaging.dicom.utils.pixelmed.view;

import java.awt.EventQueue;

import gov.va.med.imaging.dicom.utilities.view.general.metadata.MetaDataFrame;
import gov.va.med.imaging.dicom.utils.pixelmed.model.MetaDataInfoControl;

public class PMMetaDataInfoFrame {

	public PMMetaDataInfoFrame() {
	}
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MetaDataFrame frame = new MetaDataFrame(new MetaDataInfoControl());
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}


}
