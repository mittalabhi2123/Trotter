package com.trotter.server.servlet;

import static com.trotter.common.MongoDBStructure.TRIP_RESPONSE_TBL;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.trotter.common.Const.ResultType;
import com.trotter.common.Const.TripResponse;
import com.trotter.common.Const.rejectRequestParam;
import com.trotter.common.ManageConnection;
import com.trotter.common.MongoDBStructure;
import com.trotter.common.MongoDBStructure.TRIP_RESPONSE_TABLE_COLS;
import com.trotter.common.Utility;

@WebServlet("/acceptTrip")
public class AcceptTripServlet extends HttpServlet {

    public AcceptTripServlet() {
    }

	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			if (Utility.isNullEmpty(request.getParameter(rejectRequestParam.tripId.name()))) {
				System.out.println("Empty trip_id request found...:(");
				throw new ServletException("Empty trip_id request found...:("); 
			}
			if (Utility.isNullEmpty(request.getParameter(rejectRequestParam.resultId.name()))) {
				System.out.println("Empty result request found...:(");
				throw new ServletException("Empty result request found...:("); 
			}
			if (Utility.isNullEmpty(request.getParameter(rejectRequestParam.resultType.name()))) {
				System.out.println("Empty resultType request found...:(");
				throw new ServletException("Empty resultType request found...:("); 
			}
			if (Utility.isNullEmpty(request.getParameter(rejectRequestParam.outwardTrip.name()))) {
				System.out.println("Empty outwardTrip request found...:(");
				throw new ServletException("Empty outwardTrip request found...:("); 
			}
			String tripId = request.getParameter(rejectRequestParam.tripId.name());
			String resultId = request.getParameter(rejectRequestParam.resultId.name());
			String resultType = request.getParameter(rejectRequestParam.resultType.name());
			// defines whether traveler accepts a co-traveler/local, or local accepts a visitor
			String outwardTrip = request.getParameter(rejectRequestParam.outwardTrip.name());
			System.out.println(tripId);
			System.out.println(resultId);
			System.out.println(resultType);
			System.out.println(outwardTrip);
			System.out.println(request.getParameter(rejectRequestParam.tableName.name()));
			DB mongoDB = ManageConnection.getDBConnection();
			DBCollection responseTbl = mongoDB.getCollection(TRIP_RESPONSE_TBL);
			BasicDBObject doc = new BasicDBObject();
			doc.append(TRIP_RESPONSE_TABLE_COLS.trip_id.name(), tripId);
			doc.append(TRIP_RESPONSE_TABLE_COLS.result_id.name(), resultId);
			doc.append(TRIP_RESPONSE_TABLE_COLS.result_type.name(), resultType);
			doc.append(TRIP_RESPONSE_TABLE_COLS.outward_trip.name(), Boolean.parseBoolean(outwardTrip));
			doc.append(TRIP_RESPONSE_TABLE_COLS.response.name(), TripResponse.accept.name());
			responseTbl.insert(doc);
			response.setStatus(HttpServletResponse.SC_OK);
			BasicDBObject searchQuery = new BasicDBObject();
			searchQuery.append(MongoDBStructure.TRIP_RESPONSE_TABLE_COLS.trip_id.name(), new ObjectId(resultId))
					.append(MongoDBStructure.TRIP_RESPONSE_TABLE_COLS.result_id.name(), tripId)
					.append(MongoDBStructure.TRIP_RESPONSE_TABLE_COLS.response.name(), TripResponse.accept.name());
			DBObject tripDb = responseTbl.findOne(searchQuery);
			if (tripDb != null) {
				response.getWriter().write(request.getParameter(rejectRequestParam.tableName.name()));
			}
		} catch (Exception e) {
			response.setContentType("application/text");
		    response.getWriter().write(e.getMessage());
	    	response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			e.printStackTrace();
	    	return;
		}
	}


}
