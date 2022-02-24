/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: September 26, 2005
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  VHAISWPETERB
  Description: 

        ;; +--------------------------------------------------------------------+
        ;; Property of the US Government.
        ;; No permission to copy or redistribute this software is given.
        ;; Use of unreleased versions of this software requires the user
        ;;  to execute a written test agreement with the VistA Imaging
        ;;  Development Office of the Department of Veterans Affairs,
        ;;  telephone (301) 734-0100.
        ;;
        ;; The Food and Drug Administration classifies this software as
        ;; a Class II medical device.  As such, it may not be changed
        ;; in any way.  Modifications to this software may result in an
        ;; adulterated medical device under 21CFR820, the use of which
        ;; is considered to be a violation of US Federal Statutes.
        ;; +--------------------------------------------------------------------+
 */
package gov.va.med.imaging.dicom.utils.pixelmed.recon;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Vector;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeFactory;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.AttributeTag;
import com.pixelmed.dicom.CodeStringAttribute;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.DicomFileUtilities;
import com.pixelmed.dicom.OtherByteAttribute;
import com.pixelmed.dicom.OtherWordAttribute;
import com.pixelmed.dicom.SOPClass;
import com.pixelmed.dicom.TagFromName;
import com.pixelmed.dicom.ValueRepresentation;

import gov.va.med.imaging.dicom.common.configuration.DicomGatewayConfiguration;
import gov.va.med.imaging.dicom.common.configuration.GatewayDictionaryContents;
import gov.va.med.imaging.dicom.common.configuration.ModalityDicInfo;
import gov.va.med.imaging.dicom.common.configuration.ParameterDeviceInfo;
import gov.va.med.imaging.dicom.common.configuration.Parameters;
import gov.va.med.imaging.dicom.common.exceptions.ParameterDecompositionException;
import gov.va.med.imaging.dicom.utilities.exceptions.TGAFileException;
import gov.va.med.imaging.dicom.utilities.exceptions.TGAFileNotFoundException;

//FUTURE Consider changing code to instantiate the parsers once.  This requires moving
//	all instance variables.
/**
 *
 * @author William Peterson
 *
 */
public class LegacyTGAFileParser {

    private String tgaFile = null;
    
    private boolean bigFileFlag;
    
    private AttributeList toolkitDDS = null;
    
    private short tgaRows = 0;
    
    private short tgaColumns = 0;
    
    private short tgaBitsPerPixel = 0;
    
    private int imagePlanes = 0;
    
    private boolean isImageReduced = false;
    
    private OriginalPixelDataInfo originalPixelDataInfo = null;
    
    private String acquisitionSite = null;

    private ByteBuffer imageBuffer=null;
    
    //private static final Logger LOGGER = Logger.getLogger (LegacyTGAFileParser.class);
    private static final Logger LOGGER = LogManager.getLogger ("VA_DICOM");
    private static Logger TESTLOGGER = LogManager.getLogger("JUNIT_TEST");

    /**
     * Constructor
     */
    public LegacyTGAFileParser() {
        super();
    }
    
    public void updateDicomDataSetWithPixelData(AttributeList dds, 
            String tgaFile, OriginalPixelDataInfo originalPixelDataInfo)
            throws TGAFileNotFoundException, TGAFileException{

        LOGGER.info(this.getClass().getName()+": Dicom Toolkit Layer: " +
                "parsing TGA file to DicomDataSet ...");        

        this.originalPixelDataInfo = originalPixelDataInfo;
        this.acquisitionSite = "660";
        //Extract the DCF DicomDataSet object out of dicomDataSet.
        this.toolkitDDS = dds;
        try{
        	bigFileFlag=tgaFile.toLowerCase().endsWith(".big");
        	
        	//CSABA wfp-Added this IF statement to use the .tga file if the .big file does not exist.
    		if(!bigFileFlag){
    			tgaFile = tgaFile.toLowerCase();
    			tgaFile = tgaFile.replace(".big", ".tga");
    			LOGGER.warn(this.getClass().getName()+": DICOM Toolkit layer: \n"+
    					".BIG file does not exist.  Reverting to subsampled .TGA file.");
    		}

    		//CSABA wfp-Added this IF statement to check against a .DCM file.
            if(this.isPart10DicomFile(tgaFile)){
            	LOGGER.error(this.getClass().getName()+": DICOM Toolkit layer: \n"+
            			"Should not be a DICOM Part 10 file.");
            	throw new TGAFileException("This is a DICOM Part 10 File.");
            }

            //JUNIT Create test to see how it fails if not correct permissions.
            //Create a binary stream connected to the TGA file.  
            LOGGER.debug("TGA filename: "+tgaFile);
            
            FileChannel fc = new FileInputStream(tgaFile).getChannel();
            MappedByteBuffer buffer = fc.map(MapMode.READ_ONLY, 0, (int)fc.size());
            byte header[] = new byte[18]; 
            // The 18 bytes represents the TGA header which we do not want in the pixel data.
            // This makes sure the position is at 18 instead of 0.
            buffer = (MappedByteBuffer)buffer.get(header, 0, 18);
            int tgaImageBytes = processTgaHeader(header);

            // read in only TGA header sizing worth of image data!! 
            // (prevent inconsistent DICOM file and compression errors)
            int imageBytes = buffer.capacity()-buffer.position();
            if ((imageBytes > tgaImageBytes) && (tgaImageBytes>0))
            	imageBytes = tgaImageBytes; // prevent inconsistent DICOM file and compression errors)
            //  do a bulk push (transfer) to the new ByteBuffer object. The new ByteBuffer
            //  will be assign to the Pixel Data.
            imageBuffer = ByteBuffer.allocate(imageBytes);
            imageBuffer.put(buffer);
            
            processTgaData();
        }
        catch(FileNotFoundException nofile){
            LOGGER.error("Error: " + nofile.getMessage());
            LOGGER.error(this.getClass().getName() + ": Dicom Toolkit layer: " +
                    "Exception thrown while attempting to open TGA file, " + tgaFile+".");
            throw new TGAFileNotFoundException("Could not find or open " + tgaFile+".", nofile);
        }
        catch(IOException ioe){
            LOGGER.error("Error: " + ioe.getMessage());
            LOGGER.error(this.getClass().getName() + ": Dicom Toolkit layer: " +
                    "Exception thrown while attempting to open TGA file, " + tgaFile+".");
            throw new TGAFileException("Could not find or open " + tgaFile + ".", ioe);
        }
        //INFO Keep in the back of your mind about if I need to explicit status LittleEndian
        //  or BigEndian when transferring the actual pixel data.
    }
/*   
    public void updateDicomDataSetWithPixelData(IDicomDataSet dds, 
    		SizedInputStream sizedTgaStream, OriginalPixelDataInfo originalPixelDataInfo)
            throws TGAFileNotFoundException, TGAFileException{

    	LOGGER.info("... Dicom Toolkit Layer: parsing TGA file stream to DicomDataSet ...");
        
        this.originalPixelDataInfo = originalPixelDataInfo;
         //Extract the DCF DicomDataSet object out of dicomDataSet.
        this.toolkitDDS = (DicomDataSet)dds.getDicomDataSet();
        try{
            //process the binary stream connected to the TGA file.  
            LOGGER.debug("... TGA file stream processing ...");
            int byteSize = sizedTgaStream.getByteSize();
            bigFileFlag = (byteSize >= 1048576); // ** cpt .big if larger than a MB (used for CT logic only!)
            byte header[] = new byte[18];
            // The 18 bytes represents the TGA header which we do not want in the pixel data.
            // This makes sure the position is at 18 instead of 0.
            int sizeRead = sizedTgaStream.getInStream().read(header, 0, 18);
            if (sizeRead != 18)
            	throw new TGAFileException("Could not read TGA file stream header.");
            int tgaImageBytes = processTgaHeader(header);

            // read in only TGA header sizing worth of image data!! 
            int imageBytes = byteSize-header.length;
            if ((imageBytes > tgaImageBytes) && (tgaImageBytes>0))
            	imageBytes = tgaImageBytes; // prevent inconsistent DICOM file and compression errors)

   			imageBuffer = ByteBuffer.allocate(imageBytes);
            //  do a bulk push (transfer) to the new ByteBuffer object. The new ByteBuffer
            //  will be assign to the Pixel Data.
   			sizeRead = sizedTgaStream.getInStream().read(imageBuffer.array(), 0, imageBytes);
//   		LOGGER.info(sizeRead + " bytes of " + imageBytes + " in imageBuffer");

   			processTgaData(dds);
        }
        catch(IOException ioe){
            LOGGER.error("Error: " + ioe.getMessage());
            LOGGER.error("Dicom Toolkit layer: Exception thrown while attempting to process TGA file stream.");
        }     
    }
*/   
    private int processTgaHeader(byte[] header)
    throws TGAFileException
    {
        int tgaDataByteSize=0;
    	//Read Byte 2 (1 byte) to confirm it is a "3".  If not throw exception.
        if(header[2] != 3){
            throw new TGAFileException();
        }
        //Read Bytes 12-13 for # of columns.
        this.tgaColumns = 0;
        this.tgaColumns |= (0xFF & header[13]);
        this.tgaColumns <<=8;
        this.tgaColumns |= (0xFF & header[12]);  
        LOGGER.debug("TGA Number of Columns: " + this.tgaColumns);
        //Read Bytes 14-15 for # of rows.
        this.tgaRows = 0;
        this.tgaRows |= (0xFF & header[15]);
        this.tgaRows <<=8;
        this.tgaRows |= (0xFF & header[14]);
        LOGGER.debug("TGA Number of Rows: " + this.tgaRows);
        //Read Byte 16 for bits/pixel.
        this.tgaBitsPerPixel = header[16];
        LOGGER.debug("TGA Bits Per Pixel: " + this.tgaBitsPerPixel);
        int bytesAllocated = (this.tgaBitsPerPixel > 8)? 2 : 1;
        tgaDataByteSize = this.tgaRows * this.tgaColumns * bytesAllocated;
        return tgaDataByteSize;
    }

    //INFO Keep in the back of your mind if we need explicit status LittleEndian
    //  or BigEndian when transferring the actual pixel data.
    private void processTgaData()
    throws TGAFileException
    {
    	try {
        	//SRS This is basically where we introduce Patch 50 Header fixes.
            //Update Pixel Module with information extracted above.
            this.updateImagePixelInfo();

            //Original if statement.  MR IOD will not have the same rules applied.  Thus, I removed
            //MR IOD from if statement.  Currently, we do not alter the MR IOD.
            //if(this.isImageCTIOD() || this.isImageMRIOD()){
            if(this.isImageCTIOD()){
                this.updateCTIODInformation();
            }
            if(this.isMultiFrameIOD()){
                this.updateMultiFrameInfoToSingleFrameInfo();
            }
            this.checkVendorCompression();
            this.updatesForAllModalities();
            //CSABA wfp-The following line is active in Patch 66.
            // this.updateModalityCode();  // *** cpt must check if this is ok!!
            //Read from Byte 18 until the EOF.  Pass into Pixel Data element.  Save to DDS.
            //Images looked like crap.  Had to add statement to resolve byte order from .tga file.     
            imageBuffer.order(ByteOrder.LITTLE_ENDIAN);
            if(this.originalPixelDataInfo.getOriginalVR() == this.getShortValue(ValueRepresentation.OB)){
                OtherByteAttribute pixelData = new OtherByteAttribute(new AttributeTag(0x7FE0,0x0010));
                pixelData.setValues(imageBuffer.array());
                this.toolkitDDS.put(pixelData);
            }
            else if(this.originalPixelDataInfo.getOriginalVR() == this.getShortValue(ValueRepresentation.OW)){
                OtherWordAttribute pixelData = new OtherWordAttribute(new AttributeTag(0x7FE0,0x0010));
                pixelData.setValues(this.getShortArray(imageBuffer.array()));
                this.toolkitDDS.put(pixelData);
            }
            else{
                throw new TGAFileException("No matching VR for Pixel Data.");
            }
            //Encapsulate the DicomDataSet.
            //this.encapsulateDicomDataSet(dds);
        }
    	catch(DicomException dcse){
            LOGGER.error("Error: " + dcse.getMessage());
            LOGGER.error("Dicom Toolkit layer: Exception thrown while attempting to process TGA data.");
            throw new TGAFileException("Could not process TGA data.", dcse);
        }
    }
/*
    private IDicomDataSet encapsulateDicomDataSet(IDicomDataSet dds){

        dds.setDicomDataSet(this.toolkitDDS);
        return dds;
    }
*/
    
    private void updateImagePixelInfo() throws DicomException{
        boolean isEnoughRows = false;
        boolean isEnoughColumns = false;
        boolean isEnoughBits = false;
        boolean isSamplePerPixel = false;
        
        //Some of Patch 50 work is located in this method.  I had to perform this work right here
        //  and not somewhere else at a later date.
        
        //If Rows and Columns from TGA is smaller than those in dds.
        //Allow Columns-1 to be accepted.  This is due to odd column length resolution.
        short ddsRows = (short)this.toolkitDDS.get(TagFromName.Rows).getSingleIntegerValueOrDefault(0);
        short ddsColumns = (short)this.toolkitDDS.get(TagFromName.Columns).getSingleIntegerValueOrDefault(0);
        LOGGER.debug("Original number of Rows: " + ddsRows);
        if(this.tgaRows == ddsRows){
            isEnoughRows = true;
        }
        LOGGER.debug("Original number of Columns: " + ddsColumns);
        if((this.tgaColumns == ddsColumns)||(this.tgaColumns == (short)(ddsColumns-1))){
            isEnoughColumns = true;
            //Had to add this insert() method.  This is because Columns-1 is accepted, however 
            //  this is a difference that causes the image to be distorted.
            //this.toolkitDDS.insert(DCM.E_COLUMNS, this.tgaColumns);
            Attribute element = AttributeFactory.newAttribute(TagFromName.Columns);
            element.setValue(this.tgaColumns);
            this.toolkitDDS.put(element);
        }
        //Historical: We have always converted from b16 to b12 for CT images.  However, there is
        //  no missing data.  The CT only used 12bits, not 16.  This was a common practice for CT
        //  as well as some MR and a couple of CR/DX images.  The only time the image are really
        //  reduced was when using the R factors.  The R factors always took it down to 8bits.
        //  Thus, anything above 8bits is not a reduction.  A b12 image that was a b16 still 
        //  contains all the original information.
        short ddsBitsStored = (short)this.toolkitDDS.get(TagFromName.BitsStored).getSingleIntegerValueOrDefault(0);
        short ddsBitsAllocated = (short)this.toolkitDDS.get(TagFromName.BitsAllocated).getSingleIntegerValueOrDefault(0);
        short ddsSamplesPerPixel = (short)this.toolkitDDS.get(TagFromName.SamplesPerPixel).getSingleIntegerValueOrDefault(0);
        LOGGER.debug("Original number of Bits Stored: " + ddsBitsStored);
        if((this.tgaBitsPerPixel == ddsBitsStored)||(this.tgaBitsPerPixel > 8)){
           isEnoughBits = true;
           
           Attribute bitsStoredElement = AttributeFactory.newAttribute(TagFromName.BitsStored);
           bitsStoredElement.setValue(this.tgaBitsPerPixel);
           //this.toolkitDDS.insert(DCM.E_BITS_STORED, this.tgaBitsPerPixel);
           Attribute highBitElement = AttributeFactory.newAttribute(TagFromName.HighBit);
           highBitElement.setValue(this.tgaBitsPerPixel - 1);
           //this.toolkitDDS.insert(DCM.E_HIGH_BIT, this.tgaBitsPerPixel - 1);
           this.toolkitDDS.put(bitsStoredElement);
           this.toolkitDDS.put(highBitElement);
        }
        LOGGER.debug("Original number of Samples Per Pixel: " + ddsSamplesPerPixel);
        if(ddsSamplesPerPixel == 1){
            isSamplePerPixel = true;
        }
            
        //SRS Patch50 3.2.3.5 Update Image Module info based on tga file used.    
        //SRS Patch50 3.2.3.9 Update Image Module info based on tga file used.
        if(isEnoughRows && isEnoughColumns && isEnoughBits && isSamplePerPixel){
            //if rows, columns, and bits match between DDS and TGA, do nothing.
        	// if IOD is already Secondary Capture remove rescale slope/intercept, etc. 
        	if (this.isImageSCIOD()) {
        		modifyImageToSCIOD();
        	}
        }
        else{
            //add the tga rows/columns fields to the DDS (early, so other computations are correct).
        	//this.toolkitDDS.insert(DCM.E_ROWS, this.tgaRows);
        	Attribute rowsElement = AttributeFactory.newAttribute(TagFromName.Rows);
        	rowsElement.setValue(this.tgaRows);
        	this.toolkitDDS.put(rowsElement);
            //this.toolkitDDS.insert(DCM.E_COLUMNS, this.tgaColumns);
        	Attribute columnsElement = AttributeFactory.newAttribute(TagFromName.Columns);
        	columnsElement.setValue(this.tgaColumns);
        	
            int fullImageSize = (int)ddsBitsAllocated
                * (int)ddsRows
                * (int)ddsColumns
                * (int)ddsSamplesPerPixel;
             
            int bitsAllocated;
            if(this.tgaBitsPerPixel > 8){
                bitsAllocated = 16;
                //this.toolkitDDS.insert(DCM.E_BITS_ALLOCATED, bitsAllocated);
                Attribute bitsAllocatedElement = AttributeFactory.newAttribute(TagFromName.BitsAllocated);
                bitsAllocatedElement.setValue(bitsAllocated);
                this.toolkitDDS.put(bitsAllocatedElement);
                //this.toolkitDDS.insert(DCM.E_BITS_STORED, this.tgaBitsPerPixel);
                Attribute bitsStoredElement = AttributeFactory.newAttribute(TagFromName.BitsStored);
                bitsStoredElement.setValue(this.tgaBitsPerPixel);
                this.toolkitDDS.put(bitsStoredElement);
                
                if(this.toolkitDDS.containsKey(TagFromName.HighBit)){
                    int highBit = this.toolkitDDS.get(TagFromName.HighBit).getSingleIntegerValueOrDefault(0);
                    if(highBit > this.tgaBitsPerPixel){
                        //this.toolkitDDS.insert(DCM.E_HIGH_BIT, (this.tgaBitsPerPixel - 1));
                    	Attribute highBitElement = AttributeFactory.newAttribute(TagFromName.HighBit);
                    	highBitElement.setValue(this.tgaBitsPerPixel - 1);
                    	this.toolkitDDS.put(highBitElement);
                    }
                }
                else{
                    //this.toolkitDDS.insert(DCM.E_HIGH_BIT, (this.tgaBitsPerPixel - 1));
                	Attribute highBitElement = AttributeFactory.newAttribute(TagFromName.HighBit);
                	highBitElement.setValue(this.tgaBitsPerPixel - 1);
                	this.toolkitDDS.put(highBitElement);
                }
            }
            else{
                bitsAllocated = 8;
                //this.toolkitDDS.insert(DCM.E_BITS_ALLOCATED, bitsAllocated);
            	Attribute bitsAllocatedElement = AttributeFactory.newAttribute(TagFromName.BitsAllocated);
            	bitsAllocatedElement.setValue(bitsAllocated);
            	this.toolkitDDS.put(bitsAllocatedElement);
                
                //this.toolkitDDS.insert(DCM.E_BITS_STORED, this.tgaBitsPerPixel);
            	Attribute bitsStoredElement = AttributeFactory.newAttribute(TagFromName.BitsStored);
            	bitsStoredElement.setValue(this.tgaBitsPerPixel);
            	this.toolkitDDS.put(bitsStoredElement);
            	
                //this.toolkitDDS.insert(DCM.E_HIGH_BIT, (this.tgaBitsPerPixel - 1));
            	Attribute highBitElement = AttributeFactory.newAttribute(TagFromName.HighBit);
            	highBitElement.setValue(this.tgaBitsPerPixel - 1);
            	this.toolkitDDS.put(highBitElement);
            	
            }
            int reducedImageSize = this.tgaRows * this.tgaColumns * bitsAllocated;
            
	        if (!(isEnoughRows && isEnoughColumns))
	        	// cpt - 06/28/07: 
	        	// if Columns and Rows changed, and (0028, 0030) Pixel Spacing and/or
	        	// (0018, 1164) Imager Pixel Spacing is present, adjust them respectively
	        	adjustPixelSpacing();

            Integer reductionFactor = fullImageSize / reducedImageSize;
// ** cpt: set legacy lossy compression method for "down-sampled" image
            if (reductionFactor > 1.0) {
	            //set Lossy Image Compression field to 01.
	            //this.toolkitDDS.insert(DCM.E_LOSSY_IMAGE_COMPRESSION,"01");  // VR=CS - coded string!
            	Attribute lossyCompressionElement = AttributeFactory.newAttribute(TagFromName.LossyImageCompression);
            	lossyCompressionElement.setValue("01");
            	this.toolkitDDS.put(lossyCompressionElement);
            	
	            //set Ratio of Lossy Compression.
	            //this.toolkitDDS.insert(DCM.E_LOSSY_IMAGE_CMP_RATIO, reductionFactor);
            	Attribute lossyCompressionRatioElement = AttributeFactory.newAttribute(TagFromName.LossyImageCompressionRatio);
            	lossyCompressionRatioElement.setValue(reductionFactor);
            	this.toolkitDDS.put(lossyCompressionRatioElement);
            	
	            //set lossy compression method
//	            if (reductionFactor > 2) {
		            //this.toolkitDDS.insert(DCM.E_LOSSY_IMAGE_CMP_METHOD, "DOWN-SAMPLED");
            	Attribute lossyCompressionMethodElement = AttributeFactory.newAttribute(TagFromName.LossyImageCompressionMethod);
            	lossyCompressionMethodElement.setValue("DOWN-SAMPLED");
            	this.toolkitDDS.put(lossyCompressionMethodElement);
            	
//	            } else {
//		            this.toolkitDDS.insert(DCM.E_LOSSY_IMAGE_CMP_METHOD, "Reduced Bit Depth");
//	            }
            }
	        //Change Window Width and Level to match pixel depth.
            this.updateWindowValues();
            //Change Image Type to Derived.
            this.updateImageTypeToDerived();
            //Change SOP Class to Secondary Capture.
            //VERIFY proper solution for the following.
            this.updateSOPClassToSecondaryCapture(); // *** cpt: this puts every downsampled CR into SC IOD!!!
        }
    }

    private void adjustPixelSpacing()throws DicomException {
    	// if present adjust (0028, 0030) Pixel Spacing and/or  (0018, 1164) Imager Pixel Spacing
    	// based on changed rows and columns ratio
    	checkAndUpdatePixelSpacing(TagFromName.ImagerPixelSpacing);
    	checkAndUpdatePixelSpacing(TagFromName.PixelSpacing);
    }
    
    private void checkAndUpdatePixelSpacing(AttributeTag pSTag) throws DicomException {
    	
	   	Attribute dE=null;
		dE=this.toolkitDDS.get(pSTag);
	   	String pSString1=null;
	   	String pSString2=null;
		try {
			String[] values = dE.getStringValues();
			pSString1 = values[0];
			pSString2 = values[1];
	    } catch (DicomException dcse){
		}
	    short rowsValue = (short)this.toolkitDDS.get(TagFromName.Rows).getSingleIntegerValueOrDefault(0);
	    short columnsValue = (short)this.toolkitDDS.get(TagFromName.Columns).getSingleIntegerValueOrDefault(0);
		float rowsRatio = rowsValue / this.tgaRows;
		float columnsRatio = columnsValue / this.tgaColumns;
		Float pSR=null;
		Float pSC=null;
		
		try {
			String pSString="";
			if ((pSString1 != null) && (pSString1.length() > 0)) { 
				pSR = (Float)(Float.parseFloat(pSString1) * rowsRatio);
				pSString = pSR.toString();
			}
			if ((pSString2 != null) && (pSString2.length() > 0)) { 
				pSC = (Float)(Float.parseFloat(pSString2) * columnsRatio);
				if (pSString.length() > 0) {
		   			pSString += "\\" + pSC.toString();    				
				} else {
	    			pSString = pSC.toString();
	   			}
			}
			if (pSString.length()>0) {
	            //this.toolkitDDS.put(pSTag, pSString);
            	Attribute element = AttributeFactory.newAttribute(pSTag);
            	element.setValue(pSString);
            	this.toolkitDDS.put(element);
			}
		} 
		catch (NumberFormatException nfe) {}
	}

    private void updateImageTypeToDerived()throws DicomException{
        
        //SRS Patch50 3.2.3.1 All tga images shall set ImageType to Derived.
    	
        //change Image Type from Original to Derived.
    	//   but first check if type 1 field Image Type exists; if does not, create it as DERIVED/PRIMARY
    	//   -- for ACR-NEMA 2.0 compatibility
    	CodeStringAttribute imageType=null;
    	imageType = (CodeStringAttribute)this.toolkitDDS.get(TagFromName.ImageType);
        //String imageTypeValues[] = new String[imageType.getVM()];
        String[] imageTypes = this.toolkitDDS.get(TagFromName.ImageType).getStringValues();
        if((imageTypes[0].equals("ORIGINAL"))){
            CodeStringAttribute nuImageType = new CodeStringAttribute(TagFromName.ImageType);
            for(int i=0; i<imageType.getVM(); i++){
                String singleType = imageTypes[i];
                if(i == 0){
                    nuImageType.addValue("DERIVED");
                }
                else{
                    nuImageType.addValue(singleType);
                }
            }
            this.toolkitDDS.put(nuImageType);
        }
    }
   
    private void changePhotometricInterpretation()throws DicomException{
        
        //SRS Patch50 3.2.3.8 Convert all reconstituted images to Monochrome2.
        //set Photometric Interpretation to Monochrome2.
    	Attribute element = AttributeFactory.newAttribute(TagFromName.PhotometricInterpretation);
    	element.setValue("MONOCHROME2");
        this.toolkitDDS.put(element);
    }
    
    private void updateSOPClassToSecondaryCapture()throws DicomException{
        
        //SRS Patch50 3.2.3.2 Change SOP Class to Secondary Capture.
        //change SOP Class to Secondary Capture.
        //FUTURE I'm doing this, but I think it is wrong.  We should not change the SOP Class.
        //  However, this matches what is done in Patch 50. *** cpt: it is mostly overprotection now
        //this.toolkitDDS.insert(new AttributeTag("0002,0002"), UID.SOPCLASSSECONDARYCAPTURE);
    	Attribute SOPClassElement1 = AttributeFactory.newAttribute(new AttributeTag(0x0002, 0x0002));
    	SOPClassElement1.setValue(SOPClass.SecondaryCaptureImageStorage);
    	this.toolkitDDS.put(SOPClassElement1);
        //this.toolkitDDS.insert(DCM.E_SOPCLASS_UID, UID.SOPCLASSSECONDARYCAPTURE);
    	Attribute SOPClassElement2 = AttributeFactory.newAttribute(TagFromName.SOPClassUID);
    	SOPClassElement2.setValue(SOPClass.SecondaryCaptureImageStorage);
    	this.toolkitDDS.put(SOPClassElement2);
    	
    }
    
    private boolean isImageCTIOD(){
        
        boolean CT = false;
        String sopClass;
        sopClass = this.toolkitDDS.get(TagFromName.SOPClassUID).getSingleStringValueOrEmptyString();
        if(sopClass.equals(SOPClass.CTImageStorage)){
            CT = true;
        }
        return CT;
    }

    private boolean isImageSCIOD(){
        
        boolean SC = false;
        String sopClass;
        sopClass = this.toolkitDDS.get(TagFromName.SOPClassUID).getSingleStringValueOrEmptyString();
        if(sopClass.equals(SOPClass.SecondaryCaptureImageStorage)){
            SC = true;
        }
        return SC;
    }
    
    private boolean isImageMRIOD(){
        
        boolean MR = false;
        String sopClass;
        sopClass = this.toolkitDDS.get(TagFromName.SOPClassUID).getSingleStringValueOrEmptyString();
        if(sopClass.equals(SOPClass.MRImageStorage)){
           MR = true;
        }
        return MR;
    }
    
    private void updateModalityCode()throws DicomException{
        
        //SRS Patch50 3.2.3.6 Validate/correct there is a single Modality Code in DDS.
                
        CodeStringAttribute modalityCodeElement = null;
        SOPClassModalityCodeMapping mcMap = new SOPClassModalityCodeMapping();
        if(this.toolkitDDS.containsKey(TagFromName.Modality)){
           modalityCodeElement = (CodeStringAttribute)this.toolkitDDS.get(TagFromName.Modality);
           String updateModalityCode;
           if(modalityCodeElement.getVM() > 1){
               updateModalityCode = mcMap.getModalityCode(this.toolkitDDS.get(TagFromName.SOPClassUID).getSingleStringValueOrEmptyString());
               if(updateModalityCode == null){
            	   updateModalityCode = modalityCodeElement.getSingleStringValueOrEmptyString();
               }
               //this.toolkitDDS.insert(DCM.E_MODALITY, updateModalityCode);
               Attribute element = AttributeFactory.newAttribute(TagFromName.Modality);
               element.setValue(updateModalityCode);
               this.toolkitDDS.put(element);
           }
        }
        else{
            String nuModalityCode = "OT";
            if(this.toolkitDDS.containsKey(TagFromName.SOPClassUID)){
                nuModalityCode = mcMap.getModalityCode(this.toolkitDDS.get(TagFromName.SOPClassUID).getSingleStringValueOrEmptyString());
            }
            //this.toolkitDDS.insert(DCM.E_MODALITY, nuModalityCode);
            Attribute element = AttributeFactory.newAttribute(TagFromName.Modality);
            element.setValue(nuModalityCode);
            this.toolkitDDS.put(element);
        }
    }
    
    private boolean isMultiFrameIOD(){
        boolean multiFrameCheck = false;
        if (this.toolkitDDS.get(TagFromName.SOPClassUID).getSingleStringValueOrEmptyString().equals(SOPClass.UltrasoundMultiframeImageStorage)){
            multiFrameCheck = true;
        } 
        else if (this.toolkitDDS.get(TagFromName.SOPClassUID).getSingleStringValueOrEmptyString().equals(SOPClass.NuclearMedicineImageStorage)){
            multiFrameCheck = true;
        }
        else if ((this.toolkitDDS.containsKey(TagFromName.NumberOfFrames)) &&
        	(Integer.decode(this.toolkitDDS.get(TagFromName.NumberOfFrames).getSingleStringValueOrEmptyString())!=1))
            multiFrameCheck = true;
        
        return multiFrameCheck;
    }
    
    private void updateCTIODInformation()throws DicomException{
        
        LOGGER.debug(this.getClass().getName()+": Dicom Toolkit Layer: "+
                "...updating CT IOD using possible CT Parameters.");
        //Parameters parameters = this.getCTParameters();
        Parameters parameters = null;
        //This method determines if the CT image is destined as a CT IOD or a SC IOD.
        //If CT Parameters exist, modify the dataset as CT IOD.
        if(parameters != null){
            LOGGER.debug("Contains valid CT Parameters. Leave it as DICOM CT IOD.");
            this.modifyCTImageToCTIOD(parameters);
        }
        //If CT Parameters do not exist, modify the dataset as SC IOD.
        else{
            this.modifyImageToSCIOD();
        }
    }
    
    private void updateWindowValues()throws DicomException{
        
        //SRS Patch50 3.2.3.10 Calibrating the Window and Level values to match the 
        //  reconstituted image.
        //maximum_pixel_value = (1 << input_bits_per_pixel) - 1;
        
        //Determine Maximum Window and Level
// ** cpt: fixed window width (removed - 1) and center ( added - 1)
    	int maxWindowWidthValue = 1 << this.tgaBitsPerPixel;
        int maxWindowLevelValue = (maxWindowWidthValue/2) - 1;
        boolean wWCSet=false;
        
        //See if the current values in the DDS are larger
        if(this.toolkitDDS.containsKey(TagFromName.WindowWidth)){
            if(this.toolkitDDS.get(TagFromName.WindowWidth).getSingleIntegerValueOrDefault(0) > maxWindowWidthValue){
                //if true, assign new, default values.
                //this.toolkitDDS.insert(DCM.E_WINDOW_WIDTH,maxWindowWidthValue);
                Attribute windowWidthElement = AttributeFactory.newAttribute(TagFromName.WindowWidth);
                windowWidthElement.setValue(maxWindowWidthValue);
                this.toolkitDDS.put(windowWidthElement);
                //this.toolkitDDS.insert(DCM.E_WINDOW_CENTER, maxWindowLevelValue);
                Attribute windowCenterElement = AttributeFactory.newAttribute(TagFromName.WindowCenter);
                windowCenterElement.setValue(maxWindowLevelValue);
                this.toolkitDDS.put(windowCenterElement); 
                this.toolkitDDS.remove(TagFromName.WindowCenterWidthExplanation);
                wWCSet=true;
            }
        }
        else{
            //if no values, assign new, default values.
            //But first check to make sure there is no VOI LUT Sequence.
            if(!this.toolkitDDS.containsKey(TagFromName.VOILUTSequence)){
                //this.toolkitDDS.insert(DCM.E_WINDOW_WIDTH,maxWindowWidthValue);
                Attribute windowWidthElement = AttributeFactory.newAttribute(TagFromName.WindowWidth);
                windowWidthElement.setValue(maxWindowWidthValue);
                this.toolkitDDS.put(windowWidthElement);
                //this.toolkitDDS.insert(DCM.E_WINDOW_CENTER, maxWindowLevelValue);
                Attribute windowCenterElement = AttributeFactory.newAttribute(TagFromName.WindowCenter);
                windowCenterElement.setValue(maxWindowLevelValue);
                this.toolkitDDS.put(windowCenterElement); 
                this.toolkitDDS.remove(TagFromName.WindowCenterWidthExplanation);
	            wWCSet=true;
            }
        }
        if (wWCSet) {
            // optionally adjust W/C explanation: take off 2nd,.. entries
            updateWindowLevelExplanation();
        }
    }

// ** cpt: added method to recompute window/center based on pixel values
    private void reComputeWindowValues()throws DicomException{
        
        //Recalculating the Window and Level values (for SC IODs only).
        //maximum_pixel_value = (1 << input_bits_per_pixel) - 1;
        int pixelMask = (1 << this.tgaBitsPerPixel) - 1;
        
        // find lowest and highest pixel values
        int minValue = pixelMask;
        int maxValue = 0;
        int pixelValue;
        int numPxs = this.tgaRows * this.tgaColumns;
    	if (pixelMask > 0xFF) { // word
    		for (int i=0; i < numPxs; i++ ) {
    			// pixelValue = (imageBuffer.getShort(i) & pixelMask);
    			pixelValue = (((imageBuffer.get((2*i) + 1) & 0xFF) * 256) + (imageBuffer.get(2*i)& 0xFF)) & pixelMask;
	        	if (pixelValue < minValue) minValue = pixelValue;
	        	if (pixelValue > maxValue) maxValue = pixelValue;
	        	pixelValue=0;
    		}
    	} else {
    		for (int i=0; i < numPxs; i++ ) {
    			pixelValue = (imageBuffer.get(i) & pixelMask);       		
	        	if (pixelValue < minValue) minValue = pixelValue;
	        	if (pixelValue > maxValue) maxValue = pixelValue;
	        	pixelValue=0;
    		}
    	}
        imageBuffer.rewind();

        int windowWidthValue;
        int windowLevelValue;
        //Determine Maximum Window and Level
        if (maxValue > (minValue + 1)) {
        	windowWidthValue = maxValue - minValue;
            windowLevelValue = minValue + (windowWidthValue/2);
        } else {
        	windowWidthValue = 1 << this.tgaBitsPerPixel;
            windowLevelValue = (windowWidthValue/2) - 1;
        }
        
        // assign new, default values.
        //this.toolkitDDS.insert(DCM.E_WINDOW_WIDTH, windowWidthValue);
        Attribute windowWidthElement = AttributeFactory.newAttribute(TagFromName.WindowWidth);
        windowWidthElement.setValue(windowWidthValue);
        this.toolkitDDS.put(windowWidthElement);
        //this.toolkitDDS.insert(DCM.E_WINDOW_CENTER, windowLevelValue);
        Attribute windowCenterElement = AttributeFactory.newAttribute(TagFromName.WindowCenter);
        windowCenterElement.setValue(windowLevelValue);
        this.toolkitDDS.put(windowCenterElement);        
        // optionally adjust W/C explanation: take off 2nd,.. entries
        updateWindowLevelExplanation();
    }
    
//  ** cpt: added method to optionally adjust W/C explanation: take off 2nd,.. entries
//          must be called only if W/L was changed
    private void updateWindowLevelExplanation()throws DicomException{
    	
        if (this.toolkitDDS.containsKey(TagFromName.WindowCenterWidthExplanation)){
        	// take first value only
        	String wCExpl=this.toolkitDDS.get(TagFromName.WindowCenterWidthExplanation).getSingleStringValueOrEmptyString();
        	// replace old value(s) with first value
	        //this.toolkitDDS.insert(DCM.E_WINDOW_CENTER_WIDTH_EXPL, wCExpl);
	        Attribute element = AttributeFactory.newAttribute(TagFromName.WindowCenterWidthExplanation);
	        element.setValue(wCExpl);
	        this.toolkitDDS.put(element);
        }    	
    }
    
    private void updateMultiFrameInfoToSingleFrameInfo()throws DicomException{
        //this.toolkitDDS.insert(DCM.E_NUMBER_OF_FRAMES,1);
    	Attribute element = AttributeFactory.newAttribute(TagFromName.NumberOfFrames);
    	element.setValue(1);
    	this.toolkitDDS.put(element);
        this.updateSOPClassToSecondaryCapture(); // *** cpt: must be updated with MF SC IODs
    }
    
    private void checkVendorCompression(){
        //Add code to check in comments field from GE PACS.
        //Here is the information for GE PACS archived images.
        //0008,2111|Derivation Description^ST|1,1|Non-reversible compressed image: For reference only [99]

        try{
        	//VERIFY Should add if statement to make sure the element exists before calling it.
        	if(this.toolkitDDS.containsKey(new AttributeTag(0x0008, 0x2111))){
        		String compressionElement = this.toolkitDDS.get(new AttributeTag(0x0008,0x2111)).getSingleStringValueOrEmptyString();
        		String constant = "Non-reversible compressed image: For reference only [99]";
        		if(compressionElement.equalsIgnoreCase(constant)){
        			//set Lossy Image Compression field to 01.
        			//this.toolkitDDS.insert(DCM.E_LOSSY_IMAGE_COMPRESSION,"01");  // VR=CS - coded string!
        			Attribute element = AttributeFactory.newAttribute(TagFromName.LossyImageCompression);
        			element.setValue("01");
        			//Change Image Type to Derived.
        			this.updateImageTypeToDerived();
        		}
        	}
        }
        catch (DicomException dcse){
            return;
        }
    }
    
    private void updatesForAllModalities()throws DicomException{
        //Convert Samples Per Pixel field to 1.
        //this.toolkitDDS.insert(DCM.E_SAMPLES_PER_PIXEL,1);
        Attribute samplesPerPixelElement = AttributeFactory.newAttribute(TagFromName.SamplesPerPixel);
        samplesPerPixelElement.setValue(1);
        this.toolkitDDS.put(samplesPerPixelElement);
        //Change Pixel Representation to 0000H.
        //SRS Patch50 3.2.3.7 Correct Pixel Representation.
        //this.toolkitDDS.insert(DCM.E_PIXEL_REPRESENTATION, 0);
        Attribute pixelRepresentationElement = AttributeFactory.newAttribute(TagFromName.SamplesPerPixel);
        pixelRepresentationElement.setValue(0);
        this.toolkitDDS.put(pixelRepresentationElement);        
        //Change Photometric to Monochrome2.
        this.changePhotometricInterpretation();
    }
    
    
    private Parameters getCTParameters(){
        String parameterString = "";
        Parameters acceptedParameters = null;
        
        //1.  Get the proper DCMTOTGA parameters if pixel depth is greater than 8bits.
        if(this.tgaBitsPerPixel > 8){
            //a.  If exist, use the DCMTOTGA parameters from the top section of the 
            //  Text file.  This starts appearing with Patch 50.
            parameterString = this.originalPixelDataInfo.getDcmtotgaParameters();
            if(!(parameterString == null)){
                LOGGER.debug("The referenced Text File contains DCMTOTGA Parameters.");
                try{
                	acceptedParameters = new Parameters(parameterString);
                	if(acceptedParameters.isDICOMParameterSet()){
                		LOGGER.warn(this.getClass().getName()+": DICOM Toolkit layer: \n"+
                				"The DCMTOTGA Parameter contains <DICOM>.  This is not valid "+
                				"for a Targa image.");
                		acceptedParameters = null;
                	}
                	return acceptedParameters;
                }
                catch(ParameterDecompositionException pdX){
                	LOGGER.warn(this.getClass().getName()+": DICOM Toolkit layer: \n"+
                			"Could not decompose DCMTOTGA Parameters from referenced Text file.");
                	return null;
                }
            }
            
            try{
                //c.  If CT Image is newer than the date of the CT Historical Settings 
                //  file, use the DCMTOTGA parameters from the current Modality.dic global.
                
                //Get the CT_Parameters file Timestamp.  Convert to a Date.
                String ctParameterFileTimeStamp = DicomGatewayConfiguration.getInstance().getCTParametersTimeStamp();
                LOGGER.debug("CT Parameter Date Stamp: " + ctParameterFileTimeStamp);
                // SimpleDateFormat filePattern = new SimpleDateFormat("d-MMM-yyyy");
                SimpleDateFormat filePattern = new SimpleDateFormat("d-MMM-yyyy hh:mm:ss");
                Date ctParamFileDate;
                if(!ctParameterFileTimeStamp.equals("")){
                    ctParamFileDate = filePattern.parse(ctParameterFileTimeStamp);
                }
                else{
                    LOGGER.debug("No CT Parameter file Time Stamp was found.");
                   return null;
                }
                                
                //Get the Study Date from the DDS.  Convert to a Date.
                String studyDateString = this.toolkitDDS.get(TagFromName.StudyDate).getSingleStringValueOrEmptyString();
                LOGGER.debug("CT Image Study Date: "+studyDateString);
                SimpleDateFormat dicomPattern = new SimpleDateFormat("yyyyMMdd");
                Date studyDate = dicomPattern.parse(studyDateString);
                ModalityDicInfo deviceInfo = new ModalityDicInfo();
                deviceInfo.setManufacturer(this.toolkitDDS.get(TagFromName.Manufacturer).getSingleStringValueOrEmptyString().trim());
                deviceInfo.setModel(this.toolkitDDS.get(TagFromName.ManufacturerModelName).getSingleStringValueOrEmptyString().trim());
                deviceInfo.setModalityCode(this.toolkitDDS.get(TagFromName.Modality).getSingleStringValueOrEmptyString());
                LOGGER.debug("Mfg: " + deviceInfo.getManufacturer() +
                		 "   Model: "+deviceInfo.getModel() +
                		 "   Modality: " + deviceInfo.getModalityCode());
                if(studyDate.after(ctParamFileDate) || studyDate.equals(ctParamFileDate)){
                    //If the CT_Parmeters file is older, look in Modality.dic and extract 
                    //  the correct parameters.
                    Vector<ModalityDicInfo> modalityMatchingList;
                    modalityMatchingList = GatewayDictionaryContents.getInstance().getModalityDictionaryEntries();
                    
                    Iterator<ModalityDicInfo> iterator = modalityMatchingList.iterator();
                    while(iterator.hasNext()){
                        ModalityDicInfo temp = (ModalityDicInfo)iterator.next();
                        if(deviceInfo.equals(temp)){
                            //Return parameters from modality.dic file.
                            LOGGER.debug("Found match with Modality.dic file.");
                            acceptedParameters = temp.getDCMTOTGAParameters();
                        }
                    }
                }
                else{
                    //If the CT_Parameters file is newer, continue with the steps below.
                    //b.  If exist, return the DCMTOTGA parameters from the CT Historical 
                    //  Settings file (Ed’s Log).
                    Vector<ParameterDeviceInfo> ctParameterList;
                    ctParameterList = GatewayDictionaryContents.getInstance().getCTParametersList();
                    //CSABA wfp-The Comparator below is not needed.  The collection is already
                    //	sorted before it is passed here.
                    //Create Comparator for sort mechanism.  Have Date in descending order.
                    //  This is needed for searching later.  If this does not work, just revers
                    //  the Iterator while cycling thru the list.
                    //CTParameterComparator sorter = new CTParameterComparator();
                    //Sort list.
                    //Collections.sort(ctParameterList, sorter);
                    //Find match based on Site/Mfg/Model
                    //ParameterDeviceInfo device = (ParameterDeviceInfo) deviceInfo;
                    ParameterDeviceInfo device = new ParameterDeviceInfo();
                    device.setManufacturer(deviceInfo.getManufacturer());
                    device.setModel(deviceInfo.getModel());
                    device.setModalityCode(deviceInfo.getModalityCode());
                    device.setDCMTOTGAParameters(deviceInfo.getDCMTOTGAParameters());
                    
                    if(this.acquisitionSite == null){
                        return null;
                    }
                    device.setSiteID(this.acquisitionSite);
                    Iterator<ParameterDeviceInfo> iterator = ctParameterList.iterator();
                    
                    while(iterator.hasNext()){
                         ParameterDeviceInfo deviceEntry = (ParameterDeviceInfo)iterator.next();
                         if(device.equals(deviceEntry)){
                             Date deviceEntryDate = deviceEntry.getChangeDate();
                             //Compare Date in List to current Study Date.
                             //You are looking for an entry that is same or older.
                             if((studyDate.equals(deviceEntryDate))
                                     ||(studyDate.after(deviceEntryDate))){
                                 //If above condition is met, assign the parameters from list entry to 
                                 //  current deviceInfo.
                                 LOGGER.debug("Found match in CT Parameters file.");
                                 LOGGER.debug("The Entry Date in the CT Parameters file is: " + deviceEntryDate);
                                 acceptedParameters = deviceEntry.getDCMTOTGAParameters();
                                 //I have to return as I find the first match.  I cannot
                                 // cycle through entire list.
                                 break;
                             }
                         }
                    }
                }
                //If no match is found in either search, return null.
                if(acceptedParameters == null){
                    return null;
                }

                //d.  If the DCMOTGA parameters cannot be retrieved by the steps 
                //  above, set to NULL.  This happens automatically if nothing is assigned
                //  to acceptedParameters variable.
                
                //2.  Determine appropriate action if there is a Rxx value (Reduction Factor)
                //  in the DCMTOTGA parameters.
                if(acceptedParameters.containsReductionFactor()){
                    if(acceptedParameters.containsSlash()){
                        //b.  Retrieve the .big image file instead of the .tga image file 
                        //  if there is a slash (“/”) symbol in the DCMTOTGA parameters.
                        //  Set the DCMTOTGA parameters to NULL if no .big image file exists.
                        if(bigFileFlag){
                            acceptedParameters.setFullParameterSet();
                        }
                        else{
                            acceptedParameters = null;
                        }
                        
                    }
                    else{
                        //a.  Set the retrieved DCMTOTGA parameters to NULL if there is no 
                        //  slash (“/”) symbol in the DCMTOTGA parameters.
                        acceptedParameters = null;
                    }
                }
                if(acceptedParameters != null){
                	if(acceptedParameters.isDICOMParameterSet()){
                		LOGGER.warn(this.getClass().getName()+": DICOM Toolkit layer: \n"+
                				"The DCMTOTGA Parameter contains <DICOM>.  This is not valid "+
                				"for a Targa image.");
                		acceptedParameters = null;
                	}
                }
                return acceptedParameters;
            }
            catch(ParseException noparse){
                return null;
            }
       }        
       return null;
    }
    
    private void modifyCTImageToCTIOD(Parameters params) throws DicomException{
        
        
        //SRS Patch50 3.2.3.10 Multiple fixes for CT images.
        //Remove selected fields from CT Image Module: 
        
        //a.  Add Sxxxx variable, if any, to Rescale Intercept.  Subtract the Axxxx variable, 
        //  if any, from the new Rescale Intercept value.  Update the Rescale Intercept field 
        //  with the new Rescale Intercept value.
        int nuRescaleIntercept = 0;
        if(this.toolkitDDS.containsKey(TagFromName.RescaleIntercept)){
            nuRescaleIntercept = this.toolkitDDS.get(TagFromName.RescaleIntercept).getSingleIntegerValueOrDefault(0);
        }
        else{
            nuRescaleIntercept = 0;
        }
        
        if(params.isSubtractionSet()){
            nuRescaleIntercept += params.getSubtractionParameter();
        }
        if(params.isAdditionSet()){
            nuRescaleIntercept -= params.getAdditionParameter();
        }
        
        //this.toolkitDDS.insert(DCM.E_RESCALE_INTERCEPT, nuRescaleIntercept);
        Attribute rescaleInterceptElement = AttributeFactory.newAttribute(TagFromName.RescaleIntercept);
        rescaleInterceptElement.setValue(nuRescaleIntercept);
        this.toolkitDDS.put(rescaleInterceptElement);
        //this.toolkitDDS.insert(DCM.E_RESCALE_SLOPE, 0x01);
        Attribute rescaleSlopeElement = AttributeFactory.newAttribute(TagFromName.RescaleSlope);
        rescaleSlopeElement.setValue(0x01);
        this.toolkitDDS.put(rescaleSlopeElement);        
        //b.  Remove Largest Pixel field.
        this.toolkitDDS.remove(TagFromName.LargestMonochromePixelValue);
        //this.toolkitDDS.remove(TagFromName.LargestValidPixelValue);
        //this.toolkitDDS.remove(TagFromName.LargestPixelValueInSeries);
        //c.  Remove Smallest Pixel field.
        //this.toolkitDDS.remove(TagFromName.SmallestImagePixelValueInPlane);
        //this.toolkitDDS.remove(TagFromName.SmallestValidPixelValue);
        //this.toolkitDDS.remove(TagFromName.SmallestPixelValueInSeries);
        //d.  Remove Largest Image Pixel field.
        this.toolkitDDS.remove(TagFromName.LargestImagePixelValue);
        //e.  Remove Smallest Image Pixel field.
        this.toolkitDDS.remove(TagFromName.SmallestImagePixelValue);
        //f.  Remove Pixel Padding field.
        this.toolkitDDS.remove(TagFromName.PixelPaddingValue);
        //Remove VOI LUT Sequence.
        this.toolkitDDS.remove(TagFromName.VOILUTSequence);
//        //g.  Set the Lossy Compression value to 01 if there is a Sxxxx or Axxxx value 
//        //  in the DCMTOTGA parameters or the original Pixel Representation value is set 
//        //  to 01 (signed data).
        //CSABA wfp-I disagree with your explanation here because this is still a CT IOD.
        // ** cpt: this flag is explicit about lossy compression. 
        // The fact that CT turns to SC is quite enough to emphasize data degradation,
        // but it is not compression
        if(params.isAdditionSet() || params.isSubtractionSet()){
            //this.toolkitDDS.insert(DCM.E_LOSSY_IMAGE_COMPRESSION, "01"); // VR=CS - coded string!
            Attribute lossyCompressionElement = AttributeFactory.newAttribute(TagFromName.LossyImageCompression);
            lossyCompressionElement.setValue("01");
            this.toolkitDDS.put(lossyCompressionElement);
        }
        //h.  Set Pixel Representation value to 00 (unsigned data).
        //this.toolkitDDS.insert(DCM.E_PIXEL_REPRESENTATION, 0x00);
        Attribute pixelRepresentationElement = AttributeFactory.newAttribute(TagFromName.PixelRepresentation);
        pixelRepresentationElement.setValue(0x00);
        this.toolkitDDS.put(pixelRepresentationElement);        
        //i.  Re-calculate Window Width/Center values if Window Width value is greater 
        //  than the pixel depth.
        this.updateWindowValues();
        
        this.updateImageTypeToDerived();
    }
    
    private void modifyImageToSCIOD() throws DicomException{
        
        //SRS Patch50 3.2.3.10 Multiple fixes for CT images.
        //Remove selected fields from CT Image Module
        
    	//this.toolkitDDS.insert(DCM.E_CONVERSION_TYPE, "WSD");
        Attribute conversionTypeElement = AttributeFactory.newAttribute(TagFromName.ConversionType);
        conversionTypeElement.setValue("WSD");
        this.toolkitDDS.put(conversionTypeElement);
        //a.  Remove Rescale Slope field.
        this.toolkitDDS.remove(TagFromName.RescaleSlope);
        //b.  Remove Rescale Intercept field.
        this.toolkitDDS.remove(TagFromName.RescaleIntercept);
        //c.  Change SOP Class value to Secondary Capture.
        this.updateSOPClassToSecondaryCapture();
        //d.  Set Lossy Compression value to 1.
//CSABA wfp-I still disagree with this below, but I will leave it for now.
// ** cpt: this flag is explicit about lossy compression. 
// The fact that IOD turns to SC is quite enough to emphasize data degradation,
// but it is not necessarily compression
//        this.toolkitDDS.insert(DCM.E_LOSSY_IMAGE_COMPRESSION, "01");
        //e.  Set Pixel Representation value to 00 (unsigned data).
        //this.toolkitDDS.insert(DCM.E_PIXEL_REPRESENTATION, 0x00);
        Attribute pixelRepresentationElement = AttributeFactory.newAttribute(TagFromName.PixelRepresentation);
        pixelRepresentationElement.setValue(0x00);
        this.toolkitDDS.put(pixelRepresentationElement);
        //f.  Remove Largest Pixel field.
        this.toolkitDDS.remove(TagFromName.LargestMonochromePixelValue);
        //this.toolkitDDS.remove(TagFromName.LargestMonochromePixelValue);
        //this.toolkitDDS.remove(TagFromName.LargestPixelValueInSeries);
        //g.  Remove Smallest Pixel field.
        //this.toolkitDDS.remove(TagFromName.SmallestMonochromePixelValue);
        //this.toolkitDDS.remove(TagFromName.LargestMonochromePixelValue);
        //this.toolkitDDS.remove(TagFromName.SmallestPixelValueInSeries);
        //h.  Remove Largest Image Pixel field.
        this.toolkitDDS.remove(TagFromName.LargestImagePixelValue);
        //i.  Remove Smallest Image Pixel field.
        this.toolkitDDS.remove(TagFromName.SmallestImagePixelValue);
        //j.  Remove Pixel Padding field.
        this.toolkitDDS.remove(TagFromName.PixelPaddingValue);
        //Remove VOI LUT Sequence.
        this.toolkitDDS.remove(TagFromName.VOILUTSequence);
        //k.  Re-calculate Window Width/Center values if Window Width value is greater 
        //  than the pixel depth
// ** cpt: always recalculate window/center values for secondary capture IODs 
// as rescale slope and intercept is gone 
        this.reComputeWindowValues();
        
        //Alter Image Type field appropriately.
        this.updateImageTypeToDerived();
    }
    
    
    private boolean isPart10DicomFile(String filename){
    	boolean part10 = false;
    		part10 = DicomFileUtilities.isDicomOrAcrNemaFile(filename);
    	return part10;
    }
    
    private short getShortValue(byte[] data){    
	    short value = data[1];
	    value = (short)((value << 8) | data[0]);
	    
	    return value;
    }
    
    private short[] getShortArray(byte[] data){
    	return null;
    }
}

