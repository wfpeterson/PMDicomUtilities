package gov.va.med.imaging.dicom.utilities.model;

import java.io.File;
import java.util.List;

import gov.va.med.imaging.dicom.utilities.information.MetaDataInfo;

public interface IMetaDataInfoControl{

	boolean isRecursive();
	
	void changeRecursiveCheck();

	String getStartingFolder();
	
	String setStartingFolder();
	
	List<MetaDataInfo> getFileMetaDataCollection(File file); 
	
	public void setFolderAction (IFolderActionProgress progress);

	int getTotalFilesInFolder();

	int getCurrentFileInFolder();
	
	
}
