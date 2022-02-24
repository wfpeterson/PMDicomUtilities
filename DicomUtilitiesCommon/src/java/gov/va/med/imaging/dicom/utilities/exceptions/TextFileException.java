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
public class TextFileException extends Exception {

    /**
     * Constructor
     *
     * 
     */
    public TextFileException() {
        super();
    }

    /**
     * Constructor
     *
     * @param message
     */
    public TextFileException(String message) {
        super(message);
    }

    /**
     * Constructor
     *
     * @param cause
     */
    public TextFileException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructor
     *
     * @param message
     * @param cause
     */
    public TextFileException(String message, Throwable cause) {
        super(message, cause);
    }

}
