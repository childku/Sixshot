package com.jk.sixshot.organ.language;

import java.util.Map;

import com.jk.sixshot.Sixshot;
import com.sinovoice.hcicloudsdk.api.HciLibPath;
import com.sinovoice.hcicloudsdk.common.tts.TtsConfig;
import com.sinovoice.hcicloudsdk.common.tts.TtsInitParam;
import com.sinovoice.hcicloudsdk.pc.tts.player.TTSPlayer;
import com.sinovoice.hcicloudsdk.player.TTSCommonPlayer.PlayerEvent;
import com.sinovoice.hcicloudsdk.player.TTSPlayerListener;
public class Speaker {

	private Map<String,String> accountInfo = null;
	
	//TTS Player
	private TTSPlayer ttsPlayer = null;
	
	//TTS 配置
	private TtsConfig ttsConfig;
	
	private Sixshot brain = null;
	
	public Speaker(){
		init();
	}
	
	public Speaker(Sixshot brain, Map<String,String> account){
		this.brain = brain;
		this.accountInfo = account;
		init();
	}

	private void init(){
		
		// 初始化加载相关库文件
		importLibs();
		
		initTtsPlayer();
		initTtsConfig();
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
			System.out.println(" speaker 程序已出错，错误码为"+errorCode);
		}

		//播放进度回调
		@Override
		public void onPlayerEventProgressChange(PlayerEvent playerEvent, int start, int end) {
		}
		
		//状态改变回调
		@Override
		public void onPlayerEventStateChange(PlayerEvent playerEvent) {
			if(playerEvent == PlayerEvent.PLAYER_EVENT_BEGIN){
				System.out.println("---in speaker , 开始说话 ");
			}else if(playerEvent == PlayerEvent.PLAYER_EVENT_END){
				System.out.println("---in speaker , 话已说完 ");
				brain.weakup();
			}		
		}		
	}
	private static void importLibs(){
		String path =System.getProperty("user.dir");
		
		String ttsLibPath[] = new String[]{
				path + "\\libs\\libcurl.dll",
				path + "\\libs\\jtopus.dll",
				path + "\\libs\\jtspeex.dll",
				path + "\\libs\\hci_sys.dll",
				path + "\\libs\\hci_tts.dll",
				path + "\\libs\\hci_tts_local_v6_synth.dll",
				path + "\\libs\\hci_tts_cloud_synth.dll",
				path + "\\libs\\hci_tts_jni.dll",
		};
		HciLibPath.setTtsLibPath(ttsLibPath);
	}
    
	public static void main(String[] args) {
		Speaker speaker = new Speaker();
		speaker.speak("你好，世界!");
	}
}
