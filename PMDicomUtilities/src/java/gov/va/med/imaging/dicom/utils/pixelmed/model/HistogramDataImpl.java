/*
 * Created on Dec 22, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gov.va.med.imaging.dicom.utils.pixelmed.model;

import gov.va.med.imaging.dicom.utilities.model.IHistogramData;

/**
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 *
 *
 * @author William Peterson
 *
 */
public class HistogramDataImpl implements IHistogramData {

    private int[] pixelBin;
    private int trueLowPixelValue;
    private int trueHighPixelValue;

    /**
     * Constructor
     *
     * 
     */
    public HistogramDataImpl() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    /**
     * Constructor
     *
     * 
     */
    public HistogramDataImpl(int size) {
        super();
        // TODO Auto-generated constructor stub
        pixelBin = new int[size];
    }

    /* (non-Javadoc)
     * @see gov.va.med.imaging.dicom.utils.pixelmed.model.interfaces.IHistogramData#getDataBins()
     */
    public int[] getPixelBins() {
        // TODO Auto-generated method stub
        return this.pixelBin;
    }

    /* (non-Javadoc)
     * @see gov.va.med.imaging.dicom.utils.pixelmed.model.interfaces.IHistogramData#getTrueLowValue()
     */
    public int getTrueLowPixelValue() {
        // TODO Auto-generated method stub
        return this.trueLowPixelValue;
    }

    /* (non-Javadoc)
     * @see gov.va.med.imaging.dicom.utils.pixelmed.model.interfaces.IHistogramData#getTrueHighValue()
     */
    public int getTrueHighPixelValue() {
        // TODO Auto-generated method stub
        return this.trueHighPixelValue;
    }

    /**
     * @param bin The bin to set.
     */
    public void setPixelBin(int[] bin) {
        this.pixelBin = bin;
    }
    /**
     * @param trueHighValue The trueHighValue to set.
     */
    public void setTrueHighPixelValue(int trueHighValue) {
        this.trueHighPixelValue = trueHighValue;
    }
    /**
     * @param trueLowValue The trueLowValue to set.
     */
    public void setTrueLowPixelValue(int trueLowValue) {
        this.trueLowPixelValue = trueLowValue;
    }
}
