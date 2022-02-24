/*

	Build a Modality Worklist SCU OK :-)

	Minimal recommended Data Objects for MWL Query:

	PatientName
	PatientID
	PatientBirthDate
	PatientSex
	PregnancyStatus
	AccessionNumber
	RequestingPhysician
	ScheduledProcedureStepSequence

		ScheduledStationAET
		ScheduledProcedureStepStartDate
		ScheduledProcedureStepStartTime
		Modality
		ScheduledPerformingPhysicianName

	------------------------------------------------
	(C) 2009 Ing. Büro Kleiber, 8142 Uitikon-Waldegg
	------------------------------------------------

	V1.0	27/11/2009/mkl	initial version

	Redistribution and use in source and binary forms, with or without modification, are
	permitted provided that the following conditions are met:
 
	1. Redistributions of source code must retain the above copyright notice, this list of
	   conditions and the following disclaimers.
 
	2. Redistributions in binary form must reproduce the above copyright notice, this list of
	   conditions and the following disclaimers in the documentation and/or other materials
	   provided with the distribution.
 
	3. Neither the name of PixelMed Publishing nor the names of its contributors, including
	   Ing. Buero Kleiber, may be used to endorse or promote products derived from this software.
 
	This software is provided by the copyright holders and contributors "as is" and any
	express or implied warranties, including, but not limited to, the implied warranties
	of merchantability and fitness for a particular purpose are disclaimed. In no event
	shall the copyright owner or contributors be liable for any direct, indirect, incidental,
	special, exemplary, or consequential damages (including, but not limited to, procurement
	of substitute goods or services; loss of use, data or profits; or business interruption)
	however caused and on any theory of liability, whether in contract, strict liability, or
	tort (including negligence or otherwise) arising in any way out of the use of this software,
	even if advised of the possibility of such damage.
 
	This software has neither been tested nor approved for clinical use or for incorporation in
	a medical device. It is the redistributor's or user's responsibility to comply with any
	applicable local, state, national or international regulations.
*/
package gov.va.med.imaging.dicom.utils.pixelmed.mwl;

import java.util.ArrayList;

import com.pixelmed.dicom.ApplicationEntityAttribute;
import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.CodeStringAttribute;
import com.pixelmed.dicom.DateAttribute;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.LongStringAttribute;
import com.pixelmed.dicom.PersonNameAttribute;
import com.pixelmed.dicom.SequenceAttribute;
import com.pixelmed.dicom.ShortStringAttribute;
import com.pixelmed.dicom.SpecificCharacterSet;
import com.pixelmed.dicom.TagFromName;
import com.pixelmed.dicom.TimeAttribute;
import com.pixelmed.dicom.UniqueIdentifierAttribute;
import com.pixelmed.dicom.UnsignedShortAttribute;
import com.pixelmed.network.FindSOPClassSCU;
import com.pixelmed.network.IdentifierHandler;

/**
	<pre>
		Hnet Modality Worklist SCU, based on pixelmed toolkit

		Intended Usage:

		.
		.
		.
		// construct scu
		MWLScu = mwlscu new MWLScu(host, port, calledAET, callinAET, false);
		// set query parameters
		mwlscu.setPatientName("Vivaldi*");
		mwlscu.setModality("CT");
		mwlscu.setProcedureStepDate("20091114-20091115");
		int n = mwlscu.cFind();
		if(n > 0){
			WorkListEntry[] wList = mwlcsu.getResultSet();
			for(int i = 0; i < n; i++){

				// get single fields of the returned AttributeList
				String AccessionNo = wList[i].getAccessionNumber();
				String Modality = wList[i].getModality();
				.
				.
				.
			}
		}
		else{
			// nothing found
		}
		.
		.
		.

		Comments:	Search Criteria are cleaned when cFind() returns
				Any Critaria can be combined for a search by means
				of the setXX() methods.

				This is experimental stuff (yet) :-)

		11/2009/mkl  
	</pre>
*/
public final class MWLScu extends IdentifierHandler {

	private boolean debug = false;
	private final static String Version = "H-Net Modality Worklist SCU, V1.0";
	private AttributeList request = null;
	private AttributeList sequence = null;
	private SpecificCharacterSet scSet = null;
	private String host = null;
	private String CalledAET = null;
	private String CallingAET = null;
	private int port = 0;
	private int total = 0;
	private WorkListEntry[] wL= null;
	private ArrayList<WorkListEntry> wList = new ArrayList<WorkListEntry>();

	// this should go into com/pixelmed/dicom/dicom/SOPClass.java !!

	public static final String ModalityWorklistQueryRetrieveInformationModelFind  = "1.2.840.10008.5.1.4.31";
 
	public MWLScu(String host, int port, String CalledAET, String CallingAET, boolean debug) throws Exception {

		this.debug = debug;
		this.host = host;
		this.port = port;
		this.CalledAET = CalledAET;
		this.CallingAET = CallingAET;

		if(debug) System.err.println(Version);

		setupRequest();
	}

	private void setupRequest(){

		scSet = new SpecificCharacterSet((String[])null);
		request = new AttributeList();
		Attribute a;

		// fill empty values for all fields we'd like to receive (and possibly search on)

		// ids
		a = new ShortStringAttribute(		TagFromName.AccessionNumber, scSet); request.put(a);
		a = new UniqueIdentifierAttribute(	TagFromName.StudyInstanceUID); request.put(a);
		// patient
		a = new PersonNameAttribute(		TagFromName.PatientName, scSet); request.put(a);
		a = new LongStringAttribute(		TagFromName.PatientID); request.put(a);
		a = new DateAttribute(				TagFromName.PatientBirthDate); request.put(a);
		a = new CodeStringAttribute(		TagFromName.PatientSex); request.put(a);
		a = new LongStringAttribute(		TagFromName.Allergies); request.put(a);
		a = new LongStringAttribute(		TagFromName.MedicalAlerts); request.put(a);
		a = new UnsignedShortAttribute(		TagFromName.PregnancyStatus); request.put(a);
		// aerzte
		a = new PersonNameAttribute(		TagFromName.ReferringPhysicianName, scSet); request.put(a);
		a = new PersonNameAttribute(		TagFromName.RequestingPhysician, scSet); request.put(a);
		// verlangte prozedur
		a = new LongStringAttribute(		TagFromName.RequestedProcedureDescription); request.put(a);
		a = new ShortStringAttribute(		TagFromName.RequestedProcedureID); request.put(a);
		//a = new ShortStringAttribute(		TagFromName.RequestedProcedurePriority); request.put(a);
		// verwaltung
		a = new LongStringAttribute(		TagFromName.RequestingService); request.put(a);
		a = new LongStringAttribute(		TagFromName.PatientTransportArrangements, scSet); request.put(a);

//		a = new LongStringAttribute(		TagFromName.PlacerOrderNumberImagingServiceRequest, scSet); request.put(a);
//		a = new LongStringAttribute(		TagFromName.FillerOrderNumberImagingServiceRequest, scSet); request.put(a);
		
//		a = new LongStringAttribute(		TagFromName.PlacerOrderNumberOfImagingServiceRequest, scSet); request.put(a);
//		a = new LongStringAttribute(		TagFromName.FillerOrderNumberOfImagingServiceRequest, scSet); request.put(a);
		a = new LongStringAttribute(		TagFromName.AdmissionID); request.put(a);

		// empty procedure step sequence

		sequence = new AttributeList();
		a = new CodeStringAttribute(		TagFromName.Modality); sequence.put(a);
		a = new DateAttribute(				TagFromName.ScheduledProcedureStepStartDate); sequence.put(a);
		a = new TimeAttribute(				TagFromName.ScheduledProcedureStepStartTime); sequence.put(a);
		a = new PersonNameAttribute(		TagFromName.ScheduledPerformingPhysicianName); sequence.put(a);

		a = new ShortStringAttribute(		TagFromName.ScheduledStationName); sequence.put(a);
		a = new ApplicationEntityAttribute(	TagFromName.ScheduledStationAETitle); sequence.put(a);
		//a = new ShortStringAttribute(		TagFromName.ScheduledProcedureStepID); sequence.put(a);
		a = new ShortStringAttribute(		TagFromName.ScheduledProcedureStepLocation); sequence.put(a);
		//a = new CodeStringAttribute(		TagFromName.ScheduledProcedureStepStatus); sequence.put(a);

		a = new LongStringAttribute(		TagFromName.ScheduledProcedureStepDescription, scSet); sequence.put(a);

		// prepare empty sequence
		//a = new SequenceAttribute(TagFromName.ScheduledProcedureStepSequence); request.put(a);
	}

	//
	//	perform the actual find
	//

	/**
		<pre>
		Perform the query against the constructor-configured modality worklist server.
		Returns number of result sets found.
		</pre>
	*/
	public int cFind() throws Exception {

		total = 0;

		try{
			// add in sequence here, user cannot change sequence any longer :-)
			//SequenceAttribute seq = new SequenceAttribute(TagFromName.ScheduledProcedureStepSequence);
			//seq.addItem(sequence);
			//request.put(seq);

			new FindSOPClassSCU(host, port, CalledAET, CallingAET,
//				SOPClass.ModalityWorklistQueryRetrieveInformationModelFind,	// if integrated into toolkit
				ModalityWorklistQueryRetrieveInformationModelFind,		// as defined here
				request,
				this, 0);

		}catch(Exception e){

			setupRequest();
			e.printStackTrace();
			throw(e);
		}

		setupRequest();	// make sure query "buffers" get reset
		return(total);
	}

	//
	//	Set serach attributes
	//

	//	Suche nach Patienten Kriterien

	/**
		<pre>
		Suche nach Patientenkriterien: Patientenname
		Wildcards:                     * and ?
		</pre>
	*/
	public void setPatientName(String PatientName) throws Exception {

		request.putNewAttribute(TagFromName.PatientName, scSet).addValue(PatientName);
	}

	/**
		<pre>
		Suche nach Patientenkriterien:	PatientenID
		</pre>
	*/
	public void setPatientID(String PatientID) throws Exception {

		request.putNewAttribute(TagFromName.PatientID, scSet).addValue(PatientID);
	}

	/**
		<pre>
		Suche nach Patientenkriterien:	Patienten Geschlecht
		</pre>
	*/
	public void setPatientSex(String PatientSex) throws Exception {

		request.putNewAttribute(TagFromName.PatientSex, scSet).addValue(PatientSex);
	}


	/**
		<pre>
		Suche nach Patientenkriterien:	Geburtstag
		Datum oder
		RangeQueries: 20031131-20041218    von bis
		              20031131-            ab
		              -20041231            vor
		Format:       YYYYMMDD
		</pre>
	*/
	public void setPatientBirthDate(String BirthDate) throws Exception {

		request.putNewAttribute(TagFromName.PatientBirthDate, scSet).addValue(BirthDate);
	}

	//	Suche nach Prozedur Kriterien

	/**
		<pre>
		Suche nach Prozedurkriterien: Modalitaet
		Offizielle Dicom Short Strings wie MR, CT, CR, ...
		</pre>
	*/
	public void setModality(String Modality) throws Exception {

		sequence.putNewAttribute(TagFromName.Modality, scSet).addValue(Modality);
	}

	/**
		<pre>
		Suche nach Prozedurkriterien: Prozedur Start Datum
		Datum oder
		RangeQueries: 20031131-20041218    von bis
		              20031131-            ab
		              -20041231            vor
		Format:       YYYYMMDD
		</pre>
	*/
	public void setProcedureStepDate(String sd) throws Exception {

		sequence.putNewAttribute(TagFromName.ScheduledProcedureStepStartDate).addValue(sd);
	}

	/**
		<pre>
		Suche nach Prozedurkriterien: Protedur Start Zeit
		Zeit oder
		RangeQueries: 073000-120000    von bis
		              120000-          nach
		              -150000          vor
		Format:       HHMMSS
		</pre>
	*/
	public void setProcedureStepTime(String st) throws Exception {

		sequence.putNewAttribute(TagFromName.ScheduledProcedureStepStartTime).addValue(st);
	}

	//	Suche nach Aerzten

	/**
		<pre>
		Suche nach Aerzten: Ueberweisender Arzt
		Wildcards:          * and ?
		</pre>
	*/
	public void setReferringPhysicianName(String Referring) throws Exception {

		request.putNewAttribute(TagFromName.ReferringPhysicianName, scSet).addValue(Referring);
	}

	/**
		<pre>
		Suche nach Aerzten: Verodnender Arzt
		Wildcards:          * and ?
		</pre>
	*/
	public void setRequestingPhysician(String Requesting) throws Exception {

		request.putNewAttribute(TagFromName.RequestingPhysician, scSet).addValue(Requesting);
	}

	/**
		<pre>
		Suche nach Aerzten: Ausfuehrender Arzt
		Wildcards:          * and ?
		</pre>
	*/
	public void setPerformingPhysicianName(String Performing) throws Exception {

		sequence.putNewAttribute(TagFromName.PerformingPhysicianName, scSet).addValue(Performing);
	}

	//	Suche nach Verwaltungskriterien

	/**
		<pre>
		Suche nach Verwaltungskriterien: AccesionNumber
		</pre>
	*/
	public void setAccessionNumber(String AccessionNumber) throws Exception {

		request.putNewAttribute(TagFromName.AccessionNumber, scSet).addValue(AccessionNumber);
	}

	/**
		<pre>
		Suche nach Verwaltungskriterien: AdmissionID
		</pre>
	*/
	public void setAdmissionID(String AdmissionID) throws Exception {

		request.putNewAttribute(TagFromName.AdmissionID, scSet).addValue(AdmissionID);
	}

	//
	//	ResultSet Return
	//

	/**
		<pre>
		Return result of the query as an WorkListEntry Array.
		</pre>
	*/
	public WorkListEntry[] getResult() {

		wL = wList.toArray(new WorkListEntry[0]);
		wList.clear();
		return(wL);
	}

	//
	//	per-dataset callback
	//

	/**
		<pre>
		Internal callback function, must be public.
		Do not call this one, *beware*
		<pre>
	*/
	public void doSomethingWithIdentifier(AttributeList wl) throws DicomException {

		if(debug) {
			System.err.println("==============================================");
			System.err.println(wl.toString());
			System.err.println("==============================================");
		}
		try{

			wList.add(new WorkListEntry(wl));

		}catch(Exception e){
			e.printStackTrace();
			throw new DicomException(e.getMessage());
		}
		total++;
	}

	//
	//	unit test
	//

/*
	public static void main(String[] args) throws Exception {

		MWLScu mwlscu = new MWLScu("masipc05", 1234, "Store1", "TESTMWLSCU", false);

		//
		//	 wildcard search
		//		*
		//		?
		//		""
		//		\	list matching, means or but seems not to be supported?
		//		-	range matching (numbers and date) 1962-2003, -2003, 1962- 

//		mwlscu.setPatientName("VIVALDI^ANTONIO");
//		mwlscu.setPatientName("VIVALDI*");
//		mwlscu.setPatientName("BEETHOVEN*\\VIV*");	// does not work |?
//		mwlscu.setPatientName("BEETHOVEN*");
//		mwlscu.setModality("CT");

		int i;

		// test sequence
		mwlscu.setPatientName("VIVALDI^ANTONIO");
		// check for Date
//		hnetworklist.jar:	packages/hnetworklist.jar
		// check for Time:
//		mwlscu.setProcedureStepTime("060000-190000");	//??
//		mwlscu.setProcedureStepTime("123000-");
		i = mwlscu.cFind();
		System.err.println("\n" + i + " matching worklist entries for CT AND 1993 And > 1200 found\n");
		WorkListEntry[] wL = mwlscu.getResult();
		for(int j = 0; j < i; j++){
			WorkListEntry wl = wL[j];
			System.err.println("Patient is          " + wl.getPatientName());
			System.err.println("PatientID is        " + wl.getPatientID());
			System.err.println("PatientSex is       " + wl.getPatientSex());
			System.err.println("Modality is         " + wl.getModality());
			System.err.println("AccessionNo is      " + wl.getAccessionNumber());
			System.err.println("StudyInstanceUid is " + wl.getStudyInstanceUid());
			System.err.println("ProcStepStatus is   " + wl.getScheduledProcedureStepStatus());
		}

	}
*/	
}