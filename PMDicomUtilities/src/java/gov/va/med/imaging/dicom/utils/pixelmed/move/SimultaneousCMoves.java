/**
 * Created on Jan 31, 2008
 */
package gov.va.med.imaging.dicom.utils.pixelmed.move;

import java.util.Vector;

import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.AttributeTag;
import com.pixelmed.dicom.CodeStringAttribute;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.SOPClass;
import com.pixelmed.dicom.TagFromName;
import com.pixelmed.dicom.UniqueIdentifierAttribute;

import gov.va.med.imaging.dicom.utilities.exceptions.ReadFileException;
import gov.va.med.imaging.dicom.utilities.utils.TextUtil;

/**
 *
 *
 *
 * @author William Peterson
 *
 */
public class SimultaneousCMoves {

	Vector<CMoveInfo> infoList;
	int debugLevel = 0;
	int delay = 0;
	
	/**
	 * Constructor
	 */
	public SimultaneousCMoves() {
		
	}

	/**
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		
		SimultaneousCMoves moves = new SimultaneousCMoves();
		if(args.length < 1){
			System.out.println("No arguments passed.");
			System.out.println(moves.getUsage());
			System.exit(-1);
		}
		//Receive argument for debug level.
		if(args.length == 2){
			String debug = args[1];
			Integer temp = new Integer(debug);
			moves.setDebugLevel(temp.intValue());
		}
		if(args.length == 3){
			String delay = args[2];
			Integer temp = new Integer(delay);
			moves.setDelay(temp.intValue());
		}
		//Receive argument for config file.
		String file = args[0];
		
		try{
			moves.createInfoList(file);
			moves.runThreads();
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
		CMoveThread[] threads = new CMoveThread[listSize];
		
		for(int i=0; i<listSize; i++){
			threads[i] = new CMoveThread(this.infoList.get(i));
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
		this.infoList = new Vector<CMoveInfo>();
		TextUtil parser = new TextUtil();
		parser.openTextFile(configFile);
		String line;
		while((line = parser.getNextTextLine()) != null){
			CMoveInfo info = new CMoveInfo();
			info.setHostname(parser.piece(line, '|', 1));
			System.out.println("Hostname: "+info.getHostname());
			Integer port = new Integer(parser.piece(line, '|', 2));
			info.setPort(port.intValue());
			System.out.println("Port: "+info.getPort());
			info.setCalledAETitle(parser.piece(line, '|', 3));
			System.out.println("CalledAETitle: "+info.getCalledAETitle());
			info.setCallingAETitle(parser.piece(line, '|', 4));
			System.out.println("CallingAETitle: "+info.getCallingAETitle());
			info.setMoveDestination(parser.piece(line, '|', 5));
			System.out.println("Move Destination: "+info.getMoveDestination());
			String studyInstanceUID = parser.piece(line, '|', 6);
			System.out.println("Study Instance UID: "+studyInstanceUID+"\n");
			if(studyInstanceUID.isEmpty()){
				throw new ReadFileException("No Study Instance UID value.");
			}
			String toCancel = parser.piece(line, '|', 7);
			if(toCancel != null && toCancel.equalsIgnoreCase("CANCEL")){
				info.setCancel(true);
			}
			
			String cancelDelayAsString = parser.piece(line, '|', 8);
			if(cancelDelayAsString != null){
				Long cancelDelay = new Long(cancelDelayAsString);
				info.setCancelDelay(cancelDelay.longValue());
			}

			info.setIdentifier(this.buildAttributeList(studyInstanceUID));
			
			info.setAffectedSOPClass(SOPClass.StudyRootQueryRetrieveInformationModelMove);
			
			info.setDebugLevel(this.debugLevel);
			
			this.infoList.add(info);
		}
	}
	
	private AttributeList buildAttributeList(String studyInstanceUID)throws DicomException{
		AttributeList identifier = null;
		System.out.println("Building Attribute List.");
		identifier = new AttributeList();
		AttributeTag level = TagFromName.QueryRetrieveLevel; 
		Attribute a1 = new CodeStringAttribute(level); 
		a1.addValue("STUDY"); 
		identifier.put(level,a1);
		
		AttributeTag studyUID = TagFromName.StudyInstanceUID; 
		Attribute a2 = new UniqueIdentifierAttribute(studyUID); 
		a2.addValue(studyInstanceUID); 
		identifier.put(studyUID,a2);
		
		return identifier;
	}

	/**
	 * @param debugLevel the debugLevel to set
	 */
	private void setDebugLevel(int debugLevel) {
		this.debugLevel = debugLevel;
	}
	
	private String getUsage(){
		String usage = "SimultaneousMoves <configfile> <opt:debug> <opt:delay>\n" +
				"\t<configfile> contains an entry for each new thread.\n" +
				"\t\tEach entry format: ipaddress|port|CalledAET|CallingAET|MoveAET|StudyInstanceUID"+
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
