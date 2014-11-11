package com.trotter.server.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bson.types.ObjectId;
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

@WebServlet("/fetchUser")
public class FetchUserServlet extends HttpServlet {

    public FetchUserServlet() {
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
			BasicDBObject inQuery = new BasicDBObject();
			inQuery.put(MongoDBStructure.USER_TABLE_COLS._id.name(), new ObjectId(id));
		    DBCursor cursor = userTbl.find(inQuery);
		    JSONObject userObj = new JSONObject();
		    if (cursor.count() > 0) {
		    	DBObject dbObject = cursor.next();
		    	for (MongoDBStructure.USER_TABLE_COLS colName : MongoDBStructure.USER_TABLE_COLS.values()) {
		    		if (dbObject.containsField(colName.name())) {
		    			userObj.put(colName.name(), dbObject.get(colName.name()));
		    		}
		    	}
		    	response.setContentType("application/json");
			    response.getWriter().write(userObj.toString());
		    	response.setStatus(HttpServletResponse.SC_OK);
			    return;
		    }
		    response.setContentType("application/text");
		    response.getWriter().write("User doesn't exists!!!");
	    	response.setStatus(HttpServletResponse.SC_NO_CONTENT);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}
