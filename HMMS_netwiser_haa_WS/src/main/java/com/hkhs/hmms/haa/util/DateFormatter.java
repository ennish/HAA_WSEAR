/**
 * DateFormatter.java
 * January 05, 2002
 *
 * Copyright (C) 2002 ceit.com. All rights reserved.
 */
package com.hkhs.hmms.haa.util;

import java.text.SimpleDateFormat;
import java.util.StringTokenizer;

/**
 * <p>DateFormatter </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author allisok@163.com
 * @version 1.0
 */
public class DateFormatter {
        private static String [] monEng = {
        "JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC" };

        private static String [] monEngLower = {
        "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };
	private static SimpleDateFormat formatter
		= new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");

	private static SimpleDateFormat sqlFormatter
		= new SimpleDateFormat ("dd/MM/yyyy");

	public static String formatDate(java.util.Date date){
		if(date == null) return new String("");
		return formatter.format(date);
	}

	public static String formatSqlDate(java.sql.Date date){
		if(date == null) return new String("");
		return sqlFormatter.format(date);
	}  
	   
	public static String formatDate(java.util.Date date, String pattern){
		SimpleDateFormat formatter = new SimpleDateFormat (pattern);
		return formatter.format(date);
	}
	
	public static String getCurDateString(){
		return formatter.format(new java.util.Date());
	}

        //convert date from dd/mm/yyyy to yyyy/mm/dd
        public static String ReverseDate(String dateString){
                StringTokenizer st = new StringTokenizer(dateString,"/");
		int iDay = Integer.parseInt(st.nextToken());
		int iMonth = Integer.parseInt(st.nextToken()) - 1;
		int iYear = Integer.parseInt(st.nextToken());

                return DataUtil.leftZero(Integer.toString(iYear),4) + "/" +
                DataUtil.leftZero(Integer.toString(iMonth),2) +"/" +
                DataUtil.leftZero(Integer.toString(iDay),2);
	}

       //convert date from dd/mm/yyyy to dd-Mon-yyyy
        public static String ConvertDateEng(String dateString){
                if (DataUtil.isEmpty(dateString))
                  return "";
                StringTokenizer st = new StringTokenizer(dateString,"/");
		int iDay = Integer.parseInt(st.nextToken());
		int iMonth = Integer.parseInt(st.nextToken())-1;
		int iYear = Integer.parseInt(st.nextToken());
                if (iMonth > 11)
                    iMonth = 11;
                if (iMonth < 0)
                    iMonth = 0;

                return  DataUtil.leftZero(Integer.toString(iDay),2)+ "-" +
                        monEng[iMonth] + "-" +
                        DataUtil.leftZero(Integer.toString(iYear),4);
	}

        public static String ConvertMonthEng(String dateString) {
                if (DataUtil.isEmpty(dateString))
                  return "";
                StringTokenizer st = new StringTokenizer(dateString,"/");
		int iMonth = Integer.parseInt(st.nextToken())-1;
		int iYear = Integer.parseInt(st.nextToken());
                if (iMonth > 11)
                    iMonth = 11;
                if (iMonth < 0)
                    iMonth = 0;

                return  monEngLower[iMonth] + "-" +
                        DataUtil.leftZero(Integer.toString(iYear),4);
	}

}
