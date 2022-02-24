/**
 * Created on Jan 31, 2008
 */
package gov.va.med.imaging.dicom.utils.pixelmed.findmove;

import java.util.Vector;

import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeFactory;
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
public class SingleCFindCMove {

	Vector<CFindInfoForMove> infoList;
	int debugLevel = 0;
	int delay = 0;
	
	/**
	 * Constructor
	 */
	public SingleCFindCMove() {
		
	}

	/**
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		
		SingleCFindCMove finds = new SingleCFindCMove();
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
			finds.runThread();
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
	
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.findmove.ISingleCFindCMove#runThread()
	 */
	public void runThread(){
		System.out.println("Running Threads.\n");
		int listSize = this.infoList.size();
		CFindCMoveThread[] threads = new CFindCMoveThread[listSize];
		
		for(int i=0; i<listSize; i++){
			threads[i] = new CFindCMoveThread(this.infoList.get(i));
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
		this.infoList = new Vector<CFindInfoForMove>();
		TextUtil parser = new TextUtil();
		parser.openTextFile(configFile);
		String line;
		AttributeList list;
		
		while((line = parser.getNextTextLine()) != null){
			CFindInfoForMove info = new CFindInfoForMove();
			info.setHostname(parser.piece(line, '|', 1));
			System.out.println("hostname: "+info.getHostname());
			Integer port = new Integer(parser.piece(line, '|', 2));
			info.setPort(port.intValue());
			System.out.println("Port: "+info.getPort());
			info.setCalledAETitle(parser.piece(line, '|', 3));
			System.out.println("CalledAET: "+info.getCalledAETitle());
			info.setCallingAETitle(parser.piece(line, '|', 4));
			System.out.println("CallingAET: "+info.getCallingAETitle());
			info.setMoveAETitle(parser.piece(line, '|', 5));
			System.out.println("MoveAET: "+info.getMoveAETitle());
			
			String data = parser.piece(line, '|', 6);
			if(data.isEmpty()){
				throw new ReadFileException("No Accession Number value.");				
			}
			list = new AttributeList();
			String elements[] = data.split("~");
			for(int i=0; i<elements.length; i++){
				String elementTag = parser.piece(elements[i], '=', 1);
				String elementValue = parser.piece(elements[i], '=', 2);
				Attribute attribute = AttributeFactory.newAttribute(new AttributeTag(elementTag));
				attribute.addValue(elementValue);
				list.put(attribute);
			}
			
			String toCancel = parser.piece(line, '|', 6);
			if(toCancel != null && toCancel.equalsIgnoreCase("CANCEL")){
				info.setCancel(true);
			}
			
			String cancelDelayAsString = parser.piece(line, '|', 7);
			if(cancelDelayAsString != null){
				Long cancelDelay = new Long(cancelDelayAsString);
				info.setCancelDelay(cancelDelay.longValue());
			}
			info.setIdentifier(this.completeAttributeListForCFind(list));
			
			info.setAffectedSOPClass(SOPClass.StudyRootQueryRetrieveInformationModelFind);
			
			info.setDebugLevel(this.debugLevel);
			
			this.infoList.add(info);
		}
	}
	
	private AttributeList completeAttributeListForCFind(AttributeList identifier)throws DicomException{

		System.out.println("Completing Attribute List.");
		//SpecificCharacterSet specificCharacterSet = new SpecificCharacterSet((String[])null);
		AttributeTag level = TagFromName.QueryRetrieveLevel; 
		Attribute a1 = new CodeStringAttribute(level); 
		a1.addValue("STUDY"); 
		identifier.put(level,a1);
		
		AttributeTag studyUID = TagFromName.StudyInstanceUID;
		Attribute a3 = new UniqueIdentifierAttribute(studyUID);
		a3.addValue("");
		identifier.put(studyUID, a3);

		return identifier;
		
	}

	/**
	 * @param debugLevel the debugLevel to set
	 */
	private void setDebugLevel(int debugLevel) {
		this.debugLevel = debugLevel;
	}
	
	private String getUsage(){
		String usage = "SingleFindMove <configfile> <opt:debug> <opt:delay>\n" +
				"\t<configfile> contains an entry for each new thread.\n" +
				"\t\tEach entry format: ipaddress|port|CalledAET|CallingAET|MoveAET|ElementTag=value~ElementTag=value...|CANCEL|Cancel Delay"+
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
