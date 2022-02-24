package gov.va.med.imaging.dicom.utils.pixelmed.scrubber;

import java.io.File;
import java.io.IOException;

import com.pixelmed.dicom.DicomException;

import gov.va.med.imaging.dicom.utilities.controller.NonExtOrDCMFilenameFilter;
import gov.va.med.imaging.dicom.utilities.exceptions.ReadFileException;
import gov.va.med.imaging.dicom.utilities.information.IDicomObjectChecker;
import gov.va.med.imaging.dicom.utilities.scrubber.BaseRealDataChecker;
import gov.va.med.imaging.dicom.utils.pixelmed.information.DicomObjectChecker;

public class RealDataChecker extends BaseRealDataChecker {

	public RealDataChecker() {
		super();
	}

	public static void main(String[] args) {
		
	       RealDataChecker checker = new RealDataChecker();

	       if(args.length < 3){
				System.out.println("Not enough arguments passed.");
				System.out.println(RealDataChecker.getUsage());
				System.exit(-1);
	        }
	       String path = args[0];
	       String resultsFile = args[1];
	       String folderSkipFile = args[2];

	       try{
	    	   checker.createResultsFile(resultsFile);
	    	   checker.readInSkipFolders(folderSkipFile);
	    	   checker.checkRecursiveFolder(new File(path));
	    	   checker.closeResultsFile();

	       } 
	       catch (IOException e){
	    	   System.out.println("Failed to create Results File, " + resultsFile);
	    	   e.printStackTrace();
		} catch (ReadFileException e) {
			System.out.println("Failed to read Skip Folder list file.");
			e.printStackTrace();
		}
	       
	    System.out.println("Done.");
	}
	
	
	public void checkRecursiveFolder(File pFile){
		
		for(File file : pFile.listFiles(new NonExtOrDCMFilenameFilter())){
			if(file.isDirectory()){
				System.out.println("Entering " + file.getAbsolutePath());
				if(!this.folderNames.contains(file.getAbsolutePath().toLowerCase())){
					checkRecursiveFolder(file);					
				}
			}
			else{
				
				try {
					boolean isPatientData = true;
					boolean isOverlay = true;
					boolean isAnnotated = true;
					boolean isPrivateData = true;
					
					IDicomObjectChecker objChecker = new DicomObjectChecker(file);
					if(objChecker.isDicomObjectFile()){
						isPatientData = objChecker.containsPatientData();
						isOverlay = objChecker.containsGroup6000();
						isAnnotated = objChecker.containsBurnInAnnotation();
						isPrivateData = objChecker.containsPrivateAttributes();
					
					
						if(isPatientData == true || isOverlay == true
								|| isAnnotated == true || isPrivateData == true){

							String output = "File: " + file.getAbsoluteFile().toString() + "\r\n" +
												"\tReal Patient Data: " + isPatientData + "\r\n" +
												"\tOverlay: " + isOverlay + "\r\n" +
												"\tBurned-in Annotation: " + isAnnotated + "\r\n" +
												"\tPrivate Data: " + isPrivateData + "\r\n";

							if(objChecker.containsBurnInAnnotationByModality() && isAnnotated == false){
								
								output = output + "\tWarning: Image may still have Burned-In Annotation.\r\n";	
							}
							
							output = output + "\r\n";
							
							this.writeResult(output);
						}
					}
					
				} catch (IOException e) {
					System.out.println("Failed to add new result to result file.");
					e.printStackTrace();
				} catch (DicomException e) {
					System.out.println("Failed to add new result to result file.");
					e.printStackTrace();
				}
			}
		}
	}
	
	private static String getUsage(){
		String usage = "RealDataChecker <folder path> <results filename> <skip list filename>\n" +
				"\t<folder path> starting path to check DICOM object files for PHI/PII.\n" +
				"\t<results filename> containing the DICOM object file collection for those that may contain real data.\n" +
				"\t<skip list filename> containing list of folder to NOT search.";
		return usage;
	}


}
