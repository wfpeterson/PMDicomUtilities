package gov.va.med.imaging.dicom.utilities.scrubber;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

import gov.va.med.imaging.dicom.utilities.exceptions.ReadFileException;
import gov.va.med.imaging.dicom.utilities.utils.TextUtil;

public class BaseRealDataChecker {

	protected File resultFile = null;
	protected TextUtil outputText = new TextUtil();
	protected TextUtil inputText = new TextUtil();
	protected Vector<String> folderNames = new Vector<String>();
	
	public BaseRealDataChecker() {
		
	}

	
	public void createResultsFile(String filename) throws IOException{
		this.resultFile = new File(filename);
		this.outputText.createTextFile(this.resultFile);
	}
	
	public void closeResultsFile(){
			this.outputText.closeTextFile();
	}
	
	protected void writeResult(String output) throws IOException{
		this.outputText.writeNextTextLine(output);
	}
	
	protected void readInSkipFolders(String filename) throws ReadFileException{
		this.inputText.openTextFile(filename);
		String line = null;
		while((line = this.inputText.getNextTextLine()) != null){
			this.folderNames.add(line);	
		}
	}
	


}
