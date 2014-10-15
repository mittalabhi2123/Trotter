package com.trotter.server.servlet;

import java.io.IOException;

import javax.net.ssl.SSLEngineResult.Status;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpUtils;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.trotter.common.ManageConnection;
import com.trotter.common.MongoDBStructure;
import com.trotter.common.Utility;

@WebServlet("/updateTravelBio")
public class UpdateTravelBioServlet extends HttpServlet {

    public UpdateTravelBioServlet() {
    }

	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			if (Utility.isNullEmpty(request.getParameter("data"))
					|| Utility.isNullEmpty(request.getParameter("id"))) {
				System.out.println("Empty request found...:(");
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Invalid/No request received");
				System.out.println("Return invalid request params.");
				return;
			}
			String travelBio = request.getParameter("data");
			String accessToken = request.getParameter("id");
			System.out.println("Travel Bio:" + travelBio);
			System.out.println("accessToken:" + accessToken);
			//TODO validations
			DB mongoDB = ManageConnection.getDBConnection();
			DBCollection userTblCol = mongoDB.getCollection(MongoDBStructure.USER_TBL);
			BasicDBObject condDoc = new BasicDBObject(MongoDBStructure.USER_TABLE_COLS.fb_access_token.name(), accessToken);
			BasicDBObject setObj = new BasicDBObject().append(Utility.MongoQueryHandles.$set.name(), 
					new BasicDBObject().append(MongoDBStructure.USER_TABLE_COLS.travel_bio.name(), travelBio));
			userTblCol.update(condDoc, setObj);
			response.setStatus(HttpServletResponse.SC_OK);
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}


}
