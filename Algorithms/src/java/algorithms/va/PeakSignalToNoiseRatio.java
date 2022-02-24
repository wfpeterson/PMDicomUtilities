/*
 * Created on Apr 28, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package algorithms.va;

/**
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 *
 *
 * @author William Peterson
 *
 */
public final class PeakSignalToNoiseRatio {

    /**
     * Constructor
     *
     * 
     */
    public PeakSignalToNoiseRatio() {
        super();
        // TODO Auto-generated constructor stub
    }

    public static double eqPSNR(float mse, int pixelDepth){
        //PSNR =   20 * log10 (255 / sqrt(MSE))
        
        int maxPossiblePixelValue = (1 << pixelDepth) - 1;
        double logValue = maxPossiblePixelValue/Math.sqrt(mse);
        double pSNR = 20 * log10(logValue);
        return pSNR;
    }
    
    public static double ePSNR(int[] x, int[] y, int maxPossiblePixelValue){
        
        float mse = MeanSquaredError.eqMSE(x, y);
        double pSNR = 20 * log10(maxPossiblePixelValue/Math.sqrt(mse));
        
        return pSNR;
    }
    
    private static double log10(double x) {

        return Math.log(x)/Math.log(10.0);
        
      }
}
