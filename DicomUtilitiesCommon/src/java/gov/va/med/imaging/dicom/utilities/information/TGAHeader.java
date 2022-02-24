/*
 * Created on Jul 13, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gov.va.med.imaging.dicom.utilities.information;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

/**
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 *
 *
 * @author William Peterson
 *
 */
public class TGAHeader {

    /**
     * Constructor
     *
     * 
     */
    public TGAHeader() {
        super();
        // TODO Auto-generated constructor stub
    }

    public static void main(String[] args) {
        TGAHeader hdr = new TGAHeader();
        
        if(args.length == 1 ){
            hdr.readFileContents(args[0]);
        }
        else{
            System.out.println("Please have only one argument.");
        }
        System.exit(0);
    }
    
    private void readFileContents(String tgaFile){
        try{
            byte tgaBitsPerPixel;
            short tgaColumns = 0;
            short tgaRows = 0;
            FileChannel fc = new FileInputStream(tgaFile).getChannel();
            MappedByteBuffer buffer = fc.map(MapMode.READ_ONLY, 0, (int)fc.size());
            byte header[] = new byte[18];

            //Return updated Buffer to itself.  This makes sure the position is at 18 instead
            //  of 0.
            buffer = (MappedByteBuffer)buffer.get(header, 0, 18);
           
            //Read Byte 2 (1 byte) to confirm it is a "3".  If not throw exception.
            if(header[2] != 3){
                System.out.println("This is not a VA TGA file.");
                return;
            }
         
            //Read Bytes 12-13 for # of columns.
            tgaColumns = 0;
            tgaColumns |= (0xFF & header[13]);
            tgaColumns <<=8;
            tgaColumns |= (0xFF & header[12]);  
            

            //Read Bytes 14-15 for # of rows.
            tgaRows = 0;
            tgaRows |= (0xFF & header[15]);
            tgaRows <<=8;
            tgaRows |= (0xFF & header[14]);

            //Read Byte 16 for bits/pixel.
            tgaBitsPerPixel = header[16];

            System.out.println("TGA File: "+tgaFile);
            System.out.println("Number of Rows: "+tgaRows);
            System.out.println("Number of Columns: "+tgaColumns);
            System.out.println("Bits Per Pixel: "+tgaBitsPerPixel);
            
        }
        catch(FileNotFoundException nofile){
            System.out.println("Could not find "+tgaFile+".");
            return;
        }
        catch(IOException ioe){
            System.out.println("Could not open "+tgaFile+".");
            return;
        }
        
        return;
    }
}
