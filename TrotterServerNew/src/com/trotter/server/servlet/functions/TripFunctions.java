package com.trotter.server.servlet.functions;

import org.bson.types.ObjectId;
import org.json.JSONException;
import org.json.JSONObject;

import com.mongodb.DB;
import com.mongodb.DBObject;
import com.trotter.common.MongoDBStructure;
import com.trotter.common.MongoDBStructure.TRIP_TABLE_COLS;

public class TripFunctions {

	public JSONObject createTripJsonObj(DBObject tripDbObj, UserFunctions userFunc, DB mongoDB) throws JSONException {
		JSONObject jsonTripObj = new JSONObject();
		for (MongoDBStructure.TRIP_TABLE_COLS tripColName : MongoDBStructure.TRIP_TABLE_COLS.values()) {
			if (tripDbObj.containsField(tripColName.name())) {
				jsonTripObj.put(tripColName.name(), tripDbObj.get(tripColName.name()));
			}
		}
		jsonTripObj.put(MongoDBStructure.USER_TBL, userFunc.fetchUserById(mongoDB, new ObjectId(jsonTripObj.getString(TRIP_TABLE_COLS.user_id.name()))));
		return jsonTripObj;
	}
}