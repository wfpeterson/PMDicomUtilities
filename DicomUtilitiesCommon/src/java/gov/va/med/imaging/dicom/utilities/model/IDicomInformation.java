/*
 * Created on Dec 20, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gov.va.med.imaging.dicom.utilities.model;

import java.io.File;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.table.TableModel;

import gov.va.med.imaging.dicom.utilities.exceptions.DataCreationException;
import gov.va.med.imaging.dicom.utilities.exceptions.DicomFileException;
import gov.va.med.imaging.dicom.utilities.exceptions.PixelDataException;
import gov.va.med.imaging.dicom.utilities.exceptions.ValidationException;

/**
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 *
 *
 * @author William Peterson
 *
 */

public interface IDicomInformation {
    
	
	public IDicomInformation getinstance();
    /**
     * 
     * Open DICOM file.
     * 
     * @param file
     * @throws DicomFileException
     */
    public void openFile(String file) throws DicomFileException;
    
    /**
     * Open DICOM file.
     * @param file
     * @throws DicomFileException
     */
    public void openFile(File file) throws DicomFileException;
    
    /**
     * Create a histogram table based on the Pixel Data.  The Key and the Data are both
     * Integer Objects.  I could not use an Array because the pixel values may be negative.
     * So, I convert all int values to Integer Objects.  This allows the Keys to be positive
     * or negative.
     * 
     * @return HashMap with Integer Objects as both the Key and the Data.
     * 
     */
    public IHistogramData getHistogram() throws PixelDataException;
    
    /**
     * Run a Validation check against the proper IOD.
     * 
     * @return
     */
    public String validateIOD() throws ValidationException;
    
    /**
     * Build a collection of the DICOM Header.  
     * 
     * @return
     */
    public Vector getMetaData() throws DataCreationException;
    
    /**
     * Return a collection of the Columns Header Names.
     * 
     * @return
     */
    public Vector getMetaDataHeaderNames() throws DataCreationException;

    /**
     * 
     * @return
     */
    public TableModel getDataSetTableModel();
    
    /**
     * 
     * @return
     */
    public JTable getLoadedDataSetJTree();
    
    public Vector getHistogramTableNames();
    
    public Vector getHistogramTable();



}
