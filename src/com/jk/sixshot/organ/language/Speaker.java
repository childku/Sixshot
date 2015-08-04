package com.jk.sixshot.organ.language;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import com.sinovoice.hcicloudsdk.api.HciCloudSys;
import com.sinovoice.hcicloudsdk.api.HciLibPath;
import com.sinovoice.hcicloudsdk.common.HciErrorCode;
import com.sinovoice.hcicloudsdk.common.InitParam;
import com.sinovoice.hcicloudsdk.common.tts.TtsConfig;
import com.sinovoice.hcicloudsdk.common.tts.TtsInitParam;
import com.sinovoice.hcicloudsdk.pc.tts.player.TTSPlayer;
import com.sinovoice.hcicloudsdk.player.TTSCommonPlayer.PlayerEvent;
import com.sinovoice.hcicloudsdk.player.TTSPlayerListener;
public class Speaker {

	private Map<String,String> accountInfo = new HashMap<String, String>();
	
	//TTS Player
	private TTSPlayer ttsPlayer = null;
	
	//TTS 配置
	private TtsConfig ttsConfig;
	
	//是否繁忙
	private boolean isSpeaking = false;
	
	public Speaker(){
		init();
	}

	private void init(){
		
		// 初始化加载相关库文件
		importLibs();
		
		//获取账户信息
		initAccountInfo();
	    initEnginer();
		initTtsPlayer();
		initTtsConfig();
	}
	
	private void initEnginer(){
		/*------------灵云系统初始化------------------------------------------------*/
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
		
		System.out.println("HciCloudTts HciInitConfig: " + initparam.getStringConfig());
		
		// 初始化System
		//初始化HCI的错误码
		int errorCode = HciCloudSys.hciInit(initparam.getStringConfig(), "");
		if(errorCode != HciErrorCode.HCI_ERR_NONE){
			System.out.println("HciCloudTts init error: " + errorCode);
			return;
		}
		System.out.println("HciCloudTts init OK");
	}
	
	//TTS Player 初始化
	private  void  initTtsPlayer(){
		ttsPlayer =new TTSPlayer();
		//本地音库路径
		String ttsDirPath = "data";
		TtsInitParam ttsInitParam = new TtsInitParam();
    	ttsInitParam.addParam(TtsInitParam.PARAM_KEY_DATA_PATH, ttsDirPath);
    	ttsInitParam.addParam(TtsInitParam.PARAM_KEY_INIT_CAP_KEYS, accountInfo.get("capKey"));
    	//播放器初始化
    	ttsPlayer.init(ttsInitParam.getStringConfig(), new PlayerListener());
    	if(ttsPlayer.getPlayerState() != TTSPlayer.PLAYER_STATE_IDLE){
    		System.out.println("TTS Player 初始化失败！");
    	}
	}
	
	@SuppressWarnings("deprecation")
	private void initTtsConfig(){
		ttsConfig = new TtsConfig();
		//音频格式
		ttsConfig.addParam(TtsConfig.PARAM_KEY_ADUIO_FORMAT, "pcm16k16bit");
		//所使用能力
		ttsConfig.addParam(TtsConfig.PARAM_KEY_CAP_KEY, "tts.cloud.wangjing");
		
		//播放速度
		ttsConfig.addParam(TtsConfig.PARAM_KEY_SPEED, "1");
		//编码格式
		ttsConfig.addParam(TtsConfig.PARAM_KEY_ENCODE, "none");
		
		System.out.println("HciCloudTts TtsConfig: " + ttsConfig.getStringConfig());
	}
	
	public void speak(String text){
		try{
			ttsPlayer.play(text, ttsConfig.getStringConfig());
		}catch(IllegalStateException e){
			e.printStackTrace();
		}
	}
	//播放器回调接口
	private class PlayerListener implements TTSPlayerListener{

		//错误信息回调
		@Override
		public void onPlayerEventPlayerError(PlayerEvent playerEvent, int errorCode) {
			System.out.println("程序已出错，错误码为"+errorCode);
		}

		//播放进度回调
		@Override
		public void onPlayerEventProgressChange(PlayerEvent playerEvent, int start, int end) {
		}
		
		//状态改变回调
		@Override
		public void onPlayerEventStateChange(PlayerEvent playerEvent) {
			if(playerEvent == PlayerEvent.PLAYER_EVENT_BEGIN){
				isSpeaking = true;
			}else if(playerEvent == PlayerEvent.PLAYER_EVENT_END){
				isSpeaking = false;
			}		
		}		
	}
	private void initAccountInfo() {
		readAccountInfo();
		
		if (accountInfo.get("capKey") ==null) {
			System.out.println("capKey is null ,please check it\n");
			return;
		}
	}
	private static void importLibs(){
		String path =System.getProperty("user.dir");
		String sysLibPath[] = new String[]{
				path + "/libs/libcurl.dll" ,
				path + "/libs/hci_sys.dll" ,
				path + "/libs/hci_sys_jni.dll" 
		};
		HciLibPath.setSysLibPath(sysLibPath);
		
		String ttsLibPath[] = new String[]{
				path + "\\libs\\libcurl.dll",
				path + "\\libs\\jtopus.dll",
				path + "\\libs\\jtspeex.dll",
				path + "\\libs\\hci_sys.dll",
				path + "\\libs\\hci_tts.dll",
				path + "\\libs\\hci_tts_local_synth.dll",
				path + "\\libs\\hci_tts_local_n6_synth.dll",
				path + "\\libs\\hci_tts_cloud_synth.dll",
				path + "\\libs\\hci_tts_jni.dll",
		};
		HciLibPath.setTtsLibPath(ttsLibPath);
	}
	
    //读取账户信息
    private  void  readAccountInfo() {
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
	}

    
	public static void main(String[] args) {
		Speaker speaker = new Speaker();
		speaker.speak("你好，世界!");
	}
}