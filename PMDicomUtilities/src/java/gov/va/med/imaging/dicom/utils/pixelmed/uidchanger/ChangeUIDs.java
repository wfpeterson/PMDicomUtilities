package gov.va.med.imaging.dicom.utils.pixelmed.uidchanger;

import java.io.File;
import java.io.IOException;

import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.TagFromName;
import com.pixelmed.dicom.TransferSyntax;
import com.pixelmed.dicom.UniqueIdentifierAttribute;

import gov.va.med.imaging.dicom.utilities.controller.DCMFilenameFilter;
import gov.va.med.imaging.dicom.utilities.exceptions.ReadFileException;
import gov.va.med.imaging.dicom.utilities.utils.TextUtil;

public class ChangeUIDs {

	private String uidPrefix = "";
	private String filePath = "";
	private File[] dcmfiles;
	
	public ChangeUIDs() {
		// TODO Auto-generated constructor stub
	}
	
	public ChangeUIDs(String arg){
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ChangeUIDs changer = new ChangeUIDs();
		if(args.length < 1){
			System.out.println("No arguments passed.");
			System.exit(-1);
		}
		//Receive argument for config file.
		String file = args[0];
		
		try{
			changer.parseConfigFile(file);
			changer.getFilesFromFolder();
			changer.changeTheUIDs();
			System.out.println("Success!");
		}
		catch(ReadFileException rfX){
			System.out.println("Failed to read one or more files.");
			System.out.println(rfX.getMessage());
		}
		catch(DicomException dX){
			System.out.println("Failed to read one or more files.");
			System.out.println(dX.getMessage());
		}
	}

	
	private void getFilesFromFolder()throws ReadFileException{
		
		File directory = new File(this.filePath);
        if(!directory.isDirectory()){
        	throw new ReadFileException(this.filePath+" is not a folder.");
        }
    	this.dcmfiles = directory.listFiles(new DCMFilenameFilter());
    }
	
	private void parseConfigFile(String configFile) throws ReadFileException, DicomException{
		System.out.println("Configuring from file "+configFile+".");
		TextUtil parser = new TextUtil();
		parser.openTextFile(configFile);
		String line;
		while((line = parser.getNextTextLine()) != null){
			this.uidPrefix = parser.piece(line, '|', 1);
			System.out.println("UID: "+this.uidPrefix);
			this.filePath = parser.piece(line, '|', 2);
			System.out.println("File Path: "+this.filePath);
			if(filePath.isEmpty()){
				throw new ReadFileException("No Image Folder given.");
			}
		}
	}
	
	private void changeTheUIDs() throws ReadFileException, DicomException{
		try{
			String studyUID = this.uidPrefix.concat(".7");
			String seriesUID = studyUID.concat(".1");
			
			for(int i=0; i<this.dcmfiles.length; i++){
				File dcmFile = this.dcmfiles[i];
				String filename = dcmFile.getAbsolutePath();
                AttributeList list = new AttributeList();
                list.read(filename, null, true, true);
                
                Attribute transfer = list.get(TagFromName.TransferSyntaxUID);
                String transferUID = transfer.getSingleStringValueOrEmptyString();

                list.remove(TagFromName.StudyInstanceUID);
                Attribute studyInstance = new UniqueIdentifierAttribute(TagFromName.StudyInstanceUID);
                studyInstance.addValue(studyUID);
                list.put(TagFromName.StudyInstanceUID, studyInstance);
                
                list.remove(TagFromName.SeriesInstanceUID);
                Attribute seriesInstance = new UniqueIdentifierAttribute(TagFromName.SeriesInstanceUID);
                seriesInstance.addValue(seriesUID);
                list.put(TagFromName.SeriesInstanceUID, seriesInstance);
                
                Integer ext = new Integer(i+1);
                String sopUID = seriesUID+"."+ext.toString();
                list.remove(TagFromName.SOPInstanceUID);
                Attribute sopInstance = new UniqueIdentifierAttribute(TagFromName.SOPInstanceUID);
                sopInstance.addValue(sopUID);
                list.put(TagFromName.SOPInstanceUID, sopInstance);

                //list.remove(TagFromName.MediaStorageSOPInstanceUID);
                //Attribute mediasopInstance = new UniqueIdentifierAttribute(TagFromName.MediaStorageSOPInstanceUID);
                //sopInstance.addValue(sopUID);
                //list.put(TagFromName.MediaStorageSOPInstanceUID, mediasopInstance);

                list.write(filename, TransferSyntax.ExplicitVRLittleEndian, true, true);                
			}
		}
		catch(IOException ioX){
			System.out.println("Error: "+ioX);
		}
				
	}
}
