package com.trotter.server.servlet;

import java.io.IOException;

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
import com.mongodb.DBObject;
import com.trotter.common.ManageConnection;
import com.trotter.common.MongoDBStructure;
import com.trotter.common.Utility;
import com.trotter.server.servlet.functions.TripFunctions;
import com.trotter.server.servlet.functions.UserFunctions;

@WebServlet("/fetchTripChatMembers")
public class FetchTripChatMembers extends HttpServlet {

    public FetchTripChatMembers() {
    }

	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			if (Utility.isNullEmpty(request.getParameter("trip1"))) {
				System.out.println("Trip1 request param not found");
				throw new ServletException("Trip1 request param not found"); 
			}
			System.out.println(request.getParameter("trip1"));
			System.out.println(request.getParameter("trip2"));
			String trip1Id = request.getParameter("trip1");
			String trip2Id = request.getParameter("trip2");
			//TODO validations
			DB mongoDB = ManageConnection.getDBConnection();
			TripFunctions tripFunction = new TripFunctions();
			UserFunctions userFunctions = new UserFunctions();
	    	DBCollection tripTbl = mongoDB.getCollection(MongoDBStructure.TRIP_TBL);
	    	JSONObject dataObj = new JSONObject();
    		BasicDBObject tripInQuery = new BasicDBObject();
			tripInQuery.put(MongoDBStructure.TRIP_TABLE_COLS._id.name(), new ObjectId(trip1Id));
			DBObject dbObject = tripTbl.findOne(tripInQuery);
		    if (dbObject == null) {
		    	JSONObject userJsonObj = userFunctions.fetchUserById(mongoDB, new ObjectId(trip1Id));
		    	if (userJsonObj == null) {
		    		response.setContentType("application/text");
				    response.getWriter().write("Trip/User doesn't exists!!!");
			    	response.setStatus(HttpServletResponse.SC_NO_CONTENT);
			    	return;
		    	}
		    	dataObj.put("trip1", new JSONArray(userJsonObj));
		    } else {
			    dataObj.put("trip1", tripFunction.fetchUserListForTrip(mongoDB, dbObject));		    	
		    }
	    	
		    // fetch for trip2
		    if (!Utility.isNullEmpty(trip2Id)) {
		    	tripInQuery = new BasicDBObject();
				tripInQuery.put(MongoDBStructure.TRIP_TABLE_COLS._id.name(), new ObjectId(trip2Id));
				dbObject = tripTbl.findOne(tripInQuery);
		    	if (dbObject == null) {
		    		JSONObject userJsonObj = userFunctions.fetchUserById(mongoDB, new ObjectId(trip2Id));
			    	if (userJsonObj == null) {
			    		response.setContentType("application/text");
					    response.getWriter().write("Trip/User doesn't exists!!!");
				    	response.setStatus(HttpServletResponse.SC_NO_CONTENT);
				    	return;
			    	}
				    dataObj.put("trip2", new JSONArray(userJsonObj));
			    } else {
			    	dataObj.put("trip2", tripFunction.fetchUserListForTrip(mongoDB, dbObject));
			    }
		    }
	    	response.setContentType("application/json");
		    response.getWriter().write(dataObj.toString());
	    	response.setStatus(HttpServletResponse.SC_OK);
		} catch (Exception e) {
			response.setContentType("application/text");
		    response.getWriter().write(e.getMessage());
	    	response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			e.printStackTrace();
	    	return;
		}
	}

	private void createTripJsonObj(DBObject tripDbObj, JSONObject jsonTripObj) throws JSONException {
		for (MongoDBStructure.TRIP_TABLE_COLS tripColName : MongoDBStructure.TRIP_TABLE_COLS.values()) {
			if (tripDbObj.containsField(tripColName.name())) {
				jsonTripObj.put(tripColName.name(), tripDbObj.get(tripColName.name()));
			}
		}
	}


}
