/*
 * Created on Jun 12, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gov.va.med.imaging.dicom.utils.pixelmed.model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.Vector;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.DicomFileUtilities;
import com.pixelmed.dicom.FileMetaInformation;
import com.pixelmed.dicom.TagFromName;
import com.pixelmed.dicom.TransferSyntax;

import gov.va.med.imaging.dicom.utilities.controller.NonExtOrDCMFilenameFilter;
import gov.va.med.imaging.dicom.utilities.exceptions.DataCreationException;
import gov.va.med.imaging.dicom.utilities.exceptions.DicomFileException;
import gov.va.med.imaging.dicom.utilities.model.IDataControl;
import gov.va.med.imaging.dicom.utilities.model.IFolderActionProgress;
import gov.va.med.imaging.dicom.utilities.model.IScrubControl;
import gov.va.med.imaging.dicom.utils.pixelmed.scrubber.TextFileScrub;

/**
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 *
 *
 * @author William Peterson
 *
 */
public class ScrubControl implements IScrubControl {

    private boolean removeGrp6000 = true;
    private boolean deleteOriginal = true;
    private boolean removePrivateAttributes = true;
    private boolean recursiveCheck = false;
    private boolean goldDBImages = false;
    private IDataControl dataControl;
    //private Scrubber scrubber;
    private File logFile;
    private BufferedWriter logOutput;
    private IFolderActionProgress progress = null;
    private boolean explicitVRCheck = true;
    
    private static final String SCRUBBEDPATH = "scrubbed";
    private static final String SCRUBBED = "scrubbed";
    
    private int totalFilesInFolder;
    private int currentFileInFolder;
    
    private static final Logger logger = LogManager.getLogger(ScrubControl.class);

    
    /**
     * Constructor
     *
     * 
     */
    
    public ScrubControl(IFolderActionProgress progress){
    	this();
    	this.progress = progress;

    }
    public ScrubControl() {
        super();

        dataControl = new DataControl();
        //scrubber = new Scrubber();
    }
    
    
    
    @Override
	public void setFolderAction(IFolderActionProgress progress) {
		this.progress = progress;
		
	}
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.model.IScrubControl#changeRemoveGrp6000()
	 */
    @Override
	public void changeRemoveGrp6000(){
    	this.removeGrp6000 = (this.removeGrp6000) ? false : true;
    }
    
    /* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.model.IScrubControl#isRemoveGrp6000()
	 */
    @Override
	public boolean isRemoveGrp6000(){
        return this.removeGrp6000;
    }
    
    /* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.model.IScrubControl#changeRemovePrivateAttributes()
	 */
    @Override
	public void changeRemovePrivateAttributes(){
    	this.removePrivateAttributes = (this.removePrivateAttributes) ? false : true;
    }
    
    /* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.model.IScrubControl#isRemovePrivateAttributes()
	 */
    @Override
	public boolean isRemovePrivateAttributes(){
        return this.removePrivateAttributes;
    }
    
    //recursive
    /* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.model.IScrubControl#scrubFiles(java.io.File)
	 */
    @Override
	public boolean scrubFiles(File rFile){
        
    	boolean isFailures = false;
		TransferSyntax tfrStx;
        String directory = rFile.getAbsolutePath();
        logger.info("Entering "+directory);
        if(this.progress != null){
	        this.totalFilesInFolder = rFile.listFiles(new NonExtOrDCMFilenameFilter()).length;
	        this.currentFileInFolder = 0;
	        this.progress.progressUpdate();
        }
		for(File file : rFile.listFiles(new NonExtOrDCMFilenameFilter())){
			if(file.isDirectory()){
				if(this.isRecursiveCheck()){
					isFailures = scrubFiles(file);
				}
			}
			else{
            	if(this.progress != null){
            		this.currentFileInFolder++;
            		this.progress.progressUpdate();
            	}
                try{
                	if(file != null && file.getName().toLowerCase().endsWith(".txt")){
                    	logger.info("Next file to scrub: " + file.getAbsolutePath());
                		TextFileScrub textScrubber = new TextFileScrub();
                		File nuFile = textScrubber.scrubTextFile(file, this.removePrivateAttributes);
                		this.saveNewTextFile(nuFile, file, directory);
                	}
                	else if(file != null && DicomFileUtilities.isDicomOrAcrNemaFile(file)){
                    	logger.info("Next file to scrub: " + file.getAbsolutePath());
	                	AttributeList dds = this.decodeFile(file);
						tfrStx = new TransferSyntax(dds.get(TagFromName.TransferSyntaxUID).getSingleStringValueOrNull());
						Attribute studyUIDTag = dds.get(TagFromName.StudyInstanceUID);
	                	String studyUID = studyUIDTag.getSingleStringValueOrEmptyString();
	                	//DataControl dataControl = new DataControl();
	                	Vector<Attribute> list = (Vector<Attribute>)dataControl.getDataList(studyUID);
	                	Scrubber scrubber = new Scrubber();
	                	scrubber.scrubData(dds, list, this.removeGrp6000, this.removePrivateAttributes);

	                	this.saveNewDCMFile(dds, tfrStx, file, directory);
            		}
                }
                catch(DicomFileException dfX){
                	String log = "Failed to Scrub "+ file.getAbsoluteFile() + "\r\n";
                	logger.error("failed to Scrub ["+ file.getAbsoluteFile()+"]  "+dfX.getMessage(), dfX);
                	try {
                		isFailures = true;
						this.logOutput.write(log);
					} catch (IOException e) {
						logger.error("Failed to write to log.");
					}
                }
                catch(DataCreationException dcX){
                	String log = "Failed to Scrub "+ file.getAbsoluteFile() + "\r\n";
                	logger.error("failed to Scrub ["+ file.getAbsoluteFile()+"]  "+dcX.getMessage(), dcX);
                	try {
                		isFailures = true;
						this.logOutput.write(log);
					} catch (IOException e) {
						logger.error("Failed to write to log.");
					}
                } 
                catch (IOException ioX) {
                	String log = "Failed to Scrub "+ file.getAbsoluteFile() + "\r\n";
                	logger.error("failed to Scrub ["+ file.getAbsoluteFile()+"]  "+ioX.getMessage(), ioX);
                	try {
                		isFailures = true;
						this.logOutput.write(log);
					} catch (IOException e) {
						logger.error("Failed to write to log.");
					}
				}
                catch (Exception X) {
                	String log = "Failed to Scrub "+ file.getAbsoluteFile() + "\r\n";
                	logger.error("failed to Scrub ["+ file.getAbsoluteFile()+"]  "+X.getMessage(), X);
                	try {
                		isFailures = true;
						this.logOutput.write(log);
					} catch (IOException e) {
						logger.error("Failed to write to log.");
					}
				}
            }
		}
		return isFailures;
    }
    
    private AttributeList decodeFile(File dcmFile)throws DicomFileException{
        
        AttributeList dds = new AttributeList();
        try{
            if(dcmFile.isFile()){
                String filename = dcmFile.getAbsolutePath();
                dds = new AttributeList();
				dds.setDecompressPixelData(false);
                dds.read(filename, null, true, true);
            }                
        }
        catch(DicomException de){
        	logger.error(de.getMessage());
        	logger.error(de);
            throw new DicomFileException();
        }
        catch(IOException ioe){
        	logger.error(ioe.getMessage());
        	logger.error(ioe);
            throw new DicomFileException();
        }
        return dds;
    }

    
    private void saveNewDCMFile(AttributeList dds, TransferSyntax tfrStx, File originalFile, String rPath)throws DicomFileException{
        

        String filenameOnly = originalFile.getName();
        String scrubbedFilename = rPath+"\\"+SCRUBBED+filenameOnly;

        dds.removeGroupLengthAttributes();
        
        if(this.isRemovePrivateAttributes()){
            dds.removePrivateAttributes();
        }
        
        //String transferSyntax = dds.get(TagFromName.TransferSyntaxUID).getSingleStringValueOrNull();
        //if(this.explicitVRCheck || transferSyntax == null){
        //	transferSyntax = TransferSyntax.ExplicitVRLittleEndian;
        //}
        dds.removeMetaInformationHeaderAttributes();
        
        try{
            FileMetaInformation.addFileMetaInformation(dds, tfrStx.getUID(),"VA_DICOM");
            dds.write(scrubbedFilename, tfrStx.getUID(), true, true);
            if(this.deleteOriginal){
                originalFile.delete();
            }
        }
        catch(DicomException de){
        	logger.error("Error: "+originalFile.getName());
        	logger.error(de.getMessage());
            throw new DicomFileException();
        }
        catch(IOException ioe){
            logger.error("Error: "+originalFile.getName());
            logger.error(ioe.getMessage());
            throw new DicomFileException();
        }        
    }

    
    private void saveNewTextFile(File nuFile, File originalFile, String rPath){
        

        String orgFilename = originalFile.getAbsolutePath();
        //String scrubbedFilename = nuFile.getName();
        
        if(this.deleteOriginal){
        	logger.debug("delete original file.");
            originalFile.delete();
            originalFile = null;
            
            if(this.isRecursiveCheck()){
                //File file = new File(orgFilename);
            	logger.debug("rename scrubbed file to original file.");
                nuFile.renameTo(new File(orgFilename));
            }       
        }
    }

    
    /* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.model.IScrubControl#isDeleteOriginal()
	 */
    @Override
	public boolean isDeleteOriginal(){
        return this.deleteOriginal;
    }

    /* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.model.IScrubControl#changeDeleteOriginal()
	 */
    @Override
	public void changeDeleteOriginal(){
    	this.deleteOriginal = (this.deleteOriginal) ? false : true;
    }
    

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.model.IScrubControl#isRecursiveCheck()
	 */
	@Override
	public boolean isRecursiveCheck() {
		return recursiveCheck;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.model.IScrubControl#changeRecursiveCheck()
	 */
	@Override
	public void changeRecursiveCheck() {
		this.recursiveCheck = (this.recursiveCheck) ? false : true;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.model.IScrubControl#isGoldDBImages()
	 */
	@Override
	public boolean isGoldDBImages() {
		return goldDBImages;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.model.IScrubControl#changeGoldDBImages()
	 */
	@Override
	public void changeGoldDBImages() {
		this.goldDBImages = (this.goldDBImages) ? false : true;
		this.dataControl.setGoldDBImages(this.goldDBImages);
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.model.IScrubControl#setRemoveGrp6000(boolean)
	 */
	@Override
	public void setRemoveGrp6000(boolean removeGrp6000) {
		this.removeGrp6000 = removeGrp6000;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.model.IScrubControl#setRemovePrivateAttributes(boolean)
	 */
	@Override
	public void setRemovePrivateAttributes(boolean removePrivateAttributes) {
		this.removePrivateAttributes = removePrivateAttributes;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.model.IScrubControl#setRecursiveCheck(boolean)
	 */
	@Override
	public void setRecursiveCheck(boolean recursiveCheck) {
		this.recursiveCheck = recursiveCheck;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.model.IScrubControl#setDeleteOriginal(boolean)
	 */
	@Override
	public void setDeleteOriginal(boolean deleteOriginal) {
		this.deleteOriginal = deleteOriginal;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.model.IScrubControl#openLog()
	 */
	@Override
	public void openLog(){
        this.logFile = new File(SCRUBFAILEDLOG);
        if(!this.logFile.exists()){
        	try {
				this.logFile.createNewFile();
				this.logFile.setWritable(true);
			} 
        	catch (Exception e) {
				logger.error("failure to create log file.");
        		logger.error(e);
			}
        }
		try {
			Charset cs = Charset.forName("ASCII");
			this.logOutput = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.logFile, true), cs));
		} 
		catch (Exception e) {
			logger.error("failed to open log file stream.");
			logger.error(e);
		}        

	}
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.model.IScrubControl#writeLog(java.lang.String)
	 */
	@Override
	public void writeLog(String log){
		try {
			this.logOutput.write(log);
		} catch (Exception e) {
			logger.error("failed to write to log file stream.");
			logger.error(e);
		}
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.model.IScrubControl#closeLog()
	 */
	@Override
	public void closeLog(){
		try {
			this.logOutput.flush();
			this.logOutput.close();
		} catch (Exception e) {
			logger.error("failed to close log file stream.");
			logger.error(e);
		}
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.model.IScrubControl#getTotalFilesInFolder()
	 */
	@Override
	public int getTotalFilesInFolder() {
		return totalFilesInFolder;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.model.IScrubControl#getCurrentFileInFolder()
	 */
	@Override
	public int getCurrentFileInFolder() {
		return currentFileInFolder;
	}
	@Override
	public boolean isExplicitVRCheck() {
		return explicitVRCheck;
	}
	@Override
	public void changeExplicitVRCheck() {
		this.explicitVRCheck = (this.explicitVRCheck) ? false : true;		
	}
	@Override
	public void setExplicitVRCheck(boolean explicitVRCheck) {
		this.explicitVRCheck = explicitVRCheck;
		
	}
    
}
