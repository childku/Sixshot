package com.jk.sixshot;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class Configuration {
	
	private System system = null;
	
	private Voice voice = null;
	
	public Configuration(String configPath){
		loadConfig(configPath);
	}
	
	public class System {
		
		private String system = "linux";
		private String sourcePath = null;
		private boolean logable = false;
		
		public String getSystem() {
			return system;
		}

		public void setSystem(String system) {
			this.system = system;
		}

		public String getSourcePath() {
			return sourcePath;
		}

		public void setSourcePath(String sourcePath) {
			this.sourcePath = sourcePath;
		}

		public boolean isLogable() {
			return logable;
		}

		public void setLogable(boolean logable) {
			this.logable = logable;
		}

	}
	
	public class Voice {
		
		private String appKey = null;
		private String developerKey = null;
		private String cloudUrl = null;
		private String asrCapKey = null;
		private String ttsCapKey = null;
		
		public String getAppKey() {
			return appKey;
		}
		public void setAppKey(String appKey) {
			this.appKey = appKey;
		}
		public String getDeveloperKey() {
			return developerKey;
		}
		public void setDeveloperKey(String developerKey) {
			this.developerKey = developerKey;
		}
		public String getCloudUrl() {
			return cloudUrl;
		}
		public void setCloudUrl(String cloudUrl) {
			this.cloudUrl = cloudUrl;
		}
		public String getTtsCapKey() {
			return ttsCapKey;
		}
		public void setTtsCapKey(String ttsCapKey) {
			this.ttsCapKey = ttsCapKey;
		}
		public String getAsrCapKey() {
			return asrCapKey;
		}
		public void setAsrCapKey(String asrCapKey) {
			this.asrCapKey = asrCapKey;
		}
	}

	
    private void loadConfig(String configPath) {
        Properties props = new Properties();  
        try {  
            InputStream in = new BufferedInputStream(new FileInputStream(configPath + "config.properties"));  
            props.load(in);  
            in.close();
        } catch (Exception e) {  
            e.printStackTrace();  
        }
        
        initSystem(props);
        initVoice(props);
	}
    
    private void initSystem(Properties props){
    	system = new System();
    	
    	system.setSourcePath(props.getProperty("system.source-path"));
    	system.setSystem(props.getProperty("system.system"));
    	system.setLogable(Boolean.valueOf(props.getProperty("system.logable")));
    }

    private void initVoice(Properties props){
    	voice = new Voice();
    	
    	voice.setAppKey(props.getProperty("voice.app-key"));
    	voice.setDeveloperKey(props.getProperty("voice.developer-key"));
    	voice.setCloudUrl(props.getProperty("voice.cloud-url"));
    	voice.setTtsCapKey(props.getProperty("voice.tts-cap-key"));
    	voice.setAsrCapKey(props.getProperty("voice.asr-cap-key"));
    }

	public System getSystem() {
		return system;
	}


	public Voice getVoice() {
		return voice;
	}
}
