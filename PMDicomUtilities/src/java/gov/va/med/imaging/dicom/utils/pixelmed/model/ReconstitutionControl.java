/*
 * Created on Jun 12, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gov.va.med.imaging.dicom.utils.pixelmed.model;

import java.io.File;
import java.io.IOException;

import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.FileMetaInformation;
import com.pixelmed.dicom.TransferSyntax;

import gov.va.med.imaging.dicom.utilities.controller.DCMFilenameFilter;
import gov.va.med.imaging.dicom.utilities.exceptions.DataCreationException;
import gov.va.med.imaging.dicom.utilities.exceptions.DicomFileException;
import gov.va.med.imaging.dicom.utilities.model.IDataControl;
import gov.va.med.imaging.dicom.utilities.model.IReconstitutionControl;

/**
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 *
 *
 * @author William Peterson
 *
 */
public class ReconstitutionControl implements IReconstitutionControl {

    private boolean autoMode = true;
    //private boolean scrubVAOnly = true;
    private boolean deleteOriginal = false;
    private boolean privateAttributes = false;
    private int counter = 0;
    private String path = "";
    private IDataControl dataControl;
    private Reconstitutor reconstitutor;
    
    /**
     * Constructor
     *
     * 
     */
    public ReconstitutionControl() {
        super();
        // TODO Auto-generated constructor stub
        dataControl = new DataControl();
        reconstitutor = new Reconstitutor();
    }
    
    /* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.model.IReconstitutionControl#setAutoMode(boolean)
	 */
    @Override
	public void setAutoMode(boolean mode){
        this.autoMode = mode;
    }
    
    //public void setScrubVAOnly(boolean vaOnly){
    //    this.scrubVAOnly = vaOnly;
    //}
    
    /* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.model.IReconstitutionControl#isAutoMode()
	 */
    @Override
	public boolean isAutoMode(){
        return this.autoMode;
    }
    
    //public boolean isScrubVAOnly(){
    //    return this.scrubVAOnly;
    //}
    
    /* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.model.IReconstitutionControl#setRemovePrivateAttributes(boolean)
	 */
    @Override
	public void setRemovePrivateAttributes(boolean attributes){
        this.privateAttributes = attributes;
    }
    
    /* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.model.IReconstitutionControl#isRemovePrivateAttributes()
	 */
    @Override
	public boolean isRemovePrivateAttributes(){
        return this.privateAttributes;
    }
    
    @Override
	public void reconstituteFiles(File directory)throws DicomFileException, DataCreationException{
        if(directory.isDirectory()){
            File[] dcmFiles = directory.listFiles(new DCMFilenameFilter());
            this.reconstituteFiles(dcmFiles);
        }
    }
    
    @Override
	public void reconstituteFiles(File[] dcmFiles)throws DicomFileException, DataCreationException{
        for (int i=0; i<dcmFiles.length; i++){
            if(dcmFiles[i].isFile()){
                //AttributeList dds = this.decodeFile(dcmFiles[i]);
                //Attribute studyUIDTag = dds.get(TagFromName.StudyInstanceUID);
                //String studyUID = studyUIDTag.getSingleStringValueOrEmptyString();
                //Vector list = dataControl.getDataList(studyUID);
                //reconstitutor.scrubData(dds, list);
                //this.saveNewDCMFile(dds, dcmFiles[i]);
            }
        }
    }
    
    /*
    private AttributeList decodeFile(File dcmFile)throws DicomFileException{
        
        AttributeList dds = new AttributeList();
        try{
            if(dcmFile.isFile()){
                String filename = dcmFile.getAbsolutePath();
                //this.path = dcmFile.getPath();
                dds = new AttributeList();
                dds.read(filename, null, true, true);
                
            }
        }
        catch(DicomException de){
            System.out.println(de.getMessage());
            throw new DicomFileException();
        }
        catch(IOException ioe){
            System.out.println(ioe.getMessage());
            throw new DicomFileException();
        }
        return dds;
    }
    */
    private void saveNewDCMFile(AttributeList dds, File originalFile)throws DicomFileException{
        this.counter++;
        
        String nuPath = this.path+"\\recon";
        File nuFolder = new File(nuPath);
        nuFolder.mkdir();
        String nuFilename = nuPath+"\\"+originalFile.getName();
        System.out.println("New Filename :"+nuFilename);

        dds.removeGroupLengthAttributes();
        
        if(this.isRemovePrivateAttributes() == true){
            dds.removePrivateAttributes();
        }
        
        dds.removeMetaInformationHeaderAttributes();

        //Setup the Meta Header dataset.
        //Attribute sop = dds.get(TagFromName.MediaStorageSOPClassUID);
        //String mediaSOPClassUID = sop.getSingleStringValueOrEmptyString();
        
        //Attribute instance = dds.get(TagFromName.MediaStorageSOPInstanceUID);
        //String mediaSOPInstanceUID = instance.getSingleStringValueOrEmptyString();
        
        //Attribute xfer = dds.get(TagFromName.TransferSyntaxUID);
        //String transferSyntaxUID = xfer.getSingleStringValueOrNull();
        //System.out.println("Transfer Syntax: "+transferSyntaxUID);
        
        //Attribute aet = dds.get(TagFromName.SourceApplicationEntityTitle);
        //String aeTitle = aet.getSingleStringValueOrEmptyString();
        
        
        try{
            //FileMetaInformation.addFileMetaInformation(dds, mediaSOPClassUID, mediaSOPInstanceUID, 
            //        transferSyntaxUID, aeTitle);
            //dds.write(nuFilename, transferSyntaxUID, true, true);
            FileMetaInformation.addFileMetaInformation(dds, TransferSyntax.ExplicitVRLittleEndian, 
                    "VA_DICOM");
            dds.write(nuFilename, TransferSyntax.ExplicitVRLittleEndian, true, true);
            originalFile.delete();
        }
        catch(DicomException de){
            System.out.println("Error: "+originalFile.getName());
            System.out.println(de.getMessage());
            throw new DicomFileException();
        }
        catch(IOException ioe){
            System.out.println(ioe.getMessage());
            System.out.println("Error: "+originalFile.getName());
            throw new DicomFileException();
        }        
    }
    
    /* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.model.IReconstitutionControl#isDeleteOriginal()
	 */
    @Override
	public boolean isDeleteOriginal(){
        return this.deleteOriginal;
    }

    /* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.model.IReconstitutionControl#setDeleteOriginal(boolean)
	 */
    @Override
	public void setDeleteOriginal(boolean del){
        this.deleteOriginal = del;
    }
    
    /* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.model.IReconstitutionControl#setDirectory(java.lang.String)
	 */
    @Override
	public void setDirectory(String path){
        this.path = path;
    }
}
