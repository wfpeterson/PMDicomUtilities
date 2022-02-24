/*
 * Created on Jun 12, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gov.va.med.imaging.dicom.utils.pixelmed.model;

import java.util.HashMap;
import java.util.Vector;

import com.pixelmed.dicom.AgeStringAttribute;
import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeTag;
import com.pixelmed.dicom.DateAttribute;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.LongStringAttribute;
import com.pixelmed.dicom.LongTextAttribute;
import com.pixelmed.dicom.PersonNameAttribute;
import com.pixelmed.dicom.SequenceAttribute;
import com.pixelmed.dicom.ShortStringAttribute;
import com.pixelmed.dicom.ShortTextAttribute;
import com.pixelmed.dicom.TagFromName;

import gov.va.med.imaging.dicom.utilities.exceptions.DataCreationException;
import gov.va.med.imaging.dicom.utilities.model.IDataControl;

/**
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 *
 *
 * @author William Peterson
 *
 */
public class DataControl implements IDataControl<Attribute> {

	private boolean goldDBImages = false;
    private HashMap<String,Vector<Attribute>> uids;
    
    /**
     * Constructor
     *
     * 
     */
    public DataControl() {
        super();
        // TODO Auto-generated constructor stub
        uids = new HashMap<String,Vector<Attribute>>();
    }
    
    /* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.model.IDataControl#isGoldDBImages()
	 */
    @Override
	public boolean isGoldDBImages() {
		return goldDBImages;
	}


	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.model.IDataControl#setGoldDBImages(boolean)
	 */
	@Override
	public void setGoldDBImages(boolean goldDBImages) {
		this.goldDBImages = goldDBImages;
	}

	
	@Override
	public Vector<Attribute> getDataList(String studyUID)throws DataCreationException{
        Vector<Attribute> fieldData;
        fieldData = this.getVAData(studyUID);
        
        return fieldData;
    }
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.model.IDataControl#getDataListForTextFile()
	 */
	@Override
	public HashMap<String,String> getDataListForTextFile(){
		HashMap<String,String> fieldData = this.CreateNuVADataForTextFile();
		
		return fieldData;
	}
    
	private Vector<Attribute> getVAData(String studyUID)throws DataCreationException{
        Vector<Attribute> fieldData;
        
        fieldData = (Vector<Attribute>)this.uids.get(studyUID);
        if(fieldData == null){
            fieldData = (Vector<Attribute>) this.CreateNuVAData();
            uids.put(studyUID, fieldData);
        }
        return fieldData;
    }
	
	
    
    private Vector<Attribute> CreateNuVAData() throws DataCreationException{
        Vector<Attribute> fieldData = new Vector<Attribute>();
        //Create default, bogus data according to VA specs.
        
        try{
        	if(!this.isGoldDBImages()){
	            //Social security numbers
	            Attribute a = new LongStringAttribute(TagFromName.PatientID);
	            a.addValue("000-00-0000");
	            fieldData.add(a);
        	}
            Attribute otherPatientID = new LongStringAttribute(TagFromName.OtherPatientIDs);
            fieldData.add(otherPatientID);
            if(!this.isGoldDBImages()){
	            Attribute name = new PersonNameAttribute(TagFromName.PatientName);
	            name.addValue("IMAGPATIENT^ONE");
	            fieldData.add(name);
            }
            Attribute birth = new DateAttribute(TagFromName.PatientBirthDate);
            birth.addValue("02291901");
            fieldData.add(birth);
            Attribute otherName = new PersonNameAttribute(TagFromName.OtherPatientNames);
            fieldData.add(otherName);
            Attribute maidenName = new PersonNameAttribute(TagFromName.PatientMotherBirthName);
            fieldData.add(maidenName);
            //Provider names
            Attribute physician = new PersonNameAttribute(TagFromName.ReferringPhysicianName);
            physician.addValue("IMAGPROVIDER^ONE^DR");
            fieldData.add(physician);
            Attribute physicianAddress = new ShortTextAttribute(TagFromName.ReferringPhysicianAddress);
            fieldData.add(physicianAddress);
            Attribute physicianPhone = new ShortStringAttribute(TagFromName.ReferringPhysicianTelephoneNumbers);
            fieldData.add(physicianPhone);
            Attribute physicianSequence = new SequenceAttribute(TagFromName.ReferringPhysicianIdentificationSequence);
            fieldData.add(physicianSequence);
            //Claim number
            //Service number
            //Next of kin
            //Alias
            //Telephone numbers
            Attribute patientPhone = new ShortStringAttribute(TagFromName.PatientTelephoneNumbers);
            fieldData.add(patientPhone);
            //Fax numbers
            //All elements of dates (except year) for dates directly related to an individual, 
            //  including birth date, admission date, discharge date, date of death        
            Attribute patientAge = new AgeStringAttribute(TagFromName.PatientAge);
            fieldData.add(patientAge);
            //Electronic mail addresses        
            Attribute patientAddress = new ShortTextAttribute(TagFromName.PatientAddress);
            fieldData.add(patientAddress);
            //Medical record number        
        	if(!this.isGoldDBImages()){
        		Attribute accession = new ShortStringAttribute(TagFromName.AccessionNumber);
        		accession.addValue("023006-000");
        		fieldData.add(accession);
        	}
            //Health plan beneficiary numbers
            //Account numbers
            //Certificate/license numbers
            //Vehicle identifiers and serial numbers, including license plate numbers
            //Device identifiers and serial numbers
            Attribute deviceSerialNum = new LongStringAttribute(TagFromName.DeviceSerialNumber);
            fieldData.add(deviceSerialNum);
            //Biometric identifiers, including finger and voice prints
            //Full face photographic images and any comparable images
            Attribute comments = new LongTextAttribute(new AttributeTag(0x0008,0x4000));
            fieldData.add(comments);
            Attribute patientComments = new LongTextAttribute(TagFromName.PatientComments);
            patientComments.addValue("Patient Data Scrubbed.");
            fieldData.add(patientComments);
            Attribute studyComments = new LongTextAttribute(TagFromName.StudyComments);
            studyComments.addValue("Patient Data Scrubbed.");
            fieldData.add(studyComments);
            Attribute additionalHistory = new LongTextAttribute(TagFromName.AdditionalPatientHistory);
            additionalHistory.addValue("No Additional History.");
            fieldData.add(additionalHistory);
            Attribute hospital = new LongStringAttribute(TagFromName.InstitutionName);
            hospital.addValue("ABC General Hospital");
            fieldData.add(hospital);
            Attribute hospitalAddress = new ShortTextAttribute(TagFromName.InstitutionAddress);
            fieldData.add(hospitalAddress);
            Attribute operator = new PersonNameAttribute(TagFromName.OperatorsName);
            operator.addValue("IMAGOPERATOR^ONE");
            fieldData.add(operator);
            Attribute performingPhysician = new PersonNameAttribute(TagFromName.PerformingPhysicianName);
            performingPhysician.addValue("IMAGPROVIDER^TWO^DR");
            fieldData.add(performingPhysician);
            Attribute requestingPhysician = new PersonNameAttribute(TagFromName.RequestingPhysician);
            requestingPhysician.addValue("IMAGPROVIDER^THREE^DR");
            fieldData.add(requestingPhysician);
            Attribute schedulingPhysician = new PersonNameAttribute(TagFromName.ScheduledPerformingPhysicianName);
            schedulingPhysician.addValue("IMAGPROVIDER^FOUR^DR");
            fieldData.add(schedulingPhysician);
            
            return fieldData;
        }
        catch(DicomException de){
            System.out.println("Failure while generating mock dataset.");
            throw new DataCreationException("Failed generating mock dataset.");
        }
    }
    
    
    private HashMap<String,String> CreateNuVADataForTextFile(){
    		HashMap<String,String> fieldData = new HashMap<String,String>();

    		
        //Social security numbers
        if(!this.isGoldDBImages()){
	        String patientID = "0010,0020";
	        String patientIDValue = "000-00-0000";
	        fieldData.put(patientID, patientIDValue);
	        String otherPatientID = "0010,1000";
	        String otherPatientIDValue = "";
	        fieldData.put(otherPatientID, otherPatientIDValue);
        }
    	if(!this.isGoldDBImages()){
	    	String name = "0010,0010";
	        String nameValue = "IMAGPATIENT^ONE";
	        fieldData.put(name, nameValue);
    	}
    	if(!this.isGoldDBImages()){
	        String birth = "0010,0030";
	        String birthValue = "02291901";
	        fieldData.put(birth, birthValue);
    	}
        String birthName = "0010,1005";
        String birthNameValue = "";
        fieldData.put(birthName, birthNameValue);
        String otherName = "0010,1001";
        String otherNameValue = "";
        fieldData.put(otherName, otherNameValue);
        String maidenName = "0010,1060";
        String maidenNameValue = "";
        fieldData.put(maidenName, maidenNameValue);
        //Provider names
        String physician = "0008,0090";
        String physicianValue = "IMAGPROVIDER^ONE^DR";
        fieldData.put(physician, physicianValue);
        String physicianAddress = "0008,0092";
        String physicianAddressValue = "";
        fieldData.put(physicianAddress, physicianAddressValue);
        String physicianPhone = "0008,0094";
        String physicianPhoneValue = "";
        fieldData.put(physicianPhone, physicianPhoneValue);
        String physicianSequence = "0008,0096";
        String physicianSequenceValue = "";
        fieldData.put(physicianSequence, physicianSequenceValue);
        //Claim number
        //Service number
        //Next of kin
        //Alias
        //Telephone numbers
        String patientPhone = "0010,2154";
        String patientPhoneValue = "";
        fieldData.put(patientPhone, patientPhoneValue);
        //Fax numbers
        //All elements of dates (except year) for dates directly related to an individual, 
        //  including birth date, admission date, discharge date, date of death        
        String patientAge = "0010,1010";
        String patientAgeValue = "";
        fieldData.put(patientAge, patientAgeValue);
        //Electronic mail addresses        
        String patientAddress = "0010,1040";
        String patientAddressValue = "";
        fieldData.put(patientAddress, patientAddressValue);
        //Medical record number       
    	if(!this.isGoldDBImages()){
	        String accession = "0008,0050";
	        String accessionValue = "023006-000";
	        fieldData.put(accession, accessionValue);
    	}
        //Health plan beneficiary numbers
        //Account numbers
        //Certificate/license numbers
        //Vehicle identifiers and serial numbers, including license plate numbers
        //Device identifiers and serial numbers
        String deviceSerialNum = "0018,1000";
        String deviceSerialNumValue = "";
        fieldData.put(deviceSerialNum, deviceSerialNumValue);
        //Biometric identifiers, including finger and voice prints
        //Full face photographic images and any comparable images
        String comments = "0008,0x4000";
        String commentsValue = "";
        fieldData.put(comments, commentsValue);
        String patientComments = "0010,4000";
        String patientCommentsValue = "Patient Data Scrubbed.";
        fieldData.put(patientComments, patientCommentsValue);
        String studyComments = "0032,4000";
        String studyCommentsValue = "Patient Data Scrubbed.";
        fieldData.put(studyComments, studyCommentsValue);
        String additionalHistory = "0010,21B0";
        String additionalHistoryValue = "No Additional History.";
        fieldData.put(additionalHistory, additionalHistoryValue);
        String hospital = "0008,0080";
        String hospitalValue = "ABC General Hospital";
        fieldData.put(hospital, hospitalValue);
        String hospitalAddress = "0008,0081";
        String hospitalAddressValue = "";
        fieldData.put(hospitalAddress, hospitalAddressValue);
        String operator = "0008,1070";
        String operatorValue = "IMAGOPERATOR^ONE";
        fieldData.put(operator, operatorValue);
        String performingPhysician = "0008,1050";
        String performingPhysicianValue = "IMAGPROVIDER^TWO^DR";
        fieldData.put(performingPhysician, performingPhysicianValue);
        String requestingPhysician = "0032,1032";
        String requestingPhysicianValue = "IMAGPROVIDER^THREE^DR";
        fieldData.put(requestingPhysician, requestingPhysicianValue);
        String schedulingPhysician = "0040,0006";
        String schedulingPhysicianValue = "IMAGPROVIDER^FOUR^DR";
        fieldData.put(schedulingPhysician, schedulingPhysicianValue);
	
        return fieldData;
    }
}
