package gov.va.med.imaging.dicom.utilities.information;

public interface IDicomObjectChecker {

	public IDicomObjectChecker getInstance();
	
	public boolean isDicomObjectFile();

	public boolean containsPatientData();

	public boolean containsGroup6000();

	public boolean containsBurnInAnnotation();

	public boolean containsBurnInAnnotationByModality();

	public boolean containsPrivateAttributes();

}