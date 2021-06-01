package com.script.runner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.jdbc.ScriptRunner;

import com.anthem.DbObjectsMigration.Utilities;

public class RunningScripts {

	public static void main(String[] args) throws SQLException, IOException {
	
		Utilities utility = new Utilities();
		
		String user 				= args[0]; 
		String pwd 					= args[1]; 
		String url 					= args[2];
		String sqlFilesPath 		= args[3];		   
		String listFilesPath 		= args[4]; 
		String env 					= args[5];
		
		//String user 			= "abc"; 
		//String pwd  			= "abc"; 
		//String url  			= "jdbc:oracle:thin:@//abc.internal.das:1525/abc";
		//String sqlFilesPath 	= "C:\\Users\\AF38889\\Documents\\DevOps\\Bitbucket_Repos\\TurboDB\\EclipseTesting\\Oracle\\";
		//String listFilesPath 	= "C:\\Users\\AF38889\\Documents\\DevOps\\Bitbucket_Repos\\TurboDB\\EclipseTesting\\Oracle\\list.csv";
		//String env 				= "A";
		
		  
		System.out.println("Start...........");
		//Fetch the SQL File Names
		List<String> listFiles = utility.getOracleSQLFileNames(listFilesPath,env);
		List<String> listFilePaths = new ArrayList<String>();
		
		for (String fileName : listFiles) {
			File sqlFile = new File(fileName);
			listFilePaths.add(sqlFilesPath + fileName);
		}
		System.out.println("Info:- Complete Paths::> "+listFilePaths);
		
		
		for (String sqlFilePath : listFilePaths) {
			boolean sendFullScript = false;
			if(sqlFilePath.contains("Stored_Procedures") || sqlFilePath.contains("Packages") || sqlFilePath.contains("Triggers") || sqlFilePath.contains("Functions") ) {
				sendFullScript = true;
			}else {
				sendFullScript = false;
			}
			
			String fileName = sqlFilePath.substring(sqlFilePath.lastIndexOf("\\")+1, sqlFilePath.length()-4);
			System.out.println("------------Executing "+fileName +"-------------");
			System.out.println("Info:- Log FileName: "+"log_"+fileName+".txt");
			System.out.println("Info:- Error FileName: "+"error_"+fileName+".txt");
			
			FileWriter errorWriter = new FileWriter("error_"+fileName+".txt"); 
			PrintWriter errorPrintWriter = new PrintWriter(errorWriter);
			
			FileWriter logWriter = new FileWriter("log_"+fileName+".txt"); 
			PrintWriter logPrintWriter = new PrintWriter(logWriter);
			
			Connection con = null;
			Reader reader = null;			
			
			try {
				  //Registering the Driver
			      DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
			      
			      //Getting the connection
			      con = DriverManager.getConnection(url, user, pwd);
			      System.out.println("Info:- Connection established......");
			      
			      //Initialize the script runner
			      ScriptRunner sr = new ScriptRunner(con);
			      
			      //Creating a reader object
			      reader = new BufferedReader(new FileReader(sqlFilePath));
			      
			      //Running the script
			      sr.setErrorLogWriter(errorPrintWriter);
			      sr.setLogWriter(logPrintWriter);			
			      //sr.setAutoCommit(true);
			      if(sendFullScript)
			    	  sr.setSendFullScript(true);
			      else
			    	  sr.setSendFullScript(false);
			      sr.setStopOnError(true);
			      sr.runScript(reader);
			      
			}
			catch (Exception e) {
				  e.printStackTrace();
				  System.exit(1);
			}finally {
				  reader.close();
			      con.close();
			}
		  
		}
	}

}
