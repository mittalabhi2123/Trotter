package com.trotter.server.servlet;

import static com.trotter.common.MongoDBStructure.REJECT_TBL;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.trotter.common.Const.rejectRequestParam;
import com.trotter.common.ManageConnection;
import com.trotter.common.MongoDBStructure.REJECT_TABLE_COLS;
import com.trotter.common.Utility;

@WebServlet("/rejectTrip")
public class RejectTripServlet extends HttpServlet {

    public RejectTripServlet() {
    }

	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			if (Utility.isNullEmpty(request.getParameter(rejectRequestParam.tripId.name()))) {
				System.out.println("Empty trip_id request found...:(");
				throw new ServletException("Invalid/No request received"); 
			}
			if (Utility.isNullEmpty(request.getParameter(rejectRequestParam.resultId.name()))) {
				System.out.println("Empty result request found...:(");
				throw new ServletException("Invalid/No request received"); 
			}
			if (Utility.isNullEmpty(request.getParameter(rejectRequestParam.resultType.name()))) {
				System.out.println("Empty resultType request found...:(");
				throw new ServletException("Invalid/No request received"); 
			}
			String tripId = request.getParameter(rejectRequestParam.tripId.name());
			String resultId = request.getParameter(rejectRequestParam.resultId.name());
			String resultType = request.getParameter(rejectRequestParam.resultType.name());
			System.out.println(tripId);
			System.out.println(resultId);
			System.out.println(resultType);

			DB mongoDB = ManageConnection.getDBConnection();
			DBCollection rejectTbl = mongoDB.getCollection(REJECT_TBL);
			BasicDBObject doc = new BasicDBObject();
			doc.append(REJECT_TABLE_COLS.trip_id.name(), tripId);
			doc.append(REJECT_TABLE_COLS.result_id.name(), resultId);
			doc.append(REJECT_TABLE_COLS.result_type.name(), resultType);
			rejectTbl.insert(doc);
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
