package com.jk.sixshot;

import java.util.List;
import java.util.Map;

import com.jk.sixshot.organ.auditory.Listener;
import com.jk.sixshot.organ.language.Speaker;
import com.jk.sixshot.organ.language.StatementAnalyzer;
import com.jk.sixshot.utils.Utils;
import com.sinovoice.hcicloudsdk.api.HciCloudSys;
import com.sinovoice.hcicloudsdk.api.HciLibPath;
import com.sinovoice.hcicloudsdk.common.HciErrorCode;
import com.sinovoice.hcicloudsdk.common.InitParam;

public class Sixshot {

	private static Map<String,String> accountInfo = null;
	private Listener  listener = null;
	private StatementAnalyzer analyzer = new StatementAnalyzer(); 
	private Speaker speaker = null;

	public Sixshot(){
		init();
	}
	private void init() {
		initAccount();
		initSysLibs();
		initEngine();
		initListener();
		initSpeaker();
	}
	private void initAccount(){
		accountInfo = Utils.getAccountInfo();
	}
	private void initSysLibs(){
		String path = System.getProperty("user.dir");
		String sysLibPath[] = new String[]{
				path + "/libs/libcurl.dll" ,
				path + "/libs/hci_sys.dll" ,
				path + "/libs/hci_sys_jni.dll" 
		};
		HciLibPath.setSysLibPath(sysLibPath);
	}

	private void initEngine(){

		//系统初始化及获取能力信息
		String authDirPath = "./UserInfo/";
		String logDirPath = "./Log/";
		
		//前置条件：无
		InitParam initparam = new InitParam();
		// 授权文件所在路径，此项必填
		initparam.addParam(InitParam.PARAM_KEY_AUTH_PATH, authDirPath);
		// 是否自动访问云授权,详见  获取授权/更新授权文件处注释
		initparam.addParam(InitParam.PARAM_KEY_AUTO_CLOUD_AUTH, "no");
		// 灵云云服务的接口地址，此项必填
		initparam.addParam(InitParam.PARAM_KEY_CLOUD_URL, accountInfo.get("cloudUrl"));	
		// 开发者Key，此项必填，由捷通华声提供
		initparam.addParam(InitParam.PARAM_KEY_DEVELOPER_KEY, accountInfo.get("developerKey"));
		// 开发者ID，此项必填，由捷通华声提供
		initparam.addParam(InitParam.PARAM_KEY_APP_KEY, accountInfo.get("appKey"));
		//开发者密钥，此项必填，由捷通华声提供
		initparam.addParam(InitParam.PARAM_KEY_LOG_FILE_COUNT, "5");
		//日志数目，默认保留多少个日志文件，超过则覆盖最旧的日志
		initparam.addParam(InitParam.PARAM_KEY_LOG_FILE_PATH, logDirPath);
		//日志的路径，可选，如果不传或者为空则不生成日志
		initparam.addParam(InitParam.PARAM_KEY_LOG_FILE_SIZE, "1024");
		//日志大小，默认一个日志文件写多大，单位为K
		initparam.addParam(InitParam.PARAM_KEY_LOG_LEVEL, "5");
		//日志等级，0=无，1=错误，2=警告，3=信息，4=细节，5=调试，SDK将输出小于等于logLevel的日志信息

		// 初始化System
		System.out.println("HciCloudAsr InitParam: " + initparam.getStringConfig());
		int errorCode = HciCloudSys.hciInit(initparam.getStringConfig(), null);
		if(errorCode != HciErrorCode.HCI_ERR_NONE){
			System.out.println("HciCloudAsr init error: " + errorCode);
			return;
		}
		System.out.println("HciCloudAsr init OK");

	}
	private void initListener(){
		listener = new Listener(this, accountInfo);
	}
	
	private void initSpeaker(){
		speaker = new Speaker(this, accountInfo);
	}
	
	/**
	 * 说话
	 * @param statement 语句
	 */
	private void speak(String statement){
		speaker.speak(statement);
	}
	public void analyze(String statement){
		List<Instruction> instructions = analyzer.analyze(statement);
		execute(instructions);
	}
	
	private void execute(List<Instruction> instructions){
		for(Instruction instruction:instructions){
			if(instruction.getType().equals(Instruction.INSTRUCTION_TYPE_SPEAK)){
				speak(instruction.getInstruction());
			}else{
				
			}
		}
	}
	
	public void weakup(){
		listener.listen();
	}
	
	public static void main(String[] args) {
		Sixshot sixshot = new Sixshot();
		sixshot.weakup();
	}
	
// 背诗
// 算数
// 唱歌
}
