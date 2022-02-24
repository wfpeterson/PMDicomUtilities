/*
		 * Created on Dec 21, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gov.va.med.imaging.dicom.utils.pixelmed.model;

import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.AttributeTag;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.OtherByteAttribute;
import com.pixelmed.dicom.OtherWordAttribute;
import com.pixelmed.dicom.TagFromName;
import com.pixelmed.dicom.TransferSyntax;

import gov.va.med.imaging.dicom.utilities.exceptions.PixelDataException;

/**
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 *
 *
 * @author William Peterson
 *
 */
public class PixelData {

    /**
     * Constructor
     *
     * 
     */
    public PixelData() {
        super();
        // TODO Auto-generated constructor stub
    }

    public int[] convertPixelDataToIntegers(AttributeList dds) throws PixelDataException{
        
        //Retrieve Transfer Syntax.
        String transfer = Attribute.getSingleStringValueOrNull(dds, TagFromName.TransferSyntaxUID);
        boolean acceptedTransfer = false;
        if(transfer.equals(TransferSyntax.ExplicitVRBigEndian)){
            acceptedTransfer = true;
        }
        if(transfer.equals(TransferSyntax.ExplicitVRLittleEndian)){
            acceptedTransfer = true;
        }
        if(transfer.equals(TransferSyntax.ImplicitVRLittleEndian)){
            acceptedTransfer = true;
        }
        //Retrieve Photometric Interpretation value.
        String photometric = Attribute.getSingleStringValueOrNull(dds, 
                TagFromName.PhotometricInterpretation);
        boolean acceptedPhotometric = false;
        if(photometric.equalsIgnoreCase("Monochrome1")){
            acceptedPhotometric = true;
        }
        if(photometric.equalsIgnoreCase("Monochrome2")){
            acceptedPhotometric = true;
        }
        //Retrieve Bits Stored value.
        int bitsStored = Attribute.getSingleIntegerValueOrDefault(dds, TagFromName.BitsStored, -1);
        //Retrieve Pixel Representation value.
        if(bitsStored == -1){
            throw new PixelDataException("DataSet does not contain a valid Bits Stored value.");
        }
        //Only create the histogram IF the Transfer Syntax is not a compression type.
        if(acceptedTransfer == false){
            throw new PixelDataException("Can only perform a Histogram on non-compressed pixel data.");
        }
        //Only create the histogram IF the Photometric Interpretation is Monochrome1 or Monochrome2.
        if(acceptedPhotometric == false){
            throw new PixelDataException("Can only perform a Histogram on Greyscale pixel data.");
        }
        //Determine if Pixel Data is OB or OW.
        String vr = dds.get(new AttributeTag(0x7FE0, 0x0010)).getVRAsString();
        int pixelRepresentation = dds.get(new AttributeTag(0x0028, 0x0103)).getSingleIntegerValueOrDefault(-1);

        if(vr.equals("OB")){
            if(pixelRepresentation == 1){
                byte[] pixelByte = null;
                OtherByteAttribute obPixelData = (OtherByteAttribute) dds.get(new AttributeTag(0x7FE0, 0x0010));
                try {
					pixelByte = obPixelData.getByteValues();
				} catch (DicomException e) {
					throw new PixelDataException(e.getMessage());
				}
                int[] pixelInteger = this.signedByteToInteger(pixelByte);
                return pixelInteger;
                //return pixelByte;
            }
            else{
                byte[] pixelByte = null;
                OtherByteAttribute obPixelData = (OtherByteAttribute) dds.get(new AttributeTag(0x7FE0, 0x0010));
                try {
					pixelByte = obPixelData.getByteValues();
				} catch (DicomException e) {
					throw new PixelDataException(e.getMessage());
				}
                int[] pixelInteger = this.unsignedByteToInteger(pixelByte);
                return pixelInteger;
            }
        }
       if(vr.equals("OW")){
           if(pixelRepresentation == 1){
               //int[] pixelWord;
               short [] pixelWord = null;
               OtherWordAttribute owPixelData = (OtherWordAttribute) dds.get(new AttributeTag(0x7FE0, 0x0010));
               //pixelWord = owPixelData.getIntegerValues();
               try {
				pixelWord = owPixelData.getShortValues();
			} catch (DicomException e) {
				throw new PixelDataException(e.getMessage());
			}
               int[]pixelInteger = this.signedShortToInteger(pixelWord);
               //return pixelWord;
               return pixelInteger;
           }
           else{
               short[] pixelWord = null;
               OtherWordAttribute owPixelData = (OtherWordAttribute) dds.get(new AttributeTag(0x7FE0, 0x0010));
               try {
				pixelWord = owPixelData.getShortValues();
			} catch (DicomException e) {
				throw new PixelDataException(e.getMessage());
			}
               int[] pixelInteger = this.unsignedShortToInteger(pixelWord);
               return pixelInteger;
           }
       }
       System.out.println("Error: Cannot convert Pixel Data to Integer values");
       return null;
   }

    public int[] convertToHounsfieldUnits(AttributeList dds) throws PixelDataException, DicomException{
        
        //Declare ByteBuffer for output.
        int[] buffer;
        //Get the Intercept element value.
        int intercept = Attribute.getSingleIntegerValueOrDefault(dds, 
                new AttributeTag(0x0028, 0x1052), -1);
        //Get the Slope element value.
        int slope = Attribute.getSingleIntegerValueOrDefault(dds, 
                new AttributeTag(0x0028, 0x1053), -1); 
        if(intercept == -1 || slope == -1){
            System.out.println("Error: Not a valid CT Image.  Does not contain Slope and Intercept.");
            throw new DicomException("Not a valid CT image.");
        }
        buffer = this.convertPixelDataToIntegers(dds);
        //Loop based on number of bytes in Pixel Data.
        for(int i=0; i<buffer.length; i++){
           //Apply the algorithm to convert the pixel values to Hounsfield Units.
            //  Hounsfield Units are signed.  Need to determine how to keep this 
            //  through the ByteBuffer.
            int hounsfield = (buffer[i] * slope) - intercept;
            //Insert calculated value and insert into the array object.
            buffer[i] = hounsfield;
        }
        return buffer;
    }

    private int[] signedByteToInteger(byte[] pixelDataInBytes){
        
        int[] pixelValues = new int[pixelDataInBytes.length];
        for(int i=0; i<pixelDataInBytes.length; i++){        
            pixelValues[i] = (int)pixelDataInBytes[i];
        }
        return pixelValues;
    }

    private int[] unsignedByteToInteger(byte[] pixelDataInBytes){
        
        int[] pixelValues = new int[pixelDataInBytes.length];
        for(int i=0; i<pixelDataInBytes.length; i++){        
            pixelValues[i] = (int)(0x000000FF & pixelDataInBytes[i]);
        }
        return pixelValues;
    }

    private int[] signedShortToInteger(short[] pixelDataInShorts){

        int[] pixelValues = new int[pixelDataInShorts.length];
        for(int i=0; i<pixelDataInShorts.length; i++){        
            pixelValues[i] = (int)pixelDataInShorts[i];
        }
        return pixelValues;
    }

    private int[] unsignedShortToInteger(short[] pixelDataInShorts){

        int[] pixelValues = new int[pixelDataInShorts.length];
        for(int i=0; i<pixelDataInShorts.length; i++){
            pixelValues[i] = (int)(0x0000FFFF & pixelDataInShorts[i]);
        }
        return pixelValues;
    }

}
