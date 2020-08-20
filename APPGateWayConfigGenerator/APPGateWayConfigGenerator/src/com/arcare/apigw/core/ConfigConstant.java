package com.arcare.apigw.core;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;


public class ConfigConstant {
public static Map<String, String> config = new ConcurrentHashMap<String, String>();
	
	public static void setConfig(Map<String, String> _config){
		config = _config;
	}
	
	public static Map<String, String> getConfig(){
		return config;
	}

	/**
	 * init config
	 * @param arg0
	 */
	public static Map<String,String> initConfig(){
		Map<String,String> map = new ConcurrentHashMap<String, String>();
		try {
			System.out.println("--- initConfig start ---");
			Properties prop = new Properties();
			prop.load(new FileInputStream("config/config.properties"));
			prop.stringPropertyNames()
				.forEach( key-> map.put(key, prop.getProperty(key)));
			System.out.println(map);
			ConfigConstant.setConfig(map);
			return map;
		} catch (IOException io) {
			io.printStackTrace();
			return map;
		}finally{
			System.out.println("--- initConfig end ---");
		}
	}
	
	/**
	 * init config
	 * @param arg0
	 */
	public static Map<String,String> initConfig(String configPath){
		Map<String,String> map=new ConcurrentHashMap<String, String>();
		try {
			System.out.println("--- initConfig start ---");
			Properties prop = new Properties();
			prop.load(new FileInputStream(configPath));
			prop.stringPropertyNames()
				.forEach( key-> map.put(key, prop.getProperty(key)));
			System.out.println(map);
			ConfigConstant.setConfig(map);
			return map;
		} catch (IOException io) {
			io.printStackTrace();
			return map;
		}finally{
			System.out.println("--- initConfig end ---");
		}
	}
}
