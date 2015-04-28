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
import com.trotter.server.servlet.functions.UserFunctions;

@WebServlet("/followUser")
public class FollowUserServlet extends HttpServlet {

    public FollowUserServlet() {
    }

	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			if (Utility.isNullEmpty(request.getParameter(Const.FollowUserRequestParam.followingUserId.name()))
					|| Utility.isNullEmpty(request.getParameter(Const.FollowUserRequestParam.userId.name()))) {
				System.out.println("Empty request found...:(");
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Invalid/No request received");
				return;
			}
			String followingUserId = request.getParameter(Const.FollowUserRequestParam.followingUserId.name());
			String userId = request.getParameter(Const.FollowUserRequestParam.userId.name());
			System.out.println("Like Pic Servlet---SocialId:"+followingUserId+", userId:"+userId);
			//TODO validations
			DB mongoDB = ManageConnection.getDBConnection();
			DBCollection userTbl = mongoDB.getCollection(MongoDBStructure.USER_TBL);
			JSONObject userTblObj = new UserFunctions().fetchUserById(mongoDB, new ObjectId(userId));
			String following = userTblObj.getString(MongoDBStructure.USER_TABLE_COLS.following.name());
			if (Utility.isNullEmpty(following)) {
				following = followingUserId;
			} else {
				following = following.concat(",").concat(followingUserId);
			}
			BasicDBObject condDoc = new BasicDBObject(MongoDBStructure.USER_TABLE_COLS._id.name(), new ObjectId(userId));
			BasicDBObject setObj = new BasicDBObject().append(Utility.MongoQueryHandles.$set.name(), 
					new BasicDBObject().append(MongoDBStructure.USER_TABLE_COLS.following.name(), following));
			userTbl.update(condDoc, setObj);

			userTblObj = new UserFunctions().fetchUserById(mongoDB, new ObjectId(followingUserId));
			String follower = userTblObj.getString(MongoDBStructure.USER_TABLE_COLS.follower.name());
			if (Utility.isNullEmpty(follower)) {
				follower = userId;
			} else {
				follower = follower.concat(",").concat(userId);
			}
			condDoc = new BasicDBObject(MongoDBStructure.USER_TABLE_COLS._id.name(), new ObjectId(followingUserId));
			setObj = new BasicDBObject().append(Utility.MongoQueryHandles.$set.name(), 
					new BasicDBObject().append(MongoDBStructure.USER_TABLE_COLS.follower.name(), follower));
			userTbl.update(condDoc, setObj);
			
			response.setStatus(HttpServletResponse.SC_OK);
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}


}
