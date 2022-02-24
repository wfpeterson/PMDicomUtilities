package gov.va.med.imaging.dicom.utilities.model;

import java.io.File;

public interface IScrubControl {

	String SCRUBFAILEDLOG = ".\\failedscrublog.txt";
	
	public void setFolderAction (IFolderActionProgress progress);

	void changeRemoveGrp6000();

	boolean isRemoveGrp6000();

	void changeRemovePrivateAttributes();

	boolean isRemovePrivateAttributes();

	//recursive
	boolean scrubFiles(File rFile);

	boolean isDeleteOriginal();

	void changeDeleteOriginal();

	boolean isRecursiveCheck();

	void changeRecursiveCheck();

	boolean isGoldDBImages();

	void changeGoldDBImages();

	void setRemoveGrp6000(boolean removeGrp6000);

	void setRemovePrivateAttributes(boolean removePrivateAttributes);

	void setRecursiveCheck(boolean recursiveCheck);

	void setDeleteOriginal(boolean deleteOriginal);

	void openLog();

	void writeLog(String log);

	void closeLog();

	int getTotalFilesInFolder();

	int getCurrentFileInFolder();
	
	boolean isExplicitVRCheck();
	
	void changeExplicitVRCheck();
	
	void setExplicitVRCheck(boolean explicitVRCheck);

}