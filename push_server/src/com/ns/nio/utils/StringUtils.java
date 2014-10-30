/**
 * 
 */
package com.ns.nio.utils;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * 
 * @ClassName: StringUtils 
 * @Description: 字符串工具类
 * @author moonights
 * @date 2014-7-24 上午10:49:24
 * 
 */
public class StringUtils {
	/**
	 * 获取当前时间的字符串， 格式："yyyy-MM-dd HH:mm:ss"
	 * 
	 * @return
	 */
	public static String getCurDateString() {
		return getDateString(new Date(), "yyyy-MM-dd HH:mm:ss");
	}

	/**
	 * 根据格式获取字符串
	 * 
	 * @param date
	 * @param format
	 * @return
	 */
	public static String getDateString(Date date, String format) {
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		String dateString = "";
		try {
			dateString = formatter.format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dateString;
	}
	
	public static Integer getInteger(String str) {
		Integer intTemp = 0;
		try {
			intTemp = Integer.parseInt(str);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return intTemp;
	}
	
	/**
	 * 判断字符串是否为空. 如果字符串为null或者全为空格或者为“null”，都返回true.
	 * 
	 * @param pStr 要检查的字符串
	 * @return boolean 值
	 * 
	 */
	public static boolean isBlank(String pStr) {
		return pStr == null || pStr.trim().length() == 0|| pStr.equalsIgnoreCase("null");
	}
}
