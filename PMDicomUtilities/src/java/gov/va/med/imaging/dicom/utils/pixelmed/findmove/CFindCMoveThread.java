/**
 * Created on Jan 31, 2008
 */
package gov.va.med.imaging.dicom.utils.pixelmed.findmove;

import gov.va.med.imaging.dicom.utils.pixelmed.find.FindSOPClassSCUWithCancel;


/**
 *
 *
 *
 * @author William Peterson
 *
 */
public class CFindCMoveThread implements Runnable {
	
	CFindInfoForMove info;


	/**
	 * Constructor
	 */
	public CFindCMoveThread() {
		// TODO Auto-generated constructor stub
		this.info = new CFindInfoForMove();
	}
	
	public CFindCMoveThread(CFindInfoForMove info){
		this.info = info;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		System.out.println("\nStarting Thread "+Thread.currentThread().getName()+" now.\n");
		try {		    
		    new FindSOPClassSCUWithCancel(info.getHostname(), info.getPort(), info.getCalledAETitle(),
		    		info.getCallingAETitle(), info.getAffectedSOPClass(),
		    		info.getIdentifier(), info.getIdentifierHandler(), info.getDebugLevel(),
		    		info.isCancel(), info.getCancelDelay());
		}
		catch (Exception e) {
		    e.printStackTrace(System.err);
		}
	}
	
	/**
	 * @param info the info to set
	 */
	public void setInfo(CFindInfoForMove info) {
		this.info = info;
	}

}
