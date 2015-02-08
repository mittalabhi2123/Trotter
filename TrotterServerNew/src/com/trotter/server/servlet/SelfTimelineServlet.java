package com.trotter.server.servlet;

import java.io.IOException;
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
import org.json.JSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.trotter.common.Const.fetchSocialRequestParam;
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
			DBCursor socialCursor = socialTbl.find(doc);
			
			JSONArray socialArr = new JSONArray();
			Map<String, JSONObject> socialMap = new HashMap<>();
			while (socialCursor.hasNext()) {
				DBObject socialObj = socialCursor.next();
				JSONObject jsonObj = socialFunc.convert2Json(mongoDB, socialObj);
				socialMap.put(jsonObj.getString(SOCIAL_TABLE_COLS._id.name()), jsonObj);
				socialArr.put(jsonObj);
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
				socialArr.put(jsonObj);
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
					socialArr.put(jsonObj);
				}
			}
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
