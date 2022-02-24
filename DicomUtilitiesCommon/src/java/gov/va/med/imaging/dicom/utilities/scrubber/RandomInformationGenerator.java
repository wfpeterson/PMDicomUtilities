/*
 * Created on May 22, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gov.va.med.imaging.dicom.utilities.scrubber;

import java.util.Random;

/**
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 *
 *
 * @author William Peterson
 *
 */
public class RandomInformationGenerator {

    /**
     * Constructor
     *
     * 
     */
    public RandomInformationGenerator() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    //CODEME Create a Name generator that contains no vowels.
    public String randomStringNameGenerator(){
        String name = "";
        Random random = new Random();
        
        byte[] letters = new byte[24];
        
        int numberOfChar = random.nextInt();
        return name;
    }

    //CODEME Create a UID Generator using VA root prefix.
    
    
}
