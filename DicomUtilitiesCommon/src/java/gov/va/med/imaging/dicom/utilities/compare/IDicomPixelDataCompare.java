package gov.va.med.imaging.dicom.utilities.compare;

import gov.va.med.imaging.dicom.utilities.exceptions.DicomFileException;
import gov.va.med.imaging.dicom.utilities.exceptions.GeneralDicomException;

public interface IDicomPixelDataCompare<R> {

	double compareDICOMPixelData(String dcmFileOne, String dcmFileTwo) throws DicomFileException, GeneralDicomException;

	R openFile(String dcmFile) throws DicomFileException;

	int[] convertToHounsfieldUnits(R dds) throws GeneralDicomException;

	int[] convertPixelDataToIntegers(R dds) throws GeneralDicomException;

}