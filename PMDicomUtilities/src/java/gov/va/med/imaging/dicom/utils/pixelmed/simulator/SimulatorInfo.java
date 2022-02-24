/**
 * Created on Jan 31, 2008
 */
package gov.va.med.imaging.dicom.utils.pixelmed.simulator;

import com.pixelmed.network.IdentifierHandler;

import gov.va.med.imaging.dicom.utilities.exceptions.GeneralDicomException;

/**
 *
 *
 *
 * @author William Peterson
 *
 */
public class SimulatorInfo extends IdentifierHandler{
	
	
	private String hostnameMWL;			// The host name of the Modality Worklist (MWL) Query SCP 
	private int portMWL = 0;			// Port the MWL Query SCP listens
	private String calledAETitleMWL;	// The MWL SCP's AE title
	private String callingAETitleMWL;	// The local MWL CSU's AE title
	private String accessionNumber;		// Accession Number
	private int debugLevel = 0;			// debug log level
	private String hostnameStore;		// Hostname of the storage Node
	private int portStore = 0;			// Port for the storage node
	private String sourceDir;			// Files to transmit.
	private int loopCount;				// the number of times this infoset is executed.
	private String callingAETitleStore;	// Called AE title of the storage Node
	private String calledAETitleStore;	// Calling AE of the storage SCU
	private String manufacturerOverride;
	private String modelOverride;
	private String modalityOverride;
	private boolean upDateUIds;
	
	

	/**
	 * Constructor
	 */
	public SimulatorInfo() {
		// TODO Auto-generated constructor stub
	}
	public SimulatorInfo(String hostnameMWL, int portMWL, String calledAETitleMWL, 
			String callingAETitleMWL, String accession, String sourceDir, String hostnameStore, int 
			portStore, String calledAEStore, String callingAEStore,	int loopCount, int debugLevel){
		
		this.hostnameMWL = hostnameMWL;
		this.portMWL = portMWL;
		this.calledAETitleMWL = calledAETitleMWL;
		this.callingAETitleMWL = callingAETitleMWL;
		this.accessionNumber = accession;
		this.sourceDir = sourceDir;
		this.hostnameStore = hostnameStore;
		this.portStore = portStore;
		this.calledAETitleStore = calledAEStore;
		this.callingAETitleStore = callingAEStore;
		this.loopCount = loopCount;
		this.debugLevel = debugLevel;
		
	}
	public String getHostnameMWL() {
		return hostnameMWL;
	}
	public void setHostnameMWL(String hostnameMWL) {
		this.hostnameMWL = hostnameMWL;
	}
	public int getPortMWL() {
		return portMWL;
	}
	public void setPortMWL(int portMWL) {
		this.portMWL = portMWL;
	}
	public String getCalledAETitleMWL() {
		return calledAETitleMWL;
	}
	public void setCalledAETitleMWL(String calledAETitleMWL) {
		this.calledAETitleMWL = calledAETitleMWL;
	}
	public String getCallingAETitleMWL() {
		return callingAETitleMWL;
	}
	public void setCallingAETitleMWL(String callingAETitleMWL) {
		this.callingAETitleMWL = callingAETitleMWL;
	}
	public String getaccessionNumber() {
		return accessionNumber;
	}
	public void setaccessionNumber(String accessionNumber) {
		this.accessionNumber = accessionNumber;
	}
	public String getHostnameStore() {
		return hostnameStore;
	}
	public void setHostnameStore(String hostnameStore) {
		this.hostnameStore = hostnameStore;
	}
	public int getPortStore() {
		return portStore;
	}
	public void setPortStore(int portStore) {
		this.portStore = portStore;
	}
	public String getCalledAETitleStore() {
		return calledAETitleStore;
	}
	public void setCalledAETitleStore(String calledAETitleStore) {
		this.calledAETitleStore = calledAETitleStore;
	}
	public String getCallingAETitleStore() {
		return callingAETitleStore;
	}
	public void setCallingAETitleStore(String callingAETitleStore) {
		this.callingAETitleStore = callingAETitleStore;
	}
	public String getsourceDir() {
		return sourceDir;
	}
	public void setsourceDir(String sourceDir) {
		this.sourceDir = sourceDir;
	}
	public int getloopCount() {
		return loopCount;
	}
	public void setloopCount(int loopCount) {
		this.loopCount = loopCount;
	}
	public int getDebugLevel() {
		return debugLevel;
	}
	public void setDebugLevel(int debugLevel) {
		this.debugLevel = debugLevel;
	}
		
	public String getManufacturerOverride() {
		return manufacturerOverride;
	}
	public void setManufacturerOverride(String manufacturerOverride) {
		this.manufacturerOverride = manufacturerOverride;
	}
	public String getModelOverride() {
		return modelOverride;
	}
	public void setModelOverride(String modelOverride) {
		this.modelOverride = modelOverride;
	}
	public String getModalityOverride() {
		return modalityOverride;
	}
	public void setModalityOverride(String modalityOverride) {
		this.modalityOverride = modalityOverride;
	}
	public void doSomethingWithIdentifier(Object identifier) throws GeneralDicomException {
		//super.doSomethingWithIdentifier(identifier);
		System.out.println("\nThread ID: "+Thread.currentThread().getId());
		System.out.println("\nThread Name: "+Thread.currentThread().getName());
		System.out.println(identifier.toString());
		
	}
	public boolean isUpDateUIds() {
		return upDateUIds;
	}
	public void setUpDateUIds(boolean upDateUIds) {
		this.upDateUIds = upDateUIds;
	}


}
