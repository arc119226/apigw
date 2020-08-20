package com.arcare.apigw.core;


/**
 * 
 * @author FUHSIANG_LIU
 *
 */
public class ConfigGenerator {
	
	public static void main(String[] args) throws Exception {
		ConfigConstant.initConfig();
		APPGateWaySettingBuilder.start();
		APPGateWaySettingBuilder.end();
	}

	
}
