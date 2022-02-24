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
public class TextFileExtractionException extends Exception {

    /**
     * Constructor
     *
     * 
     */
    public TextFileExtractionException() {
        super();
    }

    /**
     * Constructor
     *
     * @param message
     */
    public TextFileExtractionException(String message) {
        super(message);
    }

    /**
     * Constructor
     *
     * @param cause
     */
    public TextFileExtractionException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructor
     *
     * @param message
     * @param cause
     */
    public TextFileExtractionException(String message, Throwable cause) {
        super(message, cause);
    }

}
