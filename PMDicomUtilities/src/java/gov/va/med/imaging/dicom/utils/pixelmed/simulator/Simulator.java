/**
 * Created on Jan 31, 2008
 */
package gov.va.med.imaging.dicom.utils.pixelmed.simulator;

import java.util.Vector;

import com.pixelmed.dicom.DicomException;

import gov.va.med.imaging.dicom.utilities.exceptions.ReadFileException;
import gov.va.med.imaging.dicom.utilities.utils.TextUtil;
import gov.va.med.imaging.dicom.utils.pixelmed.mwl.MWLScu;
import gov.va.med.imaging.dicom.utils.pixelmed.mwl.WorkListEntry;

/**
 *
 *
 *
 * @author Dee Csipo
 *
 */
public class Simulator {

	Vector<SimulatorInfo> infoList;
	int debugLevel = 0;
	int Loopcount = 1;
	int threadDelay = 0;
	
	/**
	 * Constructor
	 */
	public Simulator() {
		
	}

	/**
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		
		Simulator simulator = new Simulator();
		if(args.length < 4){
			System.out.println("Not enough arguments passed.");
			System.out.println(simulator.getUsage());
			System.exit(-1);
		}
		//Receive argument for debug level.
		String debug = args[1];
		Integer temp = new Integer(debug);
		simulator.setDebugLevel(temp.intValue());

		//Receive argument for loop count.
		String loopcnt = args[2];
		Integer loopTemp = new Integer(loopcnt);
		simulator.setloopCount(loopTemp.intValue());
		
		//Receive argument for delay.
		String delay = args[3];
		Integer delayTemp = new Integer(delay);
		simulator.setThreadDelay(delayTemp.intValue());
		
		//Receive argument for config file.
		String file = args[0];
		
		try{
			simulator.createInfoList(file);
			simulator.runThreads();
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
		System.out.println("Running loops.\n");
		for (int i=0; i < this.Loopcount; i++){
			System.out.println("outer loop executing " + (i+1) + ".\n");
			int listSize = this.infoList.size();
			for(int j=0; j<listSize; j++){
				try {
					executeOneItem(this.infoList.get(j));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					Thread.sleep(this.threadDelay);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	private void createInfoList(String configFile) throws ReadFileException, DicomException{

		System.out.println("Creating Info List from file "+configFile+".");
		this.infoList = new Vector<SimulatorInfo>();
		TextUtil parser = new TextUtil();
		parser.openTextFile(configFile);
		String line;
		while((line = parser.getNextTextLine()) != null){
			SimulatorInfo info = new SimulatorInfo();
			info.setHostnameMWL(parser.piece(line, '|', 1));
			System.out.println("hostname MWL: "+info.getHostnameMWL());
			Integer port = new Integer(parser.piece(line, '|', 2));
			info.setPortMWL(port.intValue());
			System.out.println("Port MWL: "+info.getPortMWL());
			info.setCalledAETitleMWL(parser.piece(line, '|', 3));
			System.out.println("CalledAET MWL: "+info.getCalledAETitleMWL());
			info.setCallingAETitleMWL(parser.piece(line, '|', 4));
			System.out.println("CallingAET MWL: "+info.getCallingAETitleMWL());
			info.setaccessionNumber(parser.piece(line, '|', 5));
			System.out.println("Accession#: "+info.getaccessionNumber());
			info.setsourceDir(parser.piece(line, '|', 6));
			System.out.println("Source dir: "+info.getsourceDir());
			info.setHostnameStore(parser.piece(line, '|', 7));
			System.out.println("hostname Store: "+info.getHostnameStore());
			port = new Integer(parser.piece(line, '|', 8));
			info.setPortStore(port.intValue());
			System.out.println("Port  Store: "+info.getPortStore());
			info.setCalledAETitleStore(parser.piece(line, '|', 9));
			System.out.println("CalledAET Store: "+info.getCalledAETitleStore());
			info.setCallingAETitleStore(parser.piece(line, '|', 10));
			System.out.println("CallingAET Store: "+info.getCallingAETitleStore());
			Integer loopCount = new Integer(parser.piece(line, '|', 11));
			info.setloopCount(loopCount.intValue());

			info.setDebugLevel(this.debugLevel);
			
			
			this.infoList.add(info);
		}
	}
	
	private void executeOneItem(SimulatorInfo item) throws Exception{
		
		MWLScu mwlscu = new MWLScu(item.getHostnameMWL(), item.getPortMWL(), 
								   item.getCalledAETitleMWL(), item.getCallingAETitleMWL(), false);
// 		set query arguments
		mwlscu.setAccessionNumber(item.getaccessionNumber());
		System.out.println("Accession#: "+item.getaccessionNumber());

		// perform the query
		int n = mwlscu.cFind();
		SendDirectoryOfImages sender;
		
		// eval results
		if(n > 0){
			System.err.println("" + n + " matching Worklist Entries found");

			WorkListEntry[] wL = mwlscu.getResult();

			for(int i = 0; i < n; i++){

				WorkListEntry wl = wL[i];
				System.err.println("============ #" + (i+1) + " ================");
				System.err.println("Patient is          " + wl.getPatientName());
				System.err.println("PatientID is        " + wl.getPatientID());
				System.err.println("PatientSex is       " + wl.getPatientSex());
				System.err.println("Modality is         " + wl.getModality());
				System.err.println("AccessionNo is      " + wl.getAccessionNumber());
				System.err.println("StudyInstanceUid is " + wl.getStudyInstanceUid());
				System.err.println("ProcStepStatus is   " + wl.getScheduledProcedureStepStatus());
				System.err.println("Study Date is   " + wl.getScheduledProcedureStepStartDate());

			}
			if (n == 1){
				// one matching entry found...
				// update the header from the worklist entry and send the set of files from the directory
				sender = new SendDirectoryOfImages(item,wL[0]);
				for (int j = 0; j<item.getloopCount(); j++){
					System.out.println("Inner loop executing " + (j+1) + ".\n");
					sender.execute();
				}
			} else {
				System.err.println("Multiple matching Worklist Entries found sending failed");
			}
			
		}
		else{
			System.err.println("No matching Worklist Entries found");
		}

	}
	
	/**
	 * @param debugLevel the debugLevel to set
	 */
	private void setDebugLevel(int debugLevel) {
		this.debugLevel = debugLevel;
	}
	
	/**
	 * @param loopCount the loopCount to set
	 */
	private void setloopCount(int loopCount) {
		this.Loopcount = loopCount;
	}
	
	private void setThreadDelay(int threadDelay) {
		this.threadDelay = threadDelay;
	}

	private String getUsage(){
		String usage = "Simulator <configfile> <debug> <loop count> <delay>\n" +
				"\t<configfile> contains an entry for each transfer.\n" +
				"\t\tEach entry format: ipaddress MWL|port MWL|CalledAET MWL|CallingAET MWL|Accession Number|File Dir|Store IP|Store Port|Store Called AE|Store Calling AE|Loop Count\n"+
				"\t<debug> sets the debug verbose level, 0-3\n" +
				"\t<loop count> number of repeating executions. Minimum value is 1.\n"+
				"\t<delay> sets the delay time, in milliseconds, between start of each thread process.\n";
		return usage;
	}

	/**
	 * @param delay the delay to set
	 */
}
