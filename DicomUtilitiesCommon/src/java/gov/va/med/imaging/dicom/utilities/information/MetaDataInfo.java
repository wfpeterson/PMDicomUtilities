package gov.va.med.imaging.dicom.utilities.information;

public class MetaDataInfo {

	private String filename = null;
	private String sopClass = null;
	private String sopInstance = null;
	private String transferSyntax = null;
	private String implementationClassUID = null;
	private String implementationVersionName = null;
	private String sourceAETitle = null;
	private String manufacturer = null;
	private String model = null;
	private String mfgVersion = null;
	
	
	public MetaDataInfo() {
		
	}


	public String getFilename() {
		return filename;
	}


	public void setFilename(String filename) {
		this.filename = filename;
	}


	public String getSopClass() {
		return sopClass;
	}


	public void setSopClass(String sopClass) {
		this.sopClass = sopClass;
	}


	public String getSopInstance() {
		return sopInstance;
	}


	public void setSopInstance(String sopInstance) {
		this.sopInstance = sopInstance;
	}


	public String getTransferSyntax() {
		return transferSyntax;
	}


	public void setTransferSyntax(String transferSyntax) {
		this.transferSyntax = transferSyntax;
	}


	public String getImplementationClassUID() {
		return implementationClassUID;
	}


	public void setImplementationClassUID(String implementationClassUID) {
		this.implementationClassUID = implementationClassUID;
	}


	public String getImplementationVersionName() {
		return implementationVersionName;
	}


	public void setImplementationVersionName(String implementationVersionName) {
		this.implementationVersionName = implementationVersionName;
	}


	public String getSourceAETitle() {
		return sourceAETitle;
	}


	public void setSourceAETitle(String sourceAETitle) {
		this.sourceAETitle = sourceAETitle;
	}


	public String getManufacturer() {
		return manufacturer;
	}


	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}


	public String getModel() {
		return model;
	}


	public void setModel(String model) {
		this.model = model;
	}


	public String getMfgVersion() {
		return mfgVersion;
	}


	public void setMfgVersion(String mfgVersion) {
		this.mfgVersion = mfgVersion;
	}

	
	

}
