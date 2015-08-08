package com.jk.sixshot.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class Utils {

	public static Map<String,String>  getAccountInfo(){
		return readAccountInfo();
	}
	
	   //读取账户信息
    private  static Map<String,String>  readAccountInfo() {
    	Map<String,String> accountInfo = new HashMap<String, String>();
    	try{
			FileReader filereader=null;
			filereader = new FileReader("./resources/account-info.txt");
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
			System.out.println("load account info error\n");
    	}
    	return accountInfo;
	}
}
