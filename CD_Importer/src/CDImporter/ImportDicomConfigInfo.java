/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CDImporter;

/**
 *
 * @author vhaiswcsipod
 */
public class ImportDicomConfigInfo {
    private String mAlias = "";
    private String mApplicationEntity = "";
    private String mCallingAE = "";
    private String mRole = "";
    private String mHost = "";
    private String mPort = "";
    private boolean mChanged = false;

    
    public ImportDicomConfigInfo(){
    }
    
    public ImportDicomConfigInfo(String _Alias, String _AE, String _CallingAE, String _Role, String _Host, String _Port){
        mAlias = _Alias;
        mApplicationEntity = _AE;
        mCallingAE = _CallingAE;
        mRole = _Role;
        mHost = _Host;
        mPort = _Port;
    }
    public ImportDicomConfigInfo(ImportDicomConfigInfo info){
        mAlias = info.getAlias();
        mApplicationEntity = info.getApplicationEntity();
        mCallingAE = info.getmCallingAE();
        mRole = info.getRole();
        mHost = info.getHost();
        mPort = info.getPort(); 

    }
    public void Copy(ImportDicomConfigInfo info){
        /**
         * Copies the entire object, could use an operator as well, but i do not like operators :)
         * 
         */
        mAlias = info.getAlias();
        mApplicationEntity = info.getApplicationEntity();
        mCallingAE = info.getmCallingAE();
        mRole = info.getRole();
        mHost = info.getHost();
        mPort = info.getPort(); 
    }
    public void setAlias(String _Alias){
        mAlias = _Alias;
    }
    public void setApplicationEntity (String _AE){
        mApplicationEntity = _AE;
    }
    public void setRole(String _Role){
        mRole = _Role;
    }
    public void setHost (String _Host){
        mHost = _Host;
    }
    public void setPort(String _Port){
        mPort = _Port;
    }
    public String getAlias(){
        return mAlias;
    }
    public String getApplicationEntity(){
        return mApplicationEntity;
    }
    public String getRole(){
        return mRole;
    }
    public String getHost(){
        return mHost;
    }
    public String getPort(){
        return mPort;
    }
    public boolean contentEquals(ImportDicomConfigInfo that){

        boolean aeEquals = mApplicationEntity.contentEquals(that.mApplicationEntity);
        boolean callingAEEquals = mCallingAE.equals(that.mCallingAE);
        boolean hostEquals = mHost.equals(that.mHost);
        boolean roleEquals = mRole.equals(that.mRole);
        boolean aliasEquals = mAlias.equals(that.mAlias);
        boolean portEquals = mPort.equals(that.mPort);
        if ( aeEquals && hostEquals && roleEquals && aliasEquals && callingAEEquals && portEquals){
            return true;
        }
        return false;
    }
    public boolean isEmpty(){
        if (mAlias.isEmpty() &&
            mApplicationEntity.isEmpty() && 
            mCallingAE.isEmpty() &&
            mPort.isEmpty() &&
            mHost.isEmpty()){
            return true;
        }
        return false;
    }
    public boolean isValid(){
        int port;
        if (mAlias.isEmpty()){
            return false;
        }
        if (mApplicationEntity.length() > 16 || mApplicationEntity.isEmpty()){
            return false;
        }
        if (mCallingAE.length() > 16 || mCallingAE.isEmpty()){
            return false;
        }
        if (mPort.isEmpty()){
            return false;
        }
        try {
            port = Integer.parseInt(mPort);
        }
        catch (NumberFormatException e){
           return false; 
        }
        if (port > 0xffff) {
            return false;
        }
        if (!mRole.equals("MWL SCP") && !mRole.equals("STORE SCP")){
            return false;
        }
        if (mHost.isEmpty()){
            return false;
        }
        return true;
    }

    /**
     * @return the mCallingAE
     */
    public String getmCallingAE() {
        return mCallingAE;
    }

    /**
     * @param mCallingAE the mCallingAE to set
     */
    public void setmCallingAE(String CallingAE) {
        mCallingAE = CallingAE;
    }

    /**
     * @return the mChanged
     */
    public boolean isChanged() {
        return mChanged;
    }

    /**
     * @param mChanged the mChanged to set
     */
    public void setChanged(boolean _Changed) {
        mChanged = _Changed;
    }
}