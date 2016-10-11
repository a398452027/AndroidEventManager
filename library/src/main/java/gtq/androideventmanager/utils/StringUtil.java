package gtq.androideventmanager.utils;

import android.test.FlakyTest;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <一句话功能简述> <功能详细描述>
 * 
 * @author Administrator
 * @version [版本号, 2014年4月10日]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public class StringUtil {

	/**
	 * 判断字符串是否为空串 <功能详细描述>
	 * 
	 * @param str
	 * @return [参数说明]
	 * 
	 * @return boolean [返回类型说明]
	 * @exception throws [违例类型] [违例说明]
	 * @see [类、类#方法、类#成员]
	 */
	public static boolean isEmpty(String str) {
		if (str == null || str.length() == 0 || str.equals("")
				|| str.trim().length() == 0) {
			return true;
		}
		return false;
	}
	
	public static boolean isNumeric(String str)
	{ 
	   Pattern pattern = Pattern.compile("[0-9]*"); 
	   Matcher isNum = pattern.matcher(str);
	   if(!isNum.matches())
	   {
	       return false; 
	   } 
	   return true; 
	}

	/**
	 * 去掉字符串前后的 引号 <功能详细描述>
	 * 
	 * @param str
	 * @return [参数说明]
	 * 
	 * @return String [返回类型说明]
	 * @exception throws [违例类型] [违例说明]
	 * @see [类、类#方法、类#成员]
	 */
	public static String trimQuot(String str) {
		String[] quots = { "\"", "'" };
		if (str == null) {
			return str;
		}
		str = str.trim();
		for (String quot : quots) {
			if (str.startsWith(quot)) {
				str = str.substring(quot.length());
			}
			if (str.endsWith(quot)) {
				str = str.substring(0, str.length() - quot.length());
			}
		}

		return str;
	}

	/**
	 * 返回字节数组的字符串形式 例如：[12,23,127,-44] <功能详细描述>
	 * 
	 * @param data
	 * @return [参数说明]
	 * 
	 * @return String [返回类型说明]
	 * @exception throws [违例类型] [违例说明]
	 * @see [类、类#方法、类#成员]
	 */
	public static String getBytesString(byte[] data) {
		if (data == null) {
			return null;
		}
		if (data.length == 0) {
			return "[]";
		}
		StringBuilder result = new StringBuilder("[");
		for (int i = 0; i < data.length; i++) {
			byte b = data[i];
			if (i == 0) {
				result.append(b);
				continue;
			}
			result.append("," + b);
		}
		result.append("]");
		return result.toString();
	}

	public static String fillCharLen(String ch, int len) {
		StringBuilder builder = new StringBuilder(len * 2);
		int chlen = ch.length();
		boolean bofrist = false;
		String repeatstr = ch;
		while (true) {
			if (!bofrist) {
				builder.append(ch);
				bofrist = true;
			} else {
				if (repeatstr.length() < (len / 4)) {
					repeatstr = builder.toString();
				}
				builder.append(repeatstr);
			}
			if (builder.length() >= len) {
				break;
			}
		}
		String strret = builder.toString();
		if (strret.length() == len) {
			return strret;
		} else {
			return strret.substring(0, len);
		}
	}

	public static String safe_filter(String safe_str) {
		if (safe_str != null) {
			if (safe_str.length() >= 8) {
				int nch = safe_str.length() / 6;
				int endstart = safe_str.length() - nch;
				return safe_str.substring(0, nch)
						+ fillCharLen("*", safe_str.length() - nch * 2)
						+ safe_str.substring(endstart, safe_str.length());
			} else if (safe_str.length() >= 3) {
				return safe_str.charAt(0)
						+ fillCharLen("*", safe_str.length() - 2)
						+ safe_str.charAt(safe_str.length() - 1);
			} else {
				return safe_str;
			}
		}
		return safe_str;
	}


}
