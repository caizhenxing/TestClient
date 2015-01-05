package ctripwireless.testclient.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class EncodePwdUtils {
	private static final Logger logger = Logger.getLogger(EncodePwdUtils.class);
	
	private static String getPassword(String argument,String input){
		String key="";
		try{
			Runtime runtime = Runtime.getRuntime();
			Process p = runtime.exec("cmd /k PwdTool.exe "+argument+" "+input);
			InputStream is = p.getInputStream();
			OutputStream os = p.getOutputStream();
			os.close();
			key = IOUtils.toString(is,"gbk");
			key=StringUtils.substringBetween(key, "", "\r\n");
		}catch(Exception e){
			key="请正确安装AuthTool.exe(联系系统管理员)";
		}
		return key;
	}
	
	public static String getBase64Password(String input){
		return getPassword("/base64",input);
	}
	
	public static String getMD5Password(String input){
		return getPassword("/md5",input);
	}
	
	public static String getCtripDataTransportPassword(String input){
		return getPassword("/ctripdatatransport",input);
	}
	
	public static String getCtripCreditCardTransactionPassword(String input){
		return getPassword("/ctripcreditcardtransaction",input);
	}
	
	public static void main(String args[]) throws Exception{
		BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
		String input = br.readLine();
		System.out.println("Base64加密：" + getBase64Password(input));
		System.out.println("交易密码加密MD5加密：" + getMD5Password(input));
		System.out.println("无线传输交易密码加密：" + getCtripDataTransportPassword(input));
		System.out.println("信用卡加密：" + getCtripCreditCardTransactionPassword(input));
	}
}
