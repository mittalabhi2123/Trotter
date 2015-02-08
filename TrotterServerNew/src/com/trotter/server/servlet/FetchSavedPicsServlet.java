package com.trotter.server.servlet;

import java.io.IOException;

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
			DBCollection userTblCol = mongoDB.getCollection(MongoDBStructure.USER_TBL);
			JSONObject userObj = new UserFunctions().fetchUserById(mongoDB, new ObjectId(userId));
			JSONArray savedPostsArr = new JSONArray();
			if (userObj.has(MongoDBStructure.USER_TABLE_COLS.saved_pics.name())){
				String saved = userObj.getString(MongoDBStructure.USER_TABLE_COLS.saved_pics.name());
				SocialFunctions socialFunc = new SocialFunctions();
				for (String socialId : saved.split(",")) {
					if (!Utility.isNullEmpty(socialId))
						savedPostsArr.put(socialFunc.fetchById(mongoDB, new ObjectId(socialId)));
				}
			}

			response.setContentType("application/json");
		    response.getWriter().write(savedPostsArr.toString());
	    	response.setStatus(HttpServletResponse.SC_OK);
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}


}
