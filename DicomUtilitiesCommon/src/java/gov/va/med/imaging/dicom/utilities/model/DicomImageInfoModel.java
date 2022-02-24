/*
 * Created on Dec 29, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gov.va.med.imaging.dicom.utilities.model;

/**
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 *
 *
 * @author William Peterson
 *
 */
public class DicomImageInfoModel{

    /**
     * Constructor
     *
     * 
     */
    public DicomImageInfoModel() {
        super();
        // TODO Auto-generated constructor stub
    }
    
	public String getInitialDirectory(){
        String path;
        try {
            //path = System.getProperty("user.home");
            path = "\\";
            }
            catch (SecurityException se) {
            path = null;
            }
            if (path == null) {
            System.out.println("Could not determine home directory");
            }
        return path;
    }
}
