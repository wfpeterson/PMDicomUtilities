package gov.va.med.imaging.dicom.utils.pixelmed.information;

import java.io.IOException;

import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.TagFromName;

import gov.va.med.imaging.dicom.utilities.exceptions.DicomFileException;
import gov.va.med.imaging.dicom.utilities.information.MetaDataInfo;

public class MetaInfo{

	public MetaInfo() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		if(args.length != 1){
			System.out.print("Not correct number of arguments.\n");
			printUsage();
			System.exit(-1);
		}
		
		String file = args[0];
		
		MetaInfo meta = new MetaInfo();
		try {
			MetaDataInfo info = meta.collectMetaInfo(file);
        	System.out.println("SOP Class UID: "+info.getSopClass());
        	System.out.println("SOP Instance UID: "+info.getSopInstance());
        	System.out.println("Transfer Syntax UID: "+info.getTransferSyntax());
        	System.out.println("Implementation Class UID: "+info.getImplementationClassUID());
        	System.out.println("Implementation Version Name: "+info.getImplementationVersionName());
        	System.out.println("Source AET: "+info.getSourceAETitle());
		} 
		catch (DicomFileException dfX) {
			System.out.println("Failed to read DICOM object file.");
			System.out.println(dfX.getMessage());
			System.exit(-1);
		}
		System.exit(0);
	}
	
	
    public MetaDataInfo collectMetaInfo(String filename) throws DicomFileException {
        
        AttributeList dds = new AttributeList();
        MetaDataInfo info = new MetaDataInfo();
        try{
                dds.read(filename, null, true, true);
                info.setFilename(filename);
                Attribute sopClass = dds.get(TagFromName.MediaStorageSOPClassUID);
                if(sopClass != null){
                	String value = sopClass.getDelimitedStringValuesOrEmptyString();
                	info.setSopClass(value);
                }
                Attribute instance = dds.get(TagFromName.MediaStorageSOPInstanceUID);
                if(instance != null){
                	String value = instance.getDelimitedStringValuesOrEmptyString();
                	info.setSopInstance(value);
                }
                Attribute transferSyntax = dds.get(TagFromName.TransferSyntaxUID);
                if(transferSyntax != null){
                	String value = transferSyntax.getDelimitedStringValuesOrEmptyString();
                	info.setTransferSyntax(value);
                }
                Attribute implementationUID = dds.get(TagFromName.ImplementationClassUID);
                if(implementationUID != null){
                	String value = implementationUID.getDelimitedStringValuesOrEmptyString();
                	info.setImplementationClassUID(value);
                }
                Attribute implementationName = dds.get(TagFromName.ImplementationVersionName);
                if(implementationName != null){
                	String value = implementationName.getDelimitedStringValuesOrEmptyString();
                	info.setImplementationVersionName(value);
                }
                Attribute sourceAET = dds.get(TagFromName.SourceApplicationEntityTitle);
                if(sourceAET != null){
                	String value = sourceAET.getDelimitedStringValuesOrEmptyString();
                	info.setSourceAETitle(value);
                }
                Attribute manufacturer = dds.get(TagFromName.Manufacturer);
                if(manufacturer != null){
                	String value = manufacturer.getDelimitedStringValuesOrEmptyString();
                	info.setManufacturer(value);
                }
                Attribute mfgModel = dds.get(TagFromName.ManufacturerModelName);
                if(mfgModel != null){
                	String value = mfgModel.getDelimitedStringValuesOrEmptyString();
                	info.setModel(value);
                }
                Attribute mfgVersion = dds.get(TagFromName.SoftwareVersions);
                if(mfgVersion != null){
                	String value = mfgVersion.getDelimitedStringValuesOrEmptyString();
                	info.setMfgVersion(value);
                }
                return info;
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
		
    
    private static void printUsage(){
    	System.out.println("Usage: MetaInfo <DICOM Filename>");
    }
	

}
