/**
 * Created on Jan 31, 2008
 */
package gov.va.med.imaging.dicom.utils.pixelmed.findmove;

import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.AttributeTag;
import com.pixelmed.dicom.CodeStringAttribute;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.SOPClass;
import com.pixelmed.dicom.TagFromName;
import com.pixelmed.dicom.UniqueIdentifierAttribute;
import com.pixelmed.network.IdentifierHandler;

import gov.va.med.imaging.dicom.utils.pixelmed.move.CMoveInfo;
import gov.va.med.imaging.dicom.utils.pixelmed.move.CMoveThread;

/**
 *
 *
 *
 * @author William Peterson
 *
 */
public class CFindInfoForMove extends IdentifierHandler{
	
	
	private String hostname;
	private int port = 0;
	private String calledAETitle;
	private String callingAETitle;
	private String moveAETitle;
	private String affectedSOPClass;
	private AttributeList identifier;
	private int debugLevel = 0;
	private boolean cancel = false;
	private long cancelDelay = 0;
	int delay = 0;

	

	/**
	 * Constructor
	 */
	public CFindInfoForMove() {
		// TODO Auto-generated constructor stub
	}

	public CFindInfoForMove(String hostname, int port, String calledAETitle, String callingAETitle, 
			String moveAETitle, String affectedSOPClass, AttributeList identifier, int debugLevel){
		
		this.hostname = hostname;
		this.port = port;
		this.calledAETitle = calledAETitle;
		this.callingAETitle = callingAETitle;
		this.moveAETitle = moveAETitle;
		this.affectedSOPClass = affectedSOPClass;
		this.identifier = identifier;
		this.debugLevel = debugLevel;		
	}

	public CFindInfoForMove(String hostname, int port, String calledAETitle, String callingAETitle, String moveAETitle,  
			String affectedSOPClass, AttributeList identifier, int debugLevel, boolean isCancel, long cancelDelayInMilliSec){
		
		this.hostname = hostname;
		this.port = port;
		this.calledAETitle = calledAETitle;
		this.callingAETitle = callingAETitle;
		this.affectedSOPClass = affectedSOPClass;
		this.identifier = identifier;
		this.debugLevel = debugLevel;
		this.cancel = isCancel;
		this.cancelDelay = cancelDelayInMilliSec;
	}

	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.findmove.ICFindInfoForMove#getHostname()
	 */
	public String getHostname() {
		return hostname;
	}
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.findmove.ICFindInfoForMove#setHostname(java.lang.String)
	 */
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.findmove.ICFindInfoForMove#getPort()
	 */
	public int getPort() {
		return port;
	}
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.findmove.ICFindInfoForMove#setPort(int)
	 */
	public void setPort(int port) {
		this.port = port;
	}
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.findmove.ICFindInfoForMove#getCalledAETitle()
	 */
	public String getCalledAETitle() {
		return calledAETitle;
	}
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.findmove.ICFindInfoForMove#setCalledAETitle(java.lang.String)
	 */
	public void setCalledAETitle(String calledAETitle) {
		this.calledAETitle = calledAETitle;
	}
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.findmove.ICFindInfoForMove#getCallingAETitle()
	 */
	public String getCallingAETitle() {
		return callingAETitle;
	}
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.findmove.ICFindInfoForMove#setCallingAETitle(java.lang.String)
	 */
	public void setCallingAETitle(String callingAETitle) {
		this.callingAETitle = callingAETitle;
	}
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.findmove.ICFindInfoForMove#getAffectedSOPClass()
	 */
	public String getAffectedSOPClass() {
		return affectedSOPClass;
	}
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.findmove.ICFindInfoForMove#setAffectedSOPClass(java.lang.String)
	 */
	public void setAffectedSOPClass(String affectedSOPClass) {
		this.affectedSOPClass = affectedSOPClass;
	}
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.findmove.ICFindInfoForMove#getIdentifier()
	 */
	public AttributeList getIdentifier() {
		return identifier;
	}
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.findmove.ICFindInfoForMove#setIdentifier(com.pixelmed.dicom.AttributeList)
	 */
	public void setIdentifier(AttributeList identifier) {
		this.identifier = identifier;
	}
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.findmove.ICFindInfoForMove#getIdentifierHandler()
	 */
	public IdentifierHandler getIdentifierHandler() {
		return this;
	}
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.findmove.ICFindInfoForMove#getDebugLevel()
	 */
	public int getDebugLevel() {
		return debugLevel;
	}
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.findmove.ICFindInfoForMove#setDebugLevel(int)
	 */
	public void setDebugLevel(int debugLevel) {
		this.debugLevel = debugLevel;
	}
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.findmove.ICFindInfoForMove#isCancel()
	 */
	public boolean isCancel() {
		return cancel;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.findmove.ICFindInfoForMove#setCancel(boolean)
	 */
	public void setCancel(boolean cancel) {
		this.cancel = cancel;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.findmove.ICFindInfoForMove#getCancelDelay()
	 */
	public long getCancelDelay() {
		return cancelDelay;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.findmove.ICFindInfoForMove#setCancelDelay(long)
	 */
	public void setCancelDelay(long cancelDelay) {
		this.cancelDelay = cancelDelay;
	}
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.findmove.ICFindInfoForMove#getMoveAETitle()
	 */
	public String getMoveAETitle() {
		return moveAETitle;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.findmove.ICFindInfoForMove#setMoveAETitle(java.lang.String)
	 */
	public void setMoveAETitle(String moveAETitle) {
		this.moveAETitle = moveAETitle;
	}

	/* (non-Javadoc)
	 * @see com.pixelmed.network.IdentifierHandler#doSomethingWithIdentifier(com.pixelmed.dicom.AttributeList)
	 */
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.findmove.ICFindInfoForMove#doSomethingWithIdentifier(com.pixelmed.dicom.AttributeList)
	 */
	@Override
	public void doSomethingWithIdentifier(AttributeList identifier)
			throws DicomException {
		//System.out.println("\nThread ID: "+Thread.currentThread().getId());
		//System.out.println("Thread Name: "+Thread.currentThread().getName());
		//System.out.println(identifier.toString());
		
		//create new attribute list for CMove
		AttributeList moveIdentifier = this.buildAttributeList(identifier);
		
		//populate CMoveInfo object
		CMoveInfo moveInfo = new CMoveInfo();
		moveInfo.setAffectedSOPClass(SOPClass.StudyRootQueryRetrieveInformationModelMove);
		moveInfo.setCalledAETitle(calledAETitle);
		moveInfo.setCallingAETitle(callingAETitle);
		moveInfo.setCancel(false);
		moveInfo.setDebugLevel(debugLevel);
		moveInfo.setHostname(hostname);
		moveInfo.setPort(port);
		moveInfo.setIdentifier(moveIdentifier);
		moveInfo.setMoveDestination(moveAETitle);
		
		//execute a CMove action.  This will open up a separate DICOM association with the SCP.  But that's fine.
		CMoveThread moveThread = new CMoveThread(moveInfo);
		new Thread(moveThread).start();
		try{
			Thread.sleep(this.delay);
		}
		catch(InterruptedException iX){
			//do nothing
		}
	}
	
	private AttributeList buildAttributeList(AttributeList identifier)throws DicomException{
		
		AttributeList moveList = null;
		System.out.println("Building Attribute List.");
		moveList = new AttributeList();
		AttributeTag level = TagFromName.QueryRetrieveLevel; 
		Attribute a1 = new CodeStringAttribute(level); 
		a1.addValue("STUDY"); 
		moveList.put(level,a1);
		
		AttributeTag studyUID = TagFromName.StudyInstanceUID; 
		Attribute a2 = new UniqueIdentifierAttribute(studyUID); 
		a2.addValue(identifier.get(studyUID).getSingleStringValueOrNull()); 
		moveList.put(studyUID,a2);
		
		return moveList;
	}

}
