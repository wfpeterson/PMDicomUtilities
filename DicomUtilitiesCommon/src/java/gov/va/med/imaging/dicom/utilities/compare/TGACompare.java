/*
 * Created on May 1, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gov.va.med.imaging.dicom.utilities.compare;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

import algorithms.va.MeanSquaredError;
import algorithms.va.PeakSignalToNoiseRatio;
import gov.va.med.imaging.dicom.utilities.exceptions.TGAFileException;

/**
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 *
 *
 * @author William Peterson
 *
 */
public class TGACompare {

    int tgaBitsPerPixel = 0;
    
    /**
     * Constructor
     *
     * 
     */
    public TGACompare() {
        super();
        // TODO Auto-generated constructor stub
    }

    public static void main(String[] args) {
        TGACompare compare = new TGACompare();
        String firstTextFile = args[0];
        String secondTextFile = args[1];
        double pSNR;
        try{
            if(args.length < 2 ){
                throw new TGAFileException();
            }
            pSNR = compare.compareImageFiles(firstTextFile, secondTextFile);
            System.out.println("\nPeak Signal-to-Noise Ratio: "+pSNR+" dB.");
        }
        catch(TGAFileException tgaError){
            System.out.println("Error: Could not calculate Peak Signal To Noise Ratio.");
            tgaError.printStackTrace();
            System.exit(-1);
        }
        
        System.exit(0);
    }
    
    private double compareImageFiles(String TextFileOne, String TextFileTwo)throws TGAFileException{
        
        ByteBuffer firstImage;
        ByteBuffer secondImage;
        
        firstImage = this.openFile(TextFileOne);
        secondImage = this.openFile(TextFileTwo);
        
        float mse = MeanSquaredError.eqMSE(firstImage, secondImage, this.tgaBitsPerPixel);
        System.out.println("MSE: "+mse);
        double result = PeakSignalToNoiseRatio.eqPSNR(mse, this.tgaBitsPerPixel);
        System.out.println("PSNR: "+result);
        return result;
    }
    
    private ByteBuffer openFile(String tgaFile)throws TGAFileException{
        
        try{
            FileChannel fc = new FileInputStream(tgaFile).getChannel();
            MappedByteBuffer buffer = fc.map(MapMode.READ_ONLY, 0, (int)fc.size());
            byte header[] = new byte[18];
            //Modified the following line because total pixel data was always 18 bytes
            //  to long.  I was not updating the buffer variable properly.
            //  It should be fine now.
            buffer = (MappedByteBuffer)buffer.get(header, 0, 18);
            //Read Byte 2 (1 byte) to confirm it is a "3".  If not throw exception.
            if(header[2] != 3){
                throw new TGAFileException();
            }
            //Read Byte 16 for bits/pixel.
            if(this.tgaBitsPerPixel == 0){
                this.tgaBitsPerPixel = header[16];
            }
            else{
                if(this.tgaBitsPerPixel != header[16]){
                    throw new TGAFileException();
                }
            }
            //Pass the Pixel Data.     
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            
            return buffer;
        }
        catch(FileNotFoundException noFile){
            throw new TGAFileException();
        }
        catch(IOException ioe){
            throw new TGAFileException();
        }
    }
}
