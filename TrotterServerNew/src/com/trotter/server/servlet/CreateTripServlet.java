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

import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;
import com.trotter.common.ManageConnection;
import com.trotter.common.MongoDBStructure;
import com.trotter.common.Utility;
import com.trotter.server.servlet.functions.UserFunctions;

@WebServlet("/createTrip")
public class CreateTripServlet extends HttpServlet {

    public CreateTripServlet() {
    }

	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			if (Utility.isNullEmpty(request.getParameter("data"))) {
				System.out.println("Empty request found...:(");
				throw new ServletException("Invalid/No request received"); 
			}
			System.out.println(request.getParameter("data"));
			JSONObject requestObj = new JSONObject(request.getParameter("data"));
			if (!requestObj.has(MongoDBStructure.TRIP_TABLE_COLS.user_id.name())) {
				response.setContentType("application/text");
			    response.getWriter().write("User id is not specified for the trip");
		    	response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		    	return;
			}
			long duration = requestObj.getLong(MongoDBStructure.TRIP_TABLE_COLS.end_date.name())
					- requestObj.getLong(MongoDBStructure.TRIP_TABLE_COLS.start_date.name());
			requestObj.put(MongoDBStructure.TRIP_TABLE_COLS.duration.name(), duration);
			//TODO validations
			DB mongoDB = ManageConnection.getDBConnection();
			DBCollection tripTbl = mongoDB.getCollection(MongoDBStructure.TRIP_TBL);
			
			String[] missionArr = requestObj.has(MongoDBStructure.TRIP_TABLE_COLS.mission.name())
					? requestObj.getString(MongoDBStructure.TRIP_TABLE_COLS.mission.name()).split(",") : null;
			List<String> missionList = missionArr != null ? Arrays.asList(missionArr) : new ArrayList<String>();
			String[] groupMembersFbIds = requestObj.has(MongoDBStructure.TRIP_TABLE_COLS.group_members.name())
					? requestObj.getString(MongoDBStructure.TRIP_TABLE_COLS.group_members.name()).split(",") : null;
			JSONArray groupUserArray = new UserFunctions().fetchUserByFbIdList(mongoDB, groupMembersFbIds);
			
			List<String> groupMembersList = new ArrayList<>();
			if (groupUserArray != null) {
				for(int i = 0 ; i < groupUserArray.length() ; i++) {
					groupMembersList.add(((JSONObject)groupUserArray.get(i)).getString(MongoDBStructure.USER_TABLE_COLS._id.name()));
				}
			}
			BasicDBObject doc = new BasicDBObject();
			for (MongoDBStructure.TRIP_TABLE_COLS col : MongoDBStructure.TRIP_TABLE_COLS.values()) {
				if (MongoDBStructure.TRIP_TABLE_COLS.mission.name().equals(col.name()))
					doc.append(col.name(), missionList);
				else if (MongoDBStructure.TRIP_TABLE_COLS.group_members.name().equals(col.name()))
					doc.append(col.name(), groupMembersList);
				else if (requestObj.has(col.name()))
					doc.append(col.name(), requestObj.get(col.name()));
			}
			if (!requestObj.has(MongoDBStructure.TRIP_TABLE_COLS._id.name())
					&& Utility.isNullEmpty(requestObj.getString(MongoDBStructure.TRIP_TABLE_COLS._id.name())))
				tripTbl.insert(doc); // insert the trip
			else {			 
				// update the trip
				BasicDBObject searchQuery = new BasicDBObject().append(MongoDBStructure.TRIP_TABLE_COLS._id.name(),
						new ObjectId((String)requestObj.get(MongoDBStructure.TRIP_TABLE_COLS._id.name())));
			 
				tripTbl.update(searchQuery, doc);
			}
			
			@SuppressWarnings("deprecation")
			ObjectId tripId = null;
			Object idObj = doc.get(MongoDBStructure.TRIP_TABLE_COLS._id.name());
			tripId = idObj instanceof ObjectId ? (ObjectId) idObj : new ObjectId((String) idObj);
			
			//Updating userTbl with this tripId
			String tripUserId = requestObj.getString(MongoDBStructure.TRIP_TABLE_COLS.user_id.name());
			groupMembersList.add(tripUserId);
			DBCollection userTbl = mongoDB.getCollection(MongoDBStructure.USER_TBL);
			BasicDBObject inQuery =  null;
			DBCursor cursor = null;
			DBObject userObj = null;
			List<ObjectId> myTrips = null;
			BasicDBObject cmd = null;
			for (String groupMember : groupMembersList) {
				System.out.println(groupMember);
				inQuery = new BasicDBObject();
				inQuery.put(MongoDBStructure.USER_TABLE_COLS._id.name(), new ObjectId(groupMember));
			    cursor = userTbl.find(inQuery);
			    if (cursor.hasNext()) {
			    	userObj = cursor.next();
			    	if (userObj.containsKey(MongoDBStructure.USER_TABLE_COLS.own_trips.name())){
			    		Object obj = userObj.get(MongoDBStructure.USER_TABLE_COLS.own_trips.name());
			    		if (obj == null || Utility.isNullEmpty(obj.toString()))
			    			myTrips = new ArrayList<>();
			    		else
			    			myTrips = new ArrayList<>((List<ObjectId>) obj);
			    	} else {
			    		myTrips = new ArrayList<>();
			    	}
			    	myTrips.add(tripId);
			    	System.out.println(myTrips);
			    	cmd = new BasicDBObject().append(Utility.MongoQueryHandles.$set.name(),
							new BasicDBObject(MongoDBStructure.USER_TABLE_COLS.own_trips.name(), myTrips));
					userTbl.update(new BasicDBObject().append(MongoDBStructure.USER_TABLE_COLS._id.name(), new ObjectId(groupMember)), cmd);
					JSONObject responseObj = new JSONObject();
			    	responseObj.put("id", tripId);
				    System.out.println("ID::" + responseObj.getString("id"));
				    response.getWriter().write(responseObj.toString());
			    }
			}
			response.setStatus(HttpServletResponse.SC_OK);
		} catch (Exception e) {
			response.setContentType("application/text");
		    response.getWriter().write(e.getMessage());
	    	response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			e.printStackTrace();
	    	return;
		}
	}


}
