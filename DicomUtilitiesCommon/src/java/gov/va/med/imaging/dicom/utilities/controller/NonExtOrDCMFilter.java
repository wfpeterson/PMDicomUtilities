/*
 * Created on Jun 1, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gov.va.med.imaging.dicom.utilities.controller;

import java.io.File;
import java.io.FileFilter;

//import javax.swing.filechooser.FileFilter;

/**
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 *
 *
 * @author William Peterson
 *
 */
public class NonExtOrDCMFilter implements FileFilter {

    /**
     * Constructor
     *
     * 
     */
    public NonExtOrDCMFilter() {
        //super();
        // TODO Auto-generated constructor stub
    }

    /* (non-Javadoc)
     * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
     */
    public boolean accept(File f) {
        // TODO Auto-generated method stub
        boolean acceptFile;
        if(f.getName().toLowerCase().endsWith(".dcm") 
        		|| f.getName().toLowerCase().endsWith(".")
        		|| f.isDirectory()){
            acceptFile = true;
        }
        else{
            acceptFile = false;
        }
        
        return acceptFile;
        //return f.getName().toLowerCase().endsWith(".dcm")
        //    ||(!(f.isFile()));
    }

    /* (non-Javadoc)
     * @see javax.swing.filechooser.FileFilter#getDescription()
     */
//    public String getDescription() {
//        // TODO Auto-generated method stub
//        return "DICOM Files";
//    }

}
