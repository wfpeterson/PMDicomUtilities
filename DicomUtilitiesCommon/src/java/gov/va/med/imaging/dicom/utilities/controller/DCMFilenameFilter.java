/*
 * Created on Jun 1, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gov.va.med.imaging.dicom.utilities.controller;

import java.io.File;
import java.io.FilenameFilter;

/**
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 *
 *
 * @author William Peterson
 *
 */
public class DCMFilenameFilter implements FilenameFilter {

    /**
     * Constructor
     *
     * 
     */
    public DCMFilenameFilter() {
        //super();
        // TODO Auto-generated constructor stub
    }

    /* (non-Javadoc)
     * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
     */
    public boolean accept(File f) {
        // TODO Auto-generated method stub
        
        return f.getName().toLowerCase().endsWith(".dcm") || f.getName().endsWith(".");
        //    ||f.isDirectory();
    }

    /* (non-Javadoc)
     * @see javax.swing.filechooser.FileFilter#getDescription()
     */
    public String getDescription() {
        // TODO Auto-generated method stub
        return "DICOM Files";
    }

    /* (non-Javadoc)
     * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
     */
    public boolean accept(File dir, String name) {
        // TODO Auto-generated method stub
        
        return name.toLowerCase().endsWith(".dcm") ||name.endsWith(".");
        //	||(!dir.isFile());
    }
}
