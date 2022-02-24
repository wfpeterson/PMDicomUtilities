/*
 * Created on Sep 20, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package gov.va.med.imaging.dicom.utils.pixelmed.sender;

import java.io.IOException;

import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.AttributeTag;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.SOPClass;
import com.pixelmed.network.DicomNetworkException;
import com.pixelmed.network.StorageSOPClassSCU;




/**
 * @author William Peterson
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class Sendimage
{
    String args[];
    
    public Sendimage (String args[])
    {
        this.args = args;
    }
    public static void main(String[] args) throws Exception
    {
        
        if(args.length < 4)
            System.out.println("Usage: Sendimage <ip> <port> <filename> <debug> <OPT:called AETitle> <OPT:calling AETitle>");
         
        try
        {
            normal_scu(args);
            //abort_scu(args);
        }
        catch (Exception e)
        {
            System.out.println("normal_scu method failed");
            e.printStackTrace();
        }

    }
    
    private static void normal_scu(String args[]) throws Exception
    {
        String ip;
        String portString;
        String calledaet = "";
        String callingaet = "";
        String file;
        String debugString;
        int port;
        int debug;
        boolean sopPrivateImage;
        boolean sopPrivateNonImage;
        
        ip = args[0];     
        portString = args[1];
        file = args[2];
        debugString = args[3];
        if(args.length > 4){
            calledaet = args[4];
            callingaet = args[5];
        }

        Integer temp = new Integer(portString);
        port = temp.intValue();
        Integer temp1 = new Integer(debugString);
        debug = temp1.intValue();
        
        AttributeTag sopclass = new AttributeTag(0x0008,0x0016);
        AttributeTag sopinstance = new AttributeTag(0x0008,0x0018);
        Attribute sopclassuid;
        Attribute sopinstanceuid;
        AttributeList al = new AttributeList();
        
        try
        {
            al.read(file);
            sopclassuid = al.get(sopclass);
            sopPrivateImage = SOPClass.isPrivateImageStorage(sopclassuid.getSingleStringValueOrNull());
            sopPrivateNonImage = SOPClass.isPrivateNonImageStorage(sopclassuid.getSingleStringValueOrNull());
            if (sopPrivateImage || sopPrivateNonImage)
            {
                throw new Exception("No or invalid SOP Class");
            }
            sopinstanceuid = al.get(sopinstance);
            new StorageSOPClassSCU(ip, port, calledaet, callingaet, file, sopclassuid.getSingleStringValueOrNull(), sopinstanceuid.getSingleStringValueOrNull(), 0, debug);
            System.out.println("Image Send Successful!");
        }
        catch (IOException ioe)
        {
            System.out.println ("IO Exception");
            ioe.printStackTrace();
            throw new Exception();
        }
        catch (DicomException de)
        {
            System.out.println ("DICOM Exception");
            de.printStackTrace();
            throw new Exception();
        }
        catch (DicomNetworkException dne)
        {
            System.out.println ("DICOM Network Exception");
            dne.printStackTrace();
            throw new Exception();
        }
    }
    
/**
    private static void abort_scu(String args[]) throws Exception
    {
        String ip;
        String port;
        String calledaet;
        String callingaet;
        String file;
        boolean sop;
        
        ip = args[0];     
        port = args[1];
        calledaet = args[2];
        callingaet = args[3];
        file = args[4];

        AttributeTag sopclass = new AttributeTag(0x0002,0x0002);
        AttributeTag sopinstance = new AttributeTag(0x0002,0x0003);
        AttributeTag transfer = new AttributeTag(0x0002,0x0010);
        Attribute sopclassuid;
        Attribute sopinstanceuid;
        Attribute transferuid;
        AttributeList al = new AttributeList();
        
        
        try
        {
            al.read(file);
            sopclassuid = al.get(sopclass);
            sop = SOPClass.isImageStorage(sopclassuid.getSingleStringValueOrNull());
            if (sop == false)
            {
                throw new Exception("No or invalid SOP Class");
            }
            sopinstanceuid = al.get(sopinstance);
            transferuid = al.get(transfer);
            
            //LinkedList presentationContexts = new LinkedList();
            
            //LinkedList tslist = new LinkedList();
            //tslist.add(TransferSyntax.Default);
            //tslist.add(TransferSyntax.ImplicitVRLittleEndian);
            //tslist.add(TransferSyntax.ExplicitVRLittleEndian);
            //tslist.add(TransferSyntax.ExplicitVRBigEndian);
            //PresentationContext pcid = new PresentationContext((byte) 0x01, sopclassuid.getSingleStringValueOrNull(), tslist);
          
            Association assoc;
            assoc.getSuitablePresentationContextID(sopclassuid.getSingleStringValueOrNull(), transferuid.getSingleStringValueOrNull());
            
            //presentationContexts.add(new PresentationContext((byte)0x01, sopclassuid.getSingleStringValueOrNull(), tslist));

            //Association association = AssociationFactory.createNewAssociation(ip,port,calledaet,callingaet, presentationContexts,0);

            byte usePresentationContextID = assoc.getSuitablePresentationContextID(SOPClass.Verification);
            //byte cEchoRequestCommandMessage[] = new CEchoRequestCommandMessage().getBytes();
            //association.setReceivedDataHandler(new CEchoResponseHandler(debugLevel));
            //association.send(usePresentationContextID,cEchoRequestCommandMessage,null);
            try 
            {
                assoc.waitForCommandPDataPDUs();
                // State 6
                assoc.release();
            }
            catch (AReleaseException e) 
            {
                // State 1
                // the other end released and didn't wait for us to do it
                System.out.println("did not abort");
            }

        }
        catch (Exception e)
        {
            System.out.println("Did not complete Abort Command");
        }
    }
**/
}
   

