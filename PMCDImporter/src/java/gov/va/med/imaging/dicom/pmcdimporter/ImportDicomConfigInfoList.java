/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.va.med.imaging.dicom.pmcdimporter;

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
public class ImportDicomConfigInfoList{
    
    private  String DCMConfigFile;
    private static EventList<ImportDicomConfigInfo> mInstances = GlazedLists.eventList(null); 
    private static ImportDicomConfigInfoList mImportDicomConfigInfoList = null;

    
    private ImportDicomConfigInfoList(){
        if (mInstances == null) {
            mInstances = GlazedLists.eventList(null);
        }

    }
    public static ImportDicomConfigInfoList getInstance(){
        if (mImportDicomConfigInfoList == null){
            mImportDicomConfigInfoList = new ImportDicomConfigInfoList();
        }
        return mImportDicomConfigInfoList;
    }
    public static EventList<ImportDicomConfigInfo> getObservableInstance(){
        if (mInstances == null) {
            mInstances = GlazedLists.eventList(null);
        }
        return mInstances;
    }
    public boolean isUnique(ImportDicomConfigInfo that){
        if ( mInstances != null && mInstances.size() > 0){
            for (ImportDicomConfigInfo info : mInstances) {
                if (info != null && (info.getAlias().equalsIgnoreCase(that.getAlias()))) {
                    return false;
                }
            }
        }
        return true;
    }
    private ArrayList<ImportDicomConfigInfo> makeArrayListfromInstances(){
        ArrayList<ImportDicomConfigInfo> writeList = new ArrayList<ImportDicomConfigInfo>();
        
        for (ImportDicomConfigInfo info : mInstances)
        {
            if (info != null ) {
                writeList.add(info);

            }
        }
        return writeList;
    }
    private void populateInstanceFromArrayList(ArrayList<ImportDicomConfigInfo> readInstances){
       
       mInstances.clear();
       
       for (ImportDicomConfigInfo info : readInstances)
        {
            if (info != null ){
                mInstances.add(info);
            }
        }
     }
    public String write(){
       ArrayList<ImportDicomConfigInfo> writeList = makeArrayListfromInstances();
       
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
       ArrayList<ImportDicomConfigInfo> readInstances;
       
       XStream xs = new XStream();
       Object fromXML;
        try {
            FileInputStream fis = new FileInputStream(DCMConfigFile);
            readInstances = (ArrayList<ImportDicomConfigInfo>) xs.fromXML(fis);
        } catch (FileNotFoundException ex) {
            return ex.getMessage();
        }
        populateInstanceFromArrayList (readInstances);
        return null;
    }
    public ImportDicomConfigInfo findByAE (Object AETitle){
         if ( mInstances != null && mInstances.size() > 0){
            for (ImportDicomConfigInfo info : mInstances)
            {
                if (info != null && info.getApplicationEntity().equals(AETitle)) {
                    return info;
                }
            }
        }
        return null;
    }
    public ImportDicomConfigInfo findByAlias (Object _Alias){
         if ( mInstances != null && mInstances.size() > 0){
            for (ImportDicomConfigInfo info : mInstances)
            {
                if (info != null && info.getAlias().equals(_Alias)) {
                    return info;
                }
            }
        }
        return null;
    }

}