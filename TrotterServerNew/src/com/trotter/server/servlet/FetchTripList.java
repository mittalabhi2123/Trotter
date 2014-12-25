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

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.trotter.common.ManageConnection;
import com.trotter.common.MongoDBStructure;
import com.trotter.common.Utility;
import com.trotter.server.servlet.functions.TripFunctions;

@WebServlet("/fetchTripList")
public class FetchTripList extends HttpServlet {

    public FetchTripList() {
    }

	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		TripFunctions tripFunc = new TripFunctions();
		try {
			if (Utility.isNullEmpty(request.getParameter("id"))) {
				System.out.println("Empty request found...:(");
				throw new ServletException("Invalid/No request received"); 
			}
			System.out.println(request.getParameter("id"));
			String id = request.getParameter("id");
			//TODO validations
			DB mongoDB = ManageConnection.getDBConnection();
			DBCollection userTbl = mongoDB.getCollection(MongoDBStructure.USER_TBL);
			DBCollection tripTbl = mongoDB.getCollection(MongoDBStructure.TRIP_TBL);
			BasicDBObject inQuery = new BasicDBObject();
			inQuery.put(MongoDBStructure.USER_TABLE_COLS._id.name(), new ObjectId(id));
		    DBCursor cursor = userTbl.find(inQuery);
		    if (cursor.count() <= 0) {
		    	response.setContentType("application/text");
			    response.getWriter().write("User doesn't exists!!!");
		    	response.setStatus(HttpServletResponse.SC_NO_CONTENT);
		    	return;
		    }
	    	DBObject dbObject = cursor.next();
	    	BasicDBList mineTrip = (BasicDBList) dbObject.get(MongoDBStructure.USER_TABLE_COLS.own_trips.name());
	    	
	    	if (mineTrip == null || mineTrip.size() == 0) {
	    		response.setContentType("application/text");
			    response.getWriter().write("No trip exists!!!");
		    	response.setStatus(HttpServletResponse.SC_NO_CONTENT);
		    	return;
	    	}
	    	List<JSONObject> tripList = new ArrayList<>();
	    	JSONObject jsonTripObj = null;
	    	BasicDBObject inTripQuery = null;
	    	for (int i = 0 ; i < mineTrip.size() ; i++) {
	    		if (mineTrip.get(i) == null || mineTrip.get(i).toString().equals(""))
	    			continue;
	    		ObjectId tripId = ((ObjectId)mineTrip.get(i));
	    		inTripQuery = new BasicDBObject();
	    		inTripQuery.put(MongoDBStructure.TRIP_TABLE_COLS._id.name(), tripId);
	    		DBObject tripObj = tripTbl.findOne(inTripQuery);
	    		if (tripObj != null) {
	    			jsonTripObj = tripFunc.createTripJsonObj(tripObj);
	    			jsonTripObj.put("own", true);
	    			tripList.add(jsonTripObj);
	    		}
	    	}
	    	Collections.sort(tripList, new Comparator<JSONObject>() {

				@Override
				public int compare(JSONObject o1, JSONObject o2) {
					try {
						return o1.getInt(MongoDBStructure.TRIP_TABLE_COLS.start_date.name())
								- o2.getInt(MongoDBStructure.TRIP_TABLE_COLS.start_date.name());
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
					return 0;
				}
			});
	    	JSONArray jsonTripList = new JSONArray(tripList);
	    	response.setContentType("application/json");
		    response.getWriter().write(jsonTripList.toString());
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
