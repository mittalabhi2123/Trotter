package com.trotter.server.servlet.functions;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.trotter.common.MongoDBStructure;
import com.trotter.common.MongoDBStructure.SOCIAL_TABLE_COLS;
import com.trotter.common.MongoDBStructure.USER_TABLE_COLS;
import com.trotter.common.Utility;

public class SocialFunctions {

	public List<DBCursor> getFriendsPosts(DB mongoDB, String friendId) throws JSONException {
		if (Utility.isNullEmpty(friendId)){
			return new ArrayList<>();
		}
		UserFunctions userFunc = new UserFunctions();
		List<DBCursor> dbCursorList = new ArrayList<>();
		JSONArray jsonArr = userFunc.fetchUserByFbIdList(mongoDB, friendId.split(","));
		System.out.println("Friend's Id lists:" + jsonArr);
		if (jsonArr == null || jsonArr.length() == 0 || jsonArr.get(0) == null) {
			System.out.println("Invalid friend facebook id provided.");
			//response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Invalid friend facebook id provided.");
			return new ArrayList<>();
		}
		for (int i = 0 ; i <jsonArr.length() ; i++) {
			DBCollection userSocialTbl = mongoDB.getCollection(jsonArr.getJSONObject(i).getString(
					USER_TABLE_COLS._id.name()) + "_" + MongoDBStructure.SOCIAL_TBL);
			if (userSocialTbl == null) {
				continue;
			}
			//TODO implement pagination
			DBCursor dbCursor = userSocialTbl.find();
			if (dbCursor != null)
				dbCursorList.add(dbCursor);
		}
		return dbCursorList;
	}

	public List<DBCursor> getFollowingPosts(DB mongoDB, boolean isFollowingCall, String userId, String followRequestParam) throws JSONException {
		if (Utility.isNullEmpty(followRequestParam)){
			return new ArrayList<>();
		}
		UserFunctions userFunc = new UserFunctions();
		JSONObject userObj = userFunc.fetchUserById(mongoDB, new ObjectId(userId));
		String associatedUserStr = "";
		String colName = isFollowingCall ? MongoDBStructure.USER_TABLE_COLS.following.name()
				: MongoDBStructure.USER_TABLE_COLS.follower.name();
		if (userObj.has(colName))
			associatedUserStr = userObj.getString(colName);
		System.out.println("Following user id list:" + associatedUserStr);
		if (Utility.isNullEmpty(associatedUserStr)) {
			System.out.println("User doesn't follow any other user.");
			return new ArrayList<>();
		}
		List<DBCursor> dbCursorList = new ArrayList<>();
		for (String followingUser : associatedUserStr.split(",")) {
			DBCollection userSocialTbl = mongoDB.getCollection(followingUser + "_" + MongoDBStructure.SOCIAL_TBL);
			if (userSocialTbl == null) {
				continue;
			}
			//TODO implement pagination
			DBCursor dbCursor = userSocialTbl.find();
			if (dbCursor != null)
				dbCursorList.add(dbCursor);
		}
		return dbCursorList;
	}

	public JSONObject fetchById(DB mongoDB, ObjectId id) throws JSONException {
		DBCollection socialTbl = mongoDB.getCollection(MongoDBStructure.SOCIAL_TBL);
		if (socialTbl == null) {
			return null;
		}
		//TODO implement pagination
		BasicDBObject inQuery = new BasicDBObject();
		inQuery.put(MongoDBStructure.SOCIAL_TABLE_COLS._id.name(), id);
		DBObject socialObj = socialTbl.findOne(inQuery);
		return convert2Json(mongoDB, socialObj, false);
	}

	public DBCursor getUserPosts(DB mongoDB, String userId) throws JSONException {
		UserFunctions userFunc = new UserFunctions();
		JSONObject jsonObj = userFunc.fetchUserById(mongoDB, new ObjectId(userId));
		if (jsonObj == null) {
			System.out.println("Invalid user id provided.");
			//response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Invalid friend facebook id provided.");
			return null;
		}
		DBCollection userSocialTbl = mongoDB.getCollection(jsonObj.getString(
				USER_TABLE_COLS._id.name()) + "_" + MongoDBStructure.SOCIAL_TBL);
		if (userSocialTbl == null) {
			return null;
		}
		//TODO implement pagination
		return userSocialTbl.find();
	}

	public List<DBCursor> getMissionPosts(DB mongoDB, String missions) {
		if (Utility.isNullEmpty(missions)){
			return new ArrayList<>();
		}
		List<DBCursor> dbCursorList = new ArrayList<>();
		for (String mission : missions.split(",")) {
			DBCollection missionSocialTbl = mongoDB.getCollection(mission + "_" + MongoDBStructure.SOCIAL_TBL);
			if (missionSocialTbl == null)
				continue;
			//TODO implement pagination
			DBCursor dbCursor = missionSocialTbl.find();
			if (dbCursor != null)
				dbCursorList.add(dbCursor);
		}
		return dbCursorList;
	}
	
	public JSONObject convert2Json(DB mongoDB, DBObject socialObj) throws JSONException {
		return convert2Json(mongoDB, socialObj, true);
	}
	
	public JSONObject convert2Json(DB mongoDB, DBObject socialObj, boolean addUserObj) throws JSONException {
		JSONObject jsonObj = new JSONObject();
		UserFunctions userFunc = new UserFunctions();
		for (SOCIAL_TABLE_COLS colName : SOCIAL_TABLE_COLS.values()) {
			if (colName.equals(SOCIAL_TABLE_COLS.pic)) {
				BasicDBList dbList = (BasicDBList)socialObj.get(colName.name());
				StringBuffer pics = new StringBuffer();
				for (int i = 0 ; i < dbList.size() ; i++) {
					pics.append(",");
					pics.append(dbList.get(i));
				}
				jsonObj.put(colName.name(), pics);
			} else if (socialObj.containsField(colName.name()))
				jsonObj.put(colName.name(), socialObj.get(colName.name()));
		}
		if (addUserObj) {
			String userId = jsonObj.getString(SOCIAL_TABLE_COLS.user_id.name());
			jsonObj.put(MongoDBStructure.USER_TBL, userFunc.fetchUserById(mongoDB, new ObjectId(userId)));
		}
		return jsonObj;
	}
}
