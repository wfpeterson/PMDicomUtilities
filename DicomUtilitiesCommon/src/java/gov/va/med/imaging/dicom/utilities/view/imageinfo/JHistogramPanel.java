/*
 * Created on Jan 11, 2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gov.va.med.imaging.dicom.utilities.view.imageinfo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;

import javax.swing.JPanel;

import gov.va.med.imaging.dicom.utilities.model.IHistogramData;

/**
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 *
 * General Information
 * The Java coordinate system defines the upper left corner of
 * the drawing frame as position 0, 0 and that x increases to the
 * right of this position and that y increases as we go down the
 * frame.
 *
 * To convert these real numbers to the integer coordinates of the 
 * corresponding pixel on the canvas. The formulas for the conversion are:
 *
 * a  =  (int)( (x + 5)/10 * width );
 * b  =  (int)( (5 - y)/10 * height );
 *
 * where a and b are the horizontal and vertical coordinates of the pixel, 
 * and width and height are the width and height of the canvas.
 * 
 * 
 * @author William Peterson
 *
 */
public class JHistogramPanel extends JPanel {
    
    int minimumPixelBinWithQuantity = 0;
    int maximumPixelBinWithQuantity = 0;
    int maximumQuantity = 0;
    int maximumQuantityPixelValue = 0;
    Point chartAxisOrigin;
    Dimension chartSize;
    Point panelOrigin;
    Dimension panelSize;
    Point bottomTextSpaceOrigin;
    Dimension bottomTextSpaceSize;
    Dimension ratioSize;
    IHistogramData data = null;

    /**
     * Create the panel
     */
    public JHistogramPanel() {
        super(true);
        //
        setOpaque(true);
    }
    
    
    /* (non-Javadoc)
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    protected void paintComponent(Graphics g) {
        // TODO Auto-generated method stub
        super.paintComponent(g);
        //If data is null, make the panel blank.
        if(this.data != null){      
            //Calculate panelSize by subtracting the inset.
            this.setAreaSizesAndAxisOrigin();
            //Determine Min and Max pixel values.  This is used to help set the chart X size.
            this.setMinAndMaxPixelValues();
            //Paint Text in panel.
            this.paintText(g);        
            //Calculate the ratio between chart Y size and difference between Min and Max
            //  pixel values.
            int diffX = this.maximumPixelBinWithQuantity - this.minimumPixelBinWithQuantity;
            int ratioX = (int)(diffX/this.chartSize.getWidth());
            if(ratioX < 1){
                ratioX = 1;
            }
            //Calculate the ratio between chart X size and the Max Pixel Value Quantity.
            int diffY = this.maximumQuantity;
            int ratioY = (int)(diffY/this.chartSize.getHeight());
            if(ratioY < 1){
                ratioY = 1;
            }
            this.ratioSize = new Dimension(ratioX, ratioY);
            this.drawAxisLines(g);
            //Loop thru Histogram object
            int k = 0;
            int[] quantityValues = this.data.getPixelBins();
            for(int i=this.minimumPixelBinWithQuantity; i<this.maximumPixelBinWithQuantity; i++){
                int highestQuantity = 0;
                for(int j=0; j<this.ratioSize.getWidth(); j++, i++){
                    if(quantityValues[i] > highestQuantity){
                        highestQuantity = quantityValues[i];
                    }
                }
                int quantityFactor = (int)(highestQuantity/this.ratioSize.getHeight());
                Point plotMaxPoint = new Point(k, quantityFactor);
                this.drawHistogramLine(g, 
                        this.convertChartLocationToPixelLocation(plotMaxPoint));
                k++;
            }
        }
    }
    
    public void setHistogramData(IHistogramData data){
        //Set data instance variable.
        this.data = data;
        //Re-initialize int instance variables
        this.maximumPixelBinWithQuantity = 0;
        this.minimumPixelBinWithQuantity = 0;
        this.ratioSize = new Dimension();
    }
    
    private void paintText(Graphics g){
        
        //Display Min Pixel Value at bottom
        int minPixelValue = this.minimumPixelBinWithQuantity + this.data.getTrueLowPixelValue();
        g.drawString("Min. Pixel Value: "+minPixelValue, 
                (int)this.bottomTextSpaceOrigin.getX()+10, 
                (int)this.bottomTextSpaceOrigin.getY()+20);
        //Display Max Pixel Value at bottom
        int maxPixelValue = this.maximumPixelBinWithQuantity + this.data.getTrueLowPixelValue();
        g.drawString("Max. Pixel Value: "+maxPixelValue, 
                (int)this.bottomTextSpaceOrigin.getX()+200, 
                (int)this.bottomTextSpaceOrigin.getY()+20);
        //Display Max Quantity and at what pixel value.
        String message = "Maximum Quantity is "+this.maximumQuantity+" at Pixel Value "+
                this.maximumQuantityPixelValue;
        g.drawString(message,
                (int)this.bottomTextSpaceOrigin.getX()+10, 
                (int)this.bottomTextSpaceOrigin.getY()+50);
    }
    
    private Point convertChartLocationToPixelLocation(Point chartPoint){
        int plotX = (int)(this.chartAxisOrigin.getX() + chartPoint.getX());
        int plotY = (int)(this.chartAxisOrigin.getY() - chartPoint.getY());

        Point plot = new Point(plotX, plotY);
        return plot;
    }
    
    private void setMinAndMaxPixelValues(){
        
        int[] pixelValues = this.data.getPixelBins();
        boolean lowValueSet = false;
        this.maximumPixelBinWithQuantity = 0;
        this.minimumPixelBinWithQuantity = 0;
        this.maximumQuantity = 0;
        //Cycle thru the entire data object.
        //System.out.println("Length of Pixel Value Array: "+pixelValues.length);
        for(int i = 0; i<pixelValues.length; i++){
            //Find the highest Quantity of any Pixel Value.  This is needed to help set the X Axis.
            if(pixelValues[i] > this.maximumQuantity){
                this.maximumQuantity = pixelValues[i];
                this.maximumQuantityPixelValue = this.data.getTrueLowPixelValue() + i;
            }
            //Flag the first Pixel Value that contains Quantity > 0. Set Instance variable.
            if(!lowValueSet){
                if(pixelValues[i] > 0){
                    this.minimumPixelBinWithQuantity = i;
                    lowValueSet = true;
                }
            }
            //Flag the last Pixel Value that contains Quantity > 0.  Set Instance variable.
            if(pixelValues[i] > 0){
                this.maximumPixelBinWithQuantity = i;
            }
        }
        //System.out.println("Min Pixel Value with Quantity: "+this.minimumPixelValueWithQuantity);
        //System.out.println("Max Pixel Value with Quantity: "+this.maximumPixelValueWithQuantity);
    }
    
    private void setAreaSizesAndAxisOrigin(){        
        //Set Panel Size and Origin.
        int height = this.getHeight() - this.getInsets().top - this.getInsets().bottom;
        int width = this.getWidth() - this.getInsets().left - this.getInsets().right;
        this.panelSize = new Dimension(width, height);
        System.out.println("Panel Size: "+this.panelSize.getWidth()+"x"+this.panelSize.getHeight());
        this.panelOrigin = new Point(0+this.getInsets().left, 0+this.getInsets().top);
        System.out.println("Panel Origin: "+this.panelOrigin.getX()+", "+this.panelOrigin.getY());
        //Set Bottom Text Space size and origin.
        this.bottomTextSpaceSize = new Dimension((int)this.panelSize.getWidth(), 80);

        this.bottomTextSpaceOrigin = new Point((int)(this.panelOrigin.getX() + (this.panelSize.getWidth()
                - this.bottomTextSpaceSize.getWidth())), (int)(this.panelOrigin.getY() + (this.panelSize.getHeight()
                -this.bottomTextSpaceSize.getHeight())));
        System.out.println("Bottom Text Origin: "+this.bottomTextSpaceOrigin.getX()+", "+
                this.bottomTextSpaceOrigin.getY());

        this.panelSize.setSize(this.panelSize.getWidth(), this.panelSize.getHeight()
                - this.bottomTextSpaceSize.getHeight() - 1);
        
        //Set Chart size and origin.
        this.chartSize = new Dimension((int)this.panelSize.getWidth()-10, 
                (int)this.panelSize.getHeight()-30);
        this.chartAxisOrigin = new Point((int)this.panelOrigin.getX()+10, 
                (int)this.panelSize.getHeight()-10);
        System.out.println("Chart Size: "+this.chartSize.getWidth()+"x"+this.chartSize.getHeight());
        System.out.println("Chart Origin: "+this.chartAxisOrigin.getX()+", "+this.chartAxisOrigin.getY());
    }
    
    private void drawHistogramLine(Graphics g,Point plot){
        Color originalColor = g.getColor();
        g.setColor(Color.RED);
        Point basePoint = new Point((int)plot.getX(), (int)this.chartAxisOrigin.getY());
        
        g.drawLine((int)basePoint.getX(), (int)basePoint.getY(), 
                (int)plot.getX(), (int)plot.getY());
        g.setColor(originalColor);
    }
    
    private void drawAxisLines(Graphics g){
        g.drawLine((int)this.chartAxisOrigin.getX(), (int)this.chartAxisOrigin.getY(),
                (int)this.chartAxisOrigin.getX(), (int)(this.chartAxisOrigin.getY()-this.chartSize.getHeight()));

        g.drawLine((int)this.chartAxisOrigin.getX(), (int)this.chartAxisOrigin.getY(),
                (int)(this.chartAxisOrigin.getX()+this.chartSize.getWidth()), (int)this.chartAxisOrigin.getY());

    }
}
