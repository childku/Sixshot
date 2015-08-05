package com.jk.sixshot.organ.auditory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import javax.swing.text.AbstractDocument.BranchElement;

import com.jk.sixshot.Sixshot;
import com.sinovoice.hcicloudsdk.api.HciCloudSys;
import com.sinovoice.hcicloudsdk.api.HciLibPath;
import com.sinovoice.hcicloudsdk.common.HciErrorCode;
import com.sinovoice.hcicloudsdk.common.InitParam;
import com.sinovoice.hcicloudsdk.common.asr.AsrConfig;
import com.sinovoice.hcicloudsdk.common.asr.AsrInitParam;
import com.sinovoice.hcicloudsdk.common.asr.AsrRecogResult;
import com.sinovoice.hcicloudsdk.pc.asr.recorder.ASRRecorder;
import com.sinovoice.hcicloudsdk.recorder.ASRCommonRecorder;
import com.sinovoice.hcicloudsdk.recorder.ASRCommonRecorder.RecorderEvent;
import com.sinovoice.hcicloudsdk.recorder.ASRRecorderListener;

public class Listener implements Runnable {
	
	private ASRRecorder recorder = new ASRRecorder();
	
	private AsrConfig asrConfig = new AsrConfig();
	
	private String  capKey = "asr.cloud.freetalk";
	//初始化HCI的错误码
	private static int errorCode = -1;
	
	private boolean running = false;
	
	private Sixshot brain = null;
	
	private static Map<String,String> accountInfo = new HashMap<String, String>();
	
	public static void main(String[] args) {
//		Listener listener = new Listener();
//		listener.listen();
	}
	
	public Listener(Sixshot brain){
		this.brain = brain;
		init();
	}
	
	private void init(){
//		CloudLog.debugMode();
		importLibs();
		initAccountInfo();
		initEngine();
		initRecorder();
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
		errorCode = HciCloudSys.hciInit(initparam.getStringConfig(), null);
		if(errorCode != HciErrorCode.HCI_ERR_NONE){
			System.out.println("HciCloudAsr init error: " + errorCode);
			return;
		}
		System.out.println("HciCloudAsr init OK");

	}
	private static void importLibs(){
		String path = System.getProperty("user.dir");
		String sysLibPath[] = new String[]{
				path + "/libs/libcurl.dll" ,
				path + "/libs/hci_sys.dll" ,
				path + "/libs/hci_sys_jni.dll" 
		};
		HciLibPath.setSysLibPath(sysLibPath);
		
		String asrLibPath[] = new String[]{
				path + "/libs/libcurl.dll" ,
				path + "/libs/hci_sys.dll" ,
				path + "/libs/hci_sys_jni.dll",
				path + "/libs/hci_asr.dll" ,
				path + "/libs/hci_asr_jni.dll",
				path + "/libs/hci_asr_cloud_recog.dll",
				path + "/libs/hci_asr_local_recog.dll",
		};
		HciLibPath.setAsrLibPath(asrLibPath);
	}

	/**
	 * 获取账户信息
	 * 
	 * @return
	 */
	private void initAccountInfo() {
		readAccountInfo();
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
    
    private void initRecorder(){
		asrConfig.addParam(AsrConfig.PARAM_KEY_CAP_KEY, capKey);
		asrConfig.addParam(AsrConfig.PARAM_KEY_AUDIO_FORMAT, "pcm16k16bit");
		asrConfig.addParam(AsrConfig.PARAM_KEY_ENCODE, "none");
		asrConfig.addParam(AsrConfig.PARAM_KEY_VAD_TAIL, "600");
		asrConfig.addParam(AsrConfig.PARAM_KEY_VAD_HEAD, "30000");
    	
    	recorder = new ASRRecorder();
		AsrInitParam initParam = new AsrInitParam();
		initParam.addParam(AsrInitParam.PARAM_KEY_INIT_CAP_KEYS, capKey);
		initParam.addParam(AsrInitParam.PARAM_KEY_DATA_PATH, System.getProperty("user.dir") + "\\data");
		recorder.init(initParam.getStringConfig(), new RecorderListener());
		if(recorder.getRecorderState() != ASRCommonRecorder.RECORDER_STATE_IDLE){
			System.out.println("初始化失败了！");
    	}
    }
    
    public void listen(){
    	if(running){
    		return;
    	}
		asrConfig.addParam(AsrConfig.PARAM_KEY_DOMAIN, null);
		
		asrConfig.addParam(AsrConfig.PARAM_KEY_REALTIME, "no");
		
		asrConfig.addParam(AsrConfig.PARAM_KEY_CAP_KEY, capKey);
		
		System.out.println("HciCloudAsr AsrConfig: " + asrConfig.getStringConfig());
		
		asrConfig.addParam(AsrConfig.PARAM_KEY_IS_FILE, "no");
		asrConfig.addParam(AsrConfig.PARAM_KEY_GRAMMAR_TYPE, "id");
		
		asrConfig.addParam(AsrConfig.PARAM_KEY_GRAMMAR_ID, "10252");
		System.out.println("asrConfig = " + asrConfig.getStringConfig());
		
		running = true;
		Thread thread = new Thread(this, "begin-listen");
		thread.start();
    }
    

	private class RecorderListener implements ASRRecorderListener{

		@Override
		public void onRecorderEventError(RecorderEvent recorderEvent, int errorCode) {
			System.out.println("出现错误，错误码为" + errorCode);	
			running = false;
		}

		//识别完成回调
		@Override
		public void onRecorderEventRecogFinsh(RecorderEvent event, AsrRecogResult result) {
			if(result == null){
				System.out.println("错误：返回结果集为空");	
				running = false;
				return;
			}
			
			//识别结果显示
			if(result.getRecogItemList().size() == 0){
				System.out.println("识别结束,没有识别结果");
			}else{
				System.out.println("----------识别结果-begin--------------------------------------");
				String statement = result.getRecogItemList().get(0).getRecogResult();
				System.out.println(statement);
				brain.analyze(statement);
				System.out.println("----------识别结果-end----------------------------------------");
			}
			running = false;
			try {
				Thread.sleep(0);
			} catch (InterruptedException e) {
				e.printStackTrace();
				running = false;
			}
		}

		//状态改变回调
		@Override
		public void onRecorderEventStateChange(RecorderEvent event) {
			try {
				if(event == RecorderEvent.RECORDER_EVENT_BEGIN_RECORD){
					Thread.sleep(500);
					System.out.println("开始录音");
				}else if(event == RecorderEvent.RECORDER_EVENT_BEGIN_RECOGNIZE){
					Thread.sleep(500);
					System.out.println("开始识别");
				}else if(event == RecorderEvent.RECORDER_EVENT_NO_VOICE_INPUT){
					Thread.sleep(100);
					System.out.println("无音频输入");
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		//录音机语音信息回调
		@Override
		public void onRecorderRecording(byte[] volumedata ,int volume){
			
		}

		public void onRecorderEventRecogProcess(RecorderEvent arg0,	AsrRecogResult arg1) {
			
		}
	}


	@Override
	public void run() {
		while(running){
			if(recorder.getRecorderState() == ASRCommonRecorder.RECORDER_STATE_IDLE){
				try {
					Thread.sleep(1000);
					recorder.cancel();
					if(running == true){
						recorder.start(asrConfig.getStringConfig(), null);
					}
				
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
