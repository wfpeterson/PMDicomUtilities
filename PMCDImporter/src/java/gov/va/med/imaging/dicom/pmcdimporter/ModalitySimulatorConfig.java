/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.va.med.imaging.dicom.pmcdimporter;

import com.thoughtworks.xstream.XStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author dcsipo
 */
public class ModalitySimulatorConfig {
    /**
     * Various pieces of configuration stored in the local directory
     */    
    private String mSelectedMWL;
    private String mSelectedStorageSCP;
    private String mSelectedManufacturer;
    private String mSelectedPath;
    private Vector mSavedPaths;
    /**
     * write the content of the configuration
     */
    public boolean write(){
        XStream xs = new XStream();
        boolean retVal = false;
        //Write to a file in the file system
        try {
            FileOutputStream fs = new FileOutputStream("./SimulatorConfig.conf");
            xs.toXML(this, fs);
            retVal = true;
        } catch (FileNotFoundException e1) {
            Logger.getLogger(ModalitySimulatorConfig.class.getName()).log(Level.WARNING, null, e1);           
        }
        return retVal;
    }
    /**
     * read the configuration
     */
    public boolean read(){
       XStream xs = new XStream();
       Object fromXML;
       boolean retVal = false;
       try {
            FileInputStream fis = new FileInputStream("./SimulatorConfig.conf");
            ModalitySimulatorConfig temp = (ModalitySimulatorConfig) xs.fromXML(fis);
            setmSelectedMWL(temp.getmSelectedMWL());
            setmSelectedStorageSCP(temp.getmSelectedStorageSCP());
            setmSelectedManufacturer(temp.getmSelectedManufacturer());
            setmSelectedPath(temp.getmSelectedPath());
            setmSavedPaths(temp.getmSavedPaths());
            retVal = true;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ModalitySimulatorConfig.class.getName()).log(Level.WARNING, null, ex);           
        } 
        return retVal;
    }

    /**
     * @return the mSelectedMWL
     */
    public String getmSelectedMWL() {
        return mSelectedMWL;
    }

    /**
     * @param mSelectedMWL the mSelectedMWL to set
     */
    public void setmSelectedMWL(String mSelectedMWL) {
        this.mSelectedMWL = mSelectedMWL;
    }

    /**
     * @return the mSelectedStorageSCP
     */
    public String getmSelectedStorageSCP() {
        return mSelectedStorageSCP;
    }

    /**
     * @param mSelectedStorageSCP the mSelectedStorageSCP to set
     */
    public void setmSelectedStorageSCP(String mSelectedStorageSCP) {
        this.mSelectedStorageSCP = mSelectedStorageSCP;
    }

    /**
     * @return the mSelectedManufacturer
     */
    public String getmSelectedManufacturer() {
        return mSelectedManufacturer;
    }

    /**
     * @param mSelectedManufacturer the mSelectedManufacturer to set
     */
    public void setmSelectedManufacturer(String mSelectedManufacturer) {
        this.mSelectedManufacturer = mSelectedManufacturer;
    }

    /**
     * @return the mSelectedPath
     */
    public String getmSelectedPath() {
        return mSelectedPath;
    }

    /**
     * @param mSelectedPath the mSelectedPath to set
     */
    public void setmSelectedPath(String mSelectedPath) {
        this.mSelectedPath = mSelectedPath;
    }

    /**
     * @return the savedPaths
     */
    public Vector getmSavedPaths() {
        return mSavedPaths;
    }

    /**
     * @param savedPaths the savedPaths to set
     */
    public void setmSavedPaths(Vector savedPaths) {
        this.mSavedPaths = savedPaths;
    }
}
