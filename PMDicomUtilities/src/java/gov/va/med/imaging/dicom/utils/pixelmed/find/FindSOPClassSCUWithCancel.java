/* Copyright (c) 2001-2010, David A. Clunie DBA Pixelmed Publishing. All rights reserved. */

package gov.va.med.imaging.dicom.utils.pixelmed.find;
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
import com.pixelmed.network.CFindRequestCommandMessage;
import com.pixelmed.network.CompositeResponseHandler;
import com.pixelmed.network.DicomNetworkException;
import com.pixelmed.network.IdentifierHandler;
import com.pixelmed.network.PresentationContext;

/**
 * <p>This class implements the SCU role of C-FIND SOP Classes.</p>
 *
 * <p>The class has no methods other than the constructor (and a main method for testing). The
 * constructor establishes an association, sends the C-FIND request, and releases the
 * association. Any identifiers received are handled by the supplied
 * {@link com.pixelmed.network.IdentifierHandler IdentifierHandler}.</p>
 *
 * <p>Debugging messages with a varying degree of verbosity can be activated.</p>
 *
 * <p>For example:</p>
 * <pre>
try {
    SpecificCharacterSet specificCharacterSet = new SpecificCharacterSet((String[])null);
    AttributeList identifier = new AttributeList();
    { AttributeTag t = TagFromName.QueryRetrieveLevel; Attribute a = new CodeStringAttribute(t); a.addValue("STUDY"); identifier.put(t,a); }
    { AttributeTag t = TagFromName.PatientID; Attribute a = new LongStringAttribute(t,specificCharacterSet); a.addValue(""); identifier.put(t,a); }
    { AttributeTag t = TagFromName.StudyInstanceUID; Attribute a = new UniqueIdentifierAttribute(t); a.addValue(""); identifier.put(t,a); }
    new FindSOPClassSCU("theirhost","104","FINDSCP","FINDSCU",SOPClass.StudyRootQueryRetrieveInformationModelFind,identifier,new IdentifierHandler(),1);
}
catch (Exception e) {
    e.printStackTrace(System.err);
}
 * </pre>
 *
 * @see com.pixelmed.network.IdentifierHandler
 *
 * @author	dclunie
 */
public class FindSOPClassSCUWithCancel extends SOPClass {

	/***/
	private static final String identString = "@(#) $Header: /userland/cvs/pixelmed/imgbook/com/pixelmed/network/FindSOPClassSCU.java,v 1.21 2010/06/17 01:18:00 dclunie Exp $";

	/***/
	private int debugLevel;

	/***/
	private class CFindResponseHandler extends CompositeResponseHandler {
		/***/
		private IdentifierHandler identifierHandler;

		/**
		 * @param	identifierHandler
		 * @param	debugLevel
		 */
		CFindResponseHandler(IdentifierHandler identifierHandler,int debugLevel) {
			super(debugLevel);
			this.identifierHandler=identifierHandler;
			allowData=true;
		}
		
		/**
		 * @param	list
		 */
		protected void evaluateStatusAndSetSuccess(AttributeList list) {
if (debugLevel > 0) System.err.println("FindSOPClassSCU.CFindResponseHandler.evaluateStatusAndSetSuccess:");
if (debugLevel > 0) System.err.println(list);
			// could check all sorts of things, like:
			// - AffectedSOPClassUID is what we sent
			// - CommandField is 0x8020 C-Find-RSP
			// - MessageIDBeingRespondedTo is what we sent
			// - DataSetType is 0101 for success (no data set) or other for pending
			// - Status is success and consider associated elements
			//
			// for now just treat success or warning as success (and absence as failure)
			int status = Attribute.getSingleIntegerValueOrDefault(list,TagFromName.Status,0xffff);
if (debugLevel > 0) System.err.println("FindSOPClassSCU.CFindResponseHandler.evaluateStatusAndSetSuccess: status = 0x"+Integer.toHexString(status));
			// possible statuses at this point are:
			// A700 Refused - Out of Resources	
			// A900 Failed - Identifier does not match SOP Class	
			// Cxxx Failed - Unable to process	
			// FE00 Cancel - Matching terminated due to Cancel request	
			// 0000 Success - Matching is complete - No final Identifier is supplied.	
			// FF00 Pending - Matches are continuing - Current Match is supplied and any Optional Keys were supported in the same manner as Required Keys.
			// FF01 Pending - Matches are continuing - Warning that one or more Optional Keys were not supported for existence and/or matching for this Identifier.

			success = status == 0x0000;	// success
			
			if (status != 0xFF00 && status != 0xFF01) {
if (debugLevel > 0) System.err.println("FindSOPClassSCU.CFindResponseHandler.evaluateStatusAndSetSuccess: status no longer pending, so stop");
				setDone(true);
			}
		}
		
		/**
		 * @param	list
		 */
		protected void makeUseOfDataSet(AttributeList list) {
if (debugLevel > 0) System.err.println("FindSOPClassSCU.CFindResponseHandler.makeUseOfDataSet:");
if (debugLevel > 0) System.err.print(list);
			try {
				identifierHandler.doSomethingWithIdentifier(list);
			}
			catch (DicomException e) {
				// do not stop ... other identifiers may be OK
				e.printStackTrace(System.err);
			}
		}
	}

	/**
	 * @param	hostname		their hostname or IP address
	 * @param	port			their port
	 * @param	calledAETitle		their AE Title
	 * @param	callingAETitle		our AE Title
	 * @param	affectedSOPClass	the SOP Class defining which query model, e.g. {@link com.pixelmed.dicom.SOPClass#StudyRootQueryRetrieveInformationModelFind SOPClass.StudyRootQueryRetrieveInformationModelFind}
	 * @param	identifier		the list of matching and return keys
	 * @param	identifierHandler	the handler to use for each returned identifier
	 * @param	debugLevel		zero for no debugging messages, higher values more verbose messages
	 * @exception	IOException
	 * @exception	DicomException
	 * @exception	DicomNetworkException
	 */
	public FindSOPClassSCUWithCancel(String hostname,int port,String calledAETitle,String callingAETitle,
			String affectedSOPClass,AttributeList identifier,IdentifierHandler identifierHandler,
			int debugLevel, boolean isCancel, long cancelDelayInMilliSec) throws DicomNetworkException, DicomException, IOException {
		this.debugLevel=debugLevel;
		
if (debugLevel > 0) System.err.println("FindSOPClassSCU(): request identifier");
if (debugLevel > 0) System.err.print(identifier);

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
if (debugLevel > 0) System.err.println("Using context ID "+usePresentationContextID);
		CFindRequestCommandMessage cFindRequest = new CFindRequestCommandMessage(affectedSOPClass);
		byte cFindRequestCommandMessage[] = cFindRequest.getBytes();
		//WFP - fix
		//byte cFindIdentifier[] = new IdentifierMessage(identifier,association.getTransferSyntaxForPresentationContextID(usePresentationContextID)).getBytes();
		association.setReceivedDataHandler(new CFindResponseHandler(identifierHandler,debugLevel));
		// for some reason association.send(usePresentationContextID,cFindRequestCommandMessage,cFindIdentifier) fails with Oldenburg imagectn
		// so send the command and the identifier separately ...
		// (was probably because wasn't setting the last fragment flag on the command in Association.send() DAC. 2004/06/10)
		// (see [bugs.mrmf] (000114) Failing to set last fragment on command when sending command and data in same PDU)
		association.send(usePresentationContextID,cFindRequestCommandMessage,null);
		//association.send(usePresentationContextID,null,cFindIdentifier);
		//WFP - fix
		association.send(usePresentationContextID,null,null);
			if(isCancel){
			try{
				Thread.sleep(cancelDelayInMilliSec);
			}
			catch(InterruptedException iX){
				//do nothing
			}
			int messageID = cFindRequest.getMessageID();
			//WFP - fix
			//byte cFindCancelCommandMessage[] = new CCancelRequestCommandMessage(messageID).getBytes();
			//association.send(usePresentationContextID, cFindCancelCommandMessage, null);
		}
if (debugLevel > 0) System.err.println("FindSOPClassSCU: waiting for PDUs");
		try {
			association.waitForPDataPDUsUntilHandlerReportsDone();
if (debugLevel > 0) System.err.println("FindSOPClassSCU: got PDU, now releasing association");
			// State 6
			association.release();
		}
		catch (AReleaseException e) {
			// State 1
			// the other end released and didn't wait for us to do it
		}
	}
}




