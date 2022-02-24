/**
 * Created on Jan 31, 2008
 */
package gov.va.med.imaging.dicom.utils.pixelmed.simulator;


import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeFactory;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.AttributeTag;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.FileMetaInformation;
import com.pixelmed.dicom.SetOfDicomFiles;
import com.pixelmed.dicom.SetOfDicomFiles.DicomFile;
import com.pixelmed.dicom.TagFromName;
import com.pixelmed.dicom.TransferSyntax;
import com.pixelmed.dicom.UIDGenerator;
import com.pixelmed.network.MultipleInstanceTransferStatusHandler;
import com.pixelmed.network.StorageSOPClassSCU;

import gov.va.med.imaging.dicom.utilities.controller.DCMFilenameFilter;
import gov.va.med.imaging.dicom.utilities.exceptions.ReadFileException;
import gov.va.med.imaging.dicom.utilities.utils.SimpleStopWatch;
import gov.va.med.imaging.dicom.utils.pixelmed.mwl.WorkListEntry;

/**
 *
 *
 *
 * @author William Peterson
 *
 */
public class SendDirectoryOfImages extends MultipleInstanceTransferStatusHandler  {
	
	private SimulatorInfo info;
	private WorkListEntry entry;
	private UIDGenerator uidgen;
	private static AttributeFactory attrFact;
	private boolean updateUIds;


	/**
	 * Constructor
	 */
	public SendDirectoryOfImages() {
		this.info = new SimulatorInfo();
	}
	
	public SendDirectoryOfImages(SimulatorInfo info, WorkListEntry entry){
		this.info = info;
		this.entry = entry;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.simulator.ISendDirectoryOfImages#execute()
	 */
	public void execute() {
		System.out.println("\nStarting from " + this.info.getsourceDir() + "\n");
		try{
			uidgen = new UIDGenerator();
			
			File[] files = this.getFilesFromFolder(this.info.getsourceDir());
			SetOfDicomFiles fileList = new SetOfDicomFiles();
			File tempFolder = null;
			for(int i=0; i<files.length; i++){
				
				File file = files[i];
				file.setReadable(true, false);
				StringBuffer buffer = new StringBuffer();
				buffer.append(file.getParent());
				buffer.append("\\temp\\");
				String tempPath = buffer.toString();
				buffer.append(file.getName());
				String outFilename = buffer.toString();
				tempFolder = new File(tempPath);
				
				if(!tempFolder.exists()){
					if(!tempFolder.mkdir()){
						throw new ReadFileException("Could not make temp folder.");
					}
				}
				
				// update all the files with the worklist information
				System.out.println("\nupdating " + file.getAbsolutePath() + "\n");
		        boolean updateSuccessful = updateFile(file.getAbsolutePath(), outFilename);
				if(updateSuccessful){
						fileList.add(outFilename);
				}
			}
			
			SimpleStopWatch timer = new SimpleStopWatch();
			timer.start();
            new StorageSOPClassSCU(info.getHostnameStore(), info.getPortStore(), info.getCalledAETitleStore(), info.getCallingAETitleStore(), 
            		fileList, 0, this, info.getDebugLevel());
            Long delta = timer.getElapsedTime();
            System.out.println("Thread "+Thread.currentThread().getName()
            		+":  Elapsed time for association and all images sent: "+delta.toString()+" milliseconds.");
            this.destroyTempFiles(fileList);	
            if(tempFolder.exists()){
            	tempFolder.delete();
            }
		}
		catch(ReadFileException rfX){
			System.err.println("Error: "+rfX);
			System.err.println("Stopping execution loop for files in "+this.info.getsourceDir()+".");
			rfX.printStackTrace();
		}
	}
	
	public void execute(Vector files) {
		System.out.println("\nStarting from " + this.info.getsourceDir() + "\n");
		try{
			uidgen = new UIDGenerator();
			
			SetOfDicomFiles fileList = new SetOfDicomFiles();
			File tempFolder = null;
			for(int i=0; i<files.size(); i++){
				
				String filename = (String) files.get(i);
				File file = new File(filename);
				file.setReadable(true, false);
				StringBuffer buffer = new StringBuffer();
				buffer.append(file.getParent());
				buffer.append("\\temp\\");
				String tempPath = buffer.toString();
				buffer.append(file.getName());
				String outFilename = buffer.toString();
				tempFolder = new File(tempPath);
				
				if(!tempFolder.exists()){
					if(!tempFolder.mkdir()){
						throw new ReadFileException("Could not make temp folder.");
					}
				}
				
				// update all the files with the worklist information
				System.out.println("\nupdating " + file.getAbsolutePath() + "\n");
		        boolean updateSuccessful = updateFile(file.getAbsolutePath(), outFilename);
				if(updateSuccessful){
						fileList.add(outFilename);
				}
			}
			
			SimpleStopWatch timer = new SimpleStopWatch();
			timer.start();
            new StorageSOPClassSCU(info.getHostnameStore(), info.getPortStore(), info.getCalledAETitleStore(), info.getCallingAETitleStore(), 
            		fileList, 0, this, info.getDebugLevel());
            Long delta = timer.getElapsedTime();
            System.out.println("Thread "+Thread.currentThread().getName()
            		+":  Elapsed time for association and all images sent: "+delta.toString()+" milliseconds.");
            this.destroyTempFiles(fileList);	
            if(tempFolder.exists()){
            	tempFolder.delete();
            }
		}
		catch(ReadFileException rfX){
			System.err.println("Error: "+rfX);
			System.err.println("Stopping execution loop for files in "+this.info.getsourceDir()+".");
			rfX.printStackTrace();
		}
	}

	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.simulator.ISendDirectoryOfImages#setInfo(gov.va.med.imaging.dicom.utils.pixelmed.simulator.SimulatorInfo)
	 */
	public void setInfo(SimulatorInfo info) {
		this.info = info;
	}
	private void updateUIDs (AttributeList list) throws DicomException {
        String StudyId = Attribute.getSingleStringValueOrNull(list, TagFromName.StudyID);
        if (StudyId == null) {
            StudyId = "1";
        }
        String SeriesNumber = Attribute.getSingleStringValueOrNull(list, TagFromName.SeriesNumber);
        String InstanceNumber = Attribute.getSingleStringValueOrNull(list, TagFromName.InstanceNumber);
        updateAttribute(list, TagFromName.StudyInstanceUID, entry.getStudyInstanceUid());
        updateAttribute(list, TagFromName.SeriesInstanceUID, uidgen.getNewSeriesInstanceUID(StudyId, SeriesNumber));
        updateAttribute(list, TagFromName.SOPInstanceUID, uidgen.getNewSOPInstanceUID(StudyId, SeriesNumber, InstanceNumber));
//        updateAttribute(list, TagFromName.MediaStorageSOPInstanceUID, SOPInstanceUID);
    }

	private void updateAttribute(AttributeList list, AttributeTag attribute, String value) throws DicomException{
		// update the attribute in the header with the new value
       
        Attribute realAttribute = list.get(attribute);
        byte[] vr = realAttribute.getVR();
        list.remove(attribute);
        Attribute update = AttributeFactory.newAttribute(attribute, vr); 
        update.addValue(value);
        list.put(update);

	}
	
	private boolean updateFile(String inFile, String outFile){
        AttributeList al = new AttributeList();
        try {
			al.read(inFile, null, true, true);
	        al.removeGroupLengthAttributes();
	        al.removeMetaInformationHeaderAttributes();
	        al.remove(TagFromName.DataSetTrailingPadding);

			updateAttribute(al, TagFromName.AccessionNumber, entry.getAccessionNumber());
			updateAttribute(al, TagFromName.PatientID, entry.getPatientID());
			updateAttribute(al, TagFromName.PatientName, entry.getPatientName());
			updateAttribute(al, TagFromName.PatientSex, entry.getPatientSex());
			if(!entry.getScheduledProcedureStepStartDate().isEmpty()){
				updateAttribute(al, TagFromName.StudyDate, entry.getScheduledProcedureStepStartDate());
			}
			if(!entry.getScheduledProcedureStepStartTime().isEmpty()){
				updateAttribute(al, TagFromName.StudyTime, entry.getScheduledProcedureStepStartTime());
			}
			updateUIDs (al);
	        FileMetaInformation.addFileMetaInformation(al,TransferSyntax.ExplicitVRLittleEndian,"OURAETITLE");
			al.write(outFile,TransferSyntax.ExplicitVRLittleEndian,true,true);
		} catch (IOException e) {
			System.err.println("Failed to update file to temp, "+inFile);
			return false;
		} catch (DicomException e) {
			System.err.println("Failed to update file to temp, "+inFile);
			return false;
		} catch (Exception X){
			System.err.println("Failed to update file to temp, "+inFile);
			return false;
		}
		return true;
	}
	
	private File[] getFilesFromFolder(String folder)throws ReadFileException{
		
		File directory = new File(folder);
        if(!directory.isDirectory()){
        	throw new ReadFileException(folder+" is not a folder.");
        }
    	File[] dcmFiles = directory.listFiles(new DCMFilenameFilter());
        return dcmFiles;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.simulator.ISendDirectoryOfImages#updateStatus(int, int, int, int, java.lang.String)
	 */
	@Override
	public void updateStatus(int arg0, int arg1, int arg2, int arg3, String arg4) {
		
		System.out.println("Remaining: "+arg0+"   Completed: "+arg1+"   Failed: "+arg2+"   Warning: "+arg3);
		System.out.println("Sent Image: "+arg4);
		System.out.println(" ");
	}

	/*
	private void replaceDCMFile(String tmpFile, String dcmFile){
		File tmp = new File(tmpFile);
		File dcm = new File(dcmFile);
		if(dcm.exists()){
			dcm.delete();
		}
		if(tmp.isFile()){
			tmp.renameTo(dcm);
		}	
	}
	*/
	
	private void destroyTempFiles(SetOfDicomFiles list){
		
		Iterator<DicomFile> iter = list.iterator();
		while(iter.hasNext()){
			DicomFile dcmFile = (DicomFile)iter.next();
			String filename = dcmFile.getFileName();
			File file = new File(filename);
			if(file.exists()){
				file.delete();
			}
		}
		list.clear();	
	}

	public boolean isUpdateUIds() {
		return updateUIds;
	}

	public void setUpdateUIds(boolean updateUIds) {
		this.updateUIds = updateUIds;
	}
	
	
}
