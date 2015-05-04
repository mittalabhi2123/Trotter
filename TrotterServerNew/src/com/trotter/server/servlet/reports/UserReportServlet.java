package com.trotter.server.servlet.reports;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.trotter.common.ManageConnection;
import com.trotter.common.MongoDBStructure;

@WebServlet("/userReport")
public class UserReportServlet extends HttpServlet {

    public UserReportServlet() {
    }

	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			response.setContentType("text/html");
			response.getWriter().println("<html><title>User Report</title><body>");
			response.getWriter().println("<h1>List of registered users</h1>");
			response.getWriter().println("<table><tr>");
			response.getWriter().println("<th>S no.</th>");
			response.getWriter().println("<th>Name</th>");
			response.getWriter().println("<th>Gender</th>");
			response.getWriter().println("<th>DoB</th>");
			response.getWriter().println("<th>City</th>");
			//TODO Add registration Date
			response.getWriter().println("<th>Country</th></tr>");
			DB mongoDB = ManageConnection.getDBConnection();
			DBCollection userTbl = mongoDB.getCollection(MongoDBStructure.USER_TBL);
			DBCursor cursor = userTbl.find();
			cursor.sort(new BasicDBObject(MongoDBStructure.USER_TABLE_COLS.first_name.name(), 1));
		    DBObject userObj = null;
		    int count = 1;
		    SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
			while (cursor.hasNext()) {
				userObj = cursor.next();
				response.getWriter().println("<tr><td>"+(count++)+"</td>");
				String name = ((String)userObj.get(MongoDBStructure.USER_TABLE_COLS.first_name.name()))
						.concat(" ").concat(((String)userObj.get(MongoDBStructure.USER_TABLE_COLS.last_name.name())));
				response.getWriter().println("<td>"+name+"</td>");
				response.getWriter().println("<td>"+((String)userObj.get(MongoDBStructure.USER_TABLE_COLS.gender.name()))+"</td>");
				long date = ((Long)userObj.get(MongoDBStructure.USER_TABLE_COLS.dob.name()));
				response.getWriter().println("<td>"+sdf.format(new Date(date))+"</td>");
				response.getWriter().println("<td>"+((String)userObj.get(MongoDBStructure.USER_TABLE_COLS.home_state.name()))+"</td>");
				response.getWriter().println("<td>"+((String)userObj.get(MongoDBStructure.USER_TABLE_COLS.home_country.name()))+"</td></tr>");
		    }
			response.getWriter().println("</table></body></html>");
		} catch (Exception e) {
			e.printStackTrace();
			response.setContentType("application/text");
		    response.getWriter().write(e.getMessage());
	    	response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			e.printStackTrace();
	    	return;
		}
	}

	

}
