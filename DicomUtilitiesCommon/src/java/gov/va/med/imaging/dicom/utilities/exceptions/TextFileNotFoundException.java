/*
 * Created on Sep 28, 2005
 *
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
public class TextFileNotFoundException extends Exception {

    /**
     * Constructor
     *
     * 
     */
    public TextFileNotFoundException() {
        super();
    }

    /**
     * Constructor
     *
     * @param message
     */
    public TextFileNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructor
     *
     * @param cause
     */
    public TextFileNotFoundException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructor
     *
     * @param message
     * @param cause
     */
    public TextFileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
