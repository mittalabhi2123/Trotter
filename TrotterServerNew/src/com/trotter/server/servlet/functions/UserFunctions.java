package com.trotter.server.servlet.functions;
import org.bson.types.ObjectId;
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


public class UserFunctions {

	public JSONObject fetchUserById(DB mongoDB, ObjectId id) throws JSONException {
		DBCollection userTbl = mongoDB.getCollection(MongoDBStructure.USER_TBL);
		BasicDBObject inQuery = new BasicDBObject();
		inQuery.put(MongoDBStructure.USER_TABLE_COLS._id.name(), id);
		DBObject dbObject = userTbl.findOne(inQuery);
	    return createUserJson(dbObject);
	}
	
	public JSONObject createUserJson(DBObject dbObject) throws JSONException {
	    if (dbObject == null)
	    	return null;
	    JSONObject userObj = new JSONObject();
    	for (MongoDBStructure.USER_TABLE_COLS colName : MongoDBStructure.USER_TABLE_COLS.values()) {
    		if (dbObject.containsField(colName.name())) {
    			userObj.put(colName.name(), dbObject.get(colName.name()));
    		}
    	}
    	return userObj;
	}
	
	public JSONArray fetchUserByFbIdList(DB mongoDB, String[] fbIdList) throws JSONException {
		if (fbIdList == null || fbIdList.length == 0)
			return null;
		DBCollection userTbl = mongoDB.getCollection(MongoDBStructure.USER_TBL);
		BasicDBObject inQuery = new BasicDBObject();
		BasicDBObject fields = new BasicDBObject();
		fields.put(MongoDBStructure.USER_TABLE_COLS.fb_id.name(), 1);
		inQuery.put(MongoDBStructure.USER_TABLE_COLS.fb_id.name(), new BasicDBObject(
				Utility.MongoQueryHandles.$in.name(), fbIdList));
	    DBCursor cursor = userTbl.find(inQuery, fields);
	    JSONArray responseArr = new JSONArray();
	    while (cursor.hasNext()) {
			responseArr.put(createUserJson(cursor.next()));
		}
	    return responseArr; 
	}
}
