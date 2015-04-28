package com.trotter.server.servlet;

import static com.trotter.common.MongoDBStructure.SOCIAL_TABLE_COLS.upload_date;
import static com.trotter.common.Utility.isNullEmpty;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.trotter.common.Const.fetchSocialRequestParam;
import com.trotter.common.ManageConnection;
import com.trotter.common.MongoDBStructure;
import com.trotter.common.MongoDBStructure.MISSION_SOCIAL_TABLE_COLS;
import com.trotter.common.MongoDBStructure.SOCIAL_TABLE_COLS;
import com.trotter.common.MongoDBStructure.USER_SOCIAL_TABLE_COLS;
import com.trotter.common.Utility;
import com.trotter.server.servlet.functions.SocialFunctions;

@WebServlet("/fetchSocial")
public class FetchSocialServlet extends HttpServlet {

    public FetchSocialServlet() {
    }

	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			String friendId = request.getParameter(fetchSocialRequestParam.friendId.name());
			String mission = request.getParameter(fetchSocialRequestParam.mission.name());
			String city = request.getParameter(fetchSocialRequestParam.city.name());
			String state = request.getParameter(fetchSocialRequestParam.state.name());
			String country = request.getParameter(fetchSocialRequestParam.country.name());
			String eventId = request.getParameter(fetchSocialRequestParam.eventId.name());
			String following = request.getParameter(fetchSocialRequestParam.following.name());
			String userId = request.getParameter(fetchSocialRequestParam.userId.name());
			String follower = request.getParameter(fetchSocialRequestParam.follower.name());
			System.out.println("FriendId:"+friendId);
			System.out.println("Mission:"+mission);
			System.out.println("City-State-Country:"+city + "-" + state + "-" + country);
			System.out.println("EventId:"+eventId);
			DB mongoDB = ManageConnection.getDBConnection();
			SocialFunctions socialFunc = new SocialFunctions();
			List<DBCursor> friendsPostList = socialFunc.getFriendsPosts(mongoDB, friendId);
			List<DBCursor> followingPostList = socialFunc.getFollowingPosts(mongoDB, true, userId, following);
			List<DBCursor> followerPostList = socialFunc.getFollowingPosts(mongoDB, false, userId, follower);
			List<DBCursor> missionPostList = socialFunc.getMissionPosts(mongoDB, mission);

			DBCollection socialTbl = mongoDB.getCollection(MongoDBStructure.SOCIAL_TBL);
			Map<String, JSONObject> friendSocialMap = new HashMap<>();
			Map<String, JSONObject> followingSocialMap = new HashMap<>();
			Map<String, JSONObject> followerSocialMap = new HashMap<>();
			prepareMapFromCursorList(mongoDB, socialTbl, socialFunc, friendsPostList, friendSocialMap);
			prepareMapFromCursorList(mongoDB, socialTbl, socialFunc, followingPostList, followingSocialMap);
			prepareMapFromCursorList(mongoDB, socialTbl, socialFunc, followerPostList, followerSocialMap);
			System.out.println("FriendsMap:"+friendId + " : " + friendSocialMap.size());
			System.out.println("following : " + followingSocialMap.size());
			System.out.println("follower : " + followerSocialMap.size());
			Map<String, JSONObject> missionSocialMap = new HashMap<>();
			for (DBCursor missionCursor : missionPostList) {
				while (missionCursor.hasNext()) {
					DBObject missionSocialObj = missionCursor.next();
					String socialId = String.valueOf(missionSocialObj.get(MISSION_SOCIAL_TABLE_COLS.social_id.name()));
					BasicDBObject dbObject = new BasicDBObject().append(SOCIAL_TABLE_COLS._id.name(), new ObjectId(socialId));
					DBObject socialObj = socialTbl.findOne(dbObject);
					JSONObject jsonObj = socialFunc.convert2Json(mongoDB, socialObj);
					missionSocialMap.put(jsonObj.getString(SOCIAL_TABLE_COLS._id.name()), jsonObj);
				}
			}
			System.out.println("MissionMap:"+mission + " : " + missionSocialMap.size());

			Map<String, JSONObject> locationSocialMap = new HashMap<>();
			BasicDBObject doc = new BasicDBObject();
			if (!Utility.isNullEmpty(city))
				doc.append(SOCIAL_TABLE_COLS.city.name(), city);
			if (!Utility.isNullEmpty(state))
				doc.append(SOCIAL_TABLE_COLS.state.name(), state);
			if (!Utility.isNullEmpty(country))
				doc.append(SOCIAL_TABLE_COLS.country.name(), country);
			if (!Utility.isNullEmpty(eventId))
				doc.append(SOCIAL_TABLE_COLS.event.name(), eventId);
			DBCursor socialCursor = socialTbl.find(doc);
			while (socialCursor.hasNext()) {
				DBObject socialObj = socialCursor.next();
				JSONObject jsonObj = socialFunc.convert2Json(mongoDB, socialObj);
				locationSocialMap.put(jsonObj.getString(SOCIAL_TABLE_COLS._id.name()), jsonObj);
			}
			System.out.println(doc + " : " + locationSocialMap.size());
			if ((!isNullEmpty(friendId) && friendsPostList.isEmpty()) || (locationSocialMap.isEmpty())
					|| (!isNullEmpty(mission) && missionPostList.isEmpty())
					|| (!isNullEmpty(following) && followingPostList.isEmpty())
					|| (!isNullEmpty(follower) && followerPostList.isEmpty())) {
				// return empty response if, any of the specified filter fails
				response.setContentType("application/json");
			    response.getWriter().write(new JSONArray().toString());
		    	response.setStatus(HttpServletResponse.SC_OK);
		    	return;
			}
			Map<String, JSONObject> socialMap = new HashMap<>();
			socialMap.putAll(friendSocialMap);
			socialMap.putAll(missionSocialMap);
			socialMap.putAll(followingSocialMap);
			socialMap.putAll(followerSocialMap);
			if ((isNullEmpty(friendId) && isNullEmpty(mission)
					&& isNullEmpty(following) && isNullEmpty(follower))
					|| (doc.size() > 0)) {
				// add all the records, if no friends/mission/following/follower filter is specified, or we have filter for location as well.
				socialMap.putAll(locationSocialMap);
			}
			System.out.println("SocialList size:" + socialMap.size());
			List<JSONObject> socialList = new ArrayList<>(socialMap.values()); 
			Collections.sort(socialList, new Comparator<JSONObject>() {
				@Override
				public int compare(JSONObject o1, JSONObject o2) {
					try {
						return Long.valueOf(o1.getLong(upload_date.name())).compareTo(Long.valueOf(o2.getLong(upload_date.name()))) * -1;
					} catch (JSONException e) {
						e.printStackTrace();
					}
					return 0;
				}
			});
			JSONArray socialArr = new JSONArray(socialList);
			response.setContentType("application/json");
		    response.getWriter().write(socialArr.toString());
	    	response.setStatus(HttpServletResponse.SC_OK);
		} catch (Exception e) {
			e.printStackTrace();
			response.setContentType("application/text");
		    response.getWriter().write(e.getMessage());
	    	response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	    	return;
		}
	}
	
	private void prepareMapFromCursorList(DB mongoDB, DBCollection socialTbl, SocialFunctions socialFunc, 
			List<DBCursor> cursorList, Map<String, JSONObject> socialMap) throws JSONException{
		for (DBCursor posts : cursorList) {
			while (posts.hasNext()) {
				DBObject userSocialObj = posts.next();
				String socialId = String.valueOf(userSocialObj.get(USER_SOCIAL_TABLE_COLS.social_id.name()));
				BasicDBObject dbObject = new BasicDBObject().append(SOCIAL_TABLE_COLS._id.name(), new ObjectId(socialId));
				DBObject socialObj = socialTbl.findOne(dbObject);
				JSONObject jsonObj = socialFunc.convert2Json(mongoDB, socialObj);
				socialMap.put(jsonObj.getString(SOCIAL_TABLE_COLS._id.name()), jsonObj);
			}
		}
	}


}
