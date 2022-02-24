/*
 * Created on May 22, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gov.va.med.imaging.dicom.utils.pixelmed.scrubber;

import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.DateAttribute;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.LongStringAttribute;
import com.pixelmed.dicom.PersonNameAttribute;
import com.pixelmed.dicom.TagFromName;

/**
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 *
 *
 * @author William Peterson
 *
 */
public class DicomScrub{

    /**
     * Constructor
     *
     * 
     */
    public DicomScrub() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    //FIXME DicomScrub should just call this method in VAScrub class.  Not create a 
    //  duplicate method.
    /* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.scrubber.IDicomScrub#scrubVAElements(com.pixelmed.dicom.AttributeList)
	 */
	public void scrubVAElements(AttributeList dds) throws DicomException{
        //Strip the following info from the DICOM Header
        
        
        //Social security numbers
        dds.remove(TagFromName.PatientID);
        Attribute a = new LongStringAttribute(TagFromName.PatientID);
        a.addValue("000-00-1234");
        dds.put(TagFromName.PatientID, a);
        dds.remove(TagFromName.OtherPatientIDs);
        //Patient names
        dds.remove(TagFromName.PatientName);
        Attribute name = new PersonNameAttribute(TagFromName.PatientName);
        name.addValue("IMAGPATIENT^TEST");
        dds.put(TagFromName.PatientName, name);
        //dds.remove(TagFromName.PatientBirthName);
        dds.remove(TagFromName.OtherPatientNames);
        dds.remove(TagFromName.PatientMotherBirthName);
        //Provider names
        dds.remove(TagFromName.ReferringPhysicianName);
        Attribute physician = new PersonNameAttribute(TagFromName.ReferringPhysicianName);
        physician.addValue("IMAGPROVIDER^TEST");
        dds.put(TagFromName.ReferringPhysicianName, physician);
        dds.remove(TagFromName.ReferringPhysicianAddress);
        dds.remove(TagFromName.ReferringPhysicianIdentificationSequence);
        //Claim number
        //Service number
        //Next of kin
        //Alias
        //Telephone numbers
        dds.remove(TagFromName.PatientTelephoneNumbers);
        //Fax numbers
        //All elements of dates (except year) for dates directly related to an individual, 
        //  including birth date, admission date, discharge date, date of death
        dds.remove(TagFromName.PatientBirthDate);
        Attribute birth = new DateAttribute(TagFromName.PatientBirthDate);
        birth.addValue("19500229");
        dds.put(TagFromName.PatientBirthDate, birth);
        dds.remove(TagFromName.PatientAge);
        //Electronic mail addresses
        dds.remove(TagFromName.PatientAddress);
        //Medical record number
        dds.remove(TagFromName.AccessionNumber);
        //Health plan beneficiary numbers
        //Account numbers
        //Certificate/license numbers
        //Vehicle identifiers and serial numbers, including license plate numbers
        //Device identifiers and serial numbers
        dds.remove(TagFromName.DeviceSerialNumber);
        //Biometric identifiers, including finger and voice prints
        //Full face photographic images and any comparable images
        
        
    }
    
}
