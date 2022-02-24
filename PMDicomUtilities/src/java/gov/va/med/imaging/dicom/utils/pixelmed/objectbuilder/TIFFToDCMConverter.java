/**
 * Created on Jun 28, 2008
 */
package gov.va.med.imaging.dicom.utils.pixelmed.objectbuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.stream.FileImageInputStream;

import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.AttributeTag;
import com.pixelmed.dicom.CodeStringAttribute;
import com.pixelmed.dicom.DateAttribute;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.FileMetaInformation;
import com.pixelmed.dicom.IntegerStringAttribute;
import com.pixelmed.dicom.LongStringAttribute;
import com.pixelmed.dicom.OtherByteAttribute;
import com.pixelmed.dicom.PersonNameAttribute;
import com.pixelmed.dicom.ShortStringAttribute;
import com.pixelmed.dicom.TagFromName;
import com.pixelmed.dicom.TimeAttribute;
import com.pixelmed.dicom.TransferSyntax;
import com.pixelmed.dicom.UniqueIdentifierAttribute;
import com.pixelmed.dicom.UnsignedShortAttribute;

import gov.va.med.imaging.dicom.utilities.exceptions.TIFFFileNotFoundException;

/**
 *
 *
 *
 * @author William Peterson
 *
 */
public class TIFFToDCMConverter {
	
	
	/**
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		
		try{
			TIFFToDCMConverter converter = new TIFFToDCMConverter();
			byte[] image = converter.openTIFF("c:\\patches\\patch54\\epson200.tif");
			AttributeList dds = converter.createDDS(image);
			converter.saveObject(dds);
			
			System.out.println("Completed without known exception.");
			System.exit(0);
		}
		catch(TIFFFileNotFoundException tfnfX){
			System.out.println("Did not find or open TIFF File. /n"+tfnfX.getMessage());
			tfnfX.printStackTrace();
			System.exit(-1);
		}
		catch(DicomException dX){
			System.out.println("Dicom Exception while creating/saving DicomDataSet. /n"+dX.getMessage());
			dX.printStackTrace();
			System.exit(-1);
		}
		catch(IOException ioX){
			System.out.println("IO thrown while attempting to save to Dicom File. /n"+ioX.getMessage());
			ioX.printStackTrace();
			System.exit(-1);
		}

	}

	private byte[] openTIFF(String filename)
					throws TIFFFileNotFoundException{
		try{
			FileImageInputStream in = new FileImageInputStream(new File(filename));
			//int len = (int)in.length() - 186;
			int len = (2332 * 1694) / 8;
			byte[] pixelData = new byte[len];
			in.seek(186);
			int result = in.read(pixelData, 0, len);
			System.out.println("Result: "+result);
			in.close();
			
			return pixelData;
		}
		catch(FileNotFoundException fnfX){
			throw new TIFFFileNotFoundException();
		}
		catch(IOException ioX){
			throw new TIFFFileNotFoundException();
		}
	}
	
	private AttributeList createDDS(byte[] imageData) throws DicomException{
		AttributeList dds = new AttributeList();
		{AttributeTag t = TagFromName.PatientName; Attribute a = new PersonNameAttribute(t); a.addValue("IMAGE^PATIENT"); dds.put(t,a);}
		{AttributeTag t = TagFromName.PatientID; Attribute a = new LongStringAttribute(t); a.addValue("000-00-0001"); dds.put(t,a);}
		{AttributeTag t = TagFromName.PatientBirthDate; Attribute a = new DateAttribute(t); a.addValue("19540304"); dds.put(t,a);}
		{AttributeTag t = TagFromName.PatientSex; Attribute a = new CodeStringAttribute(t); a.addValue("M"); dds.put(t,a);}
		{AttributeTag t = TagFromName.StudyID; Attribute a = new ShortStringAttribute(t); a.addValue("191"); dds.put(t,a);}
		{AttributeTag t = TagFromName.StudyDescription; Attribute a = new LongStringAttribute(t); a.addValue("Document"); dds.put(t,a);}
		{AttributeTag t = TagFromName.StudyDate; Attribute a = new DateAttribute(t); a.addValue("20080101"); dds.put(t,a);}
		{AttributeTag t = TagFromName.StudyTime; Attribute a = new TimeAttribute(t); a.addValue("120100"); dds.put(t,a);}
		{AttributeTag t = TagFromName.ReferringPhysicianName; Attribute a = new PersonNameAttribute(t); a.addValue("IMAGE^PROVIDER"); dds.put(t,a);}
		{AttributeTag t = TagFromName.SeriesNumber; Attribute a = new IntegerStringAttribute(t); a.addValue("1"); dds.put(t,a);}
		{AttributeTag t = TagFromName.Modality; Attribute a = new CodeStringAttribute(t); a.addValue("OT"); dds.put(t,a);}
		{AttributeTag t = TagFromName.InstanceNumber; Attribute a = new IntegerStringAttribute(t); a.addValue("1"); dds.put(t,a);}
		{AttributeTag t = TagFromName.ImageType; Attribute a = new CodeStringAttribute(t); a.addValue("DERIVED"); dds.put(t,a);}
		{AttributeTag t = TagFromName.ConversionType; Attribute a = new CodeStringAttribute(t); a.addValue("SD"); dds.put(t,a);}
		{AttributeTag t = TagFromName.NumberOfFrames; Attribute a = new IntegerStringAttribute(t); a.addValue("1"); dds.put(t,a);}
		{AttributeTag t = TagFromName.StudyInstanceUID; Attribute a = new UniqueIdentifierAttribute(t); a.addValue("1.2.840.113754.1.4.660.1222365.8751.1.102198.191"); dds.put(t,a);}
		{AttributeTag t = TagFromName.SeriesInstanceUID; Attribute a = new UniqueIdentifierAttribute(t); a.addValue("1.2.840.113754.1.4.660.1222365.8751.1.102198.191.1"); dds.put(t,a);}
		{AttributeTag t = TagFromName.SOPInstanceUID; Attribute a = new UniqueIdentifierAttribute(t); a.addValue("1.2.840.113754.1.4.660.1222365.8751.1.102198.191.1.1"); dds.put(t,a);}
		{AttributeTag t = TagFromName.SOPClassUID; Attribute a = new UniqueIdentifierAttribute(t); a.addValue("1.2.840.10008.5.1.4.1.1.7.1"); dds.put(t,a);}
		//{AttributeTag t = TagFromName.NominalScannedPixelSpacing; Attribute a = new DecimalStringAttribute(t); a.addValue("1\1"); dds.put(t,a);}
		{AttributeTag t = TagFromName.NumberOfFrames; Attribute a = new IntegerStringAttribute(t); a.addValue("1"); dds.put(t,a);}
		{AttributeTag t = TagFromName.BurnedInAnnotation; Attribute a = new CodeStringAttribute(t); a.addValue("NO"); dds.put(t,a);}
		{AttributeTag t = TagFromName.AccessionNumber; Attribute a = new ShortStringAttribute(t); a.addValue("010108-133"); dds.put(t,a);}
				
		{AttributeTag t = TagFromName.SamplesPerPixel; Attribute a = new UnsignedShortAttribute(t); a.addValue(1); dds.put(t,a);}
		{AttributeTag t = TagFromName.PhotometricInterpretation; Attribute a = new CodeStringAttribute(t); a.addValue("MONOCHROME2"); dds.put(t,a);}
		{AttributeTag t = TagFromName.BitsAllocated; Attribute a = new UnsignedShortAttribute(t); a.addValue(1); dds.put(t,a);}
		{AttributeTag t = TagFromName.BitsStored; Attribute a = new UnsignedShortAttribute(t); a.addValue(1); dds.put(t,a);}
		{AttributeTag t = TagFromName.HighBit; Attribute a = new UnsignedShortAttribute(t); a.addValue(0); dds.put(t,a);}
		{AttributeTag t = TagFromName.PixelRepresentation; Attribute a = new UnsignedShortAttribute(t); a.addValue(0); dds.put(t,a);}
		{AttributeTag t = TagFromName.Rows; Attribute a = new UnsignedShortAttribute(t); a.addValue(2332); dds.put(t,a);}
		{AttributeTag t = TagFromName.Columns; Attribute a = new UnsignedShortAttribute(t); a.addValue(1694); dds.put(t,a);}
		//{AttributeTag t = TagFromName.RescaleIntercept; Attribute a = new DecimalStringAttribute(t); a.addValue("0"); dds.put(t,a);}
		//{AttributeTag t = TagFromName.RescaleSlope; Attribute a = new DecimalStringAttribute(t); a.addValue("1"); dds.put(t,a);}
		//{AttributeTag t = TagFromName.RescaleType; Attribute a = new LongStringAttribute(t); a.addValue("US"); dds.put(t,a);}
		
		{AttributeTag t = TagFromName.PixelData; Attribute a = new OtherByteAttribute(t); a.setValues(imageData); dds.put(t,a);}

		return dds;
	}
	
	private void saveObject(AttributeList dds)throws IOException, DicomException{
		
		FileMetaInformation.addFileMetaInformation(dds,TransferSyntax.ExplicitVRLittleEndian,"VISTA_IMAGING");
	    dds.write("c:\\patches\\patch54\\epson.dcm",TransferSyntax.ExplicitVRLittleEndian,true,true);
	}
}
