/*
	Modality Worklist Result Fields

	-------------------------------------------------
	(C) 2009 Ing. Buero Kleiber, 8142 Uitikon-Waldegg
	-------------------------------------------------

	27/1172009/mkl	V1.0


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
import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.SequenceAttribute;
import com.pixelmed.dicom.SequenceItem;
import com.pixelmed.dicom.TagFromName;

/**
	Returned Worklist Fields.
*/
public class WorkListEntry{

	private String PatientName;
	private String PatientID;
	private String PatientBirthDate;
	private String PatientSex;
	private String MedicalAlerts;
	private String Allergies;
	private String PregnancyStatus;

	private String AccessionNumber;
	private String StudyInstanceUid;
	private String AdmissionID;

	private String ReferringPhysicianName;
	private String RequestingPhysician;
	private String RequestingService;
	private String RequestedProcedureDescription;
	private String RequestedProcedureID;
	private String RequestedProcedurePriority;
	private String PatientTransportArrangements;
	private String PlacerOrderNumberOfImageServiceRequest;
	private String FillerOrderNumberOfImageServiceRequest;

	//	ScheduledProcedureStepSequence as SequenceAttribute:
	private String Modality;
	private String ScheduledStationAeTitle;
	private String ScheduledProcedureStepStartDate;
	private String ScheduledProcedureStepStartTime;
	private String ScheduledPerformingPhysician;
	private String ScheduledProcedureStepDescription;
	private String ScheduledProcedureStepID;
	private String ScheduledProcedureStepStatus;
	private String ScheduledStationName;
	private String ScheduledProcedureStepLocation;

	public WorkListEntry(AttributeList al) throws Exception{

		// Direct accessible tags
		PatientName = 	Attribute.getSingleStringValueOrNull(al, TagFromName.PatientName);
		PatientID = 	Attribute.getSingleStringValueOrNull(al, TagFromName.PatientID);
		PatientBirthDate = Attribute.getSingleStringValueOrNull(al, TagFromName.PatientBirthDate);
		PatientSex = 	Attribute.getSingleStringValueOrNull(al, TagFromName.PatientSex);
		MedicalAlerts = 	Attribute.getSingleStringValueOrNull(al, TagFromName.MedicalAlerts);
		Allergies = 	Attribute.getSingleStringValueOrNull(al, TagFromName.Allergies);
		PregnancyStatus = Attribute.getSingleStringValueOrNull(al, TagFromName.PregnancyStatus);

		AccessionNumber = 	Attribute.getSingleStringValueOrNull(al, TagFromName.AccessionNumber);
		StudyInstanceUid = 	Attribute.getSingleStringValueOrNull(al, TagFromName.StudyInstanceUID);
		AdmissionID = 		Attribute.getSingleStringValueOrNull(al, TagFromName.AdmissionID);

		ReferringPhysicianName = 			Attribute.getSingleStringValueOrNull(al, TagFromName.ReferringPhysicianName);
		RequestingPhysician = 				Attribute.getSingleStringValueOrNull(al, TagFromName.RequestingPhysician);
		RequestingService = 				Attribute.getSingleStringValueOrNull(al, TagFromName.RequestingService);
		RequestedProcedureDescription = 		Attribute.getSingleStringValueOrNull(al, TagFromName.RequestedProcedureDescription);
		RequestedProcedureID = 				Attribute.getSingleStringValueOrNull(al, TagFromName.RequestedProcedureID);
		//RequestedProcedurePriority = 			Attribute.getSingleStringValueOrNull(al, TagFromName.RequestedProcedurePriority);
		PatientTransportArrangements = 		Attribute.getSingleStringValueOrNull(al, TagFromName.PatientTransportArrangements);
//		PlacerOrderNumberOfImageServiceRequest = 	Attribute.getSingleStringValueOrNull(al, TagFromName.PlacerOrderNumberImagingServiceRequest);
//		FillerOrderNumberOfImageServiceRequest = 	Attribute.getSingleStringValueOrNull(al, TagFromName.FillerOrderNumberImagingServiceRequest);
//		PlacerOrderNumberOfImageServiceRequest = 	Attribute.getSingleStringValueOrNull(al, TagFromName.PlacerOrderNumberOfImagingServiceRequest);
//		FillerOrderNumberOfImageServiceRequest = 	Attribute.getSingleStringValueOrNull(al, TagFromName.FillerOrderNumberOfImagingServiceRequest);

		// Sequence accessible Tags
		//SequenceAttribute se = (SequenceAttribute) al.get(TagFromName.ScheduledProcedureStepSequence);
		//SequenceItem seI = se.getItem(0);
		//AttributeList seq = seI.getAttributeList();

		//Modality = Attribute.getSingleStringValueOrNull(seq, TagFromName.Modality);
		//ScheduledStationAeTitle = Attribute.getSingleStringValueOrNull(seq, TagFromName.ScheduledStationAETitle);
		//ScheduledProcedureStepStartDate = Attribute.getSingleStringValueOrNull(seq, TagFromName.ScheduledProcedureStepStartDate);
		//ScheduledProcedureStepStartTime = Attribute.getSingleStringValueOrNull(seq, TagFromName.ScheduledProcedureStepStartTime);
		//ScheduledPerformingPhysician = Attribute.getSingleStringValueOrNull(seq, TagFromName.ScheduledPerformingPhysicianName);
		//ScheduledProcedureStepDescription = Attribute.getSingleStringValueOrNull(seq, TagFromName.ScheduledProcedureStepDescription);
		//ScheduledProcedureStepID = Attribute.getSingleStringValueOrNull(seq, TagFromName.ScheduledProcedureStepID);
		//ScheduledStationName = Attribute.getSingleStringValueOrNull(seq, TagFromName.ScheduledStationName);
		//ScheduledProcedureStepLocation = Attribute.getSingleStringValueOrNull(seq, TagFromName.ScheduledProcedureStepLocation);
		//ScheduledProcedureStepStatus = Attribute.getSingleStringValueOrNull(seq, TagFromName.ScheduledProcedureStepStatus);
	}
	// Getter Functions
	public String getPatientName(){
		return(PatientName);
	}
	public String getPatientID(){
		return(PatientID);
	}
	public String getPatientBirthDate(){
		return(PatientBirthDate);
	}
	public String getPatientSex(){
		return(PatientSex);
	}
	public String getMedicalAlerts(){
		return(MedicalAlerts);
	}
	public String getAllergies(){
		return(Allergies);
	}

	public String getAccessionNumber(){
		return(AccessionNumber);
	}
	/**
		Fallnummer?
	*/
	public String getAdmissionID(){
		return(AdmissionID);
	}
	public String getStudyInstanceUid(){
		return(StudyInstanceUid);
	}

	/**
		Ueberweisender Arzt
	*/
	public String getReferringPhysicianName(){
		return(ReferringPhysicianName);
	}
	/**
		Verordnender Arzt
	*/
	public String getRequestingPhysician(){
		return(RequestingPhysician);
	}
	public String getRequestingService(){
		return(RequestingService);
	}
	public String getRequestedProcedureDescription(){
		return(RequestedProcedureDescription);
	}
	public String getRequestedProcedureID(){
		return(RequestedProcedureID);
	}
	public String getRequestedProcedurePriority(){
		return(RequestedProcedurePriority);
	}
	public String getPatientTransportArrangements(){
		return(PatientTransportArrangements);
	}
	public String getPlacerOrderNumberOfImageServiceRequest(){
		return(PlacerOrderNumberOfImageServiceRequest);
	}
	public String getFillerOrderNumberOfImageServiceRequest(){
		return(FillerOrderNumberOfImageServiceRequest);
	}

	public String getModality(){
		return(Modality);
	}
	public String getScheduledStationAeTitle(){
		return(ScheduledStationAeTitle);
	}
	public String getScheduledProcedureStepStartDate(){
		return(ScheduledProcedureStepStartDate);
	}
	public String getScheduledProcedureStepStartTime(){
		return(ScheduledProcedureStepStartTime);
	}
	/**
		Ausfuehrender Arzt
	*/
	public String getScheduledPerformingPhysician(){
		return(ScheduledPerformingPhysician);
	}
	public String getScheduledProcedureStepDescription(){
		return(ScheduledProcedureStepDescription);
	}
	public String getScheduledProcedureStepID(){
		return(ScheduledProcedureStepID);
	}
	public String getScheduledStationName(){
		return(ScheduledStationName);
	}
	public String getScheduledProcedureStepLocation(){
		return(ScheduledProcedureStepLocation);
	}
	public String getScheduledProcedureStepStatus(){
		return(ScheduledProcedureStepStatus);
	}
	
	public String getFormattedString(){
		return this.PatientName+"; "+this.PatientID+"; "+this.AccessionNumber;
	}
}