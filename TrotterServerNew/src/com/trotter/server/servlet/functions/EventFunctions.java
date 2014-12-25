package com.trotter.server.servlet.functions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.trotter.common.MongoDBStructure;
import com.trotter.common.Utility;

public class EventFunctions {

	public JSONArray fetchCurrentEventList(DB mongoDB) throws JSONException {
		DBCollection eventTbl = mongoDB.getCollection(MongoDBStructure.EVENTS_TBL);
		BasicDBObject inQuery = new BasicDBObject();
		inQuery.put(MongoDBStructure.EVENTS_COLS.start_datetime.name(), new BasicDBObject(Utility.MongoQueryHandles.$gte.name(), System.currentTimeMillis()));
		DBCursor cursor = eventTbl.find(inQuery);
	    if (cursor.count() <= 0)
	    	return null;
	    JSONArray eventTblLst = new JSONArray();
	    while (cursor.hasNext()) {
	    	JSONObject eventTblObj = new JSONObject();
	    	DBObject dbObject = cursor.next();
	    	for (MongoDBStructure.EVENTS_COLS colName : MongoDBStructure.EVENTS_COLS.values()) {
	    	    if (dbObject.containsField(colName.name())) {
	    			eventTblObj.put(colName.name(), dbObject.get(colName.name()));
	    		}
	    	}
    	    eventTblLst.put(eventTblObj);
	    }
	    
    	return eventTblLst;
	}

}
