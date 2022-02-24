/**
 * Created on Jan 31, 2008
 */
package gov.va.med.imaging.dicom.utils.pixelmed.move;

import com.pixelmed.dicom.AttributeList;

/**
 *
 *
 *
 * @author William Peterson
 *
 */
public class CMoveInfo {
	
	
	private String hostname;
	private int port = 0;
	private String calledAETitle;
	private String callingAETitle;
	private String moveDestination;
	private String affectedSOPClass;
	private AttributeList identifier;
	private int debugLevel = 0;
	private boolean cancel = false;
	private long cancelDelay = 0;


	/**
	 * Constructor
	 */
	public CMoveInfo() {
		// TODO Auto-generated constructor stub
	}
	public CMoveInfo(String hostname, int port, String calledAETitle, String callingAETitle, 
			String moveAETitle, String affectedSOPClass, AttributeList identifier, int debugLevel){
		
		this.hostname = hostname;
		this.port = port;
		this.calledAETitle = calledAETitle;
		this.callingAETitle = callingAETitle;
		this.moveDestination = moveAETitle;
		this.affectedSOPClass = affectedSOPClass;
		this.identifier = identifier;
		this.debugLevel = debugLevel;
		
	}
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.move.ICMoveInfo#getHostname()
	 */
	public String getHostname() {
		return hostname;
	}
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.move.ICMoveInfo#setHostname(java.lang.String)
	 */
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.move.ICMoveInfo#getPort()
	 */
	public int getPort() {
		return port;
	}
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.move.ICMoveInfo#setPort(int)
	 */
	public void setPort(int port) {
		this.port = port;
	}
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.move.ICMoveInfo#getCalledAETitle()
	 */
	public String getCalledAETitle() {
		return calledAETitle;
	}
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.move.ICMoveInfo#setCalledAETitle(java.lang.String)
	 */
	public void setCalledAETitle(String calledAETitle) {
		this.calledAETitle = calledAETitle;
	}
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.move.ICMoveInfo#getCallingAETitle()
	 */
	public String getCallingAETitle() {
		return callingAETitle;
	}
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.move.ICMoveInfo#setCallingAETitle(java.lang.String)
	 */
	public void setCallingAETitle(String callingAETitle) {
		this.callingAETitle = callingAETitle;
	}
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.move.ICMoveInfo#getAffectedSOPClass()
	 */
	public String getAffectedSOPClass() {
		return affectedSOPClass;
	}
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.move.ICMoveInfo#setAffectedSOPClass(java.lang.String)
	 */
	public void setAffectedSOPClass(String affectedSOPClass) {
		this.affectedSOPClass = affectedSOPClass;
	}
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.move.ICMoveInfo#getIdentifier()
	 */
	public AttributeList getIdentifier() {
		return identifier;
	}
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.move.ICMoveInfo#setIdentifier(com.pixelmed.dicom.AttributeList)
	 */
	public void setIdentifier(AttributeList identifier) {
		this.identifier = identifier;
	}
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.move.ICMoveInfo#getDebugLevel()
	 */
	public int getDebugLevel() {
		return debugLevel;
	}
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.move.ICMoveInfo#setDebugLevel(int)
	 */
	public void setDebugLevel(int debugLevel) {
		this.debugLevel = debugLevel;
	}
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.move.ICMoveInfo#getMoveDestination()
	 */
	public String getMoveDestination() {
		return moveDestination;
	}
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.move.ICMoveInfo#setMoveDestination(java.lang.String)
	 */
	public void setMoveDestination(String moveDestination) {
		this.moveDestination = moveDestination;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.move.ICMoveInfo#isCancel()
	 */
	public boolean isCancel() {
		return cancel;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.move.ICMoveInfo#setCancel(boolean)
	 */
	public void setCancel(boolean cancel) {
		this.cancel = cancel;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.move.ICMoveInfo#getCancelDelay()
	 */
	public long getCancelDelay() {
		return cancelDelay;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.move.ICMoveInfo#setCancelDelay(long)
	 */
	public void setCancelDelay(long cancelDelay) {
		this.cancelDelay = cancelDelay;
	}

}
