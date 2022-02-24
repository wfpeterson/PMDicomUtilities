/**
 * Created on Jan 31, 2008
 */
package gov.va.med.imaging.dicom.utils.pixelmed.find;

import com.pixelmed.dicom.AttributeList;
import com.pixelmed.network.IdentifierHandler;

/**
 *
 *
 *
 * @author William Peterson
 *
 */
public class CFindInfo extends IdentifierHandler{
	
	
	private String hostname;
	private int port = 0;
	private String calledAETitle;
	private String callingAETitle;
	private String affectedSOPClass;
	private AttributeList identifier;
	private int debugLevel = 0;
	private boolean cancel = false;
	private long cancelDelay = 0;
	

	/**
	 * Constructor
	 */
	public CFindInfo() {
		// TODO Auto-generated constructor stub
	}

	public CFindInfo(String hostname, int port, String calledAETitle, String callingAETitle, 
			String affectedSOPClass, String[] identifier, int debugLevel){
		
		this.hostname = hostname;
		this.port = port;
		this.calledAETitle = calledAETitle;
		this.callingAETitle = callingAETitle;
		this.affectedSOPClass = affectedSOPClass;
		//this.identifier = identifier;
		this.debugLevel = debugLevel;		
	}

	public CFindInfo(String hostname, int port, String calledAETitle, String callingAETitle, 
			String affectedSOPClass, String[] identifier, int debugLevel, boolean isCancel, long cancelDelayInMilliSec){
		
		this.hostname = hostname;
		this.port = port;
		this.calledAETitle = calledAETitle;
		this.callingAETitle = callingAETitle;
		this.affectedSOPClass = affectedSOPClass;
		//this.identifier = identifier;
		this.debugLevel = debugLevel;
		this.cancel = isCancel;
		this.cancelDelay = cancelDelayInMilliSec;
	}

	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.find.ICFindInfo#getHostname()
	 */
	public String getHostname() {
		return hostname;
	}
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.find.ICFindInfo#setHostname(java.lang.String)
	 */
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.find.ICFindInfo#getPort()
	 */
	public int getPort() {
		return port;
	}
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.find.ICFindInfo#setPort(int)
	 */
	public void setPort(int port) {
		this.port = port;
	}
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.find.ICFindInfo#getCalledAETitle()
	 */
	public String getCalledAETitle() {
		return calledAETitle;
	}
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.find.ICFindInfo#setCalledAETitle(java.lang.String)
	 */
	public void setCalledAETitle(String calledAETitle) {
		this.calledAETitle = calledAETitle;
	}
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.find.ICFindInfo#getCallingAETitle()
	 */
	public String getCallingAETitle() {
		return callingAETitle;
	}
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.find.ICFindInfo#setCallingAETitle(java.lang.String)
	 */
	public void setCallingAETitle(String callingAETitle) {
		this.callingAETitle = callingAETitle;
	}
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.find.ICFindInfo#getAffectedSOPClass()
	 */
	public String getAffectedSOPClass() {
		return affectedSOPClass;
	}
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.find.ICFindInfo#setAffectedSOPClass(java.lang.String)
	 */
	public void setAffectedSOPClass(String affectedSOPClass) {
		this.affectedSOPClass = affectedSOPClass;
	}
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.find.ICFindInfo#getIdentifier()
	 */
	public AttributeList getIdentifier() {
		return identifier;
	}
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.find.ICFindInfo#setIdentifier(com.pixelmed.dicom.AttributeList)
	 */
	public void setIdentifier(AttributeList identifier) {
		this.identifier = identifier;
	}
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.find.ICFindInfo#getIdentifierHandler()
	 */
	public IdentifierHandler getIdentifierHandler() {
		return this;
	}
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.find.ICFindInfo#getDebugLevel()
	 */
	public int getDebugLevel() {
		return debugLevel;
	}
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.find.ICFindInfo#setDebugLevel(int)
	 */
	public void setDebugLevel(int debugLevel) {
		this.debugLevel = debugLevel;
	}
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.find.ICFindInfo#isCancel()
	 */
	public boolean isCancel() {
		return cancel;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.find.ICFindInfo#setCancel(boolean)
	 */
	public void setCancel(boolean cancel) {
		this.cancel = cancel;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.find.ICFindInfo#getCancelDelay()
	 */
	public long getCancelDelay() {
		return cancelDelay;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.find.ICFindInfo#setCancelDelay(long)
	 */
	public void setCancelDelay(long cancelDelay) {
		this.cancelDelay = cancelDelay;
	}

	/* (non-Javadoc)
	 * @see com.pixelmed.network.IdentifierHandler#doSomethingWithIdentifier(com.pixelmed.dicom.AttributeList)
	 */
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.find.ICFindInfo#doSomethingWithIdentifier(com.pixelmed.dicom.AttributeList)
	 */
	public void doSomethingWithIdentifier(AttributeList identifier) {
		//super.doSomethingWithIdentifier(identifier);
		System.out.println("\nThread ID: "+Thread.currentThread().getId());
		System.out.println("Thread Name: "+Thread.currentThread().getName());
		System.out.println(identifier.toString());
	}

}
