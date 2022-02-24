package gov.va.med.imaging.dicom.utilities.model;

import java.io.File;

import gov.va.med.imaging.dicom.utilities.exceptions.DataCreationException;
import gov.va.med.imaging.dicom.utilities.exceptions.DicomFileException;

public interface IReconstitutionControl {

	void setAutoMode(boolean mode);

	boolean isAutoMode();

	void setRemovePrivateAttributes(boolean attributes);

	boolean isRemovePrivateAttributes();

	void reconstituteFiles(File directory) throws DicomFileException, DataCreationException;

	void reconstituteFiles(File[] dcmFiles) throws DicomFileException, DataCreationException;

	boolean isDeleteOriginal();

	void setDeleteOriginal(boolean del);

	void setDirectory(String path);

}