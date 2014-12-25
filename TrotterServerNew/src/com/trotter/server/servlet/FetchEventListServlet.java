package com.trotter.server.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;

import com.mongodb.DB;
import com.trotter.common.ManageConnection;
import com.trotter.server.servlet.functions.EventFunctions;

@WebServlet("/fetchEvents")
public class FetchEventListServlet extends HttpServlet {

    public FetchEventListServlet() {
    }

	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			DB mongoDB = ManageConnection.getDBConnection();
			JSONArray eventTblLst = new EventFunctions().fetchCurrentEventList(mongoDB);
			if (eventTblLst == null) {
				response.setContentType("application/text");
			    response.getWriter().write("No event found for future dates.");
		    	response.setStatus(HttpServletResponse.SC_NO_CONTENT);
		    	return;
			}
	    	response.setContentType("application/json");
		    response.getWriter().write(eventTblLst.toString());
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
