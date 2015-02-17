package com.trotter.server.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.trotter.common.ManageConnection;
import com.trotter.common.MongoDBStructure;
import com.trotter.common.Utility;
import static com.trotter.common.MongoDBStructure.SOCIAL_TABLE_COLS;
import static com.trotter.common.MongoDBStructure.USER_SOCIAL_TABLE_COLS;
import static com.trotter.common.MongoDBStructure.MISSION_SOCIAL_TABLE_COLS;

@WebServlet("/updateSocialPic")
public class UploadSocialPicServlet extends HttpServlet {

    public UploadSocialPicServlet() {
    }

	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			if (Utility.isNullEmpty(request.getParameter("pics"))) {
				System.out.println("Empty request found...:( Pics missing");
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Invalid/No request received");
				return;
			}
			if (Utility.isNullEmpty(request.getParameter("id"))) {
				System.out.println("Empty request found...:( User Id missing");
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Invalid/No request received");
				return;
			}
			String userId = request.getParameter("id");
			String pics = request.getParameter("pics");
			String caption = request.getParameter("caption");
			String mission = request.getParameter("mission");
			String city = request.getParameter("city");
			String state = request.getParameter("state");
			String country = request.getParameter("country");
			String eventId = request.getParameter("event");
			
			caption = Utility.isNullEmpty(caption) ? "" : caption;
			city = Utility.isNullEmpty(city) ? "" : city;
			state = Utility.isNullEmpty(state) ? "" : state;
			country = Utility.isNullEmpty(country) ? "" : country;
			List<String> missionList = !Utility.isNullEmpty(mission) ? Arrays.asList(mission.split(",")) : new ArrayList<String>();
			List<String> picUrlList = Arrays.asList(pics.split(","));
			//TODO validations
			DB mongoDB = ManageConnection.getDBConnection();
			System.out.println(System.currentTimeMillis());
			DBCollection socialTblCol = mongoDB.getCollection(MongoDBStructure.SOCIAL_TBL);
			BasicDBObject socialDoc = new BasicDBObject(SOCIAL_TABLE_COLS.user_id.name(), userId)
				.append(SOCIAL_TABLE_COLS.caption.name(), caption).append(SOCIAL_TABLE_COLS.city.name(), city)
				.append(SOCIAL_TABLE_COLS.state.name(), state).append(SOCIAL_TABLE_COLS.country.name(), country)
				.append(SOCIAL_TABLE_COLS.mission.name(), missionList).append(SOCIAL_TABLE_COLS.pic.name(), picUrlList)
				.append(SOCIAL_TABLE_COLS.upload_date.name(), System.currentTimeMillis()).append(SOCIAL_TABLE_COLS.event.name(), eventId);
			socialTblCol.insert(socialDoc);
			DBCollection userSocialTbl = mongoDB.getCollection(userId + "_" + MongoDBStructure.SOCIAL_TBL);
			BasicDBObject userSocialDoc = new BasicDBObject(USER_SOCIAL_TABLE_COLS.social_id.name(), socialDoc.get(SOCIAL_TABLE_COLS._id.name()));
			userSocialTbl.insert(userSocialDoc);
			for (String missionStr : missionList) {
				DBCollection missionTbl = mongoDB.getCollection(missionStr + "_" + MongoDBStructure.SOCIAL_TBL);
				BasicDBObject missionSocialDoc = new BasicDBObject(MISSION_SOCIAL_TABLE_COLS.social_id.name(), socialDoc.get(SOCIAL_TABLE_COLS._id.name()));
				missionTbl.insert(missionSocialDoc);
			}
			response.setStatus(HttpServletResponse.SC_OK);
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}


}
