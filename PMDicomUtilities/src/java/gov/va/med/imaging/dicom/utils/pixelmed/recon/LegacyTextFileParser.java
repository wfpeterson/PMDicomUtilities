/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: September 26, 2006
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


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeFactory;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.AttributeTag;
import com.pixelmed.dicom.CodeStringAttribute;
import com.pixelmed.dicom.DicomDictionary;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.OtherByteAttribute;
import com.pixelmed.dicom.OtherWordAttribute;
import com.pixelmed.dicom.SequenceAttribute;
import com.pixelmed.dicom.SignedLongAttribute;
import com.pixelmed.dicom.SignedShortAttribute;
import com.pixelmed.dicom.TagFromName;
import com.pixelmed.dicom.UnsignedLongAttribute;
import com.pixelmed.dicom.UnsignedShortAttribute;
import com.pixelmed.dicom.ValueRepresentation;

import gov.va.med.imaging.dicom.utilities.exceptions.TextFileException;
import gov.va.med.imaging.dicom.utilities.exceptions.TextFileExtractionException;
import gov.va.med.imaging.dicom.utilities.exceptions.TextFileNotFoundException;
import gov.va.med.imaging.dicom.utilities.utils.StringUtils;

/**
 *
 * Text File Parser class. This parser is specific to the Vista Imaging Legacy
 * environment.  The class will parse the Text file passed from Archiving 
 * and build a new DicomDataSet from the Text file information.  Then the class shall
 * wrap the DicomDataSet object into a generic DicomDataSet object.  The wrapping allows
 * the DicomDataSet object to be passed to the Dicom Generic Layer.
 *
 * @author William Peterson
 * extended by Csaba Titton
 * 			for ViX streaming 
 */

//NOTE 0008,1032/0008,0103 and
//		0040,0260/0008,0103 and
//		0040,0275/0040,0008/0008,0103 has no value in output14.dcm. Investigated the issue.
//	This is acceptable according to the DICOM Standard.

public class LegacyTextFileParser {
    /*
     * Create a DicomDataSet object.  This object is DCF Toolkit specific.
     */
    private AttributeList dicomDataSet = null;
    
    private DicomDictionary dd = null;
    
    //private IDicomDataSet toolkitDDS = null;
    
    private OriginalPixelDataInfo originalPixelData = null;
    
    //private static Logger LOGGER = Logger.getLogger (LegacyTextFileParser.class);
    private static Logger LOGGER = LogManager.getLogger ("VA_DICOM");
    private static Logger TESTLOGGER = LogManager.getLogger("JUNIT_TEST");

    
    /**
     * Using Spring Factory.  Set QueryFastLaneFacade.
     * 
     * @param represents the Spring Factory selected object
     */
    //public void setDicomDataSet(IDicomDataSet toolkitDDS) {
    //    this.toolkitDDS = toolkitDDS;
    //}

    
    /**
     * Constructor
     */
    public LegacyTextFileParser() {
        super();
        this.dd = new DicomDictionary();
    }
    
    /**
     * Invoke method to create a DicomDataSet based on a Text file.
     * 
     * @param textFilename represents the name (and path) of the Text file.
     * @return represents the encapsulated DicomDataSet that is safe for DicomGeneric Layer.
     * @throws TextFileNotFoundException
     * @throws TextFileException
     * @throws TextFileExtractionException
     */
    public AttributeList createDicomDataSet(String textFilename, OriginalPixelDataInfo pixelData)
            throws TextFileNotFoundException, TextFileException, 
            TextFileExtractionException{
        
        LOGGER.info(this.getClass().getName()+": Dicom Toolkit Layer: " +
                "...parsing Text file into DicomDataSet.");
        LOGGER.debug("Text File: "+ textFilename);
        this.originalPixelData = pixelData;
        BufferedReader buffer = null;
        try{
            //Get Text file.
            //JUNIT Create test to verify how this fails if not correct permissions.
            buffer = new BufferedReader(new FileReader(textFilename));
            //Invoke parser.
            this.parseTextFile(buffer, true);
            //REMINDER Find out why I have the following line.  It does not make sense, but
            //	I don't want to change it now.  Unsure of effects if omitted.
            pixelData = this.originalPixelData;
            //encapsulate DicomDataSet object.
            //return (this.encapsulateDicomDataSet());
            return this.dicomDataSet;
        }
        catch(FileNotFoundException noFile){
            LOGGER.error(noFile.getMessage());
            LOGGER.error(this.getClass().getName()+": Dicom Toolkit layer: " +
                    "Exception thrown while attempting to open "+textFilename+".");
            throw new TextFileNotFoundException("Could not find or open "+textFilename+".", noFile);
        }
        finally{
    		if(buffer != null){
    			try{
        			buffer.close();
        		}
    			catch(Throwable T){
        			LOGGER.error(this.getClass().getName()+": Dicom Toolkit layer: "+
        					"Exception thrown while closing Text File "+textFilename+".");
    			}
    			System.gc();
    		}
        }     
    		
    }
    /**
     * Invoke method to create a DicomDataSet based on a Text data stream.
     * 
     * @param textStream represents the stream of VistA Imaging TXT data.
     * @return represents the encapsulated DicomDataSet that is safe for DicomGeneric Layer.
     * @throws TextFileNotFoundException
     * @throws TextFileException
     * @throws TextFileExtractionException
     */
/*
    public IDicomDataSet createDicomDataSet(BufferedReader buffer, OriginalPixelDataInfo pixelData)
            throws  TextFileException, TextFileExtractionException {
        
        LOGGER.info("... Dicom Toolkit Layer: parsing Text data into DicomDataSet ...");
        LOGGER.debug("... Start Text Data Stream parsing... ");
        this.originalPixelData = pixelData;

        //Invoke parser.
        this.parseTextFile(buffer, false);
        
        pixelData = this.originalPixelData;
        //encapsulate DicomDataSet object.
        return (this.encapsulateDicomDataSet());
    }
*/
    
    /**
     * Encapsulates the DCF Toolkit specific DicomDataSet object.
     * 
     * @return represents the Generic DicomDataSet object.
     */
/*    
    private IDicomDataSet encapsulateDicomDataSet(){
        
        TESTLOGGER.info("... encapsulating DDS ...");
        //FUTURE Change this for Spring use.
        try{
            toolkitDDS = new DicomDataSetImpl(dicomDataSet);
        }
        catch(Exception e){
            LOGGER.error("Error: " + e.getMessage());
            LOGGER.error(this.getClass().getName()+": Dicom Toolkit layer: " +
                    "Exception thrown while encapsulating Dicom Dataset.");
            e.printStackTrace();
        }
        return toolkitDDS;
    }
*/
    
    /**
     * Invoke method to extract HIS updates from an open Text data stream.
     * 
     * @param buffer represents the stream of VistA Imaging TXT data.
     * @return HashMap of DICOm tag-value pairs to be updated in DICOM DataSet.
     * @throws TextFileExtractionException
     */
    public HashMap getHisUpdates(BufferedReader buffer)
            throws  TextFileException, TextFileExtractionException {
        
        LOGGER.info("... Parsing text data HIS update section ...");
        LOGGER.debug("... Continue Text Data parsing for VistA updates... ");

        HashMap hisChanges=null;
        hisChanges = this.parseHisUpdates(buffer);
        try {
        	buffer.close();
        }
        catch(IOException io){
            LOGGER.error("Cannot close Text Stream Buffer.");
            throw new TextFileExtractionException();
        }
        return (hisChanges);
    }
    
    /**
     * Invoke method to extract HIS updates from an open Text data stream.
     * 
     * @param buffer represents the stream of VistA Imaging TXT data.
     * @return HashMap of DICOm tag-value pairs to be updated in DICOM DataSet.
     * @throws TextFileExtractionException
     */
/*    
    public HashMap extractHisUpdatesfromTextStream(SizedInputStream sizedTextStream)
            throws  TextFileException, TextFileExtractionException {
        
        LOGGER.info("... Parsing text data HIS update section ...");
        LOGGER.debug("... Start parsing VistA updates... ");

        HashMap hisChanges=null;
        BufferedReader buffer = new BufferedReader(new InputStreamReader(sizedTextStream.getInStream()));

        hisChanges = this.parseHisUpdates(buffer);
        try {
        	buffer.close();
        }
        catch(IOException io){
            LOGGER.error("Cannot close Text Stream Buffer.");
            throw new TextFileExtractionException();
        }
        return (hisChanges);
    }
*/    
    /**
     * Parse the Text file.  The Text file is made up of two sections, "Data1" and 
     * "DICOM Data".  Both sections are read and decoded.
     * 
     * @param buffer represents the Text file now in the form of a BufferReader object.
     * @throws TextFileException
     * @throws TextFileExtractionException
     */
    private void parseTextFile(BufferedReader buffer, boolean doClose) throws TextFileException,
            TextFileExtractionException{
        
        String textLine = "";
        TESTLOGGER.info("... Parsing text data top section ...");
        try{
            //Loop thru the lines until $$BEGIN DATA1.
            //Ignore each line until $$BEGIN DATA1
            do{
                textLine = this.getNextTextLine(buffer);
            } while(!(textLine.equals("$$BEGIN DATA1")));
            
            do{
                textLine = this.getNextTextLine(buffer);
                //Extract only the PATIENTS_XXX fields
                //Replace commas with carats in Patient's Name field
                //100507-WFP-Removing all IF statements except DCM_TO_TGA_PARAMETERS.
                //  Reason is the application does not use this information.  It serves
                //  no purpose.  But I'm leaving the code in case I'm wrong.
                /*
                if(textLine.startsWith("PATIENTS_NAME")){
                    String patientsName = textLine.substring(textLine.indexOf("=")+1);
                }
                if(textLine.startsWith("PATIENTS_ID")){
                    String patientsID = textLine.substring(textLine.indexOf("=")+1);
                }
                if(textLine.startsWith("PATIENTS_BIRTH_DATE")){
                    String patientsBirth = textLine.substring(textLine.indexOf("=")+1);
                }
                if(textLine.startsWith("PATIENTS_AGE")){
                    String patientsAge = textLine.substring(textLine.indexOf("=")+1);
                }
                if(textLine.startsWith("PATIENTS_SEX")){
                    String patientsSex = textLine.substring(textLine.indexOf("=")+1);
                }
                if(textLine.startsWith("ACCESSION_NUMBER")){
                    String accessionNumber = textLine.substring(textLine.indexOf("=")+1);
                }
                */
                if(textLine.startsWith("DCM_TO_TGA_PARAMETERS")){
                    String dcmtotgaParameters = textLine.substring(textLine.indexOf("=")+1);
                    this.originalPixelData.setDcmtotgaParameters(dcmtotgaParameters);
                }
                //Place these PATIENTS_XXX fields into a temp DicomDataSet object.
            }while(!(textLine.equals("$$END DATA1")));
            
            //Invoke parseDicomDataSection to parse rest of file.
            this.parseDicomDataSection(buffer);
            if (doClose) 
            	buffer.close();
        }
        catch(IOException io){
            LOGGER.error("Cannot extract from Text File.");
            LOGGER.error("Working on Line: " + textLine);
            throw new TextFileExtractionException();
        }
    }
    
    /**
     * Parse the "DICOM Data" section of the Text file.  This is the grunt of the work
     * that needs to be done.
     * 
     * @param buffer represents the Text file now in the form of a BufferedReader object.
     * @throws TextFileExtractionException
     */
    private void parseDicomDataSection(BufferedReader buffer)
            throws TextFileExtractionException{
        
        this.dicomDataSet = new AttributeList();
        String textDicomLine = "";
        boolean sequenceFlag = false;
        SequenceAttribute sequenceElement = null;
        TESTLOGGER.info("... Parsing text data Dicom DataSet section ...");
        try{
            //Mark Buffer.
            buffer.mark(255);
            //Declare lineArray object.
            ArrayList<String> lineArray = new ArrayList<String>();
            //Loop thru the lines until $BEGIN DICOM DATA.
            //Ignore each line until $$BEGIN DICOM DATA. 
            do{
                textDicomLine = this.getNextTextLine(buffer);
            } while(!(textDicomLine.equals("$$BEGIN DICOM DATA")));
            
            textDicomLine = this.getNextTextLine(buffer);
            TESTLOGGER.debug("Current Line: " + textDicomLine);
            //Loop thru each line until $$END DICOM DATA.
            while(!(textDicomLine.equals("$$END DICOM DATA"))){
                //Check for Odd Group.
                String checkGroup = textDicomLine.substring(0,4);
                //String checkElement = textDicomLine.substring(5,9);
                int i = Integer.parseInt(checkGroup, 16);
                if(this.isGroupToBeAdded(i)){
                //If no Odd group or Group 88,
                    //Check if 9th character in line is a "|".
                    if(!(textDicomLine.substring(9,10).equals("|"))){
                    //If no,
                        //Add string to lineArray object.
                        lineArray.add(textDicomLine);
                        //Set Sequence flag.
                        sequenceFlag = true;
                    }
                    else{
                    //If yes, 
                        //Check if Sequence flag is set.
                        if(sequenceFlag){
                        //If yes,
                            //Reset Mark Buffer.  This allows to pick up the element after the
                            //  sequence again.
                            buffer.reset();
                            //Invoke extractSequenceData method and pass lineArray.
                            sequenceElement = this.extractSequenceData(lineArray);
                            //Add DicomSQElement object to dds.
                            this.dicomDataSet.put(sequenceElement);
                            //Clean lineArray object.
                            lineArray.clear();
                            //Unset the Sequence flag.
                            sequenceFlag = false;
                        }
                        else{
                        //If no,
                            //Invoke extractDicomElement method and pass the line.
                            this.extractDicomElement(textDicomLine, this.dicomDataSet);
                        }
                    //End If for "|" delimiter.
                    }
                //End If for Odd Group.
                }
                //Mark Buffer.
                buffer.mark(255);
                textDicomLine = this.getNextTextLine(buffer);
                TESTLOGGER.debug("Current Line: " + textDicomLine);

            //End Loop due to $$END DICOM DATA or EOF.
            }
        }
        catch(IOException io){
            LOGGER.error(io.getMessage());
            LOGGER.error(this.getClass().getName()+": " +
                    "Exception thrown while reading Text file's Dicom Data Section.");
            throw new TextFileExtractionException("Failure to read DicomData Section.", io);
        }
        catch(NumberFormatException number){
            LOGGER.error(number.getMessage());
            LOGGER.error(this.getClass().getName()+": Working on Dicom Line: " + textDicomLine);
            throw new TextFileExtractionException("Failure on Number Format.", number);
        }
        
        this.customTGAElementCleanup();
    }
    
    /**
     * Recursive method to handle the Sequences and nested Sequences inside of the "DICOM
     * Data" section.
     * 
     * @param lines represents the Array of Sequence lines in the Text file.
     * @return represents the Sequence lines converted into a single Dicom Sequence Element.
     * @throws TextFileExtractionException
     */
    private SequenceAttribute extractSequenceData(ArrayList<String> lines)
            throws TextFileExtractionException{
        
        AttributeList seqDDS = new AttributeList();
        ArrayList<AttributeList> ddsArrayList = new ArrayList<AttributeList>();
        //Create DicomSQElement object.
        SequenceAttribute sequence = null;
        
        String element = new String("");
        //Initialize seqDDS index to 0.
        int ddsIndex = 0;
        //Create previouseSeqItem.
        String previousSeqItem = "";
        //Set Sequence flag to false.
        boolean sequenceFlag = false;
        //Declare seqArray object.
        ArrayList<String> seqArray = new ArrayList<String>();
        //Loop thru each line of lines array until null.  Grab line in Loop.
        for(int x=0; x<lines.size(); x++){
            
            //Split line into two substrings using first period.
            String seqLine = (String)lines.get(x);
            String subLines[] = seqLine.split("\\.",2);
            //Assign first substring to Tag.
            element = subLines[0];
            //Split second substring into two sub-strings using first carat.
            String subSubLines[] = subLines[1].split("\\^",2);
            //Assign first sub-substring to seqItem.
            String seqItem = subSubLines[0];
            //Assign second sub-substring to seqArray object.
            String elementData = subSubLines[1];
            if(previousSeqItem.equals("")){
            //If yes, 
                //Initialize previousSeqItem with seqItem.
                previousSeqItem = seqItem;
            }
            //Check if first sub-substring matches seqItem.
            if(!(previousSeqItem.equals(seqItem))){
            //If no,
                ddsArrayList.add(seqDDS);
                seqDDS = new AttributeList();
                previousSeqItem = seqItem;
            //End If
            }
            String checkGroup = elementData.substring(0,4);
            int g = Integer.parseInt(checkGroup, 16);
            if(this.isGroupToBeAdded(g)){
                //Check if 9th character is a "|".  This means another Sequence.
                //This if/else determines if the line is another sequence.
                if(!(elementData.substring(9,10).equals("|"))){
                    //If no,
                    //Add string to seqArray object.
                    seqArray.add(elementData);
                    //Set Sequence flag.
                    sequenceFlag = true;
                    //If yes,
                }
                else{
                    //Check if Sequence flag is set.
                    if(sequenceFlag){
                        //If yes,
                        //Decrement lines array index.
                        x--;
                        //Re-invoke extractSequenceData method and pass lineArray.
                        SequenceAttribute subSequence = this.extractSequenceData(seqArray);
                        //add DicomSQElement object to dds.
                        seqDDS.put(subSequence);
                        //Unset the Sequence flag.

                        sequenceFlag = false;
                    }
                    else{
                        //If no,
                        //Invoke extractDicomElement method and pass the second substring and
                        //  and temp DicomDataSet object.
                        this.extractDicomElement(elementData, seqDDS);
                        //End If for Sequence flag.
                    }
                    //End If for "|" delimiter.
                }
                //End If for Group Check
            }
            //Increment lines array index.
        //Loop thru lines array is complete.
        }
        //Check if Sequence flag is set.
        if(sequenceFlag){
        //If yes,
            //Re-invoke extractSequenceData method and pass lineArray.
            SequenceAttribute subSequence = this.extractSequenceData(seqArray);
            //add DicomSQElement object to dds.
            seqDDS.put(subSequence);
            //Unset the Sequence flag.
            sequenceFlag = false;
        }
        AttributeTag tag = new AttributeTag(getGroupFromTag(element), getElementFromTag(element));
        sequence = new SequenceAttribute(tag);
        ddsArrayList.add(seqDDS);
        ddsIndex = ddsArrayList.size();
        for(int y=0; y<ddsIndex; y++){
           AttributeList ddsSeqItem = new AttributeList();
           ddsSeqItem = ddsArrayList.get(y); 
           sequence.addItem(ddsSeqItem);
        }
        return sequence;
    }
    
    /**
     * Extracts a single parsed line, independent of any sequences, and converts it to a DICOM 
     * Element and stores the DICOM Elment into the desired DCF Toolkit specific 
     * DicomDataSet.
     * 
     * @param line represents the parsed line from the Text file. 
     * @param dds represents the DCF Toolkit specific DicomDataSet object.
     * @throws TextFileExtractionException
     */
    private void extractDicomElement(String line, AttributeList dds)
            throws TextFileExtractionException{
        //Setup the ^ parser.
        String splitCaratPattern = "\\^";
        Pattern pInfo = Pattern.compile(splitCaratPattern);
        //Setup the | parser.
        String splitPipePattern = "\\|";
        Pattern pFields = Pattern.compile(splitPipePattern);
        //Setup the , parser.
        String splitCommaPattern = ",";
        Pattern pTag = Pattern.compile(splitCommaPattern);

        String fields[] = new String[4];
        fields = pFields.split(line);
        //Parse the basic data.
        //Set the Tag variable.
        String tag = fields[0];
        
        String elementInfo = fields[1];
        //VERIFY Make sure this is ok in the DCF environment.
        short elementVR = 0;

        try{
            AttributeTag aTag = new AttributeTag(getGroupFromTag(tag), getElementFromTag(tag));
            
            //Get the VR.
            if(elementInfo.charAt(elementInfo.length()-3) == '^'){
                String subElementInfo[] = new String[2];
                //VERIFY This line was not right, but it did not change from the Pixelmed conversion.  Make sure this works in the DCF environment.
                //subElementInfo = pInfo.split(line);
                subElementInfo = pInfo.split(elementInfo);
                elementVR = this.getVR(subElementInfo[1]);
                originalPixelData.setValueRepresentationInTextFile(true);
            }
            else{
                elementVR = this.getShortValue(dd.getValueRepresentationFromTag(aTag));
                originalPixelData.setValueRepresentationInTextFile(false);
            }

            String multiplicity;
            //This IF makes sure the VM,ML values exist.
            if(fields.length > 2){
                multiplicity = fields[2];
                //This IF makes sure there is a comma delimiter.
                if(multiplicity.indexOf(",") >= 0){
                    String value;
                    if(fields.length < 4){
                        value = "";
                    }
                    else{
                        value = fields[3];
                        if(value.equals("<unknown>")){
                            value = "";
                        }
                    }
                    String multiple[] = multiplicity.split(",");
                    //Assign the VM to a temp field. |VM,ML|
                    int vm = Integer.parseInt(multiple[0]);
                    //Assign the ML to a temp field. |VM,ML|
                    int ml = Integer.parseInt(multiple[1]);
                    //100407-WFP-Discovered DCF does not handle VR=OF.  Adding IF 
                    //  statement to ignore any text lines with this VR value.
                    if(elementVR == this.getShortValue(ValueRepresentation.OF)){
                        return;
                    }
                    //If VM is greater than 1.
                    if(vm > 1){

                        if(elementVR == this.getShortValue(ValueRepresentation.OB)){
                            OtherByteAttribute element;
                            byte[] dataArray = null;
                            //If element already exist, then pull the data and add new value.
                            if(dds.containsKey(aTag)){
                                //Extract element from dds.
                                element = (OtherByteAttribute)dds.get(aTag);
                                //Extract the existing data from the element
                                //ByteBuffer dataBuffer = element.getBuffer();
                                //dataBuffer.get(dataArray);
                                dataArray = element.getByteValues();
                            }
                            //Assign value to nuValue.  It uses Long primitive to make sure there is no
                            //  truncation of data.
                            Long nuValue = new Long(value);
                            //Add nuValue to existing data.
                            byte[] nuArray = this.addElementToByteArray(dataArray, nuValue);
                            //You cannot just add the nuArray to the existing element in the dds.
                            //  Must create a new element and insert it into the dds.  This automatically
                            //  overwrites the original element.
                            OtherByteAttribute updatedElement = new OtherByteAttribute(aTag);
                            updatedElement.setValues(nuArray);
                            dds.put(updatedElement);
                        }
                        else if(elementVR == this.getShortValue(ValueRepresentation.US)){
                            UnsignedShortAttribute element;
                            short[] dataArray = null;
                            //If element already exist, then pull the data and add new value.
                            if(dds.containsKey(aTag)){
                                //Extract element from dds.
                                element = (UnsignedShortAttribute)dds.get(aTag);
                                //Extract the existing data from the element
                                dataArray = element.getShortValues();   
                            }
                            //Assign value to nuValue.  It uses Long primitive to make sure there is no
                            //  truncation of data.
                            Long nuValue = new Long(value);
                            //Add nuValue to existing data.
                            short[] nuArray = this.addElementToShortArray(dataArray, nuValue);
                            //You cannot just add the nuArray to the existing element in the dds.
                            //  Must create a new element and insert it into the dds.  This automatically
                            //  overwrites the original element.
                            UnsignedShortAttribute updatedElement = new UnsignedShortAttribute(aTag);
                            updatedElement.setValues(nuArray);
                            dds.put(updatedElement);
                        }
                        else if(elementVR == this.getShortValue(ValueRepresentation.SL)){
                            SignedLongAttribute element;
                            short[] dataArray = null;
                            //If element already exist, then pull the data and add new value.
                            if(dds.containsKey(aTag)){
                                //Extract element from dds.
                                element = (SignedLongAttribute)dds.get(aTag);
                                //Extract the existing data from the element
                                dataArray = element.getShortValues();   
                            }
                            //Assign value to nuValue.  It uses Long primitive to make sure there is no
                            //  truncation of data.
                            Long nuValue = new Long(value);
                            //Add nuValue to existing data.
                            short[] nuArray = this.addElementToShortArray(dataArray, nuValue);
                            //You cannot just add the nuArray to the existing element in the dds.
                            //  Must create a new element and insert it into the dds.  This automatically
                            //  overwrites the original element.
                            SignedLongAttribute updatedElement = new SignedLongAttribute(aTag);
                            updatedElement.setValues(nuArray);
                            dds.put(updatedElement);
                        }
                        else if(elementVR == this.getShortValue(ValueRepresentation.OW)){
                            OtherWordAttribute element;
                            short[] dataArray = null;
                            //If element already exist, then pull the data and add new value.
                            if(dds.containsKey(aTag)){
                                //Extract element from dds.
                                element = (OtherWordAttribute)dds.get(aTag);
                                //Extract the existing data from the element
                                //ShortBuffer dataBuffer = element.getShortBuffer();
                                //dataBuffer.get(dataArray);
                                element.getShortValues();
                            }
                            //Assign value to nuValue.  It uses Long primitive to make sure there is no
                            //  truncation of data.
                            Long nuValue = new Long(value);
                            //Add nuValue to existing data.
                            short[] nuArray = this.addElementToShortArray(dataArray, nuValue);
                            //You cannot just add the nuArray to the existing element in the dds.
                            //  Must create a new element and insert it into the dds.  This automatically
                            //  overwrites the original element.
                            OtherWordAttribute updatedElement = new OtherWordAttribute(aTag);
                            updatedElement.setValues(nuArray);
                            dds.put(updatedElement);
                        }
                        else if(elementVR == this.getShortValue(ValueRepresentation.SS)){
                            SignedShortAttribute element;
                            short[] dataArray = null;
                            //If element already exist, then pull the data and add new value.
                            if(dds.containsKey(aTag)){
                                //Extract element from dds.
                                element = (SignedShortAttribute)dds.get(aTag);
                                //Extract the existing data from the element
                                dataArray = element.getShortValues();   
                            }
                            //Assign value to nuValue.  It uses Long primitive to make sure there is no
                            //  truncation of data.
                            Long nuValue = new Long(value);
                            //Add nuValue to existing data.
                            short[] nuArray = this.addElementToShortArray(dataArray, nuValue);
                            //You cannot just add the nuArray to the existing element in the dds.
                            //  Must create a new element and insert it into the dds.  This automatically
                            //  overwrites the original element.
                            SignedShortAttribute updatedElement = new SignedShortAttribute(aTag);
                            updatedElement.setValues(nuArray);
                            dds.put(updatedElement);
                        }
                        else if(elementVR == this.getShortValue(ValueRepresentation.UL)){
                            UnsignedLongAttribute element;
                            short[] dataArray = null;
                            //If element already exist, then pull the data and add new value.
                            if(dds.containsKey(aTag)){
                                //Extract element from dds.
                                element = (UnsignedLongAttribute)dds.get(aTag);
                                //Extract the existing data from the element
                                dataArray = element.getShortValues();   
                            }
                            //Assign value to nuValue.  It uses Long primitive to make sure there is no
                            //  truncation of data.
                            Long nuValue = new Long(value);
                            //Add nuValue to existing data.
                            short[] nuArray = this.addElementToShortArray(dataArray, nuValue);
                            //You cannot just add the nuArray to the existing element in the dds.
                            //  Must create a new element and insert it into the dds.  This automatically
                            //  overwrites the original element.
                            UnsignedLongAttribute updatedElement = new UnsignedLongAttribute(aTag);
                            updatedElement.setValues(nuArray);
                            dds.put(updatedElement);
                        }
                        else{
                            //then extract this Tag from the dds.
                        	Attribute element = null;
                        	String currentValue = "";
                            if(dds.containsKey(aTag)){
                                //Extract element from dds.
                                //Must retrieve each individual value this way if the VM is greater than 1.
                                //Then concatonate together again.
                                element = dds.get(aTag);
                                //for(int i=0; i<element.getVM(); i++){
                                //    currentValue = currentValue.concat(element.getStringValue(i));
                                //    if(i < element.getVM()-1){
                                //        currentValue = currentValue.concat("\\");
                                //    }
                                //}
                            }
                            else{
                                //currentValue = null;
                                element = AttributeFactory.newAttribute(aTag);
                            }
                            //then Append a "\\" and the Value variable to this Tag.

                            //updatedElement.setValue(currentValue+"\\"+value);
                            element.addValue(value);
                            dds.put(element);
                        }
                    }
                    //If ML is greater than 1.
                    else if(ml > 1){
                        //then extract this Tag from the dds.
                        String currentValue;
                        if(dds.containsKey(aTag)){
                            currentValue = dds.get(aTag).getDelimitedStringValuesOrEmptyString();
                        }
                        else{
                            currentValue = null;
                        }
                        //then Append the Value variable to this Tag.
                        if(aTag.getGroup() != 0x7FE0){
                            Attribute updatedElement = AttributeFactory.newAttribute(aTag);
                            updatedElement.setValue(currentValue+value);
                            dds.put(updatedElement);
                        }
                        if(aTag.getGroup() == 0x7FE0){
                            if(ml == 2){
                                String parsedValues[] = value.split("=");
                                String lengthValue = parsedValues[1];
                                String parsedLength[] = lengthValue.split(" ");
                                String pixelDataLength = parsedLength[0].trim();
                                Long lengthLong = new Long(pixelDataLength);
                                this.originalPixelData.setOriginalLength(lengthLong.longValue());
                            }
                            if(ml == 3){
                                String parsedValues[] = value.split("=");
                                String lengthValue = parsedValues[1];
                                String parsedOffset[] = lengthValue.split(" ");
                                String pixelDataOffset = parsedOffset[0].trim();
                                Integer offsetInt = new Integer(pixelDataOffset);
                                this.originalPixelData.setOriginalOffset(offsetInt.intValue());
                            }
                        }
                    }
                    else{
                        //FUTURE The if sequence works.  But I like to find a more efficient way.
                        //AttributeTag aTag = new AttributeTag(tag);
                        if((aTag.getElement() == 0) || (aTag.getElement() == 1)){
                            if(aTag.getGroup() <= 2){
                                Attribute nuElement = AttributeFactory.newAttribute(aTag);
                                nuElement.setValue(value);
                                dds.put(nuElement);
                            }
                        }
                        else{
                            if(aTag.getGroup() != 0x7FE0){
                                Attribute nuElement = AttributeFactory.newAttribute(aTag);
                                nuElement.setValue(value);
                                dds.put(nuElement);
                            }
                            if((aTag.getGroup() == 0x7FE0) && (aTag.getElement() == 0x0010)) {
                                char isCarat = fields[1].charAt((fields[1].length())-3);
                                if(isCarat == '^'){
                                    String desc_vrField = fields[1];
                                    String desc_vr[] = desc_vrField.split("\\^");
                                    String textfileVR = desc_vr[1];
                                    originalPixelData.setOriginalVR(this.getVR(textfileVR));
                                }
                                else{
                                    short bitsAllocated = (short)dds.get(TagFromName.BitsAllocated).getElement();
                                    this.originalPixelData.setBitsAllocated(bitsAllocated);
                                }
                            }
                        }
                    }
                    //End If for VM/VL.
                }
            }
            else if (fields.length == 2){
                byte[] vr = dd.getValueRepresentationFromTag(aTag);
                if(ValueRepresentation.getAsString(vr).equals("SQ")){
                    SequenceAttribute nuElement = new SequenceAttribute(aTag);                    
                    dds.put(nuElement);    
                }
            }
        }
        catch(DicomException dcs){
            LOGGER.error(dcs.getMessage());
            LOGGER.error(this.getClass().getName()+": " +
                    "Exception thrown while extracting Dicom Element.");
            throw new TextFileExtractionException("Failure to extract Dicom Element.", dcs);
        }
    }

    /**
     * Parse the Text file HIS Update section.  The Text file is made up of three sections,
     * "Data1", "DICOM Data" and  optionally "HIS UPDATE".  Here only the HIS UPDATE section
     * is read and decoded.
     * 
     * @param buffer represents the Text file now in the form of a BufferReader object.
     * @throws TextFileExtractionException
     */
    private HashMap parseHisUpdates(BufferedReader buffer) 
    	throws TextFileExtractionException {
        
        String textLine = "";
        HashMap hisUpdates=null;
//      TESTLOGGER.info("... Parsing text data update section ...");
        try { // parse line for initial "gggg,eeee" or "gggg,eeee gggg,eeee" tags
	        do {
	           textLine = this.getNextTextLine(buffer);
	        } while(!(textLine.startsWith("$$BEGIN HIS UPDATE")));
        }
	    catch(TextFileExtractionException tfee) { // catch all
	        LOGGER.info("Warning: NO HIS Update section in text data !!!");
	        LOGGER.debug("Warning: obsolete TXT format NO VistA updates !!! ");
	        return hisUpdates;
	    }
        textLine = this.getNextTextLine(buffer); // skip to first HIS update line
        hisUpdates=new HashMap();
           
        while(!(textLine.startsWith("$$END HIS UPDATE"))) {
           try { // parse line for initial "gggg,eeee" or "gggg,eeee gggg,eeee" tags
	           // and ending text value 
	           String splitPipePattern = "\\|";
	           Pattern pFields = Pattern.compile(splitPipePattern);
	           String fields[] = new String[4];
	           fields = pFields.split(textLine);
	           if ( ((fields[0].length()==9) || (fields[0].length()==18)) &&
	        		   (fields[3].length()>0)) {
	        	   // place tag(s)-value pairs to HashMap
	        	   hisUpdates.put(fields[0], fields[3]);
	           }
           }
           catch(Throwable t) { // catch all
           }
           textLine = this.getNextTextLine(buffer);
        } // end wile
        
        if (hisUpdates.isEmpty()) 
        	return null;
        else 
        	return hisUpdates; 
    }

    /**
     * Reads next line from the Text file, which is now in the form of BufferedReader 
     * object.  This method checks for EOF, nulls, and blank lines.  This is primarily in
     * case the Text file was not properly formatted or corrupted.  If the line is valid, 
     * the line is returned.
     * 
     * @param in represents the BufferedReader object.
     * @return represents the next line read from the BufferedReader object.
     * @throws TextFileExtractionException
     */
    private String getNextTextLine(BufferedReader in) throws TextFileExtractionException{
        String line = null;
        try{
            do{
                if((line = in.readLine()) == null){
                    throw new TextFileExtractionException();
                }
            }while(line.equals(""));
        }
        catch(IOException io){
            LOGGER.error(io.getMessage());
            LOGGER.error(this.getClass().getName()+": " +
                    "Exception thrown while getting next line from Text Line.");
            throw new TextFileExtractionException("Failure to get next line.", io);
        }
        return line;
    }
    
    private boolean isGroupToBeAdded(int Group){
        boolean addGroup = true;
        
        //Do not allow Odd Groups
        if((Group % 2) != 0){
            addGroup = false;
        }
        
        //Do not allow Icon Image Sequences
        if(Group == 0x0088){
            addGroup = false;
        }
        
        //Need to remove Group 0002 elements.  Found bug of sending Group 0002 elements
        //  to CSTore SCP device.  This is not valid.  I strongly believe to add it here.  I do
        //  not think any code between here and the sending uses this group.  But I could be 
        //  wrong.
        if(Group == 0x0002){
            addGroup = false;
        }
        
        return addGroup;
    }
    
    private void customTGAElementCleanup(){
    	try{
    		if(this.dicomDataSet.containsKey(new AttributeTag(0x0008,0x0008))){
    			CodeStringAttribute objectType = (CodeStringAttribute)this.dicomDataSet.get(new AttributeTag(0x0008,0x0008));
    			String[] values = objectType.getStringValues();
    			ArrayList<String> nuValueArray = new ArrayList<String>(values.length);
    			for(int i=0; i<values.length; i++){
    					nuValueArray.add(values[i]);
    			}
    			while(nuValueArray.contains("")){
    				nuValueArray.remove("");
    			}
    			nuValueArray.trimToSize();
    			
    			
    			CodeStringAttribute nuObjectType = new CodeStringAttribute(new AttributeTag(0x0008,0x0008));
    			Iterator<String> iter = nuValueArray.iterator();
    			while(iter.hasNext()){
    				String nuValue = iter.next();
    				nuObjectType.addValue(nuValue);
    			}
    			this.dicomDataSet.put(nuObjectType);    			
    		}
    	}
    	catch(DicomException dcsX){
    		//do nothing
    	}
    	
    }
    
    private byte[] addElementToByteArray(byte[] oldArray, Long nuValue){
    
        if(oldArray == null){
            oldArray = new byte[0];
        }
        int length = oldArray.length;
        int index = length+1;
        byte nuArray[] = new byte[index];
        System.arraycopy(oldArray, 0, nuArray, 0, length);
        nuArray[index-1] = nuValue.byteValue();
        return nuArray;
    }
    
    private short[] addElementToShortArray(short[] oldArray, Long nuValue){
        
            int length = oldArray.length;
            int index = length+1;
            short nuArray[] = new short[index];
            System.arraycopy(oldArray, 0, nuArray, 0, length);
            nuArray[index-1] = nuValue.shortValue();
            return nuArray;
        }

    private int[] addElementToIntArray(int[] oldArray, Long nuValue){
        
            int length = oldArray.length;
            int index = length+1;
            int nuArray[] = new int[index];
            System.arraycopy(oldArray, 0, nuArray, 0, length);
            nuArray[index-1] = nuValue.intValue();
            return nuArray;
        }

    private float[] addElementToFloatArray(float[] oldArray, Float nuValue){
        
            int length = oldArray.length;
            int index = length+1;
            float nuArray[] = new float[index];
            System.arraycopy(oldArray, 0, nuArray, 0, length);
            nuArray[index-1] = nuValue.floatValue();
            return nuArray;
        }

    private double[] addElementToDoubleArray(double[] oldArray, Double nuValue){
        
            int length = oldArray.length;
            int index = length+1;
            double nuArray[] = new double[index];
            System.arraycopy(oldArray, 0, nuArray, 0, length);
            nuArray[index-1] = nuValue.doubleValue();
            return nuArray;
        }    
    
    private int getGroupFromTag(String tag){
    	int group = 0;
    	String groupString = StringUtils.Piece(tag, ",", 1);
    	group = Integer.parseInt(groupString, 16);    	
    	return group;
    }
    
    private int getElementFromTag(String tag){
    	int element = 0;
    	String elementString = StringUtils.Piece(tag, ",", 2);
    	element = Integer.parseInt(elementString, 16);    	    	
    	return element;
    }
    
    private short getShortValue(byte[] data){    
	    short value = data[1];
	    value = (short)((value << 8) | data[0]);
	    
	    return value;
    }
    
    private short getVR(String vr){
    	short vrShort = 0;	
    	vrShort = this.getShortValue(vr.getBytes());
    	return vrShort;
    }
}
