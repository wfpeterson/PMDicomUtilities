package gov.va.med.imaging.dicom.utils.pixelmed.information;

public class DicomObjectFileFinder {

	public DicomObjectFileFinder() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//What arguments need to be passed in?

		
		//Have user select Drive (local or mapped)
		
		//Go looking for DICOM files.
		
			//Skip specific folders, such as Windows principal folders.
			//	Best bet is to make a simple text file containing such folders to skip.
		
		
			//Cycle thru the folders in a recursive fashion.
			//	Search for file with .dcm extension or no extension.
			//	This work should result in a list of files.
		
			//For each entry in the file list,
				//Determine if the file is actually a DICOM object file.
					//If false, remove from list.
		
				//Determine if the file is already anonymized by my scrubber.
					//If true, remove from list.
		
		//Create text file containing the list.  The list should contain the full path and the filename.
		
		
	}

}
