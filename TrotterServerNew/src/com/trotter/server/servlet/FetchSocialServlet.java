package com.trotter.server.servlet;

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
import static com.trotter.common.MongoDBStructure.SOCIAL_TABLE_COLS.*;
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
			System.out.println(friendId);
			System.out.println(mission);
			System.out.println(city + "-" + state + "-" + country);
			System.out.println(eventId);
			DB mongoDB = ManageConnection.getDBConnection();
			SocialFunctions socialFunc = new SocialFunctions();
			List<DBCursor> friendsPostList = socialFunc.getFriendsPosts(mongoDB, friendId);
			List<DBCursor> missionPostList = socialFunc.getMissionPosts(mongoDB, mission);
			
			DBCollection socialTbl = mongoDB.getCollection(MongoDBStructure.SOCIAL_TBL);
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
			
			JSONArray socialArr = new JSONArray();
			List<JSONObject> socialList = new ArrayList<>();
			Map<String, JSONObject> socialMap = new HashMap<>();
			while (socialCursor.hasNext()) {
				DBObject socialObj = socialCursor.next();
				JSONObject jsonObj = socialFunc.convert2Json(mongoDB, socialObj);
				socialMap.put(jsonObj.getString(SOCIAL_TABLE_COLS._id.name()), jsonObj);
				socialList.add(jsonObj);
			}
			for (DBCursor friendsPosts : friendsPostList) {
				while (friendsPosts.hasNext()) {
					DBObject userSocialObj = friendsPosts.next();
					String socialId = String.valueOf(userSocialObj.get(USER_SOCIAL_TABLE_COLS.social_id.name()));
					if (socialMap.containsKey(socialId))
						continue;
					BasicDBObject dbObject = new BasicDBObject().append(SOCIAL_TABLE_COLS._id.name(), new ObjectId(socialId));
					DBObject socialObj = socialTbl.findOne(dbObject);
					JSONObject jsonObj = socialFunc.convert2Json(mongoDB, socialObj);
					socialMap.put(jsonObj.getString(SOCIAL_TABLE_COLS._id.name()), jsonObj);
					socialList.add(jsonObj);
				}
			}
			for (DBCursor missionCursor : missionPostList) {
				while (missionCursor.hasNext()) {
					DBObject missionSocialObj = missionCursor.next();
					String socialId = String.valueOf(missionSocialObj.get(MISSION_SOCIAL_TABLE_COLS.social_id.name()));
					if (socialMap.containsKey(socialId))
						continue;
					BasicDBObject dbObject = new BasicDBObject().append(SOCIAL_TABLE_COLS._id.name(), new ObjectId(socialId));
					DBObject socialObj = socialTbl.findOne(dbObject);
					JSONObject jsonObj = socialFunc.convert2Json(mongoDB, socialObj);
					socialMap.put(jsonObj.getString(SOCIAL_TABLE_COLS._id.name()), jsonObj);
					socialList.add(jsonObj);
				}
			}
			Collections.sort(socialList, new Comparator<JSONObject>() {
				@Override
				public int compare(JSONObject o1, JSONObject o2) {
					int val;
					try {
						val = Long.valueOf(o1.getLong(upload_date.name())).compareTo(Long.valueOf(o2.getLong(upload_date.name())));
						return val * (-1);
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
