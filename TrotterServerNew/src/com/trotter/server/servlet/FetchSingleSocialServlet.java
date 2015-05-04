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

@WebServlet("/fetchSingleSocial")
public class FetchSingleSocialServlet extends HttpServlet {

    public FetchSingleSocialServlet() {
    }

	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			String socialId = request.getParameter("socialId");
			String userId = request.getParameter("userId");
			System.out.println("userId:"+userId);
			System.out.println("socialId:"+socialId);
			DB mongoDB = ManageConnection.getDBConnection();
			SocialFunctions socialFunc = new SocialFunctions();
			JSONObject socialObj = socialFunc.fetchById(mongoDB, new ObjectId(socialId), true);
			response.setContentType("application/json");
		    response.getWriter().write(socialObj.toString());
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
