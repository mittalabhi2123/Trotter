package com.trotter.server.servlet.functions;

import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mongodb.BasicDBList;
import com.mongodb.DB;
import com.mongodb.DBObject;
import com.trotter.common.Const;
import com.trotter.common.MongoDBStructure;
import com.trotter.common.MongoDBStructure.TRIP_TABLE_COLS;
import com.trotter.common.Utility;

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
			if (userTbl.has(MongoDBStructure.USER_TABLE_COLS.dob.name()) && !Utility.isNullEmpty(userTbl.getString(MongoDBStructure.USER_TABLE_COLS.dob.name()))) {
				System.out.println("dob::"+userTbl.getString(MongoDBStructure.USER_TABLE_COLS.dob.name()));
				long dobMillis = Long.parseLong(userTbl.getString(MongoDBStructure.USER_TABLE_COLS.dob.name()));
				long ageDiff = System.currentTimeMillis() - dobMillis;
				double millisToYearFactor = 1000L * 3600L * 24L * 365L;
				double age = ageDiff / millisToYearFactor;
				System.out.println(System.currentTimeMillis() + ", " + dobMillis + ", " + ageDiff + ", " + millisToYearFactor);
				System.out.println(age + ", " + ageMin + ", " + ageMax);
				if (age < ageMin || age > ageMax) {
					return null;
				}
			}
		}
		if (!skipAgeGenderFilter) {
			if (userTbl.has(MongoDBStructure.USER_TABLE_COLS.gender.name())) {
				Const.gender userGender = Const.gender.valueOf((String)userTbl.get(MongoDBStructure.USER_TABLE_COLS.gender.name()));
				System.out.println(gender + ", " + userGender);
				if (gender != Const.gender.both && !gender.equals(userGender))
					return null;
			}
		}
		jsonTripObj.put(MongoDBStructure.USER_TBL, userTbl);
		return jsonTripObj;
	}

	public JSONArray fetchUserListForTrip(DB mongoDB, DBObject dbObject) throws JSONException {
		JSONArray userList = new JSONArray();
		UserFunctions userFunctions = new UserFunctions();
		userList.put(userFunctions.fetchUserById(mongoDB,
				new ObjectId(String.valueOf(dbObject.get(MongoDBStructure.TRIP_TABLE_COLS.user_id.name())))));
    	if (!Boolean.parseBoolean(String.valueOf(dbObject.get(MongoDBStructure.TRIP_TABLE_COLS.is_individual.name())))) {
    		// fetch group member list
	    	BasicDBList groupMembers = (BasicDBList) dbObject.get(MongoDBStructure.TRIP_TABLE_COLS.group_members.name());
	    	if (groupMembers != null && groupMembers.size() > 0) {
	    		//Add user objects for group members
		    	for (int i = 0 ; i < groupMembers.size() ; i++) {
		    		if (groupMembers.get(i) == null || groupMembers.get(i).toString().equals(""))
		    			continue;
		    		JSONObject userObj = userFunctions.fetchUserById(mongoDB, new ObjectId(((String)groupMembers.get(i))));
		    		userList.put(userObj);
		    	}
	    	}
    	}
    	return userList;
	}
}