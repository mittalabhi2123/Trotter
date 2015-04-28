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
    
    public static enum SearchGroup{
    	individual, group, all;
    }
    
    public static enum gender{
    	male, female, both;
    }
    
    public static enum ResultType{
    	user,trip;
    }
    
    public static enum TripResponse{
    	reject, accept;
    }
    
    public static enum rejectRequestParam{
    	tripId, resultId, resultType, outwardTrip, tableName/*used by android client only in accept case, to create mysqlLite table, if match*/;
    }
    
    public static enum fetchSocialRequestParam {
    	friendId, mission, city, state, country, eventId, following, userId, follower;
    }
    
    public static enum selfTimelineRequestParam {
    	userId, mission, city, state, country, eventId;
    }
    
    public static enum SaveLikePicsRequestParam {
    	userId, socialId;
    }
    
    public static enum FollowUserRequestParam {
    	userId, followingUserId;
    }
    
    public static enum SaveSocialCommentRequestParam {
    	userId, socialId, comment;
    }
    
    public static enum SendMessageRequestparams {
    	senderTripId, targetTripId, messageId, message, senderName;
    }
    
    public static enum SaveEventsRequestParams {
    	event_name, country, state, city, date_from, date_to, remarks, event_type;
    }
}