package com.jk.sixshot.organ.auditory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.jk.sixshot.Sixshot;
import com.sinovoice.hcicloudsdk.api.HciLibPath;
import com.sinovoice.hcicloudsdk.api.asr.HciCloudAsr;
import com.sinovoice.hcicloudsdk.common.asr.AsrConfig;
import com.sinovoice.hcicloudsdk.common.asr.AsrGrammarId;
import com.sinovoice.hcicloudsdk.common.asr.AsrInitParam;
import com.sinovoice.hcicloudsdk.common.asr.AsrRecogResult;
import com.sinovoice.hcicloudsdk.pc.asr.recorder.ASRRecorder;
import com.sinovoice.hcicloudsdk.recorder.ASRCommonRecorder;
import com.sinovoice.hcicloudsdk.recorder.ASRCommonRecorder.RecorderEvent;
import com.sinovoice.hcicloudsdk.recorder.ASRRecorderListener;

public class Listener {
	
	private ASRRecorder recorder = new ASRRecorder();
	
	private AsrConfig asrConfig = new AsrConfig();
	private Sixshot brain = null;
	
	public Listener(Sixshot brain){
		this.brain = brain;
		init();
	}
	
	private void init(){
		importLibs();
		initRecorder();
	}
	private static void importLibs(){
		String classPath = Sixshot.config.getSystem().getSourcePath();
		
		String asrLibPath[] = new String[]{
				classPath + "dlls/windows/libcurl.dll" ,
				classPath + "dlls/windows/hci_sys.dll" ,
				classPath + "dlls/windows/hci_sys_jni.dll",
				classPath + "dlls/windows/hci_asr.dll" ,
				classPath + "dlls/windows/hci_asr_jni.dll",
				classPath + "dlls/windows/hci_asr_cloud_recog.dll",
//				classPath + "dlls/windows/hci_asr_local_recog.dll",
//				classPath + "dlls/windows/hci_asr_local_v4_recog.dll",
				
//				classPath + "dlls/windows/libmmd.dll",
//				classPath + "dlls/windows/mkl_avx.dll",
//				classPath + "dlls/windows/mkl_avx2.dll",
//				classPath + "dlls/windows/mkl_core.dll",
//				classPath + "dlls/windows/mkl_p4.dll",
//				classPath + "dlls/windows/mkl_p4m.dll",
//				classPath + "dlls/windows/mkl_p4m3.dll",
//				classPath + "dlls/windows/mkl_p4p.dll",
//				classPath + "dlls/windows/mkl_rt.dll",
//				classPath + "dlls/windows/mkl_sequential.dll",
//				classPath + "dlls/windows/mkl_vml_avx.dll",
//				classPath + "dlls/windows/mkl_vml_avx2.dll",
//				classPath + "dlls/windows/mkl_vml_cmpt.dll",
//				classPath + "dlls/windows/mkl_vml_ia.dll",
//				classPath + "dlls/windows/mkl_vml_p4.dll",
//				classPath + "dlls/windows/mkl_vml_p4m.dll",
//				classPath + "dlls/windows/mkl_vml_p4m2.dll",
//				classPath + "dlls/windows/mkl_vml_p4m3.dll",
//				classPath + "dlls/windows/mkl_vml_p4p.dll",
//				classPath + "dlls/windows/svml_dispmd.dll",
//				classPath + "dlls/windows/hci_asr_local_v4_recog.dll"
		};
		HciLibPath.setAsrLibPath(asrLibPath);
	}

    private void initRecorder(){
		asrConfig.addParam(AsrConfig.PARAM_KEY_CAP_KEY, Sixshot.config.getVoice().getAsrCapKey());
		asrConfig.addParam(AsrConfig.PARAM_KEY_AUDIO_FORMAT, "pcm16k16bit");
		asrConfig.addParam(AsrConfig.PARAM_KEY_ENCODE, "none");
		asrConfig.addParam(AsrConfig.PARAM_KEY_VAD_TAIL, "600");
		asrConfig.addParam(AsrConfig.PARAM_KEY_VAD_HEAD, "30000");
    	
    	recorder = new ASRRecorder();
		AsrInitParam initParam = new AsrInitParam();
		initParam.addParam(AsrInitParam.PARAM_KEY_INIT_CAP_KEYS, Sixshot.config.getVoice().getAsrCapKey());
		initParam.addParam(AsrInitParam.PARAM_KEY_DATA_PATH, Sixshot.config.getSystem().getSourcePath() + "voice-data/");
		
		System.out.println("---listener, asr param = " + initParam.getStringConfig());
		recorder.init(initParam.getStringConfig(), new RecorderListener());
		if(recorder.getRecorderState() != ASRCommonRecorder.RECORDER_STATE_IDLE){
			System.out.println("---listener, 初始化失败了！");
    	}
    }
    
    
	/**
	 * 加载本地语法
	 * @param grammarConfig AsrConfig获取的配置字符串
	 * @param grammar 加载的语法字符串
	 * @return
	 */
	private String getByLoadGrammar(String grammarConfig, String grammar) {
		AsrGrammarId grammarId = new AsrGrammarId();
		
		int errorCode = HciCloudAsr.hciAsrLoadGrammar(grammarConfig, grammar, grammarId);
		
		System.out.println("---listener, HciCloudAsr Frame LoadGrammar return:" + errorCode);
		return grammarId.getGrammarId() + "";
	}
	
	private String loadGrammarFile(String path) {
		ByteArrayOutputStream baos = null;
		FileInputStream fis = null;
		String grammar = null;
		try{
			File grammarFile = new File(path);
			baos = new ByteArrayOutputStream();
			fis = new FileInputStream(grammarFile);
			byte[] buf = new byte[1024];
			int len = 0;
			while((len = fis.read(buf)) > 0){
				baos.write(buf, 0, len);
			}
			grammar = new String(baos.toByteArray(), "utf-8");
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			try {
				if(fis != null){
					fis.close();
				}
				if(baos != null){
					baos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return grammar;
	}
	
    public void listen(){
		asrConfig.addParam(AsrConfig.PARAM_KEY_DOMAIN, null);
		
		asrConfig.addParam(AsrConfig.PARAM_KEY_REALTIME, "no");
		
		asrConfig.addParam(AsrConfig.PARAM_KEY_CAP_KEY, Sixshot.config.getVoice().getAsrCapKey());
		
		asrConfig.addParam(AsrConfig.PARAM_KEY_IS_FILE, "no");
//		asrConfig.addParam(AsrConfig.PARAM_KEY_GRAMMAR_TYPE, "id");
		String classPath = Sixshot.config.getSystem().getSourcePath();
		String grammar = loadGrammarFile(classPath + "wordlist_utf8.txt");
		System.out.println("---listener, grammar = " + grammar);
//		String grammar = "上海机场\n重庆火车\n广州银行\n天津卫视\n三峡水利";
		String grammarId = getByLoadGrammar(asrConfig.getStringConfig(), grammar);
//		asrConfig.addParam(AsrConfig.PARAM_KEY_GRAMMAR_TYPE, AsrConfig.HCI_ASR_GRAMMAR_TYPE_ID);
//		asrConfig.addParam(AsrConfig.PARAM_KEY_GRAMMAR_TYPE, "jsgf");
		asrConfig.addParam(AsrConfig.PARAM_KEY_GRAMMAR_ID, "10252");
//		asrConfig.addParam(AsrConfig.PARAM_KEY_GRAMMAR_ID, "10217");
//		asrConfig.addParam(AsrConfig.PARAM_KEY_GRAMMAR_ID, grammarId);
		System.out.println("---listener, asrConfig = " + asrConfig.getStringConfig());
		
		listening(grammar);
    }
    
	private void listening(String grammar){
		System.out.println("---in listener， recorder state is : " + recorder.getRecorderState());
		if(recorder.getRecorderState() == ASRCommonRecorder.RECORDER_STATE_IDLE){
			try {
//				String grammar = "上海机场\r\n重庆火车\r\n广州银行\r\n天津卫视\r\n三峡水利";
				System.out.println("---in listener  recorder start");
				recorder.start(asrConfig.getStringConfig(), null);
//				recorder.start(asrConfig.getStringConfig(), grammar);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			throw new RuntimeException("---listener, listener 忙");
		}
	}
	
	private class RecorderListener implements ASRRecorderListener{

		@Override
		public void onRecorderEventError(RecorderEvent recorderEvent, int errorCode) {
			System.out.println("---listener, 出现错误，错误码为" + errorCode);
//			throw new SpeakNothingException();
			throw new RuntimeException("什么也没说");
//			brain.weakup();
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
//					brain.weakup();
					brain.setListenerIdle(true);
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
