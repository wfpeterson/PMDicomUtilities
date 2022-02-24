/*
 * Created on May 22, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gov.va.med.imaging.dicom.utils.pixelmed.scrubber;

import java.io.File;
import java.io.IOException;

import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.DicomException;

/**
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 *
 *
 * @author William Peterson
 *
 */
public class ScrubFolder {

    /**
     * Constructor
     *
     * 
     */
    public ScrubFolder() {
        super();
        // TODO Auto-generated constructor stub
    }

    public static void main(String[] args) {
        String folder = "";
        
        if(args.length != 1){
            System.out.println("Not correct numbers arguments.");
            System.out.println("Need only one argument for folder location.");
            System.exit(-1);
        }
        
        folder = args[0];
        
        File files = new File(folder);
        File[] dcmFiles;
        
        dcmFiles = files.listFiles();
        try{
            for (int i=0; i<dcmFiles.length; i++){
                if(dcmFiles[i].isFile()){
                    String dcmFile = dcmFiles[i].getAbsolutePath();
                    AttributeList list = new AttributeList();
                    list.read(dcmFile, null, true, true);
                    
                    //VAScrub scrub = new VAScrub();
                    //scrub.scrubVAElements(list);
                    //String nuDCMFile = "Scrubbed"+i+".dcm";
                    //list.write(nuDCMFile, null, true, true);
                }
            }
        }
        catch(DicomException de){
            System.exit(-1);
        }
        catch(IOException ioe){
            System.exit(-1);
        }
       System.exit(0);
    }
}
