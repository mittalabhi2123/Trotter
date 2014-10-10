package com.trotter.common;


import com.mongodb.CommandResult;
import com.mongodb.DB;
import com.mongodb.Mongo;

public class ManageConnection {

	private static DB mongoDb = null;
	static {
		try {
			Mongo client = new Mongo(MongoDBStructure.HOST,MongoDBStructure.PORT);
			client.getDatabaseNames();
			//MongoClient.connect(new DBAddress(MongoDBStructure.HOST,MongoDBStructure.PORT,MongoDBStructure.DB_NAME));
			mongoDb = client.getDB(client.getDatabaseNames().get(0));
			CommandResult lastError = mongoDb.getLastError();
		} catch (Exception f) {
	       throw new RuntimeException(f);
	    }  
	}
	
	public static DB getDBConnection() throws Exception{
//		if(mongoDb == null){
//			//mongoDb = new MongoClient(MongoDBStructure.HOST,MongoDBStructure.PORT).getDB(MongoDBStructure.DB_NAME);
//		}
//		mongoDb.requestStart();
		return mongoDb;     
	}
}
