/**
 * 
 */
package gov.va.med.imaging.dicom.utils.pixelmed.recon;

import java.io.File;
import java.io.IOException;

import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.FileMetaInformation;

import gov.va.med.imaging.dicom.utilities.exceptions.DicomFileException;

/**
 * @author vhaiswpeterb
 *
 */
public class TSChanger {

	private AttributeList dds;
	private String filename;
	private String outputTransferSyntax;
	
	/**
	 * 
	 */
	public TSChanger(String file, String transferSyntax) {
		this.filename = file;
		this.outputTransferSyntax = transferSyntax;
		this.dds = null;
		
	}
	
	public void convertToNewTransferSyntax() throws DicomFileException{
		this.openFile();
		this.closeFileWithNewTransferSyntax();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		if(args.length != 2){
			System.out.print("Not correct number of arguments.");
			printUsage();
			System.exit(-1);
		}
		
		String file = args[0];
		String transferSyntax = args[1];
		
		TSChanger changer = new TSChanger(file, transferSyntax);
		try {
			changer.convertToNewTransferSyntax();
		} 
		catch (DicomFileException dfX) {
			System.out.println("Failed to create new DICOM object file.");
			System.out.println(dfX.getMessage());
			System.exit(-1);
		}
		System.out.println("0");
		System.exit(0);
	}
	
    private void openFile() throws DicomFileException {
        
        this.dds = new AttributeList();
        try{
                dds.read(this.filename, null, true, false);                
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
    
    private void closeFileWithNewTransferSyntax()throws DicomFileException{

        try{
        	File file = new File(this.filename);
        	file.delete();
        	
            FileMetaInformation.addFileMetaInformation(dds, this.outputTransferSyntax, 
                    "VA_DICOM");
            dds.write(this.filename, this.outputTransferSyntax, true, false);
        }
        catch(DicomException de){
            System.out.println("Error: "+this.filename);
            System.out.println(de.getMessage());
            throw new DicomFileException();
        }
        catch(IOException ioe){
            System.out.println(ioe.getMessage());
            System.out.println("Error: "+this.filename);
            throw new DicomFileException();
        }        
    }
    
    private static void printUsage(){
    	System.out.println("Usage: TSChanger <DICOM Filename> <Transfer Syntax UID>");
    }
	

}
