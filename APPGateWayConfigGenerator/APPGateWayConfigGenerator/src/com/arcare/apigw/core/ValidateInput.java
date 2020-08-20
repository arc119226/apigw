package com.arcare.apigw.core;

import java.util.Scanner;
/**
 * 
 * @author FUHSIANG_LIU
 *
 */
@SuppressWarnings("resource")
public class ValidateInput {
	/**
	 * check yes or no
	 * @return
	 */
	public static String validataYesOrN(){
		String yOrN = new Scanner(System.in).nextLine();
		if(!yOrN.matches("y|Y|n|N")){
			System.out.println("Not valid input, please try again.");
			return ValidateInput.validataYesOrN();
		}
		return yOrN;
	}
	/**
	 * check valid ip format
	 * @return
	 */
	public static String validateIP(){
		String ip = new Scanner(System.in).nextLine();
		if(!ip.matches("^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$")){
			System.out.println("Not valid input, please try again.");
			return ValidateInput.validateIP();
		}
		return ip;
	}
	/**
	 * check valid port number
	 * @return
	 */
	public static String validatePort(){
		String port = new Scanner(System.in).nextLine();
		if(!port.matches("^([0-9]{1,4}|[1-5][0-9]{4}|6[0-4][0-9]{3}|65[0-4][0-9]{2}|655[0-2][0-9]|6553[0-5])$")){
			System.out.println("Not valid input, please try again.");
			return ValidateInput.validatePort();
		}
		return port;
	}
	/**
	 * check valid dbname
	 * @return
	 */
	public static String validateDatabaseName(){
		String dbName = new Scanner(System.in).nextLine();
		if(!dbName.matches("^[0-9a-zA-Z$_]+$")){
			System.out.println("Not valid input, please try again.");
			return ValidateInput.validateDatabaseName();
		}
		return dbName;
	}
	/**
	 * 1 maria
	 * 2 mysql
	 * 3 mssql
	 * @return
	 */
	public static String validateChooseJDBCDriver(){
		String db=new Scanner(System.in).nextLine();
		if(!db.matches("1|2|3")){
			System.out.println("Not valid input, please try again.");
			return ValidateInput.validateChooseJDBCDriver();
		}
		return db;
	}
	/**
	 * 
	 * @return
	 */
	public static String validatePath(){
		String path=new Scanner(System.in).nextLine();
		if(!path.matches("^[0-9a-zA-Z$_]+$")){
			System.out.println("Not valid input, please try again.");
			return validatePath();
		}
		return path;
	}
}
