package com.trotter.server.servlet;

import static com.trotter.common.MongoDBStructure.SOCIAL_TABLE_COLS.upload_date;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
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
import static com.trotter.common.Utility.isNullEmpty;

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
			System.out.println(friendId);
			System.out.println(mission);
			System.out.println(city + "-" + state + "-" + country);
			System.out.println(eventId);
			DB mongoDB = ManageConnection.getDBConnection();
			JSONArray socialArr = new JSONArray();
			SocialFunctions socialFunc = new SocialFunctions();
			List<DBCursor> friendsPostList = socialFunc.getFriendsPosts(mongoDB, friendId);
			List<DBCursor> missionPostList = socialFunc.getMissionPosts(mongoDB, mission);

			DBCollection socialTbl = mongoDB.getCollection(MongoDBStructure.SOCIAL_TBL);
			List<JSONObject> friendSocialList = new ArrayList<>();
			Map<String, JSONObject> friendSocialMap = new HashMap<>();
			for (DBCursor friendsPosts : friendsPostList) {
				while (friendsPosts.hasNext()) {
					DBObject userSocialObj = friendsPosts.next();
					String socialId = String.valueOf(userSocialObj.get(USER_SOCIAL_TABLE_COLS.social_id.name()));
					BasicDBObject dbObject = new BasicDBObject().append(SOCIAL_TABLE_COLS._id.name(), new ObjectId(socialId));
					DBObject socialObj = socialTbl.findOne(dbObject);
					JSONObject jsonObj = socialFunc.convert2Json(mongoDB, socialObj);
					friendSocialMap.put(jsonObj.getString(SOCIAL_TABLE_COLS._id.name()), jsonObj);
					friendSocialList.add(jsonObj);
				}
			}
			System.out.println(friendId + " : " + friendSocialList.size());
			List<JSONObject> missionSocialList = new ArrayList<>();
			Map<String, JSONObject> missionSocialMap = new HashMap<>();
			for (DBCursor missionCursor : missionPostList) {
				while (missionCursor.hasNext()) {
					DBObject missionSocialObj = missionCursor.next();
					String socialId = String.valueOf(missionSocialObj.get(MISSION_SOCIAL_TABLE_COLS.social_id.name()));
					BasicDBObject dbObject = new BasicDBObject().append(SOCIAL_TABLE_COLS._id.name(), new ObjectId(socialId));
					DBObject socialObj = socialTbl.findOne(dbObject);
					JSONObject jsonObj = socialFunc.convert2Json(mongoDB, socialObj);
					missionSocialMap.put(jsonObj.getString(SOCIAL_TABLE_COLS._id.name()), jsonObj);
					missionSocialList.add(jsonObj);
				}
			}
			System.out.println(mission + " : " + missionSocialList.size());

			List<JSONObject> locationSocialList = new ArrayList<>();
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
			System.out.println("Doc Size:"+doc.size());
			System.out.println(System.currentTimeMillis());
			System.out.println(new Date(System.currentTimeMillis()));
			System.out.println(new Date());
			System.out.println(Calendar.getInstance());
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(System.currentTimeMillis());
			System.out.println(cal.getTime());
			while (socialCursor.hasNext()) {
				DBObject socialObj = socialCursor.next();
				JSONObject jsonObj = socialFunc.convert2Json(mongoDB, socialObj);
				locationSocialMap.put(jsonObj.getString(SOCIAL_TABLE_COLS._id.name()), jsonObj);
				locationSocialList.add(jsonObj);
			}
			System.out.println(doc + " : " + locationSocialList.size());
			if ((!isNullEmpty(friendId) && friendsPostList.isEmpty()) || (locationSocialList.isEmpty())
					|| (!isNullEmpty(mission) && missionPostList.isEmpty())) {
				// return empty response if, any of the specified filter fails
				response.setContentType("application/json");
			    response.getWriter().write(socialArr.toString());
		    	response.setStatus(HttpServletResponse.SC_OK);
		    	return;
			}
			List<JSONObject> socialList = new ArrayList<>();
			Map<String, JSONObject> socialMap = new HashMap<>();
			socialList.addAll(friendSocialList);
			socialList.addAll(missionSocialList);
			socialMap.putAll(friendSocialMap);
			socialMap.putAll(missionSocialMap);
			if ((isNullEmpty(friendId) && isNullEmpty(mission)) || (doc.size() > 0)) {
				
				// add all the records, if no friends/mission filter is specified, or we have filter for location as well.
				socialList.addAll(locationSocialList);
				socialMap.putAll(locationSocialMap);
			}
			System.out.println("SocialList size:" + socialList.size());
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
			for(JSONObject obj : socialList)
				socialArr.put(obj);
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


}
