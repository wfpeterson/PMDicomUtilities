/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: December 6, 2005
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  VHAISWPETERB
  Description: 

        ;; +--------------------------------------------------------------------+
        ;; Property of the US Government.
        ;; No permission to copy or redistribute this software is given.
        ;; Use of unreleased versions of this software requires the user
        ;;  to execute a written test agreement with the VistA Imaging
        ;;  Development Office of the Department of Veterans Affairs,
        ;;  telephone (301) 734-0100.
        ;;
        ;; The Food and Drug Administration classifies this software as
        ;; a Class II medical device.  As such, it may not be changed
        ;; in any way.  Modifications to this software may result in an
        ;; adulterated medical device under 21CFR820, the use of which
        ;; is considered to be a violation of US Federal Statutes.
        ;; +--------------------------------------------------------------------+
 */
package gov.va.med.imaging.dicom.utils.pixelmed.recon;

import java.util.HashMap;

import com.pixelmed.dicom.SOPClass;

/**
 *
 * @author William Peterson
 *
 */
public class SOPClassModalityCodeMapping {

    
    HashMap<String, String> sop_mcMapping = null;
    /**
     * Constructor
     *
     * 
     */
    public SOPClassModalityCodeMapping() {
        super();
        //
        this.makeMappingSet(); 
        
    }

    //This only is used to populate the Modality Code, if necessary, in case there is more 
    //  than one Modality Code in the field.  This is only called when converting a Legacy
    //  TGA file.  I do not currently see the need to implement it in the Listen file.
    private void makeMappingSet(){
        this.sop_mcMapping = new HashMap<String, String>();
        
        this.sop_mcMapping.put(SOPClass.ComputedRadiographyImageStorage, "CR");
        this.sop_mcMapping.put(SOPClass.CTImageStorage, "CT");
        this.sop_mcMapping.put(SOPClass.MRImageStorage, "MR");
        this.sop_mcMapping.put(SOPClass.UltrasoundImageStorage, "US");
        this.sop_mcMapping.put(SOPClass.UltrasoundMultiframeImageStorage, "US");
        this.sop_mcMapping.put(SOPClass.NuclearMedicineImageStorage, "NM");
        //this.sop_mcMapping.put(UID.SOPCLASSSECONDARYCAPTURE, "OT");
        this.sop_mcMapping.put(SOPClass.XRayAngiographicImageStorage, "XA");
        this.sop_mcMapping.put(SOPClass.XRayRadioFlouroscopicImageStorage, "RF");
        this.sop_mcMapping.put(SOPClass.DigitalXRayImageStorageForPresentation, "XA");
        this.sop_mcMapping.put(SOPClass.DigitalXRayImageStorageForProcessing, "XA");
        this.sop_mcMapping.put(SOPClass.VisibleLightEndoscopicImageStorage, "ES");
        this.sop_mcMapping.put(SOPClass.VisibleLightMicroscopicImageStorage, "GM");
        this.sop_mcMapping.put(SOPClass.VisibleLightSlideCoordinatesMicroscopicImageStorage, "SM");
        this.sop_mcMapping.put(SOPClass.VisibleLightPhotographicImageStorage, "XC");
        this.sop_mcMapping.put(SOPClass.VideoEndoscopicImageStorage, "ES");
        this.sop_mcMapping.put(SOPClass.VideoMicroscopicImageStorage, "GM");
        this.sop_mcMapping.put(SOPClass.VideoPhotographicImageStorage, "XC");
        this.sop_mcMapping.put(SOPClass.BasicVoiceStorage, "AU");
        this.sop_mcMapping.put(SOPClass.TwelveLeadECGStorage, "ECG");
        this.sop_mcMapping.put(SOPClass.GeneralECGStorage, "ECG");
        this.sop_mcMapping.put(SOPClass.AmbulatoryECGStorage, "ECG");
        this.sop_mcMapping.put(SOPClass.HemodynamicWaveformStorage, "HD");
        this.sop_mcMapping.put(SOPClass.CardiacElectrophysiologyWaveformStorage, "EPS");
        this.sop_mcMapping.put(SOPClass.EnhancedXAImageStorage, "XA");
        this.sop_mcMapping.put(SOPClass.EnhancedXRFImageStorage, "RF");
        this.sop_mcMapping.put(SOPClass.RTImageStorage, "RTIMAGE");
        this.sop_mcMapping.put(SOPClass.RTDoseStorage, "RTDOSE");
        this.sop_mcMapping.put(SOPClass.RTStructureSetStorage, "RTSTRUCT");
        this.sop_mcMapping.put(SOPClass.RTIonPlanStorage, "RTPLAN");
        this.sop_mcMapping.put(SOPClass.RTTreatmentSummaryRecordStorage, "RTRECORD");
        this.sop_mcMapping.put(SOPClass.RTBeamsTreatmentRecordStorage, "RTRECORD");
        this.sop_mcMapping.put(SOPClass.RTBrachyTreatmentRecordStorage, "RTRECORD");
        this.sop_mcMapping.put(SOPClass.DigitalMammographyXRayImageStorageForPresentation, "MG");
        this.sop_mcMapping.put(SOPClass.DigitalMammographyXRayImageStorageForProcessing, "MG");
        this.sop_mcMapping.put(SOPClass.EnhancedMRImageStorage, "MR");
        this.sop_mcMapping.put(SOPClass.EnhancedCTImageStorage, "CT");
        this.sop_mcMapping.put(SOPClass.OphthalmicPhotography16BitImageStorage, "OP");
        this.sop_mcMapping.put(SOPClass.OphthalmicPhotography8BitImageStorage, "OP");
        this.sop_mcMapping.put(SOPClass.StereometricRelationshipStorage, "SMR");
    }
    
    public String getModalityCode(String sopClassUID){
        
        return this.sop_mcMapping.get(sopClassUID);
    }
}
