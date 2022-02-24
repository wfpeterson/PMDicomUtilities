/*
 * Created on May 4, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gov.va.med.imaging.dicom.utils.pixelmed.compare;

import java.io.IOException;

import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.AttributeTag;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.OtherByteAttribute;
import com.pixelmed.dicom.OtherWordAttribute;

import algorithms.va.MeanSquaredError;
import algorithms.va.PeakSignalToNoiseRatio;
import gov.va.med.imaging.dicom.utilities.compare.IDicomPixelDataCompare;
import gov.va.med.imaging.dicom.utilities.compare.PixelDataTypeUtility;
import gov.va.med.imaging.dicom.utilities.exceptions.DicomFileException;
import gov.va.med.imaging.dicom.utilities.exceptions.GeneralDicomException;


/**
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 *
 *
 * @author William Peterson
 *
 */
public class DicomPixelDataCompare implements IDicomPixelDataCompare<AttributeList> {

    int bitsStored = 0;
    int bitsAllocated = 0;
    
    /**
     * Constructor
     *
     * 
     */
    public DicomPixelDataCompare() {
        super();
        // TODO Auto-generated constructor stub
    }

    public static void main(String[] args) {
        IDicomPixelDataCompare<AttributeList> compare = new DicomPixelDataCompare();
        String firstDCMFile = args[0];
        String secondDCMFile = args[1];
        double pSNR;
        try{
            if(args.length != 2 ){
                System.out.println("Requires two arguments.");
                throw new DicomFileException();
            }
            pSNR = compare.compareDICOMPixelData(firstDCMFile, secondDCMFile);
            System.out.println("\nPeak Signal-to-Noise Ratio: "+pSNR+" dB.");
        }
        catch(DicomFileException tgaError){
            System.out.println("Error: Could not calculate Peak Signal To Noise Ratio.");
            tgaError.printStackTrace();
            System.exit(-1);
        } 
        catch (GeneralDicomException dX) {
            System.out.println("Error: Could not calculate Peak Signal To Noise Ratio.");
            dX.printStackTrace();
            System.exit(-1);
		}
        
        System.exit(0);
    }
    
    /* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.compare.IDicomPixelDataCompare#compareDICOMPixelData(java.lang.String, java.lang.String)
	 */
    @Override
	public double compareDICOMPixelData(String dcmFileOne, String dcmFileTwo)
            throws DicomFileException, GeneralDicomException{
        
        AttributeList firstDDS;
        AttributeList secondDDS;        
        firstDDS = this.openFile(dcmFileOne);
        secondDDS = this.openFile(dcmFileTwo);
        
        //System.out.println(firstDDS.toString());
        //System.out.println("\n\n\n\n");
        //System.out.println(secondDDS.toString());
        int[] firstImage;
        int[] secondImage;
        firstImage = this.convertToHounsfieldUnits(firstDDS);
        secondImage = this.convertToHounsfieldUnits(secondDDS);
        
        float mse = MeanSquaredError.eqMSE(firstImage, secondImage);
        System.out.println("MSE: "+mse);
        double result = PeakSignalToNoiseRatio.eqPSNR(mse, this.bitsStored);
        System.out.println("PSNR: "+result);
        return result;
    }
    
    /* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.compare.IDicomPixelDataCompare#openFile(java.lang.String)
	 */
    @Override
	public AttributeList openFile(String dcmFile)throws DicomFileException{
        
        try{
            //Open DICOM file.
            //fill in an AttributeList object.
            AttributeList dds = new AttributeList();
            dds.read(dcmFile);
            //Read BitsAllocate and BitsStored elements.
            if(this.bitsStored == 0){
                
                this.bitsStored = Attribute.getSingleIntegerValueOrDefault(dds, 
                        new AttributeTag(0x0028,0x0101), -1);
                this.bitsAllocated = Attribute.getSingleIntegerValueOrDefault(dds, 
                        new AttributeTag(0x0028,0x0100), -1);

                if(this.bitsStored == -1 || this.bitsAllocated == -1){
                    System.out.println("No Bits Stored element value in first object.");
                    throw new DicomFileException();
                }
            }
            else{
                int secondBitsStored = Attribute.getSingleIntegerValueOrDefault(dds, 
                        new AttributeTag(0x0028,0x0101), -1);
                int secondBitsAllocated = Attribute.getSingleIntegerValueOrDefault(dds, 
                        new AttributeTag(0x0028,0x0100), -1);

                if(secondBitsStored == -1 || secondBitsAllocated == -1){
                    System.out.println("No Bits Stored element value in second object.");
                    throw new DicomFileException();
                }

                if(this.bitsAllocated != secondBitsAllocated){
                    System.out.println("Error: Bits Allocated do not match.  \nCannot " +
                            "compare Pixel Values.");
                    throw new DicomFileException();
                }
                if(this.bitsStored != secondBitsStored){
                        System.out.println("Warning: Bits Stored " +
                                "element values do not match.");
                }   
            }
            //Pass the Pixel Data.     
            //buffer.order(ByteOrder.LITTLE_ENDIAN);
            
            return dds;
        }
        catch(IOException ioe){
            throw new DicomFileException();
        }
        catch(DicomException noFile){
            throw new DicomFileException();
        }
    }
    
    /* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.compare.IDicomPixelDataCompare#convertToHounsfieldUnits(com.pixelmed.dicom.AttributeList)
	 */
    @Override
	public int[] convertToHounsfieldUnits(AttributeList dds) throws GeneralDicomException{
        
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
            throw new GeneralDicomException("Not a valid CT image.");
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
    
    /* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.compare.IDicomPixelDataCompare#convertPixelDataToIntegers(com.pixelmed.dicom.AttributeList)
	 */
    @Override
	public int[] convertPixelDataToIntegers(AttributeList dds)throws GeneralDicomException{
        
        
        //Determine if Pixel Data is OB or OW.
        String vr = dds.get(new AttributeTag(0x7FE0, 0x0010)).getVRAsString();
        int pixelRepresentation = dds.get(new AttributeTag(0x0028, 0x0103)).getSingleIntegerValueOrDefault(-1);

        try{
	        if(vr.equals("OB")){
	            if(pixelRepresentation == 1){
	                int[] pixelByte;
	                OtherByteAttribute obPixelData = (OtherByteAttribute) dds.get(new AttributeTag(0x7FE0, 0x0010));
	                pixelByte = obPixelData.getIntegerValues();
	                return pixelByte;
	            }
	            else{
	                byte[] pixelByte;
	                OtherByteAttribute obPixelData = (OtherByteAttribute) dds.get(new AttributeTag(0x7FE0, 0x0010));
	                pixelByte = obPixelData.getByteValues();
	                int[] pixelInteger = PixelDataTypeUtility.byteToInteger(pixelByte);
	                return pixelInteger;
	            }
	        }
	       if(vr.equals("OW")){
	           if(pixelRepresentation == 1){
	               int[] pixelWord;
	               OtherWordAttribute owPixelData = (OtherWordAttribute) dds.get(new AttributeTag(0x7FE0, 0x0010));
	               pixelWord = owPixelData.getIntegerValues();
	               return pixelWord;
	           }
	           else{
	               short[] pixelWord;
	               OtherWordAttribute owPixelData = (OtherWordAttribute) dds.get(new AttributeTag(0x7FE0, 0x0010));
	               pixelWord = owPixelData.getShortValues();
	               int[] pixelInteger = PixelDataTypeUtility.shortToInteger(pixelWord);
	               return pixelInteger;
	           }
	       }
        }
        catch(DicomException dX){
        	throw new GeneralDicomException(dX);
        }
       System.out.println("Error: Cannot convert Pixel Data to Integer values");
       return null;
   }

}