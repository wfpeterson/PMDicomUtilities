/**
 * Created on Jan 31, 2008
 */
package gov.va.med.imaging.dicom.utils.pixelmed.sender;


import java.io.File;
import java.io.IOException;

import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.AttributeTag;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.SOPClass;
import com.pixelmed.dicom.SetOfDicomFiles;
import com.pixelmed.network.DicomNetworkException;
import com.pixelmed.network.MultipleInstanceTransferStatusHandler;
import com.pixelmed.network.StorageSOPClassSCU;

import gov.va.med.imaging.dicom.utilities.controller.DCMFilenameFilter;
import gov.va.med.imaging.dicom.utilities.exceptions.ReadFileException;
import gov.va.med.imaging.dicom.utilities.utils.SimpleStopWatch;

/**
 *
 *
 *
 * @author William Peterson
 *
 */
public class SendThread extends MultipleInstanceTransferStatusHandler implements Runnable {
	
	SendInfo info;


	/**
	 * Constructor
	 */
	public SendThread() {
		this.info = new SendInfo();
	}
	
	public SendThread(SendInfo info){
		this.info = info;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		System.out.println("\nStarting Thread "+Thread.currentThread().getName()+" now.\n");
		try{
			File[] files = this.getFilesFromFolder(this.info.getImageFolder());
			SetOfDicomFiles fileList = new SetOfDicomFiles();
			
			for(int i=0; i<files.length; i++){
				File file = files[i];
				String filename = file.getAbsolutePath();
				//this.sendImage(filename);
				fileList.add(filename);
			}
			
			SimpleStopWatch timer = new SimpleStopWatch();
			timer.start();
            new StorageSOPClassSCU(info.getHostname(), info.getPort(), info.getCalledAETitle(), info.getCallingAETitle(), 
            		fileList, 0, this, info.getDebugLevel());
            Long delta = timer.getElapsedTime();
            System.out.println("Thread "+Thread.currentThread().getName()
            		+":  Elapsed time for association and all images sent: "+delta.toString()+" milliseconds.");
            		
		}
		catch(ReadFileException rfX){
			System.out.println("Error: "+rfX);
		}
	}
	
	/**
	 * @param info the info to set
	 */
	public void setInfo(SendInfo info) {
		this.info = info;
	}
	
	private void sendImage(String file){
        AttributeTag sopclass = new AttributeTag(0x0008,0x0016);
        AttributeTag sopinstance = new AttributeTag(0x0008,0x0018);
        Attribute sopclassuid;
        Attribute sopinstanceuid;
        AttributeList al = new AttributeList();
        boolean sop;
		try {		    
		    al.read(file);
            sopclassuid = al.get(sopclass);
            sop = SOPClass.isImageStorage(sopclassuid.getSingleStringValueOrNull());
            if (sop == false)
            {
                throw new Exception("No or invalid SOP Class");
            }
            sopinstanceuid = al.get(sopinstance);
            new StorageSOPClassSCU(info.getHostname(), info.getPort(), info.getCalledAETitle(), info.getCallingAETitle(), 
            		file, sopclassuid.getSingleStringValueOrNull(), sopinstanceuid.getSingleStringValueOrNull(), 
            		0, info.getDebugLevel());
            System.out.println("Image Send Successful!");
		    
		}
        catch (IOException ioe)
        {
		    ioe.printStackTrace(System.err);
        }
        catch (DicomException de)
        {
		    de.printStackTrace(System.err);
        }
        catch (DicomNetworkException dne)
        {
		    dne.printStackTrace(System.err);
        }
		catch (Exception e) {
		    e.printStackTrace(System.err);
		}
	}
	
	private File[] getFilesFromFolder(String folder)throws ReadFileException{
		
		File directory = new File(folder);
        if(!directory.isDirectory()){
        	throw new ReadFileException(folder+" is not a folder.");
        }
    	File[] dcmFiles = directory.listFiles(new DCMFilenameFilter());
        return dcmFiles;
	}

	@Override
	public void updateStatus(int arg0, int arg1, int arg2, int arg3, String arg4) {
		
		System.out.println("Remaining: "+arg0+"   Completed: "+arg1+"   Failed: "+arg2+"   Warning: "+arg3);
		System.out.println("Sent Image: "+arg4);
		System.out.println(" ");
	}
	
}
