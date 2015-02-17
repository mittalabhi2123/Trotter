package com.trotter.server.servlet;

import static com.trotter.common.MongoDBStructure.EVENTS_TBL;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.trotter.common.Const.ResultType;
import com.trotter.common.Const.TripResponse;
import com.trotter.common.Const.SaveEventsRequestParams;
import com.trotter.common.ManageConnection;
import com.trotter.common.MongoDBStructure.EVENT_TBL_COLS;
import com.trotter.common.Utility;

@WebServlet("/SaveEvents")
public class SaveEventsServlet extends HttpServlet {

    public SaveEventsServlet() {
    }

	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			String eventName = request.getParameter(SaveEventsRequestParams.event_name.name());
			String eventType = request.getParameter(SaveEventsRequestParams.event_type.name());
			String fromDate = request.getParameter(SaveEventsRequestParams.date_from.name());
			String toDate = request.getParameter(SaveEventsRequestParams.date_to.name());
			String remarks = request.getParameter(SaveEventsRequestParams.remarks.name());
			String country = request.getParameter(SaveEventsRequestParams.country.name());
			String state = request.getParameter(SaveEventsRequestParams.state.name());
			String city = request.getParameter(SaveEventsRequestParams.city.name());
			System.out.println(eventName);
			System.out.println(eventType);
			System.out.println(fromDate);
			System.out.println(toDate);
			System.out.println(remarks);
			System.out.println(country);
			System.out.println(state);
			System.out.println(city);
			DB mongoDB = ManageConnection.getDBConnection();
			DBCollection eventTbl = mongoDB.getCollection(EVENTS_TBL);
			BasicDBObject doc = new BasicDBObject();
			doc.append(EVENT_TBL_COLS.event_name.name(), eventName);
			doc.append(EVENT_TBL_COLS.event_type.name(), eventType);
			doc.append(EVENT_TBL_COLS.date_from.name(), Long.parseLong(fromDate));
			doc.append(EVENT_TBL_COLS.date_to.name(), Long.parseLong(toDate));
			doc.append(EVENT_TBL_COLS.remarks.name(), remarks);
			doc.append(EVENT_TBL_COLS.country.name(), country);
			doc.append(EVENT_TBL_COLS.state.name(), state);
			doc.append(EVENT_TBL_COLS.city.name(), city);
			eventTbl.insert(doc);
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
