/*
 * Created on Mar 4, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gov.va.med.imaging.dicom.utilities.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import gov.va.med.imaging.dicom.utilities.exceptions.ReadFileException;



/**
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 *
 *
 * @author William Peterson
 *
 */
public class TextUtil {

    //private final static Logger logger = LogManager.getLogger(TextUtil.class);

    
    private BufferedReader buffer;
    private BufferedWriter writeBuffer;
    private FileWriter fileWriter= null;

    /**
     * Constructor
     *
     * 
     */
    public TextUtil() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    public void openTextFile(String textFilename)throws ReadFileException{
        
        try{
            //Get Text file.
            //JUNIT Create test to verify how this fails if not correct permissions.
            this.buffer = new BufferedReader(new FileReader(textFilename));
        }
        catch(FileNotFoundException noFile){
        	//logger.error("Error: "+noFile.getMessage());
        	//logger.error(this.getClass().getName()+": Dicom Toolkit layer: " +
            //        "Exception thrown while attempting to open "+textFilename+".");
            throw new ReadFileException("Could not find or open "+textFilename+".", noFile);
        }
    }
    
    public void openTextFile(File file) throws ReadFileException{
        try{
            //Get Text file.
            //JUNIT Create test to verify how this fails if not correct permissions.
            
            this.buffer = new BufferedReader(new FileReader(file));
        }
        catch(FileNotFoundException noFile){
        	//logger.error("Error: "+noFile.getMessage());
        	//logger.error(this.getClass().getName()+": Dicom Toolkit layer: " +
            //        "Exception thrown while attempting to open "+file.getPath()+".");
            throw new ReadFileException("Could not find or open "+file.getPath()+".", noFile);
        }

    }
    
    public void createTextFile(String textFilename) throws IOException{
    	this.createTextFile(new File(textFilename));
    	
    	
    }
    
    public void createTextFile(File file) throws IOException{
    	//file.createNewFile();
    	//file.setWritable(true);
        if(!file.exists()){
        	file.delete();
        	file.createNewFile();
        	file.setWritable(true);
        }
        this.fileWriter = new FileWriter(file.getAbsoluteFile());
    	this.writeBuffer = new BufferedWriter(this.fileWriter);

    }

    public String getNextTextLine() throws ReadFileException{
        String line;
        try{
            boolean eof = false;
            do{
                if((line = this.buffer.readLine()) == null){
                    eof = true;
                    break;
                    //return null;
                    //notEOF = false;
                    //line = "";
                }
                else{
                    if(line.length() > 1){
                        line.trim();
                        char firstChar;
                        firstChar = line.charAt(0);
                        if(firstChar == '#'){
                            line = "";
                        }
                    }
                    else{
                        line = "";
                    }
                }
            }while(line.equals(""));
            
            if (eof){
                buffer.close();
                return null;
            }
        }
        catch(IOException io){
        	//logger.error("Error: "+io.getMessage());
        	//logger.error(this.getClass().getName()+": " +
            //        "Exception thrown while getting next line from Text Line.");
            throw new ReadFileException("Failure to get next line.", io);
        }
        catch(NullPointerException noptr){
        	//logger.error("Error: "+noptr.getMessage());
        	//logger.error(this.getClass().getName()+": "+
            //        "Exception thrown while getting next line from Text Line.");
            throw new ReadFileException("Failure to get next line.", noptr);
        }
        catch(StringIndexOutOfBoundsException noIndex){
        	//logger.error("Error: "+noIndex.getMessage());
        	//logger.error(this.getClass().getName()+": "+
            //        "Exception thrown while getting next line from Text Line.");
            throw new ReadFileException("Failure to get next line.", noIndex);
        }
        return line;
    }
    
    public void writeNextTextLine(String line) throws IOException{
    	this.writeBuffer.write(line);
    }

    public String piece(String line, String delimiter, int interestedPiece){
        
        //Disect the line and return only the interested piece based on delimiters.
        String[] pieces;
        //String strDelimiter = String.valueOf(delimiter);
        pieces = line.split(delimiter);
        if(interestedPiece <= pieces.length){
            String wantedPiece = pieces[interestedPiece-1].trim();
            if(wantedPiece.equals("")){
                return null;
            }
            return wantedPiece;
        }
        return null;
    }
    
    public String piece(String line, char delimiter, int interestedPiece){
        
        //Disect the line and return only the interested piece based on delimiters.
        String[] pieces;
        String strDelimiter = "\\"+String.valueOf(delimiter);
        pieces = line.split(strDelimiter);
        if(interestedPiece <= pieces.length){
            String wantedPiece = pieces[interestedPiece-1].trim();
            if(wantedPiece.equals("")){
                return null;
            }
            return wantedPiece;
        }
        return null;
    }
    
    
    public void closeTextFile(){
    	try {
    		if(this.writeBuffer != null){
    			this.writeBuffer.close();
    		}
    		if(this.buffer != null){
    			this.buffer.close();
    		}
		} catch (IOException e) {
			//ignore
		}
    }
}
