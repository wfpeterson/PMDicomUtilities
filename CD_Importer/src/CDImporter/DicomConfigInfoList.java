/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CDImporter;

import com.thoughtworks.xstream.XStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import java.util.ArrayList;

/**
 *
 * @author vhaiswcsipod
 */
public class DicomConfigInfoList{
    
    private static final String DCMConfigFile = "./DicomConfig.conf";
    private static EventList<DicomConfigInfo> mInstances = GlazedLists.eventList(null); 
    private static DicomConfigInfoList mDicomConfigInfoList = null;

    
    private DicomConfigInfoList(){
        if (mInstances == null) {
            mInstances = GlazedLists.eventList(null);
        }

    }
    public static DicomConfigInfoList getInstance(){
        if (mDicomConfigInfoList == null){
            mDicomConfigInfoList = new DicomConfigInfoList();
        }
        return mDicomConfigInfoList;
    }
    public static EventList<DicomConfigInfo> getObservableInstance(){
        if (mInstances == null) {
            mInstances = GlazedLists.eventList(null);
        }
        return mInstances;
    }
    public boolean isUnique(DicomConfigInfo that){
        if ( mInstances != null && mInstances.size() > 0){
            for (DicomConfigInfo info : mInstances)
            {
                if (info != null && (info.getAlias().equalsIgnoreCase(that.getAlias()))) {
                    return false;
                }
            }
        }
        return true;
    }
    private ArrayList<DicomConfigInfo> makeArrayListfromInstances(){
        ArrayList<DicomConfigInfo> writeList = new ArrayList<DicomConfigInfo>();
        
        for (DicomConfigInfo info : mInstances)
        {
            if (info != null ) {
                writeList.add(info);

            }
        }
        return writeList;
    }
    private void populateInstanceFromArrayList(ArrayList<DicomConfigInfo> readInstances){
       
       mInstances.clear();
       
       for (DicomConfigInfo info : readInstances)
        {
            if (info != null ){
                mInstances.add(info);
            }
        }
     }
    public String write(){
       ArrayList<DicomConfigInfo> writeList = makeArrayListfromInstances();
       
       XStream xs = new XStream();

        //Write to a file in the file system
        try {
            FileOutputStream fs = new FileOutputStream(DCMConfigFile);
            xs.toXML(writeList, fs);
        } catch (FileNotFoundException e1) {
           return e1.getMessage();
            
        }
        return null;
    }
    public String read(){
       ArrayList<DicomConfigInfo> readInstances;
       
       XStream xs = new XStream();
       Object fromXML;
        try {
            FileInputStream fis = new FileInputStream(DCMConfigFile);
            readInstances = (ArrayList<DicomConfigInfo>) xs.fromXML(fis);
        } catch (FileNotFoundException ex) {
            return ex.getMessage();
        }
        populateInstanceFromArrayList (readInstances);
        return null;
    }
    public DicomConfigInfo findByAE (Object AETitle){
         if ( mInstances != null && mInstances.size() > 0){
            for (DicomConfigInfo info : mInstances)
            {
                if (info != null && info.getApplicationEntity().equals(AETitle)) {
                    return info;
                }
            }
        }
        return null;
    }
    public DicomConfigInfo findByAlias (Object _Alias){
         if ( mInstances != null && mInstances.size() > 0){
            for (DicomConfigInfo info : mInstances)
            {
                if (info != null && info.getAlias().equals(_Alias)) {
                    return info;
                }
            }
        }
        return null;
    }

}