package com.jk.sixshot.organ.auditory;

import com.jk.sixshot.Account;
import com.jk.sixshot.Sixshot;
import com.jk.sixshot.utils.Utils;
import com.sinovoice.hcicloudsdk.api.HciLibPath;
import com.sinovoice.hcicloudsdk.common.asr.AsrConfig;
import com.sinovoice.hcicloudsdk.common.asr.AsrInitParam;
import com.sinovoice.hcicloudsdk.common.asr.AsrRecogResult;
import com.sinovoice.hcicloudsdk.pc.asr.recorder.ASRRecorder;
import com.sinovoice.hcicloudsdk.recorder.ASRCommonRecorder;
import com.sinovoice.hcicloudsdk.recorder.ASRCommonRecorder.RecorderEvent;
import com.sinovoice.hcicloudsdk.recorder.ASRRecorderListener;

public class Listener {
	
	private ASRRecorder recorder = new ASRRecorder();
	
	private AsrConfig asrConfig = new AsrConfig();
	
	private String  asrCapKey = "asr.cloud.freetalk";
//	private String  asrCapKey = "asr.local.grammar";
//	private String  asrCapKey = "asr.local.grammar.chinese";
	
	private Sixshot brain = null;
	
	private Account account = null;
	
	public static void main(String[] args) {
//		Listener listener = new Listener();
//		listener.listen();
	}
	
	public Listener(Sixshot brain, Account account){
		this.brain = brain;
		this.account = account;
		init();
	}
	
	private void init(){
		importLibs();
		initRecorder();
	}
	private static void importLibs(){
		String classPath = Utils.getClassPath();
		
		String asrLibPath[] = new String[]{
				classPath + "libs/libcurl.dll" ,
				classPath + "libs/hci_sys.dll" ,
				classPath + "libs/hci_sys_jni.dll",
				classPath + "libs/hci_asr.dll" ,
				classPath + "libs/hci_asr_jni.dll",
				classPath + "libs/hci_asr_cloud_recog.dll",
				classPath + "libs/hci_asr_local_recog.dll",
		};
		HciLibPath.setAsrLibPath(asrLibPath);
	}

    private void initRecorder(){
		asrConfig.addParam(AsrConfig.PARAM_KEY_CAP_KEY, asrCapKey);
		asrConfig.addParam(AsrConfig.PARAM_KEY_AUDIO_FORMAT, "pcm16k16bit");
		asrConfig.addParam(AsrConfig.PARAM_KEY_ENCODE, "none");
		asrConfig.addParam(AsrConfig.PARAM_KEY_VAD_TAIL, "600");
		asrConfig.addParam(AsrConfig.PARAM_KEY_VAD_HEAD, "30000");
    	
    	recorder = new ASRRecorder();
		AsrInitParam initParam = new AsrInitParam();
		initParam.addParam(AsrInitParam.PARAM_KEY_INIT_CAP_KEYS, asrCapKey);
		initParam.addParam(AsrInitParam.PARAM_KEY_DATA_PATH, Utils.getClassPath() + "voice-data/");
		recorder.init(initParam.getStringConfig(), new RecorderListener());
		if(recorder.getRecorderState() != ASRCommonRecorder.RECORDER_STATE_IDLE){
			System.out.println("初始化失败了！");
    	}
    }
    
    public void listen(){
		asrConfig.addParam(AsrConfig.PARAM_KEY_DOMAIN, null);
		
		asrConfig.addParam(AsrConfig.PARAM_KEY_REALTIME, "no");
		
		asrConfig.addParam(AsrConfig.PARAM_KEY_CAP_KEY, asrCapKey);
		
		asrConfig.addParam(AsrConfig.PARAM_KEY_IS_FILE, "no");
		asrConfig.addParam(AsrConfig.PARAM_KEY_GRAMMAR_TYPE, "id");
		
		asrConfig.addParam(AsrConfig.PARAM_KEY_GRAMMAR_ID, "10252");
		System.out.println("---listener, asrConfig = " + asrConfig.getStringConfig());
		
		listening();
    }
    
	private void listening(){
		System.out.println("---in listener， recorder state is : " + recorder.getRecorderState());
		if(recorder.getRecorderState() == ASRCommonRecorder.RECORDER_STATE_IDLE){
			try {
				System.out.println("---in listener  recorder start");
				recorder.start(asrConfig.getStringConfig(), null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private class RecorderListener implements ASRRecorderListener{

		@Override
		public void onRecorderEventError(RecorderEvent recorderEvent, int errorCode) {
			System.out.println(" listener 出现错误，错误码为" + errorCode);	
		}

		//识别完成回调
		@Override
		public void onRecorderEventRecogFinsh(RecorderEvent event, AsrRecogResult result) {
			if(result == null){
				System.out.println("错误：返回结果集为空");	
				return;
			}
			
			//识别结果显示
			if(result.getRecogItemList().size() == 0){
				System.out.println("识别结束,没有识别结果");
			}else{
				System.out.println("----------识别结果-begin--------------------------------------");
				String statement = result.getRecogItemList().get(0).getRecogResult();
				System.out.println("----" + statement);
				if(statement.trim().equals("")){
//					System.out.println("---listener, to be weakup, recorder state is : " + recorder.getRecorderState());
					brain.weakup();
				}else{
					brain.analyze(statement);
				}
				System.out.println("----------识别结果-end----------------------------------------");
			}
			try {
				Thread.sleep(0);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		//状态改变回调
		@Override
		public void onRecorderEventStateChange(RecorderEvent event) {
//			System.out.println("---listener, event state ：" + event);
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

}
