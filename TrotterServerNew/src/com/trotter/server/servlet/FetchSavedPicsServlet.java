package com.trotter.server.servlet;

import static com.trotter.common.MongoDBStructure.SOCIAL_TABLE_COLS.upload_date;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mongodb.DB;
import com.trotter.common.Const;
import com.trotter.common.ManageConnection;
import com.trotter.common.MongoDBStructure;
import com.trotter.common.Utility;
import com.trotter.server.servlet.functions.SocialFunctions;
import com.trotter.server.servlet.functions.UserFunctions;

@WebServlet("/fetchSavedPosts")
public class FetchSavedPicsServlet extends HttpServlet {

    public FetchSavedPicsServlet() {
    }

	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			if (Utility.isNullEmpty(request.getParameter(Const.SaveLikePicsRequestParam.userId.name()))) {
				System.out.println("Empty request found...:(");
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Invalid/No request received");
				return;
			}
			String userId = request.getParameter(Const.SaveLikePicsRequestParam.userId.name());
			//TODO validations
			DB mongoDB = ManageConnection.getDBConnection();
			JSONObject userObj = new UserFunctions().fetchUserById(mongoDB, new ObjectId(userId));
			List<JSONObject> savedPostsList = new ArrayList<>();
			if (userObj.has(MongoDBStructure.USER_TABLE_COLS.saved_pics.name())){
				String saved = userObj.getString(MongoDBStructure.USER_TABLE_COLS.saved_pics.name());
				SocialFunctions socialFunc = new SocialFunctions();
				for (String socialId : saved.split(",")) {
					if (!Utility.isNullEmpty(socialId))
						savedPostsList.add(socialFunc.fetchById(mongoDB, new ObjectId(socialId)));
				}
			}
			Collections.sort(savedPostsList, new Comparator<JSONObject>() {
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
			JSONArray savedPicsArr = new JSONArray(savedPostsList);
			response.setContentType("application/json");
		    response.getWriter().write(savedPicsArr.toString());
	    	response.setStatus(HttpServletResponse.SC_OK);
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}


}
