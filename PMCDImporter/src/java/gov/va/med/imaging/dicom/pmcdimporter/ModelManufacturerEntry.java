/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.va.med.imaging.dicom.pmcdimporter;

/**
 *
 * @author dcsipo
 */
public class ModelManufacturerEntry implements Comparable<ModelManufacturerEntry>{
    /**
     * Local property holders.
     */
    private String mAlias="";
    private String mManufacturer="";
    private String mModel="";
    private String mModality="";


    public ModelManufacturerEntry(){        
    }
    
    public ModelManufacturerEntry(String _Alias, String _Manufacturer, String _Model, String _Modality){
        mAlias = _Alias;
        mManufacturer = _Manufacturer;
        mModel = _Model;
        mModality = _Modality;   
    }
    public ModelManufacturerEntry(ModelManufacturerEntry info){
        mAlias = info.getAlias();
        mManufacturer = info.getManufacturer();
        mModel = info.getModel();
        mModality = info.getModality();   
    }

    /**
     * @return the mAlias
     */
    public String getAlias() {
        return mAlias;
    }

    /**
     * @param mAlias the mAlias to set
     */
    public void setAlias(String mAlias) {
        this.mAlias = mAlias;
    }

    /**
     * @return the mManufacturer
     */
    public String getManufacturer() {
        return mManufacturer;
    }

    /**
     * @param mManufacturer the mManufacturer to set
     */
    public void setManufacturer(String mManufacturer) {
        this.mManufacturer = mManufacturer;
    }

    /**
     * @return the mModel
     */
    public String getModel() {
        return mModel;
    }

    /**
     * @param mModel the mModel to set
     */
    public void setModel(String mModel) {
        this.mModel = mModel;
    }

    /**
     * @return the mModality
     */
    public String getModality() {
        return mModality;
    }

    /**
     * @param mModality the mModality to set
     */
    public void setModality(String mModality) {
        this.mModality = mModality;
    }
    public boolean isEmpty(){
        if (this.mAlias.isEmpty() &&
            this.mManufacturer.isEmpty() &&
            this.mModality.isEmpty() &&
            this.mModel.equals("<original>")) {
            return true;
        } else{
            return false;
        }            
    }
    public boolean contentEquals(ModelManufacturerEntry that){
        if (that instanceof ModelManufacturerEntry){
            boolean aliasEquals = this.mAlias.equalsIgnoreCase(that.mAlias);
            boolean manufacturerEquals = this.mManufacturer.equalsIgnoreCase(that.mManufacturer);
            boolean modelEquals = this.mModel.equalsIgnoreCase(that.mModel);
            boolean modalityEquals = this.mModality.equalsIgnoreCase(that.mModality);
            if ( aliasEquals && manufacturerEquals && modelEquals && modalityEquals){
                return true;
            }
        }
        return false;
    }
    public boolean isValid(){
        if (this.isEmpty()){
            return false; 
        }
        if (this.mAlias.equals("<original>")){
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(ModelManufacturerEntry t) {
        //use String compare
        return this.getAlias().compareTo(t.getAlias());
    }
}
