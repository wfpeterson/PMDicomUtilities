/**
 * Created on Jan 31, 2008
 */
package gov.va.med.imaging.dicom.utils.pixelmed.move;

/**
 *
 *
 *
 * @author William Peterson
 *
 */
public class CMoveThread implements Runnable {
	
	CMoveInfo info;


	/**
	 * Constructor
	 */
	public CMoveThread() {
		// TODO Auto-generated constructor stub
		this.info = new CMoveInfo();
	}
	
	public CMoveThread(CMoveInfo info){
		this.info = info;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println("\nStarting Thread "+Thread.currentThread().getName()+" now.\n");
		try {		    
		    new MoveSOPClassSCUWithCancel(info.getHostname(), info.getPort(), info.getCalledAETitle(),
		    		info.getCallingAETitle(), info.getMoveDestination(), 
		    		info.getAffectedSOPClass(), info.getIdentifier(),  info.getDebugLevel(),
		    		info.isCancel(), info.getCancelDelay());
		    System.out.println(Thread.currentThread().getName()+" Completed C-Move activity.");
		}
		catch (Exception e) {
		    e.printStackTrace(System.err);
		}
	}
	
	/**
	 * @param info the info to set
	 */
	public void setInfo(CMoveInfo info) {
		this.info = info;
	}

}
