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
    
    public static enum ResultType{
    	user,trip;
    }
    
    public static enum rejectRequestParam{
    	tripId, resultId, resultType;
    }
}
