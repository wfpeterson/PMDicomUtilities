/*
 * Created on Sep 6, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package gov.va.med.imaging.dicom.utils.pixelmed.listener;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.DicomInputStream;
import com.pixelmed.dicom.StoredFilePathStrategy;
import com.pixelmed.network.DicomNetworkException;
import com.pixelmed.network.NetworkApplicationInformation;
import com.pixelmed.network.ReceivedObjectHandler;
import com.pixelmed.network.StorageSOPClassSCPDispatcher;
/**
 * @author William Peterson
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class SimpleSink
{
    String args[];
    int port;
    String aet;
    File ImageFolder;
    int debug = 1;
    
    public SimpleSink(String args[])
    {
        this.args = args;
        port = new Integer(args[0]).intValue();
        aet = args[1];
        ImageFolder = new File(args[2]);
    }
    
    public static void main(String[] args)
    {
 
        if(args.length != 3){
             System.out.println("Usage: SimpleSink <port> <AETitle> <Folder>");
        }
        
        SimpleSink cStoreSCP = new SimpleSink (args);
        cStoreSCP.startListener();
        //TestReceivedObjectHandler validatehandler = Cstorescp.new TestReceivedObjectHandler();
    } 
    
    private void startListener(){
        try
        {
            System.out.println("Starting SCP Service");
            StorageSOPClassSCPDispatcher Dispatcher = new StorageSOPClassSCPDispatcher(
            		port, 
            		aet, 
                    ImageFolder, 
                    StoredFilePathStrategy.BYSOPINSTANCEUIDINSINGLEFOLDER, 
                    new TestReceivedObjectHandler(), 
                    null, 
                    null, 
                    null,
                    new NetworkApplicationInformation(),
                    new UnencapsulatedImplicitAndExplicitPresentationContextSelectionPolicy(),
                    false);
            Dispatcher.run();
        }
        catch (IOException IOE)
        {
            System.out.println ("Dispatcher failed.  No socket opened.");
        }
        System.exit(0);
        
    }
    private class TestReceivedObjectHandler extends ReceivedObjectHandler 
    {
        public void sendReceivedObjectIndication(String dicomFileName,String transferSyntax,String callingAETitle) throws DicomNetworkException, DicomException, IOException 
        {
//            DicomInstanceValidator validator;
//            String ValidateResult;
            
            if (dicomFileName != null) 
            {
                System.out.println("Received: "+dicomFileName+" from "+callingAETitle+" in "
                        +transferSyntax);
                System.out.println("");
                try 
                {
                    //validator = new DicomInstanceValidator();
                    //ValidateResult = null;
                    DicomInputStream i = new DicomInputStream(new BufferedInputStream(new FileInputStream(dicomFileName)));
                    AttributeList list = new AttributeList();
                    list.read(i);
                    System.out.println(list.toString());
                    //ValidateResult = validator.validate(list);
                } 
                catch (Exception e) 
                {
                    e.printStackTrace(System.err);
                }
            }
        }
    }
}
