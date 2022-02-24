/* Copyright (c) 2001-2008, David A. Clunie DBA Pixelmed Publishing. All rights reserved. */

package gov.va.med.imaging.dicom.utils.pixelmed.move;
import java.io.IOException;
import java.util.LinkedList;

import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.SOPClass;
import com.pixelmed.dicom.TagFromName;
import com.pixelmed.dicom.TransferSyntax;
import com.pixelmed.network.AReleaseException;
import com.pixelmed.network.Association;
import com.pixelmed.network.AssociationFactory;
import com.pixelmed.network.CMoveRequestCommandMessage;
import com.pixelmed.network.CompositeResponseHandler;
import com.pixelmed.network.DicomNetworkException;
import com.pixelmed.network.PresentationContext;

/**
 * <p>This class implements the SCU role of C-MOVE SOP Classes.</p>
 *
 * <p>The class has no methods other than the constructor (and a main method for testing). The
 * constructor establishes an association, sends the C-MOVE request, and releases the
 * association.</p>
 *
 * <p>Debugging messages with a varying degree of verbosity can be activated.</p>
 *
 * <p>For example, to have MOVESCU command MOVESCP to move a single image instance to STORESCP:</p>
 * <pre>
try {
    AttributeList identifier = new AttributeList();
    { AttributeTag t = TagFromName.QueryRetrieveLevel; Attribute a = new CodeStringAttribute(t); a.addValue("IMAGE"); identifier.put(t,a); }
    { AttributeTag t = TagFromName.StudyInstanceUID; Attribute a = new UniqueIdentifierAttribute(t); a.addValue("1.3.6.1.4.1.5962.1.2.0.1064923879.2077.3232235877"); identifier.put(t,a); }
    { AttributeTag t = TagFromName.SeriesInstanceUID; Attribute a = new UniqueIdentifierAttribute(t); a.addValue("1.3.6.1.4.1.5962.1.3.0.0.1064923879.2077.3232235877"); identifier.put(t,a); }
    { AttributeTag t = TagFromName.SOPInstanceUID; Attribute a = new UniqueIdentifierAttribute(t); a.addValue("1.3.6.1.4.1.5962.1.1.0.0.0.1064923879.2077.3232235877"); identifier.put(t,a); }
    new MoveSOPClassSCU("theirhost","104","MOVESCP","MOVESCU","STORESCP",SOPClass.StudyRootQueryRetrieveInformationModelMove,identifier,0);
}
catch (Exception e) {
    e.printStackTrace(System.err);
}
 * </pre>
 *
 * @author	dclunie
 */
public class MoveSOPClassSCUWithCancel extends SOPClass {

	/***/
	private static final String identString = "@(#) $Header: /userland/cvs/pixelmed/imgbook/com/pixelmed/network/MoveSOPClassSCU.java,v 1.15 2008/04/27 15:10:33 dclunie Exp $";

	/***/
	protected int debugLevel;
	/***/
	protected int status;			// set by CMoveResponseHandler
	
	public int getStatus() { return status; }

	/***/
	protected class CMoveResponseHandler extends CompositeResponseHandler {

		/***/
		CMoveResponseHandler(int debugLevel) {
			super(debugLevel);
			allowData=true;
		}
		
		/**
		 * @param	list
		 */
		protected void evaluateStatusAndSetSuccess(AttributeList list) {
if (debugLevel > 0) System.err.println("MoveSOPClassSCU.CMoveResponseHandler.evaluateStatusAndSetSuccess:");
			// could check all sorts of things, like:
			// - AffectedSOPClassUID is what we sent
			// - CommandField is 0x8021 C-MOVE-RSP
			// - MessageIDBeingRespondedTo is what we sent
			// - DataSetType is 0101 for success (no data set) or other for pending
			// - Status is success and consider associated elements
			//
			// for now just treat success or warning as success (and absence as failure)
			status = Attribute.getSingleIntegerValueOrDefault(list,TagFromName.Status,0xffff);
if (debugLevel > 0) System.err.println("MoveSOPClassSCU.CMoveResponseHandler.evaluateStatusAndSetSuccess: status =0x"+Integer.toHexString(status));
if (debugLevel > 0) System.err.print(list);
			// possible statuses at this point are:
			// A701 Refused - Out of Resources - Unable to calculate number of matches
			// A702 Refused - Out of Resources - Unable to perform sub-operations
			// A801 Refused - Move Destination unknown	
			// A900 Failed - Identifier does not match SOP Class	
			// Cxxx Failed - Unable to process	
			// FE00 Cancel - Sub-operations terminated due to Cancel Indication	
			// B000 Warning	Sub-operations Complete - One or more Failures	
			// 0000 Success - Sub-operations Complete - No Failures	
			// FF00 Pending - Matches are continuing

if (debugLevel > 0) System.err.println("Move "+(status == 0xFF00 ? "pending" : (status == 0x0000 ? "success" : ("0x"+Integer.toHexString(status)))));
			
			int failedSuboperations = Attribute.getSingleIntegerValueOrDefault(list,TagFromName.NumberOfFailedSuboperations,0);

			success = status == 0x0000;		// success
			
			if (status != 0xFF00 && failedSuboperations == 0) setDone(true);	// stop if not "pending"
		}

		/**
		 * @param	list
		 */
		protected void makeUseOfDataSet(AttributeList list) {
if (debugLevel > 0) System.err.println("MoveSOPClassSCU.CMoveResponseHandler.makeUseOfDataSet:");
if (debugLevel > 0) System.err.print(list);
			// we only get here if there are failed sub-operations, in which case we get a list
			// in Failed SOP Instance UID List (0008,0058)
			setDone(true);
		}
	}

	/**
	 * @param	hostname		their hostname or IP address
	 * @param	port			their port
	 * @param	calledAETitle		their AE Title
	 * @param	callingAETitle		our AE Title
	 * @param	moveDestination		the AE Title of the Storage AE to which the instances are to be sent
	 * @param	affectedSOPClass	the SOP Class defining which retrieve model, e.g. {@link com.pixelmed.dicom.SOPClass#StudyRootQueryRetrieveInformationModelMove SOPClass.StudyRootQueryRetrieveInformationModelMove}
	 * @param	identifier		the list of unique keys and move level
	 * @param	debugLevel		zero for no debugging messages, higher values more verbose messages
	 * @exception	IOException
	 * @exception	DicomException
	 * @exception	DicomNetworkException
	 */
	public MoveSOPClassSCUWithCancel(String hostname,int port,String calledAETitle,String callingAETitle,String moveDestination,
			String affectedSOPClass,AttributeList identifier,int debugLevel, boolean isCancel, long cancelDelayInMilliSec) throws DicomNetworkException, DicomException, IOException {

		this.debugLevel=debugLevel;
		
		LinkedList presentationContexts = new LinkedList();
		{
			LinkedList tslist = new LinkedList();
			tslist.add(TransferSyntax.Default);
			tslist.add(TransferSyntax.ExplicitVRLittleEndian);
			presentationContexts.add(new PresentationContext((byte)0x01,affectedSOPClass,tslist));
		}
		presentationContexts.add(new PresentationContext((byte)0x03,affectedSOPClass,TransferSyntax.ImplicitVRLittleEndian));
		presentationContexts.add(new PresentationContext((byte)0x05,affectedSOPClass,TransferSyntax.ExplicitVRLittleEndian));

		Association association = AssociationFactory.createNewAssociation(hostname,port,calledAETitle,callingAETitle,presentationContexts,null,false,debugLevel);
if (debugLevel > 0) System.err.println(association);
		// Decide which presentation context we are going to use ...
		byte usePresentationContextID = association.getSuitablePresentationContextID(affectedSOPClass);
if (debugLevel > 0) System.err.println("MoveSOPClassSCU: Using context ID "+usePresentationContextID);
		CMoveRequestCommandMessage cMoveRequest = new CMoveRequestCommandMessage(affectedSOPClass,moveDestination);
		byte cMoveRequestCommandMessage[] = cMoveRequest.getBytes();
		//WFP - fix
		//byte cMoveIdentifier[] = new IdentifierMessage(identifier,association.getTransferSyntaxForPresentationContextID(usePresentationContextID)).getBytes();
if (debugLevel > 0) System.err.println("MoveSOPClassSCU: Identifier:\n"+identifier);
		association.setReceivedDataHandler(new CMoveResponseHandler(debugLevel));
		// for some reason association.send(usePresentationContextID,cMoveRequestCommandMessage,cMoveIdentifier) fails with Oldenburg imagectn
		// so send the command and the identifier separately ...
		association.send(usePresentationContextID,cMoveRequestCommandMessage,null);
		//WFP -fix
		//association.send(usePresentationContextID,null,cMoveIdentifier);
		association.send(usePresentationContextID,null,null);
		if(isCancel){
			try{
				Thread.sleep(cancelDelayInMilliSec);
			}
			catch(InterruptedException iX){
				//do nothing
			}
			int messageID = cMoveRequest.getMessageID();
			//WFP - fix
			//byte cMoveCancelCommandMessage[] = new CCancelRequestCommandMessage(messageID).getBytes();
			//association.send(usePresentationContextID, cMoveCancelCommandMessage, null);
		}

if (debugLevel > 0) System.err.println("MoveSOPClassSCU: waiting for PDUs");
		try {
			association.waitForPDataPDUsUntilHandlerReportsDone();
if (debugLevel > 0) System.err.println("MoveSOPClassSCU: got PDU, now releasing association");
			// State 6
			association.release();
		}
		catch (AReleaseException e) {
			// State 1
			// the other end released and didn't wait for us to do it
		}
	}
}




