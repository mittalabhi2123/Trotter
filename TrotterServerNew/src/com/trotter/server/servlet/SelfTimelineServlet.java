package com.trotter.server.servlet;

import static com.trotter.common.MongoDBStructure.SOCIAL_TABLE_COLS.upload_date;

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
import com.trotter.common.Const.selfTimelineRequestParam;
import com.trotter.common.ManageConnection;
import com.trotter.common.MongoDBStructure;
import com.trotter.common.MongoDBStructure.MISSION_SOCIAL_TABLE_COLS;
import com.trotter.common.MongoDBStructure.SOCIAL_TABLE_COLS;
import com.trotter.common.MongoDBStructure.USER_SOCIAL_TABLE_COLS;
import com.trotter.common.Utility;
import com.trotter.server.servlet.functions.SocialFunctions;

@WebServlet("/selfTimeline")
public class SelfTimelineServlet extends HttpServlet {

    public SelfTimelineServlet() {
    }

	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			String userId = request.getParameter(selfTimelineRequestParam.userId.name());
			String mission = request.getParameter(selfTimelineRequestParam.mission.name());
			String city = request.getParameter(selfTimelineRequestParam.city.name());
			String state = request.getParameter(selfTimelineRequestParam.state.name());
			String country = request.getParameter(selfTimelineRequestParam.country.name());
			String eventId = request.getParameter(selfTimelineRequestParam.eventId.name());
			if (Utility.isNullEmpty(userId)) {
				System.out.println("Empty request found...:(");
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Invalid/No request received");
				return;
			}
			System.out.println("Self Timeline.");
			System.out.println(userId);
			System.out.println(mission);
			System.out.println(city + "-" + state + "-" + country);
			System.out.println(eventId);
			DB mongoDB = ManageConnection.getDBConnection();
			SocialFunctions socialFunc = new SocialFunctions();
			DBCursor selfPosts = socialFunc.getUserPosts(mongoDB, userId);
			if (selfPosts == null) {
				System.out.println("No data found...");
				response.setStatus(HttpServletResponse.SC_NO_CONTENT, "No data found...");
				return;
			}
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
			DBCursor socialCursor = null;
			if (doc.size() > 0) {
				socialCursor = socialTbl.find(doc);
			}
			Map<String, JSONObject> socialMap = new HashMap<>();
			if (socialCursor != null) {
				while (socialCursor.hasNext()) {
					DBObject socialObj = socialCursor.next();
					JSONObject jsonObj = socialFunc.convert2Json(mongoDB, socialObj);
					socialMap.put(jsonObj.getString(SOCIAL_TABLE_COLS._id.name()), jsonObj);
				}
				
			}
			while (selfPosts.hasNext()) {
				DBObject userSocialObj = selfPosts.next();
				String socialId = String.valueOf(userSocialObj.get(USER_SOCIAL_TABLE_COLS.social_id.name()));
				if (socialMap.containsKey(socialId))
					continue;
				BasicDBObject dbObject = new BasicDBObject().append(SOCIAL_TABLE_COLS._id.name(), new ObjectId(socialId));
				DBObject socialObj = socialTbl.findOne(dbObject);
				JSONObject jsonObj = socialFunc.convert2Json(mongoDB, socialObj);
				socialMap.put(jsonObj.getString(SOCIAL_TABLE_COLS._id.name()), jsonObj);
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
				}
			}
			List<JSONObject> socialList = new ArrayList<>();
			for (JSONObject obj : socialMap.values()) {
				String countryText = obj.getString(MongoDBStructure.SOCIAL_TABLE_COLS.country.name());
				if (!Utility.isNullEmpty(countryText))
					socialList.add(obj);// send only checked-in posts, ie, where location is mentioned.
			}
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


}
