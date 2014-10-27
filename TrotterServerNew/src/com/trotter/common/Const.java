package com.trotter.common;

public class Const {

    public static class SYST_VAR_PROP{
        public static final String STATIC_FILE_PATH = "STATIC_FILE";//E:\\Assignments\\Saurabh\\1.pdf";
        public static final String DESTINATION_FILE_PATH = "DESTINATION_FILE";//E:\\Assignments\\Saurabh\\";
        public static final String SYST_VAR_NAME = "SYST_VAR";
        public static final String DRIVER_CLASS = "DRIVER_CLASS";//"org.postgresql.Driver";
        public static final String CONN_URL = "CONN_URL";//"jdbc:postgresql://localhost:5432/Saurabh"
        public static final String DB_USER_NAME = "DB_USER_NAME";//"postgres"
        public static final String DB_PASSWORD = "DB_PASSWORD";//"abhishek"
    }
    
    public static class SEQUENCE_TBL{
        public static final String APP_ID = "APP_ID";
        public static final String CODE_ID = "CODE_ID";
        public static final String DEVICE_ID = "DEVICE_ID";
        public static final String PLAN_ID = "PLAN_ID";
        public static final String APP_PLAN_ID = "APP_PLAN_ID";
        public static final String NOTIFICATION_ID = "NOTIFICATION_ID";
        public static final String RULE_ID = "RULE_ID";
    }
    
    public static enum CodeType{
    	APP_MODE, APP_CATEGORY;
    }
    
    public static enum Gender {
    	MALE, FEMALE;
    }
    
    public static class ORIENTATION_TYPE {
		public static final int ORIENTATION_PORTRAIT = 1;
		public static final int ORIENTATION_LANDSCAPE = 2;
		public static final int ORIENTATION_SQUARE = 3;
		
		public static String getLabel(int orientationType){
			switch(orientationType){
			case ORIENTATION_LANDSCAPE:
				return "Landscape";
			case ORIENTATION_PORTRAIT:
				return "Portrait";
			case ORIENTATION_SQUARE:
				return "Square";
			default:
				return "";
			}
		}
	}
    
    public static class LOG_PARAMETER {
		public static final String OnCreate = "oncreate";
		public static final String OnPause = "onpause";
		public static final String OnResume = "onresume";
		public static final String OnDestroy = "ondestroy";
		public static final String EventTypeOpen = "open";
		public static final String EventTypeClose = "close";
		public static final String OnStart = "onstart";
		public static final String OnStop = "onstop";
		public static final String OnCreateFinish = "onloaded";
	}
	
    public static class REQUEST_PARAMETERS{
    	public static final String DEVICE_ID = "DEVICE_ID";
    	public static final String USER_ID = "USER_ID";
    	public static final String APP_ID = "APP_ID";
    	public static final String APP_KEY = "APP_KEY";
    	public static final String SINCE_DATE = "SINCE_DATE";
    	public static final String LAT = "LAT";
    	public static final String LONG = "LONG";
    	public static final String IMEI = "IMEI";
    	public static final String MAC = "MAC";
    	public static final String ANDROID_ID = "ANDROID_ID";
    	public static final String OS_VERSION = "OS_VERSION";
    	public static final String MANUFACTURER = "MANUFACTURE";
    	public static final String TIME_ZONE = "TIMEZONE";
    	public static final String PHONE_NO = "PHONENO";
    	public static final String MODEL_NAME = "MODELNAME";
		public static final String OPRATOR_NAME = "OPRATOR_NAME";
		public static final String NETWRK_TYPE = "NETWORKTYPE";
		
		public static final String GRAPH_FREQ_TYPE = "GRAPH_FREQ";
		public static final String KEYWORD_DATA_REQD = "KEYWORD_DATA_REQD";
		
		public static final String JSON_HEADER_KEY = "json_header";
		/*************LOGS********************/
		//These names are used for activity record
		public static final String CALLED_METHOD = "called_method";
		public static final String ACTIVITY_NAME = "activity_name";
		public static final String EVENT_TIME = "event_time";
		public static final String EVENT_TYPE = "event_type";
		public static final String ORIENTATION_TYPE = "orientation_type";
		
		//These names are define for Query Search Table 
		public static final String DEPART_DATE="from_date";
		public static final String RETURN_DATE="to_date";
		public static final String FROM_PLACE="from_place";
		public static final String TO_PLACE="to_place";
		public static final String CLASS_TYPE="class_type";
		
		//Notification response
		public static final String IS_DELIVERED = "delivered";
		public static final String NOTE_ID = "noteId";
		public static final String IS_CLICKED = "clicked";
    }
    
    public static class GRAPH_FREQ_TYPE{
    	public static final String HOURLY = "HOURLY";
    	public static final String DAILY = "DAILY";
    	public static final String WEEKLY = "WEEKLY";
    	public static final String MONTHLY = "MONTHLY";
    }
    
    public static class KEYWORD_DATA_REQD{
    	public static final String FROM = "FROM";
    	public static final String TO = "TO";
    	public static final String BOTH = "BOTH";
    }
    
    public static class MESSAGE_PARAMS{
    	public static final String MESSAGE = "message";
    	public static final String APP_NAME = "appName";
    	public static final String APP_PACKAGE = "appPackage";
    	public static final String URL = "url";
    	public static final String SMS_NUM = "sms_num";
    	public static final String SMS_MSG = "sms_msg";
    	public static final String CALL_NUM = "call_num";
    	public static final String NOTE_ID = "note_Id";
    }
    
    public static class TARGET_OS{
        public static final String ANDROID = "Android";
        public static final String BLACKBERRY = "Blackberry";
        public static final String SYMBIAN = "Symbian";
        public static final String IOS = "iOS";
        public static final String WINDOWS = "Windows";
    }
    
    public static class NETWORK_TYPE{
        public static final String XRTT="1xRTT";
        public static final String CDMA="CDMA";
        public static final String EGDE="EDGE";
        public static final String EHRPD="eHRPD";
        public static final String EVO_0="EVDO rev.0";
        public static final String EVO_A="EVDO rev.A";
        public static final String EVO_B="EVDO rev.B";
        public static final String GPRS="GPRS";
        public static final String HSDPA="HSDPA";
        public static final String HSPA="HSPA";
        public static final String HSPA_PLUS="HSPA_3.5G";
        public static final String HSUPA="HSUPA";
        public static final String IDEN="iDen";
        public static final String LTE="LTE";
        public static final String UMTS="UMTS";
        public static final String UNKNOWN="Unknown";
    }
}
