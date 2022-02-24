package gov.va.med.imaging.dicom.utils.pixelmed.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pixelmed.dicom.DicomFileUtilities;

import gov.va.med.imaging.dicom.utilities.controller.NonExtOrDCMFilenameFilter;
import gov.va.med.imaging.dicom.utilities.exceptions.DicomFileException;
import gov.va.med.imaging.dicom.utilities.information.MetaDataInfo;
import gov.va.med.imaging.dicom.utilities.model.IFolderActionProgress;
import gov.va.med.imaging.dicom.utilities.model.IMetaDataInfoControl;
import gov.va.med.imaging.dicom.utils.pixelmed.information.MetaInfo;

public class MetaDataInfoControl implements IMetaDataInfoControl {

    private boolean recursive = false;
    private String startingFolder = null;
    
    private IFolderActionProgress progress = null;
    private int totalFilesInFolder;
    private int currentFileInFolder;


    private static final Logger logger = LogManager.getLogger(MetaDataInfoControl.class);

    public MetaDataInfoControl(IFolderActionProgress progress){
    	this();
    	this.progress = progress;
    }

    
	public MetaDataInfoControl() {
		super();
        //dataControl = new DataControl();

	}

	@Override
	public boolean isRecursive() {
		return this.recursive;
	}

	@Override
	public void changeRecursiveCheck() {
		this.recursive = (this.recursive) ? false : true;
	}

	@Override
	public String getStartingFolder() {
		return this.startingFolder;
	}

	@Override
	public String setStartingFolder() {
		return this.startingFolder;
	}

	@Override
	public List<MetaDataInfo> getFileMetaDataCollection(File rFile) {
		List<MetaDataInfo> metaDataCollection = new ArrayList<MetaDataInfo>();
        String directory = rFile.getAbsolutePath();
        logger.info("Entering "+directory);
        if(this.progress != null){
	        this.totalFilesInFolder = rFile.listFiles(new NonExtOrDCMFilenameFilter()).length;
	        this.currentFileInFolder = 0;
	        this.progress.progressUpdate();
        }

        for(File file : rFile.listFiles(new NonExtOrDCMFilenameFilter())){
			if(file.isDirectory()){
				if(this.isRecursive()){
					List<MetaDataInfo> recursiveList = getFileMetaDataCollection(file);
					metaDataCollection.addAll(recursiveList);
				}
			}
			else{
            	if(this.progress != null){
            		this.currentFileInFolder++;
            		this.progress.progressUpdate();
            	}
                try{
                	if(file != null && DicomFileUtilities.isDicomOrAcrNemaFile(file)){
                    	logger.info("Next file to retrieve metadata: " + file.getAbsolutePath());
                    	
                    	MetaInfo metaInfo = new MetaInfo();
                    	MetaDataInfo metaData = metaInfo.collectMetaInfo(file.getAbsolutePath());
                    	metaDataCollection.add(metaData);
            		}
                }
                catch(DicomFileException dfX){
                	logger.error("failed to retrieve metadata from ["+ file.getAbsoluteFile() +"]  "+dfX.getMessage());
                }
                catch(Exception X){
                	logger.error("failed to retrieve metadata from ["+ file.getAbsoluteFile()+"]  "+X.getMessage());
                	
                }
            }
		}
		return metaDataCollection;
	}

	@Override
	public void setFolderAction(IFolderActionProgress progress) {
		this.progress = progress;
	}

	@Override
	public int getTotalFilesInFolder() {
		return this.totalFilesInFolder;
	}

	@Override
	public int getCurrentFileInFolder() {
		return this.currentFileInFolder;
	}
	


}
