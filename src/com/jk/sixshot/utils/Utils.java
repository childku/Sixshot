package com.jk.sixshot.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import com.jk.sixshot.Account;

public class Utils {
	
	public static String getRootConfigPath(){
		String classPath = Account.class.getClassLoader().getResource("").getPath();
		System.out.println("---utils, classPath = " + classPath);
		return classPath;
	}
	
	public static Account getAccount(){
		return loadAccount();
	}

	   //读取账户信息
    private  static Account  loadAccount() {
    	Map<String,String> accountInfo = new HashMap<String, String>();
    	try{
			FileReader filereader=null;
			String path = getRootConfigPath() + "account-info.txt";
			
			filereader = new FileReader(new File(path));
			BufferedReader br = new BufferedReader(filereader);
			String temp = null;
			String []sInfo = new String[2];
			temp = br.readLine();
			while(temp!=null){
				if(!temp.startsWith("#") && !temp.equalsIgnoreCase("")){
					sInfo = temp.split("=");
					if(sInfo.length == 2){
						accountInfo.put(sInfo[0], sInfo[1]);				
					}
				} 		
				temp = br.readLine();
			}
			br.close();
    	}catch(Exception e){
    		e.printStackTrace();
			System.out.println("load account info error\n");
    	}
    	Account account = new Account();
    	account.setAppKey(accountInfo.get("appKey"));
    	account.setDeveloperKey(accountInfo.get("developerKey"));
    	account.setCloudUrl(accountInfo.get("cloudUrl"));
    	account.setTtsCapKey(accountInfo.get("capKey"));
    	return account;
	}

    public static void main(String ...args){
    	loadAccount();
    }
}
