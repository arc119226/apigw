package com.arcare.apigw.core;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

import javax.sql.DataSource;

import com.mchange.v2.c3p0.ComboPooledDataSource;

@SuppressWarnings("resource")
public class APPGateWaySettingBuilder {
	
	public static void start() throws Exception{
		//close c3p0 error log
		Properties p = new Properties(System.getProperties());
		p.put("com.mchange.v2.log.MLog", "com.mchange.v2.log.FallbackMLog");
		p.put("com.mchange.v2.log.FallbackMLog.DEFAULT_CUTOFF_LEVEL", "OFF");
		System.setProperties(p);

		Map<String,String> config=APPGateWaySettingBuilder.configCreateDatabase();
		APPGateWaySettingBuilder.configGenerator(config);
		APPGateWaySettingBuilder.testConfigConnection();
	}
	
	public static void end() throws Exception{
		if(System.getProperty("os.name").toLowerCase().contains("windows")) {
			System.out.println("--- DEPLOY CONFIG TO "+ConfigConstant.config.get("win.path")+"  ---");
			new File(ConfigConstant.config.get("win.path")).mkdirs();
			Files.copy(Paths.get("./application-production.settings"),
					   Paths.get(ConfigConstant.config.get("win.path")+File.separator+"application-production.settings"),
					   StandardCopyOption.REPLACE_EXISTING);
		}else{
			System.out.println("--- DEPLOY CONFIG TO "+ConfigConstant.config.get("unix.path")+" ---");
			new File(ConfigConstant.config.get("unix.path")).mkdirs();
			Files.copy(Paths.get("./application-production.settings"),
					   Paths.get(ConfigConstant.config.get("unix.path")+File.separator+"application-production.settings"),
					   StandardCopyOption.REPLACE_EXISTING);
			Files.copy(Paths.get("./resource/APPGateWay.war"),
					   Paths.get(ConfigConstant.config.get("unix.path")+File.separator+"application-production.settings"),
					   StandardCopyOption.REPLACE_EXISTING);
			Files.copy(Paths.get("./install/APPGateWay.sh"),
					   Paths.get(ConfigConstant.config.get("unix.path")+File.separator+"application-production.settings"),
					   StandardCopyOption.REPLACE_EXISTING);
		}
		Files.delete(Paths.get("./application-production.settings"));
	}
	
	private static Map<String,String> configCreateDatabase() throws Exception{

		//for return user setting info
		Map<String,String> returnDbSettingInfo=new HashMap<>();

		System.out.println("Before Generate setting file, do you want to create a new database for install APIGateway.");
		System.out.println("(y)Yes, I want create database");
		System.out.println("(n)No, I already have database");
		String yOrN = ValidateInput.validataYesOrN();

		System.out.println("Please provide database connection info.");
		System.out.print("Database ip:");
		String ip = ValidateInput.validateIP();

		System.out.print("Database port:");
		String port = ValidateInput.validatePort();

		String dbName="";
		if(yOrN.matches("y|Y")){
			System.out.print("New database name:");
			dbName = ValidateInput.validateDatabaseName();
		}else if(yOrN.matches("n|N")){
			System.out.print("Database name:");
			dbName = ValidateInput.validateDatabaseName();
		}

		System.out.println("Choose database driver.");
		System.out.println("(1)mariadb");
		System.out.println("(2)mysql");
		System.out.println("(3)mssql");
		String db=ValidateInput.validateChooseJDBCDriver();

		String createJdbcUrl="";
		String jdbcUrl="";
		String driverCalss="";
		String dbType="";

		if("1".equals(db)){
			createJdbcUrl=String.format("jdbc:mariadb://%s:%s", ip,port);
			jdbcUrl=String.format("jdbc:mariadb://%s:%s/%s", ip,port,dbName);
			driverCalss="org.mariadb.jdbc.Driver";
			dbType="maria";
		}else if("2".equals(db)){
			createJdbcUrl=String.format("jdbc:mysql://%s:%s", ip,port);
			jdbcUrl=String.format("jdbc:mysql://%s:%s/%s", ip,port,dbName);
			driverCalss="com.mysql.jdbc.Driver";
			dbType="mysql";
		}else if("3".equals(db)){
			createJdbcUrl=String.format("jdbc:jtds:sqlserver://%s:%s", ip,port);
			jdbcUrl=String.format("jdbc:jtds:sqlserver://%s:%s;databaseName=%s", ip,port,dbName);
			driverCalss="net.sourceforge.jtds.jdbc.Driver";
			dbType="mssql";
		}

		returnDbSettingInfo.put("dataSource.url", "'"+jdbcUrl+"'");
		returnDbSettingInfo.put("dataSource.driverClassName", "'"+driverCalss+"'");
		returnDbSettingInfo.put("dbType", dbType);
		

		if(yOrN.matches("y|Y")){

			System.out.println("Input database admin account for create user:");
			//sa/root
			String adminUser=new Scanner(System.in).nextLine();
			
			System.out.println("Input database admin password for create user:");
			//''/yourrootpassword
			String adminPassword=new Scanner(System.in).nextLine();
			
			System.out.println("Input application db user account for use database:");
			String apUser=new Scanner(System.in).nextLine();
			
			System.out.println("Input application db user password for use database:");
			String apPassword=new Scanner(System.in).nextLine();
			
			returnDbSettingInfo.put("dataSource.username", "'"+apUser+"'");
			returnDbSettingInfo.put("dataSource.password", "'"+apPassword+"'");
			
			System.out.println("--- CREATE DATABASE START ---\n");
			//try create a empty database
			ComboPooledDataSource adminDS= new ComboPooledDataSource();
			adminDS.setDriverClass(driverCalss);
			adminDS.setJdbcUrl(createJdbcUrl);
			adminDS.setUser(adminUser);
			adminDS.setPassword(adminPassword);
			
			System.out.println("Do you want create new user for use database.");
			System.out.println("(y)create new account.");
			System.out.println("(n)use old account.");
			String createUserYorN = ValidateInput.validataYesOrN();
			
			if(createUserYorN.matches("y|Y")){
				System.out.println("--- CREATE USER START ---\n");
				Connection con=null;
				Statement stmt = null;
				try{
					con = adminDS.getConnection();
					stmt = con.createStatement();
					
					if(dbType.matches("maria|mysql")){
						//create user
							String createUserLocal="create user '%s'@'localhost' IDENTIFIED by '%s'";
							stmt.execute(String.format(createUserLocal, apUser,apPassword));
							String grantUserLocal="grant all PRIVILEGES on "+dbName+".* to '%s'@'localhost'";
							stmt.execute(String.format(grantUserLocal, apUser));
							String createUserRemote="create user '%s'@'%%' IDENTIFIED by '%s'";
							stmt.execute(String.format(createUserRemote, apUser,apPassword));
							String grantUserRemote="grant all PRIVILEGES on "+dbName+".* to '%s'@'%%'";
							stmt.execute(String.format(grantUserRemote, apUser));
							String flush="FLUSH PRIVILEGES";
							stmt.execute(flush);
						
					}else if(dbType.matches("mssql")){
						//username , password
						String createLogin="CREATE LOGIN [%s] WITH PASSWORD = '%s';\n";
						//username,username
						String createUser="CREATE USER [%s] FOR LOGIN [%s] WITH DEFAULT_SCHEMA = [dbo]\n";
						//username
						String grantUser="GRANT CREATE DATABASE to [%s];\n";
						String sql=String.format(createLogin+createUser+grantUser,
								apUser,apPassword,apUser,apUser,apUser);
						stmt.execute(sql);
	
					}
					System.out.println("create user "+apUser+"/"+apPassword+" complete.");
					System.out.println("create database "+dbName+" complete.");
				}catch(Exception e){
					System.out.println("create database error:"+e.getMessage());
				}finally{
			        if(stmt!=null){
			        	stmt.close();
				    }
					if(con!=null){
						con.close();
					}
					System.out.println("--- CREATE USER END ---\n");
				}
			
			}else{
				System.out.println("--- SKIP CREATE USER ---\n");
			}
			
			ComboPooledDataSource apDS= new ComboPooledDataSource();
			apDS.setDriverClass(driverCalss);
			apDS.setJdbcUrl(createJdbcUrl);
			apDS.setUser(apUser);
			apDS.setPassword(apPassword);
			Connection con1=null;
			Statement stmt1 = null;
			try{
				con1 = apDS.getConnection();
				stmt1 = con1.createStatement();
				
				if(dbType.matches("maria|mysql")){
					stmt1.execute("CREATE DATABASE IF NOT EXISTS "+dbName);
				}else if(dbType.matches("mssql")){
					stmt1.execute("CREATE DATABASE "+dbName);
				}
				System.out.println("create database "+dbName+" complete.");
			}catch(Exception e){
				System.out.println("create database error:"+e.getMessage());
			}finally{
		        if(stmt1!=null){
		        	stmt1.close();
			    }
				if(con1!=null){
					con1.close();
				}
			}
			System.out.println("--- CREATE DATABASE END ---\n");
		}else{
			System.out.println("--- SKIP CREATE DATABASE ---\n");
			
		}
		
		return returnDbSettingInfo;
	}
	
	private static void configGenerator(Map<String,String> config) throws IOException{
		List<String> resultSetting = new ArrayList<>();
		List<String> settings = Files.readAllLines(Paths.get("./resource/application-template.settings"));
		
		System.out.println("--- START SETTING ---\n");
		System.out.println("If you don't want change default value just press [ENTER].\n");
		
		settings.forEach(line -> {
			if(line.startsWith("//")){
				System.out.println(line.replace("//", ""));
			}else{
				String key = line.split(" = ")[0];
				
				String defaultValue;
				if(null != config.get(key)){
					defaultValue = config.get(key);
				}else{
					defaultValue = line.split(" = ")[1].replaceAll("\\{|\\}", "");
				}
				
				System.out.print(key + " (" + defaultValue + ")" + ":");
				
				String result = new Scanner(System.in).nextLine();
				if("".equals(result)){
					result = defaultValue;
				}else{
					if (defaultValue.trim().startsWith("'")) {
						if("".equals(result)){
							result = defaultValue;
						}else{
							result = "'" + result + "'";
						}	
					}else{
						if("".equals(result)){
							result = defaultValue;
						}
					}
				}
				resultSetting.add(key + " = " + result);
			}
		});
		System.out.println("--- END SETTING ---\n");
		System.out.println("--- SETTING RESULT ---\n");
		resultSetting.forEach(line -> System.out.println(line));
		File file=new File("./application-production.settings");
		if(file.exists()){
			file.delete();
		}
		Files.write(Paths.get("./application-production.settings"), (Iterable<String>)resultSetting::iterator);
	}

	private static DataSource getDataSourceFromConfig() throws Exception{

		List<String> settings = Files.readAllLines(Paths.get("./application-production.settings"));
		ComboPooledDataSource dataSource= new ComboPooledDataSource();
		dataSource.setDriverClass(settings.stream()
						.filter(s->s.startsWith("dataSource.driverClassName"))
						.findFirst().get().split(" = ")[1].replaceAll("\\'", "").trim());
		dataSource.setJdbcUrl(settings.stream()
						.filter(s->s.startsWith("dataSource.url"))
						.findFirst().get().split(" = ")[1].replaceAll("\\'", "").trim());
		dataSource.setUser(settings.stream()
						.filter(s->s.startsWith("dataSource.username"))
						.findFirst().get().split(" = ")[1].replaceAll("\\'", "").trim());
		dataSource.setPassword(settings.stream()
						.filter(s->s.startsWith("dataSource.password"))
						.findFirst().get().split(" = ")[1].replaceAll("\\'", "").trim());
        
		// Optional Settings
		dataSource.setMinPoolSize(1);
		dataSource.setAcquireIncrement(1);
		dataSource.setMaxPoolSize(1);
		dataSource.setMaxStatements(1);
		dataSource.setAutoCommitOnClose(false);
		dataSource.setUnreturnedConnectionTimeout(100);
		return dataSource;
	}
	
	private static void testConfigConnection() throws IOException{
		System.out.println("Do you want to test db connection now.");
		String yOrN = ValidateInput.validataYesOrN();
		if(yOrN.matches("n|N")){
			System.out.println("--- SKIP TEST CONFIG ---");
			return;
		}
		System.out.println("--- TEST CONFIG START ---");
		Connection conn = null;
		Statement stmt = null;
		   try{
			   
			  conn = getDataSourceFromConfig().getConnection();
		      System.out.println("Creating statement...");
		      stmt = conn.createStatement();

		      ResultSet rs = stmt.executeQuery("SELECT 1");

		      while(rs.next()){
		         System.out.println("TEST QUEYR..."+(rs.getInt(1)==1));
		      }
		      rs.close();
		      stmt.close();
		      conn.close();
		   }catch(SQLException se){
		      se.printStackTrace();
		   }catch(Exception e){
		      e.printStackTrace();
		   }finally{
		      try{
		         if(stmt!=null){
		            stmt.close();
		         }
		      }catch(SQLException se){
		    	  se.printStackTrace();
		      }
		      try{
		         if(conn!=null){
		            conn.close();
		         }
		      }catch(SQLException se){
		         se.printStackTrace();
		      }
		   }
		   System.out.println("--- TEST CONFIG END ---");
	}
}
