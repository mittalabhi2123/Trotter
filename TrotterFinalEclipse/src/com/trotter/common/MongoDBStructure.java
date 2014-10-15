package com.trotter.common;

public class MongoDBStructure {

	public static final String DB_NAME = "trotter";
	public static final String HOST = "127.0.0.1";
	public static final int PORT = 27017;
	public static final String USER_TBL = "user_tbl";
	
	public static enum USER_TABLE_COLS {
		_id, fb_id, fb_access_token, email, registration_date, first_name, last_name, dob, auth_token,
		home_city, home_state, home_country, gender, travel_bio, pictures, own_trips, matched_trips;
	}	
}
