/*
 * Created on Apr 28, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package algorithms.va;

import java.nio.ByteBuffer;


/**
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 *
 *
 * @author William Peterson
 *
 */
public final class MeanSquaredError {

    /**
     * Constructor
     *
     * 
     */
    public MeanSquaredError() {
        super();
        // TODO Auto-generated constructor stub
    }

    public static float eqMSE(int[] x, int[] y){
        float runningAvg = 0;
        float result = 0;
    
        int totalPixels = x.length;
        if(x.length == y.length){
            for (int j=0; j<totalPixels; j++){
                int firstArrayValue = x[j];
                int secondArrayValue = y[j];
                //System.out.println("Loop#: "+j);
                //System.out.println(firstArrayValue+"    "+secondArrayValue);
                runningAvg += (firstArrayValue - secondArrayValue) * 
                (firstArrayValue - secondArrayValue);    //Error, squared
            }
            result = runningAvg/totalPixels; //Mean Squared Error
        }
        else{
            result = Float.NaN;
        }
    
        return result;
    }

    public static float eqMSE(ByteBuffer x, ByteBuffer y, int pixelDepth){
        float runningAvg = 0;
        float result = 0;
    
        int totalPixels = x.remaining();
        if(x.remaining() == y.remaining()){
            if(pixelDepth > 8){
                for (int j=0; j<totalPixels; j+=2){
                    int firstBufferByte = (int)(0x000000FF & (int)x.getShort());
                    int secondBufferByte = (int)(0x000000FF & (int)y.getShort());
                    //System.out.println("Loop#: "+j);
                    //System.out.println(firstBufferByte+"    "+secondBufferByte);
                    runningAvg += (firstBufferByte - secondBufferByte) * 
                    (firstBufferByte - secondBufferByte);    //Error, squared
                }
                totalPixels = totalPixels/2;
            }
            else{
                for (int j=0; j<totalPixels; j++){
                    short firstBufferByte = (short)(0x00FF & (short)x.get());
                    short secondBufferByte = (short)(0x00FF & (short)y.get());
                    //System.out.println(j+"    "+x.position());
                    //System.out.println(firstBufferByte+"    "+secondBufferByte);
                    runningAvg += (firstBufferByte - secondBufferByte) * 
                    (firstBufferByte - secondBufferByte);    //Error, squared
                }
            }
            result = runningAvg/totalPixels; //Mean Squared Error
        }
        else{
            result = Float.NaN;
        }
    
        return result;
    }
        
}

