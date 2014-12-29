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
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.trotter.common.Const;
import com.trotter.common.ManageConnection;
import com.trotter.common.MongoDBStructure;
import com.trotter.common.MongoDBStructure.TRIP_TABLE_COLS;
import com.trotter.common.MongoDBStructure.USER_TABLE_COLS;
import com.trotter.common.Utility;
import com.trotter.server.servlet.functions.TripFunctions;
import com.trotter.server.servlet.functions.UserFunctions;

@WebServlet("/searchTrips")
public class TripSearchResults extends HttpServlet {

    public TripSearchResults() {
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
			long toleranceInMillis = 0;
			if (!Utility.isNullEmpty(request.getParameter("tolerance"))) {
				toleranceInMillis = Long.parseLong(request.getParameter("tolerance")) * 24 * 3600 * 1000;
			}
			Const.SearchGroup isGroup = Const.SearchGroup.valueOf(request.getParameter("search"));
			//TODO validations
			
			DB mongoDB = ManageConnection.getDBConnection();
			DBCollection userTbl = mongoDB.getCollection(MongoDBStructure.USER_TBL);
			DBCollection tripTbl = mongoDB.getCollection(MongoDBStructure.TRIP_TBL);
			BasicDBObject inQuery = new BasicDBObject();
			inQuery.put(MongoDBStructure.TRIP_TABLE_COLS._id.name(), new ObjectId(id));
			DBObject tripDbObject = tripTbl.findOne(inQuery);
		    if (tripDbObject == null) {
		    	response.setContentType("application/text");
			    response.getWriter().write("Invalid trip id in request");
		    	response.setStatus(HttpServletResponse.SC_NO_CONTENT);
		    	return;
		    }
			inQuery = new BasicDBObject();
			inQuery.put(MongoDBStructure.USER_TABLE_COLS._id.name(), new ObjectId((String)tripDbObject.get(MongoDBStructure.TRIP_TABLE_COLS.user_id.name())));
			DBObject userDbObject = userTbl.findOne(inQuery);
		    if (userDbObject == null) {
		    	response.setContentType("application/text");
			    response.getWriter().write("User not found for this trip");
		    	response.setStatus(HttpServletResponse.SC_NO_CONTENT);
		    	return;
		    }
		    inQuery = new BasicDBObject();
		    inQuery.put(TRIP_TABLE_COLS.origin_city.name(), (String)tripDbObject.get(TRIP_TABLE_COLS.origin_city.name()));
		    inQuery.put(TRIP_TABLE_COLS.origin_state.name(), (String)tripDbObject.get(TRIP_TABLE_COLS.origin_state.name()));
		    inQuery.put(TRIP_TABLE_COLS.origin_country.name(), (String)tripDbObject.get(TRIP_TABLE_COLS.origin_country.name()));
		    inQuery.put(TRIP_TABLE_COLS.dest_city.name(), (String)tripDbObject.get(TRIP_TABLE_COLS.dest_city.name()));
		    inQuery.put(TRIP_TABLE_COLS.dest_state.name(), (String)tripDbObject.get(TRIP_TABLE_COLS.dest_state.name()));
		    inQuery.put(TRIP_TABLE_COLS.dest_country.name(), (String)tripDbObject.get(TRIP_TABLE_COLS.dest_country.name()));
		    long tripStartDate = (Long)tripDbObject.get(TRIP_TABLE_COLS.start_date.name());
		    long tripEndDate = (Long)tripDbObject.get(TRIP_TABLE_COLS.end_date.name());
		    inQuery.put(TRIP_TABLE_COLS.start_date.name(), new BasicDBObject("$gte", (tripStartDate - toleranceInMillis)).append("$lte", (tripStartDate + toleranceInMillis)));
		    inQuery.put(TRIP_TABLE_COLS.end_date.name(), new BasicDBObject("$gte", (tripEndDate - toleranceInMillis)).append("$lte", (tripEndDate + toleranceInMillis)));
		    switch(isGroup) {
		    	case individual:
		    		inQuery.put(TRIP_TABLE_COLS.is_individual.name(), true);
		    		break;
		    	case group:
		    		inQuery.put(TRIP_TABLE_COLS.is_individual.name(), false);
		    		break;
		    	case all:
		    	default:
		    		break;
		    }
		    DBCursor tripTblLst = tripTbl.find(inQuery);
		    JSONArray cotravellerJsonArr = new JSONArray();
		    TripFunctions tripFunc = new TripFunctions();
		    UserFunctions userFunc = new UserFunctions();
		    while(tripTblLst.hasNext()) {
		    	cotravellerJsonArr.put(tripFunc.createTripJsonObj(tripTblLst.next(), userFunc, mongoDB));
		    }
		    JSONArray localJsonArr = getLocalCompany(tripDbObject, userTbl, userFunc);
		    JSONObject jsonObject = new JSONObject();
		    jsonObject.put("local", localJsonArr);
		    jsonObject.put("traveller", cotravellerJsonArr);
		    // TODO match mission, rejected and selected trips
	    	response.setContentType("application/json");
		    response.getWriter().write(jsonObject.toString());
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

	private JSONArray getLocalCompany(DBObject tripDbObject, DBCollection userTbl, UserFunctions userFunc) throws JSONException {
		JSONArray localJsonArr = new JSONArray();
		BasicDBObject inQuery = new BasicDBObject();
	    inQuery.put(USER_TABLE_COLS.home_city.name(), (String)tripDbObject.get(TRIP_TABLE_COLS.origin_city.name()));
	    inQuery.put(USER_TABLE_COLS.home_state.name(), (String)tripDbObject.get(TRIP_TABLE_COLS.origin_state.name()));
	    inQuery.put(USER_TABLE_COLS.home_country.name(), (String)tripDbObject.get(TRIP_TABLE_COLS.origin_country.name()));
	    DBCursor userTblLst = userTbl.find(inQuery);
	    while(userTblLst.hasNext()) {
	    	localJsonArr.put(userFunc.createUserJson(userTblLst.next()));
	    }
		return localJsonArr;
	}


}
