/**
 * Created on Jan 31, 2008
 */
package gov.va.med.imaging.dicom.utils.pixelmed.sender;

import java.util.Vector;

import com.pixelmed.dicom.DicomException;

import gov.va.med.imaging.dicom.utilities.exceptions.ReadFileException;
import gov.va.med.imaging.dicom.utilities.utils.TextUtil;

/**
 *
 *
 *
 * @author William Peterson
 *
 */
public class SimultaneousSends {

	Vector<SendInfo> infoList;
	int debugLevel = 0;
	int delay = 0;
	
	/**
	 * Constructor
	 */
	public SimultaneousSends() {
		
	}

	/**
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		
		SimultaneousSends finds = new SimultaneousSends();
		if(args.length < 1){
			System.out.println("No arguments passed.");
			System.out.println(finds.getUsage());
			System.exit(-1);
		}
		//Receive argument for debug level.
		if(args.length == 2){
			String debug = args[1];
			Integer temp = new Integer(debug);
			finds.setDebugLevel(temp.intValue());
		}
		if(args.length == 3){
			String delay = args[2];
			Integer temp = new Integer(delay);
			finds.setDelay(temp.intValue());
		}
		//Receive argument for config file.
		String file = args[0];
		
		try{
			finds.createInfoList(file);
			finds.runThreads();
		}
		catch(ReadFileException rfX){
			System.out.println("Application failed.");
			rfX.printStackTrace();
			System.exit(-1);
		}
		catch(DicomException dX){
			System.out.println("Application failed.");
			dX.printStackTrace();
			System.exit(-1);			
		}
		//System.out.println("Completed starting of threads.");
		//System.exit(0);
	}
	
	public void runThreads(){
		System.out.println("Running Threads.\n");
		int listSize = this.infoList.size();
		SendThread[] threads = new SendThread[listSize];
		
		for(int i=0; i<listSize; i++){
			threads[i] = new SendThread(this.infoList.get(i));
		}
		
		for(int j=0; j<listSize; j++){
			new Thread(threads[j]).start();
			try{
				Thread.sleep(this.delay);
			}
			catch(InterruptedException iX){
				//do nothing
			}
		}
	}
	
	private void createInfoList(String configFile) throws ReadFileException, DicomException{
		System.out.println("Creating Info List from file "+configFile+".");
		this.infoList = new Vector<SendInfo>();
		TextUtil parser = new TextUtil();
		parser.openTextFile(configFile);
		String line;
		while((line = parser.getNextTextLine()) != null){
			SendInfo info = new SendInfo();
			info.setHostname(parser.piece(line, '|', 1));
			System.out.println("hostname: "+info.getHostname());
			Integer port = new Integer(parser.piece(line, '|', 2));
			info.setPort(port.intValue());
			System.out.println("Port: "+info.getPort());
			info.setCalledAETitle(parser.piece(line, '|', 3));
			System.out.println("CalledAET: "+info.getCalledAETitle());
			info.setCallingAETitle(parser.piece(line, '|', 4));
			System.out.println("CallingAET: "+info.getCallingAETitle());
			String imagefolder = parser.piece(line, '|', 5);
			System.out.println("Image Folder: "+imagefolder);
			if(imagefolder.isEmpty()){
				throw new ReadFileException("No Image Folder given.");
			}
			info.setImageFolder(imagefolder);
			info.setDebugLevel(this.debugLevel);
			
			this.infoList.add(info);
		}
	}

	/**
	 * @param debugLevel the debugLevel to set
	 */
	private void setDebugLevel(int debugLevel) {
		this.debugLevel = debugLevel;
	}
	
	private String getUsage(){
		String usage = "SimultaneousSends <configfile> <opt:debug> <opt:delay>\n" +
				"\t<configfile> contains an entry for each new thread.\n" +
				"\t\tEach entry format: ipaddress|port|CalledAET|CallingAET|ImageFolder"+
				"\t<debug> optionally sets the debug verbose level, 0-3\n" +
				"\t<delay> optionally sets the delay time, in milliseconds, between start of each thread process.";
		return usage;
	}

	/**
	 * @param delay the delay to set
	 */
	private void setDelay(int delay) {
		this.delay = delay;
	}
	
	
}
