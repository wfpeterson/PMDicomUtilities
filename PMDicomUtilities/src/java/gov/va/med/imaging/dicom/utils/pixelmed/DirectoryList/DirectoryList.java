package gov.va.med.imaging.dicom.utils.pixelmed.DirectoryList;

 
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.AttributeTag;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.DicomInputStream;



/**
 * @author Dezso Csipo
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
/**
 * @author vhaiswcsipod
 *
 */
public class DirectoryList
{
    private static final String UNKNOWN = "Unknown|";
	private static final String SEPARATOR = "|";
	String args[];

	public DirectoryList (String args[])
    {
        this.args = args;
    }
    public static void main(String[] args) throws Exception
    /*
     * Main entry executed as a command line procedure. 
     * The program picks up all the files in a directory and if they are truly DICOM files it will 
     * list the following items for each one of them in a Comma Separated Value file suitable for Excel 
     * import.
     * 
     * 	Patient Name 		(0x0010,0x0010)
     *  Patient ID			(0x0010,0x0020)
     *  Patient Sex			(0x0010,0x0040)
     *  Patient birth date	(0x0030,0x0030)
     *  Patient birth time  (0x0010,0x0032)
     *  Accession number	(0x0008,0x0050)
     *  Req Procedure ID	(0x0040,0x1001)	
     *  Study ID			(0x0020,0x0010)
     *  Modality			(0x0008,0x0050)
     *  SOP Class UID		(0x0008,0x0016)
     *  Study Instance UID	(0x0020,0x000d)
     *  Series Instance UID	(0x0020,0x000e)
     *  SOP Instance UID	(0x0008,0x0018)
     *  File Name			<file name from the file system>
     *  
     *  In the <Output File> each line will contain the values only separated with ","
     */
    {
        
        if(args.length < 2)
            System.out.println("Usage: DirectoryList <Source folder> <output file>");
         
        try
        {
            Normal_List(args);
            //abort_scu(args);
        }
        catch (Exception e)
        {
            System.out.println("Normal_List method failed");
            e.printStackTrace();
        }

    }
    private static void Normal_List(String args[]) throws Exception
    {
        String SourceFolder = "";
        String OutputFile = "";
        String outString = "";
        
        SourceFolder = args[0];
        OutputFile = args[1];
        FileWriter Outfile = new FileWriter (OutputFile);
        AttributeList al = new AttributeList();
        
        /*
         * 	Patient Name 		(0x0010,0x0010)
         *  Patient ID			(0x0010,0x0020)
         *  Patient Sex			(0x0010,0x0040)
         *  Patient birth date	(0x0030,0x0030)
         *  Patient birth time  (0x0010,0x0032)
         *  Accession number	(0x0008,0x0050)
         *  Req Procedure ID	(0x0040,0x1001)	
         *  Study ID			(0x0020,0x0010)
         *  Modality			(0x0008,0x0060)
         *  SOP Class UID		(0x0008,0x0016)
         *  Study Instance UID	(0x0020,0x000d)
         *  Series Instance UID	(0x0020,0x000e)
         *  SOP Instance UID	(0x0008,0x0018)
         *  Transfer Syntax UID (0x0002,0x0010)
         *  File Name			<file name from the file system>
         */
        AttributeTag PatientNameTag = new AttributeTag(0x0010,0x0010);
        Attribute PatientName;
        AttributeTag PatientIDTag = new AttributeTag(0x0010,0x0020);
        Attribute PatientID;
        AttributeTag PatientSexTag = new AttributeTag(0x0010,0x0040);
        Attribute PatientSex;
        AttributeTag PatientBirthDateTag = new AttributeTag(0x0010,0x0030);
        Attribute PatientBirthDate;
        AttributeTag PatientBirthTimeTag = new AttributeTag(0x0010,0x0032);
        Attribute PatientBirthTime;
        AttributeTag AccessionNumberTag = new AttributeTag(0x0008,0x0050);
        Attribute AccessionNumber;
        AttributeTag RequestedProcedureIDTag = new AttributeTag(0x0040,0x1001);
        Attribute RequestedProcedureID;
        AttributeTag StudyIDTag = new AttributeTag(0x0020,0x0010); 
        Attribute StudyID;
        AttributeTag ModalityTag = new AttributeTag(0x0008,0x0060);
        Attribute Modality;
        AttributeTag sopClassUIDTag = new AttributeTag(0x0008,0x0016);
        Attribute sopClassUID;
        AttributeTag StudyInstanceUIDTag = new AttributeTag(0x0020,0x000d);
        Attribute StudyInstanceUID;
        AttributeTag SeriesInstanceUIDTag = new AttributeTag(0x0020,0x000e);
        Attribute SeriesInstanceUID;
        AttributeTag SOPInstanceUIDTag = new AttributeTag(0x0008,0x0018);
        Attribute SOPInstanceUID;
        AttributeTag TransferSyntaxUIDTag = new AttributeTag(0x0002,0x0010);
        Attribute TransferSyntax;
        AttributeTag PixelTag = new AttributeTag (0x7fe0,0x0010);
        Attribute Pixel;
                
		try{
			outString = "Patinet Name"+ SEPARATOR +
						"Patient ID"+ SEPARATOR +
						"Patient Sex"+ SEPARATOR +
						"Patient birth date"+SEPARATOR +
						"Patient birth time"+SEPARATOR +
						"Accession number"+SEPARATOR +
						"Req Procedure ID"+SEPARATOR +
						"Study ID"+SEPARATOR +
						"Modality"+SEPARATOR +
						"SOP Class UID"+SEPARATOR +
						"Study Instance UID"+SEPARATOR +
			            "Series Instance UID"+SEPARATOR +
			            "SOP Instance UID"+SEPARATOR +
			            "Transfer Syntax UID" +SEPARATOR +
						"File Name\n";
			Outfile.write(outString);
            System.out.println(outString);
            File StartingDitrectory = new File(SourceFolder);
		    List<File> files = getFileListing(StartingDitrectory);
			
		    for(File file : files ){
				String filename = file.getAbsolutePath();
				String ext = filename.substring(filename.lastIndexOf('.')+1, filename.length());
				if (ext.equalsIgnoreCase("dcm"))
				{
					al.clear();
                    DicomInputStream inStream = new DicomInputStream(file);
    	            al.read(inStream,PixelTag);
		            PatientName = al.get(PatientNameTag);
		            if (PatientName != null) {
		            	outString = PatientName.getSingleStringValueOrNull() + SEPARATOR;
		            } else { 
		            	outString = UNKNOWN;
		            }	
		            PatientID = al.get(PatientIDTag);
		            if (PatientID != null) {
			            outString = outString+ PatientID.getSingleStringValueOrNull() + SEPARATOR;
		            } else {
		            	outString = outString + UNKNOWN;
		            }
		            PatientSex = al.get(PatientSexTag);
		            if (PatientSex != null){
			            outString = outString+ PatientSex.getSingleStringValueOrNull() + SEPARATOR;
					} else {
						outString = outString + UNKNOWN;
	            	}
		            PatientBirthDate = al.get(PatientBirthDateTag);
		            if (PatientBirthDate != null){
			            outString = outString+ PatientBirthDate.getSingleStringValueOrNull() + SEPARATOR;
					} else {
						outString = outString + UNKNOWN;
	            	}
		            PatientBirthTime = al.get(PatientBirthTimeTag);
		            if (PatientBirthTime != null){
			            outString = outString+ PatientBirthTime.getSingleStringValueOrNull() + SEPARATOR;
					} else {
						outString = outString + UNKNOWN;
	            	}
		            AccessionNumber = al.get(AccessionNumberTag);
		            if (AccessionNumber != null){
			            outString = outString+ AccessionNumber.getSingleStringValueOrNull() + SEPARATOR;
					} else {
						outString = outString + UNKNOWN;
	            	}
		            RequestedProcedureID = al.get(RequestedProcedureIDTag);
		            if (RequestedProcedureID != null){
			            outString = outString+ RequestedProcedureID.getSingleStringValueOrNull() + SEPARATOR;
					} else {
						outString = outString + UNKNOWN;
	            	}
		            StudyID = al.get(StudyIDTag);
		            if (StudyID != null){
			            outString = outString+ StudyID.getSingleStringValueOrNull() + SEPARATOR;
					} else {
						outString = outString + UNKNOWN;
	            	}
		            Modality = al.get(ModalityTag);
		            if (Modality != null){
			            outString = outString+ Modality.getSingleStringValueOrNull() + SEPARATOR;
					} else {
						outString = outString + UNKNOWN;
	            	}
		            sopClassUID = al.get(sopClassUIDTag);
		            if (sopClassUID != null){
			            outString = outString+ sopClassUID.getSingleStringValueOrNull() + SEPARATOR;
					} else {
						outString = outString + UNKNOWN;
	            	}
		            StudyInstanceUID = al.get(StudyInstanceUIDTag);
		            if (StudyInstanceUID != null){
			            outString = outString+ StudyInstanceUID.getSingleStringValueOrNull() + SEPARATOR;
					} else {
						outString = outString + UNKNOWN;
	            	}
		            SeriesInstanceUID = al.get(SeriesInstanceUIDTag);
		            if (SeriesInstanceUID != null){
			            outString = outString+ SeriesInstanceUID.getSingleStringValueOrNull() + SEPARATOR;
					} else {
						outString = outString + UNKNOWN;
	            	}
		            SOPInstanceUID = al.get(SOPInstanceUIDTag);
		            if (SOPInstanceUID != null){
			            outString = outString+ SOPInstanceUID.getSingleStringValueOrNull() + SEPARATOR;
					} else {
						outString = outString + UNKNOWN;
	            	}
		            TransferSyntax = al.get(TransferSyntaxUIDTag);
		            if (TransferSyntax != null){
			            outString = outString+ TransferSyntax.getSingleStringValueOrNull() + SEPARATOR;
					} else {
						outString = outString + UNKNOWN;
	            	}
		            outString = outString + filename+ "\n";
                    inStream.close();
		            Outfile.write(outString);
		            Outfile.flush();
		            System.out.println(outString);
				}
			}
			Outfile.close();
            System.out.println(" It is complete my friend :-)");
		}
		catch (IOException ioe)
	    {
	        System.out.println ("IO Exception");
	        throw new Exception();
	    }
	    catch (DicomException de)
	    {
	        System.out.println ("DICOM Exception");
	        throw new Exception();
	    }
     }
	/**
	* Recursively walk a directory tree and return a List of all
	* Files found; the List is sorted using File.compareTo().
	*
	* @param aStartingDir is a valid directory, which can be read.
	*/

	static public List<File> getFileListing(File aStartingDir) throws FileNotFoundException {

	  validateDirectory(aStartingDir);
	  List<File> result = getFileListingNoSort(aStartingDir);
	  Collections.sort(result);
	  return result;
	}

	// PRIVATE //

	static private List<File> getFileListingNoSort(File aStartingDir) throws FileNotFoundException {

	  String outString;
	  
	  outString = "Processing " + aStartingDir.getAbsolutePath();
      System.out.println(outString);
	  List<File> result = new ArrayList<File>();
	  File[] filesAndDirs = aStartingDir.listFiles();
	  List<File> filesDirs = Arrays.asList(filesAndDirs);
	  for(File file : filesDirs) {
	    result.add(file); //always add, even if directory
	    if ( ! file.isFile() ) {
	      //must be a directory
	      //recursive call!
	      List<File> deeperList = getFileListingNoSort(file);
	      result.addAll(deeperList);
	    }
	  }
	  return result;
	}
	/**
	* Directory is valid if it exists, does not represent a file, and can be read.
	*/

	static private void validateDirectory (File aDirectory) throws FileNotFoundException {

	  if (aDirectory == null) {
	    throw new IllegalArgumentException("Directory should not be null.");
	  }

	  if (!aDirectory.exists()) {
	    throw new FileNotFoundException("Directory does not exist: " + aDirectory);
	  }

	  if (!aDirectory.isDirectory()) {
	    throw new IllegalArgumentException("Is not a directory: " + aDirectory);
	  }

	  if (!aDirectory.canRead()) {
	    throw new IllegalArgumentException("Directory cannot be read: " + aDirectory);
	  }
	}
}



