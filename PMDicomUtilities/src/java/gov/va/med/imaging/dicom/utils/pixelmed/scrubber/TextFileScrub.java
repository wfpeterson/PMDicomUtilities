/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: September 26, 2006
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  VHAISWPETERB
  Description: 

        ;; +--------------------------------------------------------------------+
        ;; Property of the US Government.
        ;; No permission to copy or redistribute this software is given.
        ;; Use of unreleased versions of this software requires the user
        ;;  to execute a written test agreement with the VistA Imaging
        ;;  Development Office of the Department of Veterans Affairs,
        ;;  telephone (301) 734-0100.
        ;;
        ;; The Food and Drug Administration classifies this software as
        ;; a Class II medical device.  As such, it may not be changed
        ;; in any way.  Modifications to this software may result in an
        ;; adulterated medical device under 21CFR820, the use of which
        ;; is considered to be a violation of US Federal Statutes.
        ;; +--------------------------------------------------------------------+
 */
package gov.va.med.imaging.dicom.utils.pixelmed.scrubber;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import gov.va.med.imaging.dicom.utilities.model.IDataControl;
import gov.va.med.imaging.dicom.utilities.utils.StringUtils;
import gov.va.med.imaging.dicom.utils.pixelmed.model.DataControl;


//NOTE 0008,1032/0008,0103 and
//		0040,0260/0008,0103 and
//		0040,0275/0040,0008/0008,0103 has no value in output14.dcm. Investigated the issue.
//	This is acceptable according to the DICOM Standard.

public class TextFileScrub{
    private static Logger logger = LogManager.getLogger (TextFileScrub.class);

    private File inFile;
    private File outFile;
    private BufferedReader rBuffer;
    private BufferedWriter wBuffer;
    private String SCRUBBED = "scrubbed";
    private boolean removePrivateAttributes = true;
    private IDataControl dataControl;
    private boolean eof = false;
    private String PATIENTS_NAME = "PATIENTS_NAME";
    private String PATIENTS_ID = "PATIENTS_ID";
    private String PATIENTS_DOB = "PATIENTS_BIRTH_DATE";
    private String ACCESSION_NUMBER = "ACCESSION_NUMBER";
    private String PAT_NAME_TAG = "0010,0010";
    private String PAT_ID_TAG = "0010,0020";
    private String PAT_DOB_TAG = "0010,0030";
    private String ACCESSION_TAG = "0008,0050";
    
    /**
     * Constructor
     */
    public TextFileScrub() {
        super();
    }
    
    /* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.scrubber.ITextFileScrub#scrubTextFile(java.io.File, boolean)
	 */
	public File scrubTextFile(File file, boolean removePrivateAttributes) throws Exception{
    	
    	this.inFile = file;
    	this.removePrivateAttributes = removePrivateAttributes;
    	this.dataControl = new DataControl();        
        
    	try{
    		this.openFiles();
    		this.parseTextFile();
    	}
    	catch(IOException ioX){
    		logger.error("IO Exception. " + ioX.getMessage());
    		logger.error(ioX);
    		throw new Exception("IO Exception thrown.");
    	}
    	catch(Exception X){
    		logger.error("Exception. " +X.getMessage());
    		logger.error(X);
    		throw X;
    	}
    	finally{
    		this.closeFiles();
    	}
    	return this.outFile;
    }
    
        
    private void parseTextFile() throws IOException, Exception{   
    	
    	this.parseData1Section();
    	if(this.eof != true){
    		this.parseDicomDataSection();
    	}
    	if(this.eof != true){
    		this.parseHisUpdates();
    	}
    }
    


	private void parseData1Section()throws IOException, Exception{
        String inTextLine = "";
        HashMap<String,String> map = this.dataControl.getDataListForTextFile();
        //Loop thru the lines until $$BEGIN DATA1.
        //Ignore each line until $$BEGIN DATA1
        logger.debug("parsing Data1 Section");
        inTextLine = this.getNextTextLine();
        while((inTextLine != null) && !(inTextLine.equals("$$BEGIN DATA1"))){
        	this.wBuffer.write(inTextLine+StringUtils.CRLF);

        	inTextLine = this.getNextTextLine();
        }
        if(inTextLine != null){
        	this.wBuffer.write(inTextLine+StringUtils.CRLF);
        }
        else{
        	return;
        }
        
        
        inTextLine = this.getNextTextLine();
        while((inTextLine != null) && !(inTextLine.equals("$$END DATA1"))){
        	
        	if(inTextLine.contains(PATIENTS_NAME)){
        		if(map.containsKey(PAT_NAME_TAG)){
        			String patientName = map.get(PAT_NAME_TAG);
                	logger.debug("writing line with new value to write buffer.");
        			inTextLine = this.changeData1SectionLineValue(inTextLine, patientName);
        		}
        	}
        	else if(inTextLine.contains(PATIENTS_ID)){
        		if(map.containsKey(PAT_ID_TAG)){
        			String patientID = map.get(PAT_ID_TAG);
                	logger.debug("writing line with new value to write buffer.");
        			inTextLine = this.changeData1SectionLineValue(inTextLine, patientID);
        		}
        	}
        	else if(inTextLine.contains(PATIENTS_DOB)){
        		if(map.containsKey(PAT_DOB_TAG)){
        			String patientDOB = map.get(PAT_DOB_TAG);
                	logger.debug("writing line with new value to write buffer.");
        			inTextLine = this.changeData1SectionLineValue(inTextLine, patientDOB);
        		}
        	}
        	else if(inTextLine.contains(ACCESSION_NUMBER)){
        		if(map.containsKey(ACCESSION_TAG)){
        			String accessionNumber = map.get(ACCESSION_TAG);
                	logger.debug("writing line with new value to write buffer.");
        			inTextLine = this.changeData1SectionLineValue(inTextLine, accessionNumber);
        		}
        	}
        	
        	this.wBuffer.write(inTextLine+StringUtils.CRLF);
            
            inTextLine = this.getNextTextLine();
        }
        if(inTextLine != null){
        	this.wBuffer.write(inTextLine+StringUtils.CRLF);
        }
	}
        
        
        
	private void parseDicomDataSection() throws IOException, Exception{
        
		logger.debug("parsing DICOM Data Section.");
        String inTextDicomLine = "";
        //boolean sequenceFlag = false;
        HashMap<String,String> map = this.dataControl.getDataListForTextFile();
        
        //Loop thru the lines until $BEGIN DICOM DATA.
        //Ignore each line until $$BEGIN DICOM DATA. 
        inTextDicomLine = this.getNextTextLine();
        while(inTextDicomLine != null && !(inTextDicomLine.equals("$$BEGIN DICOM DATA"))){
        	this.wBuffer.write(inTextDicomLine+StringUtils.CRLF);
        	
        	inTextDicomLine = this.getNextTextLine();
        }
        if(inTextDicomLine != null){
        	this.wBuffer.write(inTextDicomLine+StringUtils.CRLF);
        }
        else{
        	return;
        }
        
        //Loop thru each line until $$END DICOM DATA.
        inTextDicomLine = this.getNextTextLine();
        while(inTextDicomLine != null && !(inTextDicomLine.equals("$$END DICOM DATA"))){
            boolean removeLineDueToGroup = false; 
        	String checkGroup = inTextDicomLine.substring(0,4);
            int iGroup = Integer.parseInt(checkGroup, 16);
            removeLineDueToGroup = this.removeGroup(iGroup);
          
            if(removeLineDueToGroup != true){
                String tag = inTextDicomLine.substring(0, 9);
                if(map.containsKey(tag)){
                	String nuValue = (String)map.get(tag);
                	String nuLine = this.changeLineValue(inTextDicomLine, nuValue);
                	logger.debug("writing line with new value to write buffer.");
                	this.wBuffer.write(nuLine+StringUtils.CRLF);
                }
                else{
                	this.wBuffer.write(inTextDicomLine+StringUtils.CRLF);
                }
            }
            inTextDicomLine = this.getNextTextLine();
        }	//End Loop due to $$END DICOM DATA or EOF.
        if(inTextDicomLine != null){
        	this.wBuffer.write(inTextDicomLine+StringUtils.CRLF);
        }
    }
    

    private void parseHisUpdates() throws IOException, Exception{
        
    	logger.debug("parsing HIS Updates Section.");
        String inTextLine = this.getNextTextLine();
        while(inTextLine != null && !(inTextLine.equals("$$BEGIN HIS UPDATE"))){
	           this.wBuffer.write(inTextLine+StringUtils.CRLF);
	           
	           inTextLine = this.getNextTextLine();
        }
        if(inTextLine != null){
        	this.wBuffer.write(inTextLine+StringUtils.CRLF);
        }
        else{
        	return;
        }
        
        
        inTextLine = this.getNextTextLine();
        while(inTextLine != null && !(inTextLine.equals("$$END HIS UPDATE"))){
        	
        	inTextLine = this.getNextTextLine();
        } // end while
        if(inTextLine != null){
        	this.wBuffer.write(inTextLine+StringUtils.CRLF);
        }
    }

    private String getNextTextLine()throws IOException, Exception{
        String line = null;
        try{
            do{
                if((line = this.rBuffer.readLine()) == null){
                	logger.debug("End Of File.");
                	this.eof = true;
                	return null;
                }
            }while(line.equals(""));
        }
        catch(IOException ioX){
            logger.error(ioX.getMessage());
            logger.error(this.getClass().getName()+": " +
                    "Exception thrown while getting next line from Text Line.");
            throw ioX;
        }
        return line;
    }
    
    private boolean removeGroup(int group){
        boolean removeGroup = false;
        
        
        if((group % 2) != 0 && this.removePrivateAttributes){
            removeGroup = true;
        }
        
        //Do not allow Icon Image Sequences
        if(group == 0x0088){
            removeGroup = true;
        }
        
        if((group >= 0x6000) && (group <= 0x60ff)){
        	removeGroup = true;
        }
        
        return removeGroup;
    }
    
    
    private String changeLineValue(String inTextLine, String nuValue){
    	
    	logger.debug("changing line value.");
    	String nuLine = null;
    	try{
	    	String[] parts = StringUtils.Split(inTextLine, StringUtils.STICK);
	    	logger.debug("Line parts: " + parts.length);
	    	if(parts.length < 4){
	    		nuLine = "";
	    	}
	    	else{
	    		StringBuilder builder = new StringBuilder();
	    		builder.append(parts[0]);
	    		builder.append(StringUtils.STICK);
	    		builder.append(parts[1]);
	    		builder.append(StringUtils.STICK);
	    		builder.append(parts[2]);
	    		builder.append(StringUtils.STICK);
	    		builder.append(nuValue);
	    		nuLine = builder.toString();
	    	}
    	}
    	catch(Throwable T){
    		logger.error("Unknown error: " + T.getMessage());
    		logger.error(T);
    	}
    	return nuLine;
    }
    
    private String changeData1SectionLineValue(String inTextLine, String nuValue){
    	
    	logger.debug("changing line value.");
    	String nuLine = null;
    	try{
	    	String[] parts = StringUtils.Split(inTextLine, StringUtils.EQUALS);
	    	logger.debug("Line parts: " + parts.length);
	    	if(parts.length < 2){
	    		nuLine = "";
	    	}
	    	else{
	    		StringBuilder builder = new StringBuilder();
	    		builder.append(parts[0]);
	    		builder.append(StringUtils.EQUALS);
	    		builder.append(nuValue);
	    		nuLine = builder.toString();
	    	}
    	}
    	catch(Throwable T){
    		logger.error("Unknown error: " + T.getMessage());
    		logger.error(T);
    	}
    	return nuLine;
    }

    
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.scrubber.ITextFileScrub#openFiles()
	 */
	public void openFiles() throws Exception{

		//open input text file
        if(!this.inFile.exists()){
        	throw new Exception("Text file does not exist.");
        }
		try {
			Charset cs = Charset.forName("ASCII");
			this.rBuffer = new BufferedReader(new InputStreamReader(new FileInputStream(this.inFile), cs));
		} 
		catch (Exception e) {
			logger.error("failed to open input text file stream for " + this.inFile.getAbsolutePath());
			logger.error(e);
			throw e;
		}
		
		
		//open output text file
		String inFilePath = this.inFile.getParent();
		String scrubFilename = inFilePath + "\\" + SCRUBBED + this.inFile.getName();
        this.outFile = new File(scrubFilename);
        if(!this.outFile.exists()){
        	try {
				this.outFile.createNewFile();
				this.outFile.setWritable(true);
			} 
        	catch (Exception e) {
				logger.error("failure to create text file " + this.outFile.getAbsolutePath());
        		logger.error(e);
        		throw e;
			}
        }
		try {
			Charset cs = Charset.forName("ASCII");
			this.wBuffer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.outFile), cs));
		} 
		catch (Exception e) {
			logger.error("failed to open text file stream " + this.outFile.getAbsolutePath());
			logger.error(e);
			throw e;
		}        
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.scrubber.ITextFileScrub#closeFiles()
	 */
	public void closeFiles(){
		try {
			if(this.rBuffer != null){
				this.rBuffer.close();
			}
			if(this.wBuffer != null){
				this.wBuffer.flush();
				this.wBuffer.close();
			}
		} catch (Exception e) {
			logger.error("failed to close text file streams.");
			logger.error(e);
		}
	}
}
