package gov.va.med.imaging.dicom.utilities.utils;

//import gov.va.med.hds.util.date.FileManDateFormat;
//import gov.va.med.hds.util.date.PointInTime;
//import gov.va.med.hds.util.date.PointInTimeFormat;
//import gov.va.med.hds.util.date.ImprecisePointInTimeException;


import java.util.ArrayList;
import java.util.TreeMap;
//import java.util.Arrays;
// *** import System.Collections.SortedList;
// ***
// import java.util.StringTokenizer;

public class StringUtils 
{ 
    public static final String CRLF = "\r\n";
    public static final String CARET = "^";
    public static final String STICK = "|";
    public static final String COLON = ":";
    public static final String SEMICOLON = ";";
    public static final String COMMA = ",";
    public static final String PERIOD = ".";
    public static final String SLASH = "/";
    public static final String SPACE = " ";
	public static final String EQUALS = "=";
	public static final String AMPERSAND = "&";
	public static final String ATSIGN = "@";
	public static final String NEW_LINE = "\n";
	public static final String TILDE = "~";
	public static final String BACKTICK = "`";
	
    public static final char CRCHAR = '\r';
    public static final char NEW_LINECHAR = '\n';
    public static final char TABCHAR = '\t';
    public static final char FORM_FEEDCHAR = '\f';
    public static final char BACKCHAR = '\b';
    public static final char DOUBLE_QUOTECHAR = '\"';
    public static final char SINGLE_QUOTECHAR = '\'';
    public static final char SLASHCHAR = '\\';
	

	/**
	 * 
	 * @param s
	 * @param delimiter
	 * @return
	 */
    public static String[] Split( String s, String delimiter) 
    {
		if( s.endsWith(delimiter) )
			s += delimiter;
		
        ArrayList<String> al = new ArrayList<String>();
        int tokenStart = 0;
        int tokenEnd = 0;
        String prev = delimiter;
        while( (tokenEnd = s.indexOf(delimiter, tokenStart)) != -1 ) 
        {
            String token = s.substring(tokenStart,tokenEnd);
            if (token.equals(delimiter) && prev.equals(delimiter))
                al.add("");
            else if (!token.equals(delimiter))
                al.add(token);
            
            prev = token;
            tokenStart = tokenEnd + delimiter.length();
        }
        if (tokenStart != s.length()) 
        	al.add(s.substring(tokenStart));
        
        return al.toArray(new String[al.size()]);
    }

    public static boolean isNumeric(String s) {
        for (int i=0; i<s.length(); i++) {
            char c = s.charAt(i);
            if (c < '0' || c > '9') return false;
        }
        return true;
    }

	public static String prepend(String s, char c, int lth)
	{
		if (s.length() > lth)
		{
			throw new IllegalArgumentException("Input string longer than requested string");
		}
		if (s.length() == lth)
		{
			return s;
		}
		while (s.length() < lth)
		{
			s += c;
		}
		return new StringBuffer(s).reverse().toString();
	}

	public static boolean isNumericChar(char c)
	{
		return Character.isDigit(c);
	}

	public static boolean isAlphaChar(char c)
	{
		return Character.isLetter(c);
	}

	public static boolean isAlphaNumericChar(char c)
	{
		return (isAlphaChar(c) || isNumericChar(c));
	}

	public static boolean isWhiteSpace(char c)
	{
		return (c == ' ' || c == '\t' || c == '\n' || c == '\r');
	}

	public static String removeNonNumericChars(String s)
	{
		if (s == null)
		{
			return null;
		}
		String rtn = "";
		for (int i=0; i<s.length(); i++)
		{
			char c = s.charAt(i);
			if (c >= '0' && c <= '9')
			{
				rtn += c;
			}
		}
		return rtn;
	}

	public static String Piece(String s, String delimiter, int pieceNum) 
	{
		// JMW 8/7/2006 changed to pieceNum > flds.length from pieceNum >= flds.length
        String[] flds = Split(s,delimiter);
        if (pieceNum > flds.length) return null;
        return flds[pieceNum-1];
    }
	
	public static String MagPiece(String s, String delimiter, int pieceNum) {
		String[] flds = Split(s,delimiter);
        if (pieceNum > flds.length) return s;
        return flds[pieceNum-1];
	}

/* *** from System.Collections.SortedList */

	public static String[] SortByPiece(String[] s, String delimiter, int pieceNum) 
	{
/* ***	SortedList sl = new SortedList();
		//pieceNum--;
		for (int i=0; i<s.length; i++)
			sl.Add(Piece(s[i],delimiter,pieceNum),s[i]);
		for (int i=0; i<sl.get_Count(); i++) 
			s[i] = (String)sl.GetByIndex(i);
 */
		TreeMap<String, String> tm = new TreeMap<String, String>();
		for (int i=0; i<s.length; i++) {
			String key=Piece(s[i], delimiter, pieceNum);
			tm.put(key, s[i]);
		}
		Object[] iter = tm.keySet().toArray();
		for (int i=0; i<tm.size(); i++)
			s[i] = (String)tm.get(iter[i]);

		return s;
	}

	/*
	public static PointInTime cvt2PointInTime(String vistaTS)
    {
        PointInTimeFormat f = new FileManDateFormat();
        try {
            return f.parse(vistaTS);   
        } catch (ParseException pe) {
            return null;
        }
    }
    */

	public static String getMonthNumberString(String sMonth) 
	{
		sMonth = sMonth.toUpperCase(); // ToUpper();
		if (sMonth.startsWith("JAN")) return "01";
		if (sMonth.startsWith("FEB")) return "02";
		if (sMonth.startsWith("MAR")) return "03";
		if (sMonth.startsWith("APR")) return "04";
		if (sMonth.startsWith("MAY")) return "05";
		if (sMonth.startsWith("JUN")) return "06";
		if (sMonth.startsWith("JUL")) return "07";
		if (sMonth.startsWith("AUG")) return "08";
		if (sMonth.startsWith("SEP")) return "09";
		if (sMonth.startsWith("OCT")) return "10";
		if (sMonth.startsWith("NOV")) return "11";
		if (sMonth.startsWith("DEC")) return "12";
		return null;
	}


	/*
	public static String VistaDateTimeString2VistaTimestamp(String s)
	{
		if (s.indexOf('@') == -1) s += "@00:00";
		String[] parts = StringUtils.Split(s,StringUtils.ATSIGN);
		String dateStr = parts[0];
		dateStr = dateStr.replaceAll(", ", ","); //*** Replace(", ",",")
		dateStr = dateStr.replace(' ',',');
		String[] flds = StringUtils.Split(dateStr,StringUtils.COMMA);
		String month = getMonthNumberString(flds[0]);
		String day = flds[1];
		if (day.length() == 1) day = "0" + day;
		String year = flds[2];
		flds = StringUtils.Split(parts[1],StringUtils.COLON);
		String hours = flds[0];
		if (hours.length() == 1) hours = "0" + hours;
		String minutes = flds[1];
		if (minutes.length() == 1) minutes = "0" + minutes;
		String seconds = "";
		if (flds.length > 2) seconds = flds[2];
		if (seconds.length() == 1) seconds = "0" + seconds; 
		int y = Integer.parseInt(year) - 1700;
		String result = y + month + day + "." + hours + minutes + seconds;
		return result;
	}
	*/

	public static String DateTimeString2VistaTimestamp(String s)
	{
		if (s.indexOf("@") != -1) s = s.replace('@',' ');
		String[] parts = StringUtils.Split(s,StringUtils.SPACE);
		if (parts.length == 0) return "";
		String[] flds = StringUtils.Split(parts[0],StringUtils.SLASH);
		if (flds.length == 1) return "";
		int year = Integer.parseInt(flds[2]) - 1700;
		String ts = String.valueOf(year) + flds[0] + flds[1];
		if (parts.length == 2) 
		{
			flds = StringUtils.Split(parts[1],StringUtils.COLON);
			ts += "." + flds[0] + flds[1];
		}
		return ts;
	}

	/*
	public static String toTimestampString(PointInTime p)
	{
		if (p == null) return null;
		String s = "";
		s += p.getMonth() + "/" + p.getDate() + "/" + p.getYear();
		try 
		{
			String tmp = p.getHour() + "";
			if (tmp.length() == 1) tmp = "0" + tmp; // tmp.get_Length()
			s += " " + tmp;
			tmp = p.getMinute() + "";
			if (tmp.length() == 1) tmp += "0"; // tmp.get_Length()
			s += ":" + tmp;
		} 
		catch (ImprecisePointInTimeException ex) 
		{
			s += " 00:00";
		}
		return s;
	}
	*/

	/*
	public static String toVistaTimestamp(PointInTime p) 
	{
		if (p == null) return null;
		if (!p.isYearSet()) return null;
		int yr = p.getYear() - 1700;
		String s = String.valueOf(yr);
		if (p.isMonthSet()) 
		{
			int mo = p.getMonth();
			if (mo < 10) s += "0";
			s += String.valueOf(mo);
		} else 
			s += "00";
		if (p.isDateSet()) 
		{
			int dy = p.getDate();
			if (dy < 10) s += "0";
			s += String.valueOf(dy);
		} else
			s += "00";
		if (p.isHourSet()) 
		{
			s += ".";
			int hr = p.getHour();
			if (hr < 10) s += "0";
			s += String.valueOf(hr);
		} else
			return s;
		if (p.isMinuteSet()) 
		{
			int mn = p.getMinute();
			if (mn < 10) s += "0";
			s += String.valueOf(mn);
		} else
			s += "00";
		if (p.isSecondSet()) 
		{
			int sec = p.getSecond();
			if (sec < 10) s += "0";
			s += String.valueOf(sec);
		}
		return s;
	}
	*/

	public static String removeBlankLines(String s) 
	{
		String[] lines = Split(s,CRLF);
		boolean lastLineBlank = false;
		String newS = "";
		for (int i=0; i<lines.length; i++) 
		{
			if (!lines[i].equals("")) 
			{
				newS += lines[i] + CRLF;
				lastLineBlank = false;
			} 
			else 
			{
				if (!lastLineBlank) newS += CRLF;
				lastLineBlank = true;
			}
		}
		return newS;
	}

	public static boolean isEmpty(String s)
	{
		return s == null || s.length() == 0;
	}
	
	public static String replacePiece(String s, String delimiter, int piece, String newValue)
	{
		String [] pieces = Split(s, delimiter);
		pieces[piece] = newValue;
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < pieces.length; i++)
		{
			if(i != 0)
			{
				sb.append(delimiter);
			}
			sb.append(pieces[i]);			
		}
		return sb.toString();
	}
	
	/**
	 * Returns a specified piece of a string and any number of subsequent pieces desired
	 * 
	 * @param s Input string to parse
	 * @param delimiter String to use as delimiter to split input
	 * @param pieceNum Piece number to start returning values with (indexing starts at 0)
	 * @param pieceCount The number of pieces to return (count starts with 1). A value of 0 here means return all remaining pieces after pieceNum
	 * @return
	 */
	public static String MagPieceCount(String s, String delimiter, int pieceNum, int pieceCount)
    {
    	String[] flds = StringUtils.Split(s,delimiter);
        if (pieceNum > flds.length) return s;
        
        int pCount = 0;
        if(pieceCount == 0)
        	pCount = flds.length;// Integer.MAX_VALUE;
        else
        	pCount = pieceNum + pieceCount;
        if(pCount > flds.length)
        	pCount = flds.length;
        StringBuilder sb = new StringBuilder();
        
        for(int i = pieceNum; i < pCount; i++)
        {
        	if(i > pieceNum)
        		sb.append(delimiter);
        	sb.append(flds[i]);
        }        
        return sb.toString();        
    }
	
    public static String displayEncodedChars(String encodedString){
        StringBuffer decodedString = new StringBuffer();
        char[] chars = encodedString.toCharArray();
            
        for(int x=0; x<chars.length; x++){
            if(chars[x] == TABCHAR){
                decodedString.append("\\t");
            }
            else if(chars[x] == CRCHAR){
                decodedString.append("\\r");
            }
            else if(chars[x] == NEW_LINECHAR){
                decodedString.append("\\n");
            }
            else if(chars[x] == FORM_FEEDCHAR){
                decodedString.append("\\f");
            }
            else if(chars[x] == BACKCHAR){
                decodedString.append("\\b");
            }
            else if(chars[x] == DOUBLE_QUOTECHAR){
                decodedString.append("\\\"");
            }
            else if(chars[x] == SINGLE_QUOTECHAR){
                decodedString.append("\\'");
            }
            else if(chars[x] == SLASHCHAR){
                decodedString.append("\\\\");
            }
            else{
                decodedString.append(chars[x]);
            }
        }
        return decodedString.toString();
    }	
    
    
    /**
     * Method intVal Calulates integer value based on first characters in a
     * string: "123abcde" yields 123
     *
     * @param str
     *            input string
     * @return integer value of start of string
     */
    public static int intVal(String str)
    {
      int charpos = 0;
      int ready = 0;
      char letter;
      int value = 0;
      int neg = 1;

      while ((ready < 1) && (charpos < str.length ()))
      {
        letter = str.charAt (charpos++);
        switch (letter)
        {
        case '0':
        case '1':
        case '2':
        case '3':
        case '4':
        case '5':
        case '6':
        case '7':
        case '8':
        case '9':
          value = value * 10 + letter - '0';
          break;
        case '-':
          neg = -neg;
          break;
        default:
          ready = 1;
          break;
        }
      }
      return value * neg;
    }	
} 
