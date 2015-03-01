package com.trotter.server.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bson.types.ObjectId;
import org.json.JSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.trotter.common.Const;
import com.trotter.common.ManageConnection;
import com.trotter.common.MongoDBStructure;
import com.trotter.common.Utility;
import com.trotter.server.servlet.functions.SocialFunctions;

@WebServlet("/likePics")
public class LikedPicsServlet extends HttpServlet {

    public LikedPicsServlet() {
    }

	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			if (Utility.isNullEmpty(request.getParameter(Const.SaveLikePicsRequestParam.socialId.name()))
					|| Utility.isNullEmpty(request.getParameter(Const.SaveLikePicsRequestParam.userId.name()))) {
				System.out.println("Empty request found...:(");
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Invalid/No request received");
				return;
			}
			String socialId = request.getParameter(Const.SaveLikePicsRequestParam.socialId.name());
			String userId = request.getParameter(Const.SaveLikePicsRequestParam.userId.name());
			System.out.println("Like Pic Servlet---SocialId:"+socialId+", userId:"+userId);
			//TODO validations
			DB mongoDB = ManageConnection.getDBConnection();
			DBCollection socialTbl = mongoDB.getCollection(MongoDBStructure.SOCIAL_TBL);
			JSONObject socialObj = new SocialFunctions().fetchById(mongoDB, new ObjectId(socialId));
			String liked = "";
			if (socialObj.has(MongoDBStructure.SOCIAL_TABLE_COLS.liked_by_users.name()))
				liked = socialObj.getString(MongoDBStructure.SOCIAL_TABLE_COLS.liked_by_users.name());
			if (Utility.isNullEmpty(liked))
				liked = userId;
			else
				liked = liked + "," + userId;
			BasicDBObject condDoc = new BasicDBObject(MongoDBStructure.SOCIAL_TABLE_COLS._id.name(), new ObjectId(socialId));
			BasicDBObject setObj = new BasicDBObject().append(Utility.MongoQueryHandles.$set.name(), 
					new BasicDBObject().append(MongoDBStructure.SOCIAL_TABLE_COLS.liked_by_users.name(), liked));
			socialTbl.update(condDoc, setObj);
			
			response.setStatus(HttpServletResponse.SC_OK);
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}


}
