package com.trotter.common;

public class MongoDBStructure {

	public static final String DB_NAME = "trotter";
	public static final String HOST = "127.0.0.1";
	public static final int PORT = 27017;

	//Tables Names
	public static final String TRIP_TBL = "trip_tbl";
	public static final String USER_TBL = "user_tbl";
	public static final String TRIP_RESPONSE_TBL = "trip_response_tbl";
	public static final String EVENTS_TBL = "event_tbl";
	
	public static enum USER_TABLE_COLS {
		_id, fb_id, fb_access_token, email, registration_date, first_name, last_name, dob, auth_token,
		home_city, home_state, home_country, gender, travel_bio, pictures, own_trips, matched_trips;
	}
	
	public static enum TRIP_TABLE_COLS {
		_id, user_id, start_date, end_date, duration, origin_city, origin_state, origin_country,
		dest_city, dest_state, dest_country, mission, transport_mode,
		is_individual, group_name, group_icon, group_members, trip_matches, event_id, event, trips_selected, trips_rejected;
	}
	
	public static enum EVENTS_COLS {
		_id, name, icon, country, state, city, start_datetime, end_datetime, type;
	}
	
	public static enum TRIP_RESPONSE_TABLE_COLS {
		_id, trip_id, result_id, result_type, outward_trip, response;
	}
}
