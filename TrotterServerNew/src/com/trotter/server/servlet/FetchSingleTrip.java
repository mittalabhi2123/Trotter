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
import com.trotter.server.servlet.functions.UserFunctions;

@WebServlet("/fetchTrip")
public class FetchSingleTrip extends HttpServlet {

    public FetchSingleTrip() {
    }

	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
			BasicDBObject tripInQuery = new BasicDBObject();
			tripInQuery.put(MongoDBStructure.TRIP_TABLE_COLS._id.name(), new ObjectId(id));
		    DBCursor cursor = tripTbl.find(tripInQuery);
		    if (cursor.count() <= 0) {
		    	response.setContentType("application/text");
			    response.getWriter().write("Trip doesn't exists!!!");
		    	response.setStatus(HttpServletResponse.SC_NO_CONTENT);
		    	return;
		    }
	    	DBObject dbObject = cursor.next();
	    	
	    	List<JSONObject> userList = new ArrayList<>();
	    	JSONObject jsonTripObj = new JSONObject();
	    	for (MongoDBStructure.TRIP_TABLE_COLS colName : MongoDBStructure.TRIP_TABLE_COLS.values()) {
	    		if (dbObject.containsField(colName.name()))
	    			jsonTripObj.put(colName.name(), dbObject.get(colName.name()));
	    	}
	    	if (jsonTripObj.getBoolean(MongoDBStructure.TRIP_TABLE_COLS.is_individual.name())) {
	    		// return response, if its individual trip
	    		response.setContentType("application/json");
			    response.getWriter().write(jsonTripObj.toString());
		    	response.setStatus(HttpServletResponse.SC_OK);
		    	return;
	    	}
	    	// fetch group member list
	    	BasicDBList groupMembers = (BasicDBList) dbObject.get(MongoDBStructure.TRIP_TABLE_COLS.group_members.name());
	    	if (groupMembers == null || groupMembers.size() == 0) {
	    		response.setContentType("application/json");
			    response.getWriter().write(jsonTripObj.toString());
		    	response.setStatus(HttpServletResponse.SC_OK);
		    	return;
	    	}
	    	//Add user objects for group members
	    	UserFunctions userFunctions = new UserFunctions();
	    	for (int i = 0 ; i < groupMembers.size() ; i++) {
	    		if (groupMembers.get(i) == null || groupMembers.get(i).toString().equals(""))
	    			continue;
	    		ObjectId userId = ((ObjectId)groupMembers.get(i));
	    		JSONObject userObj = userFunctions.fetchUserById(mongoDB, userId);
	    		userList.add(userObj);
	    	}
	    	jsonTripObj.put(MongoDBStructure.TRIP_TABLE_COLS.group_members.name(), userList);
    		
	    	JSONArray jsonTripList = new JSONArray(jsonTripObj);
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

	private void createTripJsonObj(DBObject tripDbObj, JSONObject jsonTripObj) throws JSONException {
		for (MongoDBStructure.TRIP_TABLE_COLS tripColName : MongoDBStructure.TRIP_TABLE_COLS.values()) {
			if (tripDbObj.containsField(tripColName.name())) {
				jsonTripObj.put(tripColName.name(), tripDbObj.get(tripColName.name()));
			}
		}
	}


}
