/**
 * Created on Jan 31, 2008
 */
package gov.va.med.imaging.dicom.utils.pixelmed.sender;

/**
 *
 *
 *
 * @author William Peterson
 *
 */
public class SendInfo{
	
	
	private String hostname;
	private int port = 0;
	private String calledAETitle;
	private String callingAETitle;
	private String imageFolder;
	private int debugLevel = 0;
	

	/**
	 * Constructor
	 */
	public SendInfo() {
		// TODO Auto-generated constructor stub
	}
	public SendInfo(String hostname, int port, String calledAETitle, String callingAETitle, 
			String imagefolder, int debugLevel){
		
		this.hostname = hostname;
		this.port = port;
		this.calledAETitle = calledAETitle;
		this.callingAETitle = callingAETitle;
		this.imageFolder = imagefolder;
		this.debugLevel = debugLevel;
		
	}
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.sender.ISendInfo#getHostname()
	 */
	public String getHostname() {
		return hostname;
	}
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.sender.ISendInfo#setHostname(java.lang.String)
	 */
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.sender.ISendInfo#getPort()
	 */
	public int getPort() {
		return port;
	}
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.sender.ISendInfo#setPort(int)
	 */
	public void setPort(int port) {
		this.port = port;
	}
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.sender.ISendInfo#getCalledAETitle()
	 */
	public String getCalledAETitle() {
		return calledAETitle;
	}
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.sender.ISendInfo#setCalledAETitle(java.lang.String)
	 */
	public void setCalledAETitle(String calledAETitle) {
		this.calledAETitle = calledAETitle;
	}
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.sender.ISendInfo#getCallingAETitle()
	 */
	public String getCallingAETitle() {
		return callingAETitle;
	}
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.sender.ISendInfo#setCallingAETitle(java.lang.String)
	 */
	public void setCallingAETitle(String callingAETitle) {
		this.callingAETitle = callingAETitle;
	}
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.sender.ISendInfo#getImageFolder()
	 */
	public String getImageFolder() {
		return this.imageFolder;
	}
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.sender.ISendInfo#setImageFolder(java.lang.String)
	 */
	public void setImageFolder(String imagefolder) {
		this.imageFolder = imagefolder;
	}
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.sender.ISendInfo#getDebugLevel()
	 */
	public int getDebugLevel() {
		return debugLevel;
	}
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.sender.ISendInfo#setDebugLevel(int)
	 */
	public void setDebugLevel(int debugLevel) {
		this.debugLevel = debugLevel;
	}
}
