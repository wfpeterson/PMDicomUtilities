/*
 * Created on Dec 20, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gov.va.med.imaging.dicom.utils.pixelmed.model;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.table.TableModel;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.AttributeListTableBrowser;
import com.pixelmed.dicom.AttributeListTableModel;
import com.pixelmed.dicom.AttributeTag;
import com.pixelmed.dicom.DicomDictionary;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.SequenceAttribute;
import com.pixelmed.dicom.SequenceItem;
import com.pixelmed.dicom.TagFromName;
import com.pixelmed.validate.DicomInstanceValidator;

import gov.va.med.imaging.dicom.utilities.exceptions.DataCreationException;
import gov.va.med.imaging.dicom.utilities.exceptions.DicomFileException;
import gov.va.med.imaging.dicom.utilities.exceptions.PixelDataException;
import gov.va.med.imaging.dicom.utilities.exceptions.ValidationException;
import gov.va.med.imaging.dicom.utilities.model.IDicomInformation;
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
public class DicomInformationImpl implements IDicomInformation, IHistogramData {
    
    AttributeList dds;
    String openedFile;
    int numberOfColumns = 0;
    int maxNestedSequences = 0;
    Vector metaData = null;
    Vector metaDataColumnNames = null;
    IHistogramData histogramData = null;

        
    /**
     * Constructor
     *
     * 
     */
    public DicomInformationImpl() {
        super();
        // TODO Auto-generated constructor stub
    }

    /* (non-Javadoc)
     * @see gov.va.med.imaging.dicom.utils.pixelmed.model.interfaces.IDicomInformation#openFile(java.lang.String)
     */
    @Override
	public void openFile(String dcmFile) throws DicomFileException {
        
        this.dds = new AttributeList();
        this.openedFile = dcmFile;
        try{
                dds.read(this.openedFile, null, true, true);                
        }
        catch(DicomException de){
            System.out.println(de.getMessage());
            throw new DicomFileException();
        }
        catch(IOException ioe){
            System.out.println(ioe.getMessage());
            throw new DicomFileException();
        }
    }

    /* (non-Javadoc)
     * @see gov.va.med.imaging.dicom.utils.pixelmed.model.interfaces.IDicomInformation#openFile(java.io.File)
     */
    @Override
	public void openFile(File dcmFile) throws DicomFileException {

        if(dcmFile.isFile()){
            String filename = dcmFile.getAbsolutePath();
            this.openFile(filename);
        }
    }

    /* (non-Javadoc)
     * @see gov.va.med.imaging.dicom.utils.pixelmed.model.interfaces.IDicomInformation#getHistogram()
     */
    @Override
	public IHistogramData getHistogram() throws PixelDataException{
        // TODO Auto-generated method stub
        
        //Create PixelData object.
        PixelData pixelData = new PixelData();
        int[] pixelValues;
        //Call method to convert PixelData into Integers.
        //Retrieve Bits Stored value.
        int bitsStored = Attribute.getSingleIntegerValueOrDefault(this.dds,
                TagFromName.BitsStored, -1);
        if(bitsStored == -1){
            throw new PixelDataException("DataSet does not contain valid Bits Stored value.");
        }
        //Calculate maximum number of pixel values based on Bits Stored value.
        int maxNumberOfPixels = 1 << bitsStored;
        //System.out.println("Bits Stored: "+bitsStored);
        //System.out.println("Max Number of Pixels: "+maxNumberOfPixels);
        //Retrieve Pixel Representation.
        int pixelRepresentation = Attribute.getSingleIntegerValueOrDefault(this.dds, 
                TagFromName.PixelRepresentation, -1);
        if(pixelRepresentation == -1){
            throw new PixelDataException("DataSet does not contain valid Pixel " +
                    "Representation value.");
        }
        int numberOfImages = Attribute.getSingleIntegerValueOrDefault(this.dds, 
                TagFromName.NumberOfFrames, 1);
        if(numberOfImages > 1){
            throw new PixelDataException("DataSet is for Multiframe IOD.");
        }
        
        try{
            pixelValues = pixelData.convertPixelDataToIntegers(this.dds);
        }
        catch (PixelDataException pdX) {
            throw new PixelDataException("Exception thrown while converting Pixel Data " +
                    "to Integers. \n"+pdX.getMessage());
		}
        //Calculate the smallest and largest Possible Pixel Values based on Pixel Representation and
        //  Bits Stored values.
        int smallestPossiblePixelValue = 0;
        int largestPossiblePixelValue = 0;
        if(pixelRepresentation == 1){
            smallestPossiblePixelValue = -1*(maxNumberOfPixels/2);
            largestPossiblePixelValue = (maxNumberOfPixels/2) - 1;
        }
        else{
            smallestPossiblePixelValue = 0;
            largestPossiblePixelValue = maxNumberOfPixels-1;            
        }
        //Create a HistogramData object.
        HistogramDataImpl histogram = new HistogramDataImpl(maxNumberOfPixels);
        //Create a Pixel Bin Array to store the pixel count for each Pixel value.
        int[] pixelBin = new int[maxNumberOfPixels];
        //Loop thru the PixelValues int array.
        for(int i=0; i < pixelValues.length; i++){
            //Get pixel value.
            int pixelValue = pixelValues[i];
            //if(pixelValue > 255){
            //   System.out.println("Pixel Value over 255: "+pixelValue);
            //}
            //Offset the pixel value to normalize to positive integers.  This is basically 
            //  taking the smallest Possible Pixel Value and subtracting it from the pixel value.
            int offsetValue = pixelValue + (-1*smallestPossiblePixelValue);
            //increment the associated PixelBin cell by using the offsetted pixel value
            //  as the index.
            pixelBin[offsetValue]++;
        }
        histogram.setPixelBin(pixelBin);
        histogram.setTrueLowPixelValue(smallestPossiblePixelValue);
        histogram.setTrueHighPixelValue(largestPossiblePixelValue);
        //Return the HistogramData object.
        this.histogramData = histogram;
        return histogram;
    }

    /* (non-Javadoc)
     * @see gov.va.med.imaging.dicom.utils.pixelmed.model.interfaces.IDicomInformation#validateIOD()
     */
    @Override
	public String validateIOD() throws ValidationException{

        String result = "IOD Validation did not happen.";
        try{
            DicomInstanceValidator validator = new DicomInstanceValidator();
            result = validator.validate(this.dds);
        }
        catch(TransformerConfigurationException tce){
            throw new ValidationException("Could not read XML Transformer Configuration.");
        }
        catch(UnsupportedEncodingException uee){
            throw new ValidationException("Could not find IOD rules.");
        }
        catch(ParserConfigurationException pce){
            throw new ValidationException("Could not parse IOD xml.");
        }
        catch(TransformerException te){
            throw new ValidationException("Could not perform XML Transform.");
        }
        return result;
    }

    /* (non-Javadoc)
     * @see gov.va.med.imaging.dicom.utils.pixelmed.model.interfaces.IDicomInformation#getMetaData()
     */
    @Override
	public Vector getMetaData() throws DataCreationException{
        // TODO Auto-generated method stub
        //Only do the following if the metadata Vector object is null.
        if(this.metaData == null){
            //Call method to build metadata headings.
            this.buildMetaDataHeadings();
            //Initialize the Vector object.
            this.metaData = new Vector();
            //Create a private, recursive method to call.  Call that method here.
            this.recursiveBuildDataSet(this.dds, numberOfColumns);
        }
        //Return the vector object.
        return this.metaData;
    }

    /* (non-Javadoc)
     * @see gov.va.med.imaging.dicom.utils.pixelmed.model.interfaces.IDicomInformation#getMetaDataHeaderNames()
     */
    @Override
	public Vector getMetaDataHeaderNames() throws DataCreationException{
        // TODO Auto-generated method stub
        //Call method to build metadata headings.
        this.buildMetaDataHeadings();
        //return vector object.
        return this.metaDataColumnNames;
    }
    
    private void buildMetaDataHeadings(){
        if(this.metaDataColumnNames == null){
            this.metaDataColumnNames = new Vector();
            this.recursiveMaxNestedSequenceCount(this.dds, 0);
            //Create a Vector object containing the header names.  The first n columns represents
            //  the max depth of the nested Sequences.  
            for(int i=0; i <= maxNestedSequences; i++){
                if(i == 0){
                    this.metaDataColumnNames.addElement("Element");
                }
                else{
                    this.metaDataColumnNames.addElement("Seq. Element");
                }
            }
            //Afterward, the columns shall be named:
            //      -VR
            //      -Length
            //      -Description
            //      -Value
            //  Assigned it to the metaDataColumnNames instance variable.
            this.metaDataColumnNames.addElement("VR");
            this.metaDataColumnNames.addElement("Length");
            this.metaDataColumnNames.addElement("Description");
            this.metaDataColumnNames.addElement("Data Value");
        }
    }

    private void recursiveBuildDataSet(AttributeList list, int level){
        
        DicomDictionary dd = list.getDictionary();
        //Create loop to cycle thru the elements.  Read each element.
        Iterator iter = list.keySet().iterator();
        while(iter.hasNext()){
            AttributeTag tag = (AttributeTag)iter.next();
            Attribute element = list.get(tag);
            
            Vector attributeVector = new Vector();
            //Add the following to a Vector object:
            //      -Element value at the appropriate level.  The upper levels are set to 
            //          blank (""). The lower level should be set to "Item x" String with x 
            //          representing Item number in the Sequence.
            //      -VR value represented as the two letter code.
            //      -Length value as an Integer value.
            //      -Description value from Data Dictionary.  Look elsewhere to find this value.
            //      -The Element Data Value as a String.  If the data is a LUT or Pixel Data,
            //          do not display or truncate it.
            String elementName = element.getTag().toString();
            String elementVR = element.getVRAsString();
            //Need to handle Sequence undefined length correctly.
            long undefinedLength=0xffffffffl;
            Long lengthValue = new Long(element.getVL());
            String elementLength;
            if(lengthValue.longValue() == undefinedLength){
                elementLength = "-1";
            }
            else{
                elementLength = String.valueOf(lengthValue);                
            }
            String elementDescription = dd.getNameFromTag(element.getTag()); 
            String elementValue = element.getDelimitedStringValuesOrEmptyString();
            
            for(int i=0; i <= this.maxNestedSequences; i++){
                if(level == i){
                    attributeVector.addElement(elementName);
                }
                else{
                    attributeVector.addElement("");
                }
            }
            attributeVector.addElement(elementVR);
            attributeVector.addElement(elementLength);
            attributeVector.addElement(elementDescription);
            attributeVector.addElement(elementValue);
            
            this.metaData.addElement(attributeVector);
            //If the element is a Sequence, perform the following nested steps.
            //Increment the Level.
            
            //Extract the Sequence AttributeList into a local variable.
        
            //Call this method.  Pass the extracted Sequence AttributeList and 
            //Level variables to this method.                
            if(element.getVRAsString().equals("SQ")){
                level++;
                SequenceAttribute sqElement = (SequenceAttribute)element;
                Iterator iterSQ = sqElement.iterator();
                while(iterSQ.hasNext()){
                    SequenceItem item = (SequenceItem)iterSQ.next();
                    AttributeList tempList = item.getAttributeList();
                    this.recursiveBuildDataSet(tempList, level);
                }
                level--;
            }
        }
    }
    
    private void recursiveMaxNestedSequenceCount(AttributeList list, int level){
//        if(this.metaDataColumnNames == null){
            //Search thru dataset to determine the max nested Sequences.  Assigned it to the 
            //  numberOfColumns instance variable.
            //Create loop to cycle thru the elements.
            Iterator iter = list.keySet().iterator();
            while(iter.hasNext()){
                //Check if element is a Sequence.  If not a Sequence, skip any work and move on to the
                //  next element.  If a Sequence, 
                AttributeTag elementTag = (AttributeTag)iter.next();
                Attribute element = (Attribute)list.get(elementTag);
                if(element.getVRAsString().equals("SQ")){
                    level++;
                    if(level > maxNestedSequences){
                        maxNestedSequences = level;
                    }
                    SequenceAttribute sqElement = (SequenceAttribute)element;
                    Iterator iterSQ = sqElement.iterator();
                    while(iterSQ.hasNext()){
                        SequenceItem item = (SequenceItem)iterSQ.next();
                        AttributeList tempList = item.getAttributeList();
                        this.recursiveMaxNestedSequenceCount(tempList, level);
                    }
                    level--;
                }
            }
//        }
    }
    
    /* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.model.IHistogramData#getDataSetTableModel()
	 */
    @Override
	public TableModel getDataSetTableModel(){
        
        AttributeListTableModel tableModel = new AttributeListTableModel(this.dds);
        
        return tableModel;
    }
    
    /* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.model.IHistogramData#getLoadedDataSetJTree()
	 */
    @Override
	public JTable getLoadedDataSetJTree(){
        
        AttributeListTableModel tableModel = new AttributeListTableModel(this.dds);

        AttributeListTableBrowser tableBrowser = new AttributeListTableBrowser(tableModel);
        
        return tableBrowser;
    }
    /* (non-Javadoc)
     * @see gov.va.med.imaging.dicom.utils.pixelmed.model.interfaces.IDicomInformation#getHistogramTable()
     */
    /* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.model.IHistogramData#getHistogramTable()
	 */
    @Override
	public Vector getHistogramTable() {
        Vector table = new Vector();
        int[] pixels = histogramData.getPixelBins();
        for(int i=0; i<pixels.length; i=i+10){
            Vector row = new Vector();
            row.addElement((histogramData.getTrueLowPixelValue()+i)+":");
            for(int j=0; j<10; j++){
                if((j+i) >= pixels.length){
                    table.addElement(row);
                    return table;
                }
                if(pixels[j+i] == 0){
                    row.addElement("");
                }
                else{
                    row.addElement(new Integer(pixels[j+i]).toString());
                }
            }
            table.addElement(row);
        }
        return table;
    }
    /* (non-Javadoc)
     * @see gov.va.med.imaging.dicom.utils.pixelmed.model.interfaces.IDicomInformation#getHistogramTableNames()
     */
    /* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.model.IHistogramData#getHistogramTableNames()
	 */
    @Override
	public Vector getHistogramTableNames() {
        Vector names = new Vector();
        names.addElement("Pixel Value");
        names.addElement("+0");
        names.addElement("+1");
        names.addElement("+2");
        names.addElement("+3");
        names.addElement("+4");
        names.addElement("+5");
        names.addElement("+6");
        names.addElement("+7");
        names.addElement("+8");
        names.addElement("+9");
        
        return names;
    }

	@Override
	public int[] getPixelBins() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getTrueLowPixelValue() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getTrueHighPixelValue() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public IDicomInformation getinstance() {
		return new DicomInformationImpl();
	}
}
