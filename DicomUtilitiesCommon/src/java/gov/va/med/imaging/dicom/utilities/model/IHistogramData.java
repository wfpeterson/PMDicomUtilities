/*
 * Created on Dec 22, 2006
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
public interface IHistogramData {
    
    /**
     * 
     * @return
     */
    public int[] getPixelBins();
    
    /**
     * 
     * @return
     */
    public int getTrueLowPixelValue();
    
    /**
     * 
     * @return
     */
    public int getTrueHighPixelValue();
}
