package gov.va.med.imaging.dicom.utils.pixelmed.scrubber;

import java.io.File;

import gov.va.med.imaging.dicom.utilities.exceptions.DataCreationException;
import gov.va.med.imaging.dicom.utilities.exceptions.DicomFileException;
import gov.va.med.imaging.dicom.utilities.model.IScrubControl;
import gov.va.med.imaging.dicom.utils.pixelmed.model.ScrubControl;

public class CmdLineScrubber {

	private boolean delGrp6000 = true;
	private boolean delPrivateGroups = true;
	private boolean delOriginalFile = true;
	private boolean recursiveScrub = false;
	
	
	public CmdLineScrubber() {
		
	}
	
	public static void main(String[] args) {
        if(args.length < 1){
			System.out.println("No arguments passed.");
			System.out.println(CmdLineScrubber.getUsage());
			System.exit(-1);
        }
        
        CmdLineScrubber scrubber = new CmdLineScrubber();
        
        String path = args[0];
        for(int i=1; i<args.length; i++){
        	if(args[i].equalsIgnoreCase("-o")){
        		scrubber.setDelGrp6000(false);
        	}
        	if(args[i].equalsIgnoreCase("-p")){
        		scrubber.setDelPrivateGroups(false);
        	}
        	if(args[i].equalsIgnoreCase("-f")){
        		scrubber.setDelOriginalFile(false);
        	}
        	if(args[i].equalsIgnoreCase("-r")){
        		scrubber.setRecursiveScrub(true);
        	}
        }
         
        try
        {
        	Boolean anyFailures = false;
        	anyFailures = scrubber.startScrubber(path);
        	if(anyFailures == true){
        		System.out.println("Failed to scrub all DICOM object files.");
        		System.out.println("Refer to file(s) listed in " + IScrubControl.SCRUBFAILEDLOG);
        	}
        }
        catch (Exception e)
        {
            System.out.println("Failed to scrub files."+e.getMessage());
            e.printStackTrace();
            System.exit(-1);
        }
        System.out.println("Scrubbing completed.");
        System.exit(0);
    }
	
	
	public Boolean startScrubber(String path) throws DicomFileException, DataCreationException{
		
		Boolean isFailures = false;
		IScrubControl control = new ScrubControl();
		control.setRemoveGrp6000(delGrp6000);
		control.setRemovePrivateAttributes(delPrivateGroups);
		control.setDeleteOriginal(delOriginalFile);
		control.setRecursiveCheck(recursiveScrub);
		control.openLog();
		isFailures = control.scrubFiles(new File(path));
		control.closeLog();
		
		return isFailures;
	}
	
	
	public void setDelGrp6000(boolean delGrp6000) {
		this.delGrp6000 = delGrp6000;
	}

	public void setDelPrivateGroups(boolean delPrivateGroups) {
		this.delPrivateGroups = delPrivateGroups;
	}

	public void setDelOriginalFile(boolean delOriginalFile) {
		this.delOriginalFile = delOriginalFile;
	}

	public void setRecursiveScrub(boolean recursiveScrub) {
		this.recursiveScrub = recursiveScrub;
	}

	private static String getUsage(){
		String usage = "Scrubber <folder path> <opt:-o> <opt:-p> <opt:-f> <opt: -r>\n" +
				"\t<folder path> folder containing DICOM object files to anonymize.\n" +
				"\t<-o> optionally does not remove Group 6000 Overlay from header.\n" +
				"\t<-p> optionally does not remove Private Data Elements from header.\n" +
				"\t<-f> optionally does not delete the originaly DICOM object file.\n" + 
				"\t<-r> optionally does a recursive scrub downward from the given folder path.";
		return usage;
	}

}
