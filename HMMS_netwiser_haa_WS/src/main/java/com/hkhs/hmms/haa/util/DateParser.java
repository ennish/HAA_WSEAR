package com.hkhs.hmms.haa.util;
import java.util.StringTokenizer;

public class DateParser {
	public DateParser() {
	}

	/**
	 * parser the date string like this "dd/MM/yyyy"
	 * java.util.Date is  Deprecated in jdk 1.2.so we use java.util.Calendar
	 *
	 * @param dateString
	 * @return
	 */
	public static java.sql.Date parser(String dateString){
		if(dateString == null)
			return new java.sql.Date(System.currentTimeMillis());
		else if(dateString.equals(""))
			return new java.sql.Date(System.currentTimeMillis());

		StringTokenizer st = new StringTokenizer(dateString,"/");
		int iDay = Integer.parseInt(st.nextToken());
		int iMonth = Integer.parseInt(st.nextToken()) - 1;
		int iYear = Integer.parseInt(st.nextToken());

		java.util.Calendar cal = java.util.Calendar.getInstance();
		cal.set(iYear, iMonth, iDay);

		return  new java.sql.Date(cal.getTime().getTime());
	}
	
	public static String format(java.sql.Date date){
		if(date==null||DataUtil.isEmpty(date.toString())){
			return "";
		}else{
			String reg = "^[0-9]{4}-[0-9]{1,2}-[0-9]{1,2}$";
			String str = date.toString();
	    	if(str.matches(reg)){
	    		String[] buff = str.split("-");
	    		return buff[2]+"/"+buff[1]+"/"+buff[0];
	    	}else {
	    		return str;
	    	}
		}
	}

}
