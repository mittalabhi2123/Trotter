package com.trotter.common;

public class MongoDBStructure {

	public static final String DB_NAME = "silverpush";
	public static final String HOST = "127.0.0.1";
	public static final int PORT = 27017;
	public static final String APP_TBL = "apps";
	public static final String APP_PLAN_TBL = "app_plan_tbl";
	public static final String CODES_TBL = "codes_tbl";
	public static final String DEVICE_TBL = "device_tbl";
	public static final String NOTIFICATION_TBL = "notification_tbl";
	public static final String NOTIFICATION_SENT_TBL = "notification_sent_tbl";
	public static final String PLAN_TBL = "plan_tbl";
	public static final String RULE_TBL = "rule_tbl";
	public static final String RULE_FREQUENCY_TBL = "rule_frequency_tbl";
	public static final String RULE_LOCATION_TBL = "rule_location_tbl";
	public static final String RULE_SCHEDULE_ONE_TBL = "rule_schedule_one_tbl";
	public static final String RULE_SCHEDULE_RECUR_TBL = "rule_schedule_recur_tbl";
	public static final String APP_DEVICE_TBL = "app_users";
	public static final String MEMBERS_TBL = "members";
	public static final String SEQUENCE_TBL = "sequence_tbl";
	public static final String DEVICE_LOC_TBL = "dev_loc_tbl";
	public static final String USER_TBL = "user_tbl";
	
	public static enum USER_TABLE_COLS {
		fb_id, email, registration_date, first_name, last_name, dob, auth_token,
		home_city, home_state, home_country, gender, travel_bio, pictures, own_trips, matched_trips;
	}

	public static class DEV_LOC_COLS{
		public static final String devId = "devId";
		public static final String lastUpdateTime = "lastUpdateTime";
	}
	
	public static class SEQUENCE_COLS{
		public static final String seqKey = "seqKey";
		public static final String seqValue = "seqValue";
	}
	
	public static class MEMBERS_COLS{
		public static final String id = "_id";
		public static final String admin_of = "admin_of";
		public static final String user_of = "user_of";
		public static final String email = "email";
		public static final String full_name = "full_name";
		public static final String global_admin = "global_admin";
		public static final String password = "password";
		public static final String username = "username";
	}
	
	public static class APP_DEVICE_COLS{
		public static final String  device_id = "did";
		public static final String  last_seen = "ls";
		public static final String  session_duration = "sd";
		public static final String  total_session_duration = "tsd";
		public static final String  session_count = "sc";
		public static final String  device = "d";
		public static final String  carrier = "c";
		public static final String  country_code = "cc";
		public static final String  platform = "p";
		public static final String  platform_version = "pv";
		public static final String  app_version = "av";
	}
	
	public static class RULE_SCHEDULE_ONE_COLS{
		public static final String oneNoteId = "oneNoteId";
		public static final String oneDate = "oneDate";
		public static final String oneTimeZone = "oneTimeZone";
	}
	
	public static class RULE_SCHEDULE_RECUR_COLS{
		public static final String recurNoteId = "recurNoteId";
		public static final String recurCampaignName = "recurCampaignName";
		public static final String recurStartDate = "recurStartDate";
		public static final String recurEndDate = "recurEndDate";
		public static final String recurInterval = "recurInterval";
		public static final String recurMonday = "recurMonday";
		public static final String recurTuesday = "recurTuesday";
		public static final String recurWednesday = "recurWednesday";
		public static final String recurThursday = "recurThursday";
		public static final String recurFriday = "recurFriday";
		public static final String recurSaturday = "recurSaturday";
		public static final String recurSunday = "recurSunday";
		public static final String recurTimeZone = "recurTimeZone";
	}
	
	public static class RULE_LOCN_COLS{
		public static final String locRuleId = "locRuleId";
		public static final String locName = "locName";
		public static final String locCircleDefined = "locCircleDefined";
		public static final String locRadius = "locRadius";
		public static final String locLat1 = "locLat1";
		public static final String locLong1 = "locLong1";
		public static final String locLat2 = "locLat2";
		public static final String locLong2 = "locLong2";
		public static final String locLat3 = "locLat3";
		public static final String locLong3 = "locLong3";
		public static final String locLat4 = "locLat4";
		public static final String locLong4 = "locLong4";
		public static final String locLat5 = "locLat5";
		public static final String locLong5 = "locLong5";
		public static final String locLat6 = "locLat6";
		public static final String locLong6 = "locLong6";
		
		public static final String QUERY_INSERT_DATA = "INSERT INTO rule_location_tbl VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		public static final String QUERY_GET_DATA = "SELECT * FROM rule_location_tbl WHERE rule_id = ?";
	}
	
	public static class APP_TBL_COLS{
		public static final String appId = "_id";
		public static final String category = "category";
		public static final String country = "country";
		public static final String key = "key";
		public static final String name = "name";
		public static final String timezone = "timezone";
		public static final String packageName = "pname";
//		public static final String appCategory = "appCategory";
//		public static final String appC2DM = "appC2DM";
//		public static final String appActive = "appActive";
//		public static final String appActivePlanId = "appActivePlanId";
//		public static final String appRegisterDate = "appRegisterDate";
		
		public static final String QUERY_INSERT_DATA = "INSERT INTO app_tbl VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?)";
		public static final String QUERY_UPDATE_DATA = "UPDATE app_tbl SET app_user_id=?, app_name=?, app_logo=?, app_mode=?, app_category=?, app_android_pkg=?, app_c2dm=?, app_active=?, app_active_plan_id=? WHERE app_id=?";
		public static final String QUERY_CHECK_DUP_PKG = "SELECT * FROM app_tbl WHERE LOWER(app_android_pkg) = '?' AND app_active = 1";
		public static final String QUERY_CHECK_DUP_C2DM = "SELECT * FROM app_tbl WHERE LOWER(app_c2dm) = '?' AND app_active = 1";
		public static final String QUERY_GET_APP_LIST = "SELECT * FROM app_tbl WHERE LOWER(app_user_id) = '?' AND app_active = 1";
		public static final String QUERY_GET_APP_DATA = "SELECT * FROM app_tbl WHERE app_id = ?";
		public static final String QUERY_DELETE_APP_DATA = "UPDATE app_tbl SET app_active = 0 WHERE app_id = ?";
		public static final String QUERY_UPDATE_APP_PLAN = "UPDATE app_tbl SET app_active_plan_id = ? WHERE app_id = ?";
	}
	
	public static class RULE_FREQUENCY_TBL_COLS{
		public static final String freqRuleId = "freq_rule_id";
		public static final String freqHourMsgRate = "freq_hour_msg_rate";
		public static final String freqMinMsgGap = "freq_min_msg_gap";
		public static final String freqMaxMsgGap = "freq_max_msg_gap";
		public static final String freqMaxUserMsgPerDay = "freq_max_user_msg_per_day";
		
		public static final String QUERY_INSERT_DATA = "INSERT INTO rule_frequency_tbl VALUES (?, ?, ?, ?)";
	}
	
	public static class RULE_TBL_COLS{
		public static final String ruleId = "rule_id";
		public static final String ruleUserId = "rule_user_id";
		public static final String ruleLocation = "rule_location";
		public static final String ruleScheduleOne = "rule_schedule_one";
		public static final String ruleScheduleRecur = "rule_schedule_recur";
		public static final String ruleFrequency = "rule_freq";
		public static final String ruleActive = "rule_active";
		public static final String ruleName = "rule_active";
		
		public static final String QUERY_INSERT_DATA = "INSERT INTO rule_tbl VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
		public static final String QUERY_GET_LOCATION_APP_RULE = "SELECT * FROM rule_tbl WHERE LOWER(rule_user_id) = '?' AND rule_location = 1 AND rule_active = 1";
		public static final String QUERY_GET_LOCATION_APP_RULE_4_APPID = "SELECT * FROM rule_tbl WHERE LOWER(rule_user_id) = (SELECT LOWER(app_user_id) FROM app_tbl WHERE app_id = ?) AND rule_location = 1 AND rule_active = 1";
		public static final String QUERY_GET_LOCATION_APP_RULE_4_DEVICE = "SELECT * FROM rule_tbl WHERE LOWER(rule_user_id) IN (SELECT LOWER(app_user_id) FROM app_tbl WHERE app_id IN (SELECT app_id FROM app_device_tbl WHERE device_id = ? AND unreg_date = 0)) AND rule_location = 1 AND rule_active = 1";
		public static final String QUERY_GET_RECUR_SCHEDULE_RULE = "SELECT rule_id,rule_user_id FROM rule_tbl WHERE rule_schedule_recur = 1 AND rule_active = 1";
//		public static final String QUERY_GET_SINGLE_SCHEDULE_RULE = "SELECT rule_id,rule_user_id FROM RULE_TBL rule_schedule_one = 1 AND rule_active = 1";
		public static final String QUERY_GET_RULE_DDL = "SELECT rule_name, rule_id FROM rule_tbl WHERE LOWER(rule_user_id) = '?' AND rule_active = 1";
	}
	
	public static class PLAN_TBL_COLS{
		public static final String planId = "plan_id";
		public static final String planName = "plan_name";
		public static final String planDesc = "plan_desc";
		public static final String notificationNum = "note_num";
		public static final String planValidityDuration = "plan_validity_duration";
		public static final String planTrial = "plan_trial";
		public static final String planLocationAllowed = "plan_locn_allowed";
		public static final String planScheduleAllowed = "plan_schedule_allowed";
		public static final String planAdhocAllowed = "plan_adhoc_allowed";
		public static final String recurringAllowed = "plan_recur_allowed";
		public static final String planCost = "plan_cost";
		public static final String planType = "plan_type";
		public static final String planActive = "plan_active";
		
		public static final String QUERY_GET_PLAN_NAME_LST = "SELECT plan_id,plan_name FROM plan_tbl WHERE plan_active = 1 ORDER BY LOWER(plan_name)";
	}
	
	public static class NOTIFICATION_TBL_COLS{
		public static final String noteId = "note_id";
		public static final String noteAppId = "note_app_id";
		public static final String noteTitle = "note_title";
		public static final String noteMessage = "note_message";
		public static final String noteAndroid = "note_android";
		public static final String noteIOS = "note_ios";
		public static final String noteBlackberry = "note_blackberry";
		public static final String noteWindows = "note_windows";
		public static final String noteSymbian = "note_symbian";
		public static final String noteBroadcast = "note_broadcast";
		public static final String noteTag = "note_tag";	//for future reference. FK to TAG_TBL
		public static final String noteAdhoc = "note_adhoc";
		public static final String noteUrl = "note_url";
		public static final String noteSmsMsg = "note_sms_msg";
		public static final String noteSmsNum = "note_sms_num";
		public static final String noteCallNum = "note_call_num";
		public static final String noteRuleId = "note_rule_id";
		public static final String noteActive = "note_active";
		public static final String noteDeviceId = "note_device_id";//for single device

		public static final String QUERY_INSERT_DATA = "INSERT INTO notification_tbl VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		public static final String QUERY_GET_NOTE_LIST = "SELECT note_id, note_message FROM notification_tbl WHERE note_app_id = ? AND note_adhoc = 0 AND note_active = 1";
		public static final String QUERY_GET_NOTE_DATA = "SELECT * FROM notification_tbl WHERE note_id = ?";
		public static final String QUERY_DELETE_NOTE = "UPDATE notification_tbl SET note_active = 0 WHERE note_id = ?";
		public static final String QUERY_GET_LIST_4_USER = "SELECT * FROM notification_tbl WHERE note_app_id IN (SELECT app_id FROM app_tbl WHERE LOWER(app_user_id) = ? AND app_active = 1 AND app_active_plan_id > 1) AND note_rule_id = ? AND note_active = 1";
		public static final String QUERY_GET_LIST_4_APP = "SELECT * FROM notification_tbl WHERE note_app_id = ? AND note_rule_id = ? AND note_active = 1";

	}
	
	public static class NOTIFICATION_SENT_TBL_COLS{
		public static final String sentNoteId = "sent_note_id";
		public static final String sentDeviceId = "sent_device_id";
		public static final String sentDate = "sent_date";
		public static final String sentTime = "sent_time";
		public static final String sentLat = "sent_lat";
		public static final String sentLong = "sent_long";
		public static final String isDelivered = "is_delivered";
		public static final String isClicked = "is_clicked";
		
		public static final String QUERY_INSERT_DATA = "INSERT INTO notification_sent_tbl VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
		public static final String QUERY_UPDATE_DELIVERED = "UPDATE notification_sent_tbl SET is_delivered = 1 WHERE sent_note_id = ? AND sent_device_id = ?";
		public static final String QUERY_UPDATE_CLICKED = "UPDATE notification_sent_tbl SET is_clicked = 1 WHERE sent_note_id = ? AND sent_device_id = ?";
	}
	
	public static class APP_PLAN_TBL_COLS{
		public static final String appPlanId = "app_plan_id";
		public static final String appId = "app_id";
		public static final String planId = "plan_id";
		public static final String appPlanEffectiveDate = "app_plan_effective_date";
		public static final String appPlanInitNotificationLimit = "app_plan_init_note_limit";
		public static final String appPlanNotificationUsed = "app_plan_note_used";
		public static final String appPlanActive = "app_plan_active";
	
		public static final String QUERY_INACTIVATE_PREV_RECORD = "UPDATE app_plan_tbl SET app_plan_active = 0 WHERE app_id = ? AND app_plan_active = 1";
		public static final String QUERY_GET_ACTIVE_RECORD = "SELECT * FROM app_plan_tbl WHERE app_id = ? AND app_plan_active = 1";
		public static final String QUERY_INSERT_DATA = "INSERT INTO app_plan_tbl VALUES (?,?,?,?,?,?,?)"; 
		public static final String QUERY_UPDATE_NOTE_COUNT = "UPDATE app_plan_tbl SET app_plan_notification_used = (app_plan_notification_used + 1) WHERE app_id = ? AND app_plan_active = 1";
	}
	
	
	
}
