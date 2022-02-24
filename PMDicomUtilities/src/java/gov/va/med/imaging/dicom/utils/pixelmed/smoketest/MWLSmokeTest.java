package gov.va.med.imaging.dicom.utils.pixelmed.smoketest;

import gov.va.med.imaging.dicom.utils.pixelmed.mwl.MWLScu;
import gov.va.med.imaging.dicom.utils.pixelmed.mwl.WorkListEntry;

public class MWLSmokeTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		if(args.length < 7){
			System.out.println("Not enough arguments passed.");
			System.out.println(MWLSmokeTest.getUsage());
			System.exit(-1);
		}

		try{
			MWLScu mwlscu = new MWLScu(args[0], new Integer(args[1]).intValue(), 
					   args[2], args[3], false);
	
			mwlscu.setAccessionNumber(args[4]);
			//mwlscu.setPatientName(args[5]);
			//mwlscu.setPatientID(args[6]);
			
			int result = mwlscu.cFind();
			if(result != 1){
				System.out.println("Wrong number of MWL Responses: "+result);
				throw new Exception();
			}
			
			WorkListEntry[] entries = mwlscu.getResult();
			
			WorkListEntry entry = entries[0];
			
			if(!entry.getAccessionNumber().equals(args[4])){
				System.out.println("Accession Number does not match.");
				throw new Exception();
			}
			if(!entry.getPatientName().equalsIgnoreCase(args[5])){
				System.out.println("Patient Name does not match.");
				throw new Exception();
			}
			if(!entry.getPatientID().equals(args[6])){
				System.out.println("Patient ID does not match.");
				throw new Exception();
			}
			
			System.out.println("Test passed.");
			System.exit(0);
			
		}
		catch(Exception X){
			System.out.println("Exception thrown during MWL.");
			System.exit(-1);
		}

		
		
	}

	private static String getUsage(){
		String usage = "MWLSmokeTest "+
				"<hostname> <port> <Called AETitle> <Calling AETitle> <Accession Number> <Patient Name> <Patient ID> \n";
		return usage;
	}


}
