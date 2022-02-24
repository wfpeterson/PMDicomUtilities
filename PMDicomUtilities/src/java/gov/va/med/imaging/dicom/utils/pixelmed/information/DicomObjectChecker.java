package gov.va.med.imaging.dicom.utils.pixelmed.information;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.AttributeTag;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.DicomFileUtilities;
import com.pixelmed.dicom.SOPClass;
import com.pixelmed.dicom.TagFromName;

import gov.va.med.imaging.dicom.utilities.information.IDicomObjectChecker;

public class DicomObjectChecker implements IDicomObjectChecker {

	File file = null;
	AttributeList dds = null;
	boolean dicomObjectFile = false;
	
	protected DicomObjectChecker(){
		
	}
	
	public DicomObjectChecker(String filename) throws IOException, DicomException{
		this(new File(filename));
	}
	
	public DicomObjectChecker(File file) throws IOException, DicomException{
		this.file = file;
		dicomObjectFile = DicomFileUtilities.isDicomOrAcrNemaFile(this.file);
		if(dicomObjectFile){
			this.dds = new AttributeList();
			dds.read(this.file, TagFromName.PixelData);
		}
	}
	
	public static boolean isDicomObjectFile(String filename){
		return DicomFileUtilities.isDicomOrAcrNemaFile(filename);		
	}
	
	public static boolean isDicomObjectFile(File file){
		return DicomFileUtilities.isDicomOrAcrNemaFile(file);		
	}
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.information.IDicomObjectChecker#isDicomObjectFile()
	 */
	@Override
	public boolean isDicomObjectFile(){
		return dicomObjectFile;
	}
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.information.IDicomObjectChecker#containsPatientData()
	 */
	@Override
	public boolean containsPatientData(){
	    
		if(dds.containsKey(TagFromName.PatientComments)){
	        Attribute patientcomment = dds.get(TagFromName.PatientComments);
	        String comment = patientcomment.getSingleStringValueOrEmptyString();
	        if(comment.equals("Patient Data Scrubbed.")){
	        	return false;
	        }
		}
		if(dds.containsKey(TagFromName.ImplementationVersionName)){
	        Attribute versionName = dds.get(TagFromName.ImplementationVersionName);
	        String version = versionName.getSingleStringValueOrEmptyString();
	        if(version.equals("PIXELMEDJAVA001")){
	        	return false;
	        }
		}
		if(dds.containsKey(TagFromName.InstitutionName)){
	        Attribute institution = dds.get(TagFromName.InstitutionName);
	        String name = institution.getSingleStringValueOrEmptyString();
	        if(name.equals("ABC General Hospital")){
	        	return false;
	        }
		}
        return true;
	}
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.information.IDicomObjectChecker#containsGroup6000()
	 */
	@Override
	public boolean containsGroup6000(){
		
		Iterator<AttributeTag> iter = dds.keySet().iterator();
		
		while(iter.hasNext()){
			AttributeTag tag = (AttributeTag) iter.next();
			if(tag.isOverlayGroup()){
				return true;
			}
		}
		return false;		
	}
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.information.IDicomObjectChecker#containsBurnInAnnotation()
	 */
	@Override
	public boolean containsBurnInAnnotation(){
        if(dds.containsKey(TagFromName.BurnedInAnnotation)){
            Attribute burninAnnotation = dds.get(TagFromName.BurnedInAnnotation);
	        String annotation = burninAnnotation.getSingleStringValueOrEmptyString();
			if(annotation.equalsIgnoreCase("YES")){
				return true;
			}
        }
		return false;
	}
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.information.IDicomObjectChecker#containsBurnInAnnotationByModality()
	 */
	@Override
	public boolean containsBurnInAnnotationByModality(){
		if(dds.containsKey(TagFromName.MediaStorageSOPClassUID)){
	        Attribute sopClass = dds.get(TagFromName.MediaStorageSOPClassUID);
	        String sopClassUID = sopClass.getSingleStringValueOrEmptyString();
	        if(sopClassUID.equals(SOPClass.SecondaryCaptureImageStorage)
	        		|| sopClassUID.equals(SOPClass.UltrasoundImageStorage)
	        		|| sopClassUID.equals(SOPClass.UltrasoundMultiframeImageStorage)
	        		|| sopClassUID.equals(SOPClass.UltrasoundImageStorageRetired)
	        		|| sopClassUID.equals(SOPClass.UltrasoundMultiframeImageStorageRetired)){
	        	
	        	return true;
	        }
		}
        return false;
		
	}
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.information.IDicomObjectChecker#containsPrivateAttributes()
	 */
	@Override
	public boolean containsPrivateAttributes(){
		Iterator<AttributeTag> iter = dds.keySet().iterator();
		
		while(iter.hasNext()){
			AttributeTag tag = (AttributeTag) iter.next();
			if(tag.isPrivate()){
				return true;
			}
		}
		return false;

	}
	
	protected void checkObj (AttributeList dds){
		this.dds = dds;
	}

	@Override
	public IDicomObjectChecker getInstance() {
		// TODO Auto-generated method stub
		return null;
	}

}
