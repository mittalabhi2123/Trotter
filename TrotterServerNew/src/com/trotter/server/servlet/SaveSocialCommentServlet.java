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
import static com.trotter.common.Const.SaveSocialCommentRequestParam;
import static com.trotter.common.MongoDBStructure.SOCIAL_TABLE_COLS;

@WebServlet("/saveComments")
public class SaveSocialCommentServlet extends HttpServlet {

    public SaveSocialCommentServlet() {
    }

	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			String socialId = request.getParameter(SaveSocialCommentRequestParam.socialId.name());
			String userId = request.getParameter(SaveSocialCommentRequestParam.userId.name());
			String comment = request.getParameter(SaveSocialCommentRequestParam.comment.name());
			System.out.println("Like Pic Servlet---SocialId:"+socialId+", userId:"+userId+", comment:"+comment);
			if (Utility.isNullEmpty(socialId) || Utility.isNullEmpty(userId) || Utility.isNullEmpty(comment)) {
				System.out.println("Empty request found...:(");
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Invalid/No request received");
				return;
			}
			//TODO validations
			DB mongoDB = ManageConnection.getDBConnection();
			DBCollection socialTbl = mongoDB.getCollection(MongoDBStructure.SOCIAL_TBL);
			JSONObject socialObj = new SocialFunctions().fetchById(mongoDB, new ObjectId(socialId));
			String commentStr = userId.concat("~").concat(comment);
			String comments = "";
			if (socialObj.has(SOCIAL_TABLE_COLS.comments.name()))
				comments = socialObj.getString(SOCIAL_TABLE_COLS.comments.name());
			if (Utility.isNullEmpty(comments))
				comments = commentStr;
			else
				comments = comments + "," + commentStr;
			BasicDBObject condDoc = new BasicDBObject(SOCIAL_TABLE_COLS._id.name(), new ObjectId(socialId));
			BasicDBObject setObj = new BasicDBObject().append(Utility.MongoQueryHandles.$set.name(), 
					new BasicDBObject().append(SOCIAL_TABLE_COLS.comments.name(), comments));
			socialTbl.update(condDoc, setObj);
			
			response.setStatus(HttpServletResponse.SC_OK);
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}


}
