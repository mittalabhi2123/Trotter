package com.trotter.server.servlet;

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

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.trotter.common.ManageConnection;
import com.trotter.common.MongoDBStructure;
import com.trotter.common.MongoDBStructure.TRIP_TABLE_COLS;
import com.trotter.common.MongoDBStructure.USER_TABLE_COLS;
import com.trotter.common.Utility;
import com.trotter.server.servlet.functions.TripFunctions;
import com.trotter.server.servlet.functions.UserFunctions;

@WebServlet("/searchTravellersToMyCity")
public class SearchTravellersToMyCityResults extends HttpServlet {

    public SearchTravellersToMyCityResults() {
    }

	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			if (Utility.isNullEmpty(request.getParameter("id"))) {
				System.out.println("Empty request found...:(");
				response.setContentType("application/text");
			    response.getWriter().write("Invalid/No request received");
		    	response.setStatus(HttpServletResponse.SC_NO_CONTENT);
		    	return;
			}
			System.out.println(request.getParameter("id"));
			String id = request.getParameter("id");
			//TODO validations
			UserFunctions userFunc = new UserFunctions();
			DB mongoDB = ManageConnection.getDBConnection();
			JSONObject userJsonObj = userFunc.fetchUserById(mongoDB, new ObjectId(id));
			DBCollection userTbl = mongoDB.getCollection(MongoDBStructure.USER_TBL);
			DBCollection tripTbl = mongoDB.getCollection(MongoDBStructure.TRIP_TBL);
			BasicDBObject inQuery = new BasicDBObject();
		    if (userJsonObj == null) {
		    	response.setContentType("application/text");
			    response.getWriter().write("Invalid user id in request");
		    	response.setStatus(HttpServletResponse.SC_NO_CONTENT);
		    	return;
		    }
			inQuery = new BasicDBObject();
		    inQuery.put(TRIP_TABLE_COLS.origin_city.name(), userJsonObj.getString(USER_TABLE_COLS.home_city.name()));
		    inQuery.put(TRIP_TABLE_COLS.origin_state.name(), userJsonObj.getString(USER_TABLE_COLS.home_state.name()));
		    inQuery.put(TRIP_TABLE_COLS.origin_country.name(), userJsonObj.getString(USER_TABLE_COLS.home_country.name()));
		    inQuery.put(TRIP_TABLE_COLS.start_date.name(), new BasicDBObject("$gt", (System.currentTimeMillis())));
		    DBCursor tripTblLst = tripTbl.find(inQuery);
		    List<JSONObject> cotravellerList = new ArrayList<>();
		    TripFunctions tripFunc = new TripFunctions();
		    while(tripTblLst.hasNext()) {
		    	cotravellerList.add(tripFunc.createTripJsonObj(tripTblLst.next(), userFunc, mongoDB));
		    }
		    Collections.sort(cotravellerList, new Comparator<JSONObject>() {

				@Override
				public int compare(JSONObject o1, JSONObject o2) {
					try {
						return o1.getInt(MongoDBStructure.TRIP_TABLE_COLS.start_date.name())
								- o2.getInt(MongoDBStructure.TRIP_TABLE_COLS.start_date.name());
					} catch (JSONException e) {
						e.printStackTrace();
					} 
					return 0;
				}
			});
		    // TODO match mission, rejected and selected trips
	    	response.setContentType("application/json");
		    response.getWriter().write(new JSONArray(cotravellerList).toString());
	    	response.setStatus(HttpServletResponse.SC_OK);
		    return;
		} catch (Exception e) {
			response.setContentType("application/text");
		    response.getWriter().write(e.getMessage());
	    	response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			e.printStackTrace();
	    	return;
		}
	}
}
