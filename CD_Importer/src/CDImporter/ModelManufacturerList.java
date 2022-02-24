/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CDImporter;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import com.thoughtworks.xstream.XStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author dcsipo
 */
public class ModelManufacturerList{
    private static final String ManufacturerModelCnfFile = "./ManufacturerModel.conf";
    private static ModelManufacturerList mInstance = null;
    private static EventList<ModelManufacturerEntry> mInstances = GlazedLists.eventList(null);; 
    private ModelManufacturerList(){
    }
    private void InitList(){
        
        String msg = this.read();
        if (mInstances.isEmpty()) {      
            ModelManufacturerEntry first = new ModelManufacturerEntry("<original>","<original>","<original>","<original>");  
            mInstances.add(first);
            write();
        }
    }
    public static EventList<ModelManufacturerEntry> getObservableInstance(){
        if (mInstance == null) {
            mInstance = new ModelManufacturerList();
            mInstance.InitList();
        }
        if (mInstances == null) {
            mInstances = GlazedLists.eventList(null);
        }
        return mInstances;
    }
    public static ModelManufacturerList getInstance(){
        if (mInstance == null) {
            mInstance = new ModelManufacturerList();
            mInstance.InitList();
        }
        return mInstance;
    }
    
    public boolean isUnique(ModelManufacturerEntry that){
        if ( mInstances != null && mInstances.size() > 0){
            for (ModelManufacturerEntry info : mInstances)
            {
                if (info != null && (info.contentEquals(that) || info.getAlias().equalsIgnoreCase(that.getAlias()))) {
                    return false;
                }
            }
        }
        return true;
    }
    private ArrayList<ModelManufacturerEntry> makeArrayListfromInstances(){
        ArrayList<ModelManufacturerEntry> writeList = new ArrayList<ModelManufacturerEntry>();
        
        for (ModelManufacturerEntry info : mInstances)
        {
            if (info != null ) {
                writeList.add(info);

            }
        }
        return writeList;
    }
    private void populateInstanceFromArrayList(ArrayList<ModelManufacturerEntry> readInstances){
       
       mInstances.clear();
       
       for (ModelManufacturerEntry info : readInstances)
        {
            if (info != null ){
                mInstances.add(info);
            }
        }
     }
    public String write(){
        XStream xs = new XStream();
        FixOrder();
        ArrayList<ModelManufacturerEntry> writeList = makeArrayListfromInstances();

        //Write to a file in the file system
        try {
            FileOutputStream fs = new FileOutputStream(ManufacturerModelCnfFile);
            xs.toXML(writeList, fs);
        } catch (FileNotFoundException e1) {
           return e1.getMessage();
            
        }
        return null;
    }
    public String read(){
       ArrayList<ModelManufacturerEntry> readInstances;
       
       XStream xs = new XStream();
       Object fromXML;
        try {
            FileInputStream fis = new FileInputStream(ManufacturerModelCnfFile);
            readInstances = (ArrayList<ModelManufacturerEntry>) xs.fromXML(fis);
        } catch (FileNotFoundException ex) {
            return ex.getMessage();
        }
        populateInstanceFromArrayList (readInstances);
        FixOrder();
        return null;
    }
    public ModelManufacturerEntry findByAlias (Object _Alias){
         if ( mInstances != null && mInstances.size() > 0){
            for (ModelManufacturerEntry info : mInstances)
            {
                if (info != null && info.getAlias().equals(_Alias)) {
                    return info;
                }
            }
        }
        return null;
    }
    public void Sort(){
        Collections.sort(mInstances);
    
    }
    public void FixOrder(){
        /**
         * the first entry in the list must be the entry with the alias "<original>
         * to accomplish this first we create a copy of the list remove all items and insert them back in order.
         * 
         */
        ModelManufacturerEntry curr = findByAlias("<original>");
        mInstances.remove(curr);
        Sort();
        mInstances.add(0, curr);
    }
}
