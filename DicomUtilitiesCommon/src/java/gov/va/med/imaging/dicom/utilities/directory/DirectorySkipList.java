package gov.va.med.imaging.dicom.utilities.directory;

import java.util.ArrayList;
import java.util.List;

import gov.va.med.imaging.dicom.utilities.exceptions.DicomFileException;
import gov.va.med.imaging.dicom.utilities.exceptions.ReadFileException;
import gov.va.med.imaging.dicom.utilities.utils.TextUtil;

public class DirectorySkipList {

	
	private List<String> skipList;
	//private String skipListFile;
	
	
	public DirectorySkipList() {
		this.skipList = new ArrayList<String>();
	}
	
	public DirectorySkipList(String skipConfigFile) throws ReadFileException, DicomFileException {
		this.skipList = new ArrayList<String>();
		this.parseListFile(skipConfigFile);
	}
	
	public List<String> getSkipList(){
		return this.skipList;
	}

	
	private void parseListFile(String skipConfigFile) throws ReadFileException, DicomFileException{
		
		TextUtil parser = new TextUtil();
		parser.openTextFile(skipConfigFile);
		String line;
		
		while((line = parser.getNextTextLine()) != null){
			skipList.add(line);
		
		}
	}
}
