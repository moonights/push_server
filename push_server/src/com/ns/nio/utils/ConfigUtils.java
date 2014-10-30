package com.ns.nio.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.log4j.Logger;


/**
 * 
 * @ClassName: ConfigUtils 
 * @Description: 配置工具类
 * @author moonights
 * @date 2014-7-24 上午10:49:24
 * 
 */
public class ConfigUtils {
	private static Logger logger  =  Logger.getLogger(ConfigUtils.class);
	private static final String CONFIG_FILE = "/config.properties";
	private static Properties props = new Properties();// 属性	
	
	static{
		// 读取配置文件，根据配置文件创建配置信息		
		try {
			InputStream fileInput =ConfigUtils.class.getResourceAsStream(CONFIG_FILE);
			// 加载配置文件
			props.load(fileInput);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			logger.info("配置文件不存在，请检查路径");
		} catch (IOException e) {
			e.printStackTrace();
			logger.info("配置文件读取错误");
		}
	}
	
	public static String getStr(String key){
		return props.getProperty(key);
	}
	
	public static Long getLong(String key){
		String str = props.getProperty(key);
		if(StringUtils.isBlank(str.trim())){
			return null;
		}
		
		return Long.valueOf(str.trim()); 
	}
	
	public static Integer getInt(String key){
		String str = props.getProperty(key);
		if(StringUtils.isBlank(str.trim())){
			return null;
		}
		
		return Integer.valueOf(str.trim()); 
	}
	
	public static Boolean getBoolean(String key){
		String str = props.getProperty(key);
		if(StringUtils.isBlank(str)){
			return false;
		}
		
		return Integer.valueOf(str.trim()) > 0 ? true : false;
	}
}
