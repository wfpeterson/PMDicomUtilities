package gov.va.med.imaging.dicom.utils.pixelmed.listener;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.ListIterator;

import com.pixelmed.dicom.TransferSyntax;
import com.pixelmed.network.PresentationContext;
import com.pixelmed.network.PresentationContextSelectionPolicy;

public class UnencapsulatedImplicitAndExplicitPresentationContextSelectionPolicy
		implements PresentationContextSelectionPolicy {

	@Override
	public LinkedList applyPresentationContextSelectionPolicy(
			LinkedList presentationContexts, int associationNumber,
			int debugLevel) {

		HashSet allAbstractSyntaxesAcceptedWithUnencapsulatedVRTransferSyntax = new HashSet();
		
		// Pass 1 - fill allAbstractSyntaxesAcceptedWithImplicitVRTransferSyntax
		
		ListIterator pcsi = presentationContexts.listIterator();
		while (pcsi.hasNext()) {
			PresentationContext pc = (PresentationContext)(pcsi.next());
			String transferSyntaxUID=pc.getTransferSyntaxUID();		// There will only be one by this time
			if (transferSyntaxUID != null && TransferSyntax.isImplicitVR(transferSyntaxUID)) {
				allAbstractSyntaxesAcceptedWithUnencapsulatedVRTransferSyntax.add(pc.getAbstractSyntaxUID());
			}
		}
		
		// Pass 2 - reject any PC with an IVR for an AS that is in allAbstractSyntaxesAcceptedWithExplicitVRTransferSyntax
		
		pcsi = presentationContexts.listIterator();
		while (pcsi.hasNext()) {
			PresentationContext pc = (PresentationContext)(pcsi.next());
			String transferSyntaxUID=pc.getTransferSyntaxUID();		// There will only be one by this time
			if (transferSyntaxUID != null 
				&& TransferSyntax.isEncapsulated(transferSyntaxUID)
				&& allAbstractSyntaxesAcceptedWithUnencapsulatedVRTransferSyntax.contains(pc.getAbstractSyntaxUID())) {
				pc.setResultReason((byte)2);				// no reason (provider rejection)
			}
		}
		return presentationContexts;
	}

	@Override
	public LinkedList applyPresentationContextSelectionPolicy(LinkedList arg0, int arg1) {
		// TODO Auto-generated method stub
		return null;
	}
}
