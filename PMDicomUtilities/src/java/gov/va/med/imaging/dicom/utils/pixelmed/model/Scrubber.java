/*
 * Created on Jun 12, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gov.va.med.imaging.dicom.utils.pixelmed.model;

import java.util.Iterator;
import java.util.Vector;

import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.AttributeTag;

/**
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 *
 *
 * @author William Peterson
 *
 */
public class Scrubber {

    /**
     * Constructor
     *
     * 
     */
    public Scrubber() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    /* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.model.IScrubber#scrubData(com.pixelmed.dicom.AttributeList, java.util.Vector, boolean, boolean)
	 */
    public void scrubData(AttributeList dds, Vector<Attribute> fieldData, boolean removeGrp6000, boolean privateAttributes){
        //
        Iterator<Attribute> list = fieldData.iterator();
        while(list.hasNext()){
            Attribute currentField = (Attribute)list.next();
            AttributeTag tag = currentField.getTag();
            if(dds.get(tag) != null){
                dds.remove(tag);
            }
            dds.put(currentField);
        }
        if(removeGrp6000){
        	dds.removeOverlayAttributes();
        }
        if(privateAttributes){
        	dds.removePrivateAttributes();
        }
    }
}
