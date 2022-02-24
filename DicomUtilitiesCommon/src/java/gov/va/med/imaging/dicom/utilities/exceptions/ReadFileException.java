/*
 * Created on Mar 6, 2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gov.va.med.imaging.dicom.utilities.exceptions;

/**
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 *
 *
 * @author William Peterson
 *
 */
public class ReadFileException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = -5582543484349926318L;

	/**
     * Constructor
     *
     * 
     */
    public ReadFileException() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * Constructor
     *
     * @param arg0
     */
    public ReadFileException(String arg0) {
        super(arg0);
        // TODO Auto-generated constructor stub
    }

    /**
     * Constructor
     *
     * @param arg0
     */
    public ReadFileException(Throwable arg0) {
        super(arg0);
        // TODO Auto-generated constructor stub
    }

    /**
     * Constructor
     *
     * @param arg0
     * @param arg1
     */
    public ReadFileException(String arg0, Throwable arg1) {
        super(arg0, arg1);
        // TODO Auto-generated constructor stub
    }

}
