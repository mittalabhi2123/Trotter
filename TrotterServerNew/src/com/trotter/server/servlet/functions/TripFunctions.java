package com.trotter.server.servlet.functions;

import org.json.JSONException;
import org.json.JSONObject;

import com.mongodb.DBObject;
import com.trotter.common.MongoDBStructure;

public class TripFunctions {

	public JSONObject createTripJsonObj(DBObject tripDbObj) throws JSONException {
		JSONObject jsonTripObj = new JSONObject();
		for (MongoDBStructure.TRIP_TABLE_COLS tripColName : MongoDBStructure.TRIP_TABLE_COLS.values()) {
			if (tripDbObj.containsField(tripColName.name())) {
				jsonTripObj.put(tripColName.name(), tripDbObj.get(tripColName.name()));
			}
		}
		return jsonTripObj;
	}
}
