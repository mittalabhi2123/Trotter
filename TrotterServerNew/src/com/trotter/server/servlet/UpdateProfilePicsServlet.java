package com.trotter.server.servlet;

import java.io.IOException;
import java.util.ArrayList;
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

@WebServlet("/updateProfilePics")
public class UpdateProfilePicsServlet extends HttpServlet {

    public UpdateProfilePicsServlet() {
    }

	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			if (Utility.isNullEmpty(request.getParameter("pics"))
					|| Utility.isNullEmpty(request.getParameter("id"))) {
				System.out.println("Empty request found...:(");
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Invalid/No request received");
				return;
			}
			String picUrls = request.getParameter("pics");
			String travelBio = request.getParameter("bio");
			String fbId = request.getParameter("id");
			String city = request.getParameter("city");
			String state = request.getParameter("state");
			String country = request.getParameter("country");
			String gcm = request.getParameter("gcm");
			System.out.println("pic-"+picUrls);
			System.out.println("travelBio-"+travelBio);
			System.out.println("fbId-"+fbId);
			System.out.println("city-"+city);
			System.out.println("state"+state);
			System.out.println("country-"+country);
			System.out.println("gcmRegId-"+gcm);
			travelBio = Utility.isNullEmpty(travelBio) ? "" : travelBio;
			city = Utility.isNullEmpty(city) ? "" : city;
			state = Utility.isNullEmpty(state) ? "" : state;
			country = Utility.isNullEmpty(country) ? "" : country;
			gcm = Utility.isNullEmpty(gcm) ? "" : gcm;
			//TODO validations
			DB mongoDB = ManageConnection.getDBConnection();
			DBCollection userTblCol = mongoDB.getCollection(MongoDBStructure.USER_TBL);
			BasicDBObject condDoc = new BasicDBObject(MongoDBStructure.USER_TABLE_COLS.fb_id.name(), fbId);
			List<BasicDBObject> picUrlList = new ArrayList<>();
			int count = 1;
			for (String picUrl : picUrls.split(","))
				picUrlList.add(new BasicDBObject("pic" + (count++), picUrl));
			BasicDBObject setObj = new BasicDBObject().append(Utility.MongoQueryHandles.$set.name(), 
					new BasicDBObject().append(MongoDBStructure.USER_TABLE_COLS.pictures.name(), picUrlList)
					.append(MongoDBStructure.USER_TABLE_COLS.travel_bio.name(), travelBio)
					.append(MongoDBStructure.USER_TABLE_COLS.home_city.name(), city)
					.append(MongoDBStructure.USER_TABLE_COLS.home_state.name(), state)
					.append(MongoDBStructure.USER_TABLE_COLS.home_country.name(), country)
					.append(MongoDBStructure.USER_TABLE_COLS.gcm_reg_id.name(), gcm));
			userTblCol.update(condDoc, setObj);
			
			response.setStatus(HttpServletResponse.SC_OK);
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}


}
