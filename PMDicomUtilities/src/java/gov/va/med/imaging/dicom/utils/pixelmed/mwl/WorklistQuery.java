package gov.va.med.imaging.dicom.utils.pixelmed.mwl;


public class WorklistQuery {

	public static void main(String[] args) throws Exception {

		// initialize SCU to local dcmtk worklist server
		MWLScu mwlscu = new MWLScu("vhaiswimgdg01", 60010, "VISTA_WORKLIST", "TEST", true);
// vhaiswimgdg01	60101	VISTA_STORAGE	32	Structured Reports	localhost	62600	STR_SCU_3	1		
//mwlscu = new MWLScu(host, port, CalledAET, CallingAET, debug)
		// set query arguments
//		mwlscu.setPatientName("PATIENT*");	// patient name wildcard
//		mwlscu.setPatientName("");	// patient name wildcard
//		mwlscu.setModality("CT");

		// perform the query
		int n = mwlscu.cFind();

		// eval results
		if(n > 0){
			System.out.println("" + n + " matching Worklist Entries found");

			WorkListEntry[] wL = mwlscu.getResult();

			for(int i = 0; i < n; i++){

				WorkListEntry wl = wL[i];
				System.out.println("============ #" + (i+1) + " ================");
				System.out.println("Patient is          " + wl.getPatientName());
				System.out.println("PatientID is        " + wl.getPatientID());
				System.out.println("PatientSex is       " + wl.getPatientSex());
				System.out.println("Modality is         " + wl.getModality());
				System.out.println("AccessionNo is      " + wl.getAccessionNumber());
				System.out.println("StudyInstanceUid is " + wl.getStudyInstanceUid());
				System.out.println("ProcStepStatus is   " + wl.getScheduledProcedureStepStatus());

			}
		}
		else{
			System.out.println("No matching Worklist Entries found");
		}
	}

}
