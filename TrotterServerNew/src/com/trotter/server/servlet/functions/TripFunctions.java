package com.trotter.server.servlet.functions;

import org.bson.types.ObjectId;
import org.json.JSONException;
import org.json.JSONObject;

import com.mongodb.DB;
import com.mongodb.DBObject;
import com.trotter.common.Const;
import com.trotter.common.MongoDBStructure;
import com.trotter.common.MongoDBStructure.TRIP_TABLE_COLS;

public class TripFunctions {

	public JSONObject createTripJsonObj(DBObject tripDbObj, UserFunctions userFunc, DB mongoDB) throws JSONException {
		return createTripJsonObj(tripDbObj, userFunc, mongoDB, 0, 99, Const.gender.both, true);
	}

	public JSONObject createTripJsonObj(DBObject tripDbObj, UserFunctions userFunc, DB mongoDB, int ageMin, int ageMax, Const.gender gender, boolean skipAgeGenderFilter)
			throws JSONException {
		JSONObject jsonTripObj = new JSONObject();
		for (MongoDBStructure.TRIP_TABLE_COLS tripColName : MongoDBStructure.TRIP_TABLE_COLS.values()) {
			if (tripDbObj.containsField(tripColName.name())) {
				jsonTripObj.put(tripColName.name(), tripDbObj.get(tripColName.name()));
			}
		}
		JSONObject userTbl = userFunc.fetchUserById(mongoDB, new ObjectId(jsonTripObj.getString(TRIP_TABLE_COLS.user_id.name())));
		if (!skipAgeGenderFilter) {
			if (userTbl.has(MongoDBStructure.USER_TABLE_COLS.dob.name())) {
				long dobMillis = Long.parseLong((String)userTbl.get(MongoDBStructure.USER_TABLE_COLS.dob.name()));
				double age = dobMillis / (1000 * 3600 * 24 * 365);
				if (age < ageMin || age > ageMax) {
					return null;
				}
			}
		}
		if (!skipAgeGenderFilter) {
			if (userTbl.has(MongoDBStructure.USER_TABLE_COLS.gender.name())) {
				Const.gender userGender = Const.gender.valueOf((String)userTbl.get(MongoDBStructure.USER_TABLE_COLS.gender.name()));
				if (gender != Const.gender.both && !gender.equals(userGender))
					return null;
			}
		}
		jsonTripObj.put(MongoDBStructure.USER_TBL, userTbl);
		return jsonTripObj;
	}
}