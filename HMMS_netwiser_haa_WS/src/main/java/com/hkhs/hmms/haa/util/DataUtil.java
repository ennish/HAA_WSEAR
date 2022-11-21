package com.hkhs.hmms.haa.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataUtil {

	//TODO REGEX is not a good solution preventing sql injection,check every input value,or use function in db to check them
	public final static String SQL_REG = "(?:')|(?:--)|(\\*(?:.|[\\\\n\\\\r])*?\\\\)|(\\\\b(select|update|and|or|delete|insert|trancate|char|into|substr|ascii|declare|exec|count|master|into|drop|execute)\\b)";

	public static boolean isEmpty(String s) {
		if (s == null)
			return true;
		s = s.trim();
		if (s.length() == 0)
			return true;
		return false;
	}

	public static boolean isEmpty(String[] arrayStr) {
		if (arrayStr == null)
			return true;

		if (arrayStr.length == 0)
			return true;
		return false;
	}

	public static boolean isNotEmpty(String[] arrayStr) {
		return !isEmpty(arrayStr);
	}

	public static boolean isNotEmpty(String s) {
		return !isEmpty(s);
	}

	public static String[] split(String str, String delim) {
		Vector splitList = null;
		StringTokenizer st = null;

		if (str == null) {
			return null;
		}
		if (str.indexOf(delim) < 0) {
			String[] arr = new String[1];
			arr[0] = str;
			return arr;
		}
		if (delim != null) {
			st = new StringTokenizer(str, delim);
		} else {
			st = new StringTokenizer(str);

		}
		if (st != null && st.hasMoreTokens()) {
			splitList = new Vector();
			while (st.hasMoreTokens()) {
				splitList.add(st.nextToken());
			}
		}

		String[] arr = new String[splitList.size()];
		splitList.toArray(arr);

		return arr;
	}

	public static Integer StringToInt(String f) {
		if (f == null || f.equals(""))
			return null;
		try {
			return Integer.valueOf(f);
		} catch (NumberFormatException e) {
			return new Integer(0);
		}
	}

	public static Double StringToDouble(String f) {
		if (f == null || f.equals(""))
			return null;
		try {
			return Double.valueOf(f);
		} catch (NumberFormatException e) {
			return new Double((double) 0);
		}
	}

	public static String nvl(String param, String val) {
		int i;
		String s, s1;
		if (DataUtil.isEmpty(param)) // param == null
			return val;
		else
			return param;
	}

	public static String leftZero(String s, int len) {
		int realLen, i;
		if (s == null)
			return s = "";
		s = s.trim();
		realLen = s.length();
		for (i = 0; i < len - realLen; i++)
			s = "0" + s;
		return s;
	}

	public static String PS(String param) {
		int i;
		String s, s1;
		if (param == null)
			return "";
		else {
			s = "";
			for (i = 0; i < param.length(); i++) {
				s1 = param.substring(i, i + 1);
				if (s1.equals("\'"))
					s += "''";
				else
					s += s1;
			}
			return s;
		}
	}

	public static String strReplaceAll(String source, String find, String replace) throws IllegalArgumentException {

		boolean bIgnoreCase = false;

		if (isEmpty(source)) {
			return "";
		} else if (isEmpty(find)) {
			return (source);
		}
		if (replace == null) {
			replace = " ";
		}
		StringBuffer sb = new StringBuffer(source);
		StringBuffer mod;
		boolean bDone = false;
		int prevIndex = 0, currIndex = 0, i = 0;

		if (bIgnoreCase) {
			source = source.toLowerCase();
			find = find.toLowerCase();
		}
		mod = new StringBuffer(source);
		while (!bDone) {
			if ((currIndex = mod.toString().indexOf(find, prevIndex)) != -1) {
				sb = sb.replace(currIndex, currIndex + find.length(), replace);
				mod = mod.replace(currIndex, currIndex + find.length(), replace);
				prevIndex = currIndex + replace.length();
			} else {
				bDone = true;
			}
		}
		return (sb.toString());
	}

	public static String strReplace(String str, String srh, String rep) {
		if (str == null || srh == null || rep == null)
			return str;
		StringTokenizer st = new StringTokenizer(str, srh);
		String s = "";
		while (st.hasMoreElements()) {
			String token = st.nextToken();
			if (!s.equals(""))
				s += (rep + token);
			else
				s = token;
		}
		return s;
	}

	public static String sqlReplaceAll(String source, String find, String replace) throws IllegalArgumentException {

		boolean bIgnoreCase = false;

		if (isEmpty(source)) {
			return "";
		} else if (isEmpty(find)) {
			return (source);
		}
		if (replace == null) {
			replace = " ";
		} else {
			replace = DataUtil.escSql(replace);
		}

		StringBuffer sb = new StringBuffer(source);
		StringBuffer mod;
		boolean bDone = false;
		int prevIndex = 0, currIndex = 0, i = 0;

		if (bIgnoreCase) {
			source = source.toLowerCase();
			find = find.toLowerCase();
		}
		mod = new StringBuffer(source);
		while (!bDone) {
			if ((currIndex = mod.toString().indexOf(find, prevIndex)) != -1) {
				sb = sb.replace(currIndex, currIndex + find.length(), replace);
				mod = mod.replace(currIndex, currIndex + find.length(), replace);
				prevIndex = currIndex + replace.length();
			} else {
				bDone = true;
			}
		}
		return (sb.toString());
	}

	public static String escSql(String param) {
		int i;
		String s, s1;
		if (param == null)
			return "";
		else {
			param = param.trim();
			s = "";
			for (i = 0; i < param.length(); i++) {
				s1 = param.substring(i, i + 1);
				if (s1.equals("\'"))
					s += "\'\'";
				else
					s += s1;
			}
			return s;
		}
	}

	public static String escXml(String param) {
		int i;
		String s, s1;
		if (param == null)
			return "";
		else {
			param = param.trim();
			s = "";
			for (i = 0; i < param.length(); i++) {
				s1 = param.substring(i, i + 1);
				if (s1.equals("\""))
					s += "&quot;";
				else if (s1.equals("&"))
					s += "&amp;";
				else if (s1.equals("<"))
					s += "&lt;";
				else if (s1.equals(">"))
					s += "&gt;";
				else
					s += s1;
			}
			return s;
		}
	}

	public static String escJson(String param) {
		StringBuilder stringBuilder = new StringBuilder();

		return stringBuilder.toString();
	}

	public static java.sql.Date stringToDate(String dat) {
		if (dat == null || dat.equals(""))
			return null;
		return DateParser.parser(dat);
	}

	public static Float stringToFloat(String f) {
		if (f == null || f.equals(""))
			return null;
		try {
			return Float.valueOf(f);
		} catch (NumberFormatException e) {
			return new Float(0f);
		}
	}

	public static Double stringToDouble(String f) {
		if (f == null || f.equals(""))
			return null;
		try {
			return Double.valueOf(f);
		} catch (NumberFormatException e) {
			return new Double((double) 0);
		}
	}

	public static Integer stringToInt(String f) {
		if (f == null || f.equals(""))
			return null;
		try {
			return Integer.valueOf(f);
		} catch (NumberFormatException e) {
			return new Integer(0);
		}
	}

	public static Double stringToAmount(String f) {
		if (f == null)
			return new Double(0);
		f = DataUtil.om(f);
		if (f == null || f.equals(""))
			return null;
		try {
			return Double.valueOf(f);
		} catch (NumberFormatException e) {
			return new Double((double) 0);
		}
	}

	public static String dtos(Double f) { // double to s with money deli ","
		return em(dtos_bean(f));
	}

	public static String dtos_bean(Double f) { // double to s with money deli ","
		double fv, lfv;
		long l;
		String s = "";
		int len;
		if (f != null) {
			fv = f.doubleValue();
			lfv = fv;
			if (fv < 0)
				fv = -fv;
			fv *= 100.00;
			fv = fv + 0.1;
			l = (long) fv;
			s = String.valueOf(l);
			len = s.length();
			if (len == 0)
				return "0";
			else if (len == 1)
				s = "00" + s;
			else if (len == 2)
				s = "0" + s;
			if (len < 3)
				len = 3;
			s = s.substring(0, len - 2) + "." + s.substring(len - 2, len);
			if (lfv < 0)
				s = "-" + s;
		}
		return s;
	}

	public static String em(String s) { // Embed Money Delimiter
		int i, rlen = -1, k;
		String t = "", sign = "";
		// Find delimeter
		if (isEmpty(s))
			return "";
		if ((s.substring(0, 1)).equals("."))
			s = "0" + s;
		else if (s.length() > 2) {
			if ((s.substring(0, 1)).equals("-") && (s.substring(1, 2)).equals("."))
				s = "-0." + s.substring(2);
		}
		for (i = 0; i < s.length(); i++) {
			String s1 = s.substring(i, i + 1);
			if (s1.equals(".")) {
				rlen = i;
				break;
			}
		}
		if (rlen != -1) {
			t = s.substring(rlen, s.length());
			s = s.substring(0, rlen);
		}
		k = 0;
		sign = s.substring(0, 1);
		if (sign.equals("-"))
			s = s.substring(1, s.length());
		if (s.length() <= 3)
			t = s + t;
		else {
			for (i = s.length() - 1; i >= 0; i--) {
				String s1 = s.substring(i, i + 1);
				if (k % 3 == 0 && k > 0) {
					t = s1 + "," + t;
				} else
					t = s1 + t;
				k++;
			}
		}
		if (sign.equals("-"))
			t = "-" + t;
		return t;
	}

	public static String om(String s) { // Omit Money Delimiter
		int i;
		if (DataUtil.isEmpty(s))
			return "";
		String t = "";
		for (i = 0; i < s.length(); i++) {
			String s1 = s.substring(i, i + 1);
			if (!s1.equals(","))
				t += s1;
		}
		return t;
	}

	public static String em2(String s) { // Embed Money Delimiter
		String t = DataUtil.em(s);

		if (t.indexOf('.') < 0)
			t += ".00";
		return t;
	}

	public static String es(String param) {
		if (param == null || param.equals(""))
			return "";
		param = param.trim();
		return param; // String(param.getBytes("8859_1"), "utf-8");
	}

	public static String ms(String s) {
		if (isEmpty(s))
			return "";
		else
			return (dtos(stringToDouble(om(s))));
	}

	public static String ms2(String s) {
		if (isEmpty(s))
			return "";
		else
			return (em2(om(s)));
	}

	public static String ms2(double d) {
		return em(dtos_bean(new Double(d)));
	}

	public static boolean checkDate(String dat) {
		Date parsed = null;
		boolean formatOK = false;

		// first correct format
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

		// if an unarseable exception occurs, we have to try the second format
		try {
			parsed = format.parse(dat);
			formatOK = true;
		} catch (Exception e) {
			// do whatever
		}

		return formatOK;
	}

	public static boolean checkNumber(String f) {
		if (f == null || f.equals(""))
			return true;
		try {
			double d = Double.valueOf(f);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public static String LeftZero(String s, int len) {
		int realLen, i;
		if (s == null)
			return s = "";
		s = s.trim();
		realLen = s.length();
		for (i = 0; i < len - realLen; i++)
			s = "0" + s;
		return s;
	}

	public static String dtos_bean_1(Double f) { // double to s with money deli ","
		double fv, lfv;
		long l;
		String s = "";
		int len;
		if (f != null) {
			fv = f.doubleValue();
			lfv = fv;
			if (fv < 0)
				fv = -fv;
			fv *= 100.00;
			fv += 0.0000001; // add delta to prevent from precision losing
			fv = Math.floor(fv);
			fv = fv / 10.00;
			fv = Math.round(fv);
			l = (long) fv;
			s = String.valueOf(l);
			len = s.length();
			if (len == 0)
				return "0";
			else if (len == 1)
				s = "0" + s;
			if (len < 2)
				len = 2;
			s = s.substring(0, len - 1) + "." + s.substring(len - 1, len);
			if (lfv < 0)
				s = "-" + s;
		}
		return s;
	}

	public static String LeftBlank(String s, int len) {
		int realLen, i;
		if (s == null)
			return s = "";
		// s = s.trim();
		realLen = s.length();
		for (i = 0; i < len - realLen; i++)
			s = " " + s;
		return s;
	}

	public static String RightBlank(String s, int len) {
		int realLen, i;
		if (s == null)
			return s = "";
		// s = s.trim();
		realLen = s.length();
		for (i = 0; i < len - realLen; i++)
			s = s + " ";
		if (s.length() > len)
			s = s.substring(0, len);
		return s;
	}

	public static String Left(String s, int len) {
		int realLen, i;
		if (s == null)
			return s = "";
		// s = s.trim();
		realLen = s.length();
		if (s.length() > len)
			s = s.substring(0, len);
		return s;
	}

	public static java.sql.Date StringToDate(String dat) {
		if (dat == null || dat.equals(""))
			return null;
		return DateParser.parser(dat);
	}

	public static String GetFlagValue(String xml, String tag, String nullVal) {
		String flag = "<" + tag + ">";
		String endFlag = "</" + tag + ">";

		int start = xml.indexOf(flag);
		int end = 0;

		if (start > 0) {
			start += flag.length();
			end = xml.indexOf(endFlag);
			if (end - start == 0)
				return nullVal;
			else
				// return xml.substring(start, end - start);
				return xml.substring(start, end);
		} else
			return nullVal;
	}

	public static boolean regCheck(String reg, String string) {
		boolean tem = false;

		Pattern pattern = Pattern.compile(reg, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(string);

		tem = matcher.find();
		return tem;
	}

	/**
	 * check if sql query parameter is invalid
	 * 
	 * @return true if parameter is invalid
	 * @param args
	 * @return
	 */
	public static boolean CheckCateria(String... args) {
		if (args == null || args.length == 0) {
			return false;
		}
		for (int i = 0; i < args.length; i++) {
			if (isEmpty(args[i])) {
				continue;
			}
			if (regCheck(SQL_REG, args[i])) {
				return true;
			}
		}
		return false;
	}
}
