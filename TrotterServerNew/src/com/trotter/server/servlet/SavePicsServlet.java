package com.trotter.server.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bson.types.ObjectId;
import org.json.JSONObject;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.trotter.common.Const;
import com.trotter.common.ManageConnection;
import com.trotter.common.MongoDBStructure;
import com.trotter.common.Utility;
import com.trotter.server.servlet.functions.UserFunctions;

@WebServlet("/savePics")
public class SavePicsServlet extends HttpServlet {

    public SavePicsServlet() {
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
			//TODO validations
			DB mongoDB = ManageConnection.getDBConnection();
			DBCollection userTblCol = mongoDB.getCollection(MongoDBStructure.USER_TBL);
			JSONObject userObj = new UserFunctions().fetchUserById(mongoDB, new ObjectId(userId));
			String saved = "";
			if (userObj.has(MongoDBStructure.USER_TABLE_COLS.saved_pics.name()))
				saved = userObj.getString(MongoDBStructure.USER_TABLE_COLS.saved_pics.name());
			saved = saved + "," + socialId;
			BasicDBObject condDoc = new BasicDBObject(MongoDBStructure.USER_TABLE_COLS._id.name(), new ObjectId(userId));
			BasicDBObject setObj = new BasicDBObject().append(Utility.MongoQueryHandles.$set.name(), 
					new BasicDBObject().append(MongoDBStructure.USER_TABLE_COLS.saved_pics.name(), saved));
			userTblCol.update(condDoc, setObj);
			
			response.setStatus(HttpServletResponse.SC_OK);
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}


}
