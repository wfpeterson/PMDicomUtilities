package gov.va.med.imaging.dicom.utilities.compare;

public class PixelDataTypeUtility {

	public PixelDataTypeUtility() {
		// TODO Auto-generated constructor stub
	}
	
    static public int[] byteToInteger(byte[] pixelDataInBytes){
        
        int[] pixelValues = new int[pixelDataInBytes.length];
        for(int i=0; i<pixelDataInBytes.length; i++){        
            pixelValues[i] = (int)0x000000FF & (int)pixelDataInBytes[i];
        }
        return pixelValues;
    }

    static public int[] shortToInteger(short[] pixelDataInShorts){

        int[] pixelValues = new int[pixelDataInShorts.length];
        for(int i=0; i<pixelDataInShorts.length; i++){        
            pixelValues[i] = (int)0x000000FF & (int)pixelDataInShorts[i];
        }
        return pixelValues;
    }


}
