/**
 * Created on Jan 31, 2008
 */
package gov.va.med.imaging.dicom.utils.pixelmed.find;

/**
 *
 *
 *
 * @author William Peterson
 *
 */
public class CFindThread implements Runnable {
	
	CFindInfo info;


	/**
	 * Constructor
	 */
	public CFindThread() {
		// TODO Auto-generated constructor stub
		this.info = new CFindInfo();
	}
	
	public CFindThread(CFindInfo info){
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
	public void setInfo(CFindInfo info) {
		this.info = info;
	}

}
