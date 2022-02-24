package gov.va.med.imaging.dicom.utilities.model;

import java.util.HashMap;
import java.util.Vector;

import gov.va.med.imaging.dicom.utilities.exceptions.DataCreationException;

public interface IDataControl<R> {

	boolean isGoldDBImages();

	void setGoldDBImages(boolean goldDBImages);

	Vector<R> getDataList(String studyUID) throws DataCreationException;

	HashMap<String, String> getDataListForTextFile();

}