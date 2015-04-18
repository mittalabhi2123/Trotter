package com.trotter.server.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
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

@WebServlet("/RegisterUser")
public class RegisterUserServlet extends HttpServlet {

    public RegisterUserServlet() {
    }

	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			if (Utility.isNullEmpty(request.getParameter("data"))) {
				System.out.println("Empty request found...:(");
				throw new ServletException("Invalid/No request received"); 
			}
			System.out.println(request.getParameter("data"));
			JSONObject requestObj = new JSONObject(request.getParameter("data"));
			//TODO validations
			DB mongoDB = ManageConnection.getDBConnection();
			DBCollection userTbl = mongoDB.getCollection(MongoDBStructure.USER_TBL);
			Utility.trackViaGoogleAnalytics("Register User", "New User Registers");
			BasicDBObject inQuery = new BasicDBObject();
			inQuery.put(MongoDBStructure.USER_TABLE_COLS.fb_id.name(), requestObj.getString(MongoDBStructure.USER_TABLE_COLS.fb_id.name()));
			DBObject dbObject = userTbl.findOne(inQuery);
		    if (dbObject != null) {
		    	response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		    	response.setContentType("application/json");
		    	JSONObject responseObj = new JSONObject();
		    	responseObj.put("error", "User already registered!!!");
		    	responseObj.put("id", dbObject.get(MongoDBStructure.USER_TABLE_COLS._id.name()));
			    response.getWriter().write(responseObj.toString());
			    System.out.println("ID::" + dbObject.get(MongoDBStructure.USER_TABLE_COLS._id.name()));
			    return;
		    }
		    
			BasicDBObject doc = new BasicDBObject();
			for (MongoDBStructure.USER_TABLE_COLS col : MongoDBStructure.USER_TABLE_COLS.values()) {
				if (requestObj.has(col.name()))
					doc.append(col.name(), requestObj.get(col.name()));
			}
			userTbl.insert(doc);
			JSONObject responseObj = new JSONObject();
	    	responseObj.put("id", doc.get(MongoDBStructure.USER_TABLE_COLS._id.name()));
		    System.out.println("ID::" + responseObj.getString("id"));
		    response.getWriter().write(responseObj.toString());
	    	response.setStatus(HttpServletResponse.SC_OK);
		} catch (Exception e) {
			e.printStackTrace();
			JSONObject responseObj = new JSONObject();
	    	try {
				responseObj.put("error", e.getMessage());
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		    response.getWriter().write(responseObj.toString());
	    	response.setStatus(HttpServletResponse.SC_OK);
	    	return;
		}
	}


}
