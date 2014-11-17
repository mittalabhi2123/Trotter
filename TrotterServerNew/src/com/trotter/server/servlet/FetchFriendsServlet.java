package com.trotter.server.servlet;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.trotter.common.ManageConnection;
import com.trotter.common.MongoDBStructure;
import com.trotter.common.Utility;

@WebServlet("/getFriends")
public class FetchFriendsServlet extends HttpServlet {

    public FetchFriendsServlet() {
    }

	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			if (Utility.isNullEmpty(request.getParameter("data"))) {
				System.out.println("Empty request found...:(");
				throw new ServletException("Invalid/No request received"); 
			}
			System.out.println(request.getParameter("data"));
			List<String> fbIdList = Arrays.asList(request.getParameter("data").split(","));
			
			//TODO validations
			DB mongoDB = ManageConnection.getDBConnection();
			DBCollection userTbl = mongoDB.getCollection(MongoDBStructure.USER_TBL);
			BasicDBObject inQuery = new BasicDBObject();
			BasicDBObject fields = new BasicDBObject();
			fields.put(MongoDBStructure.USER_TABLE_COLS.fb_id.name(), 1);
			inQuery.put(MongoDBStructure.USER_TABLE_COLS.fb_id.name(), new BasicDBObject(
					Utility.MongoQueryHandles.$in.name(), fbIdList));
		    DBCursor cursor = userTbl.find(inQuery, fields);
		    JSONArray responseArr = new JSONArray();
		    JSONObject dataObj = null;
		    while (cursor.hasNext()) {
				DBObject dbObj = cursor.next();
				dataObj = new JSONObject();
				dataObj.put(MongoDBStructure.USER_TABLE_COLS._id.name(), dbObj.get(MongoDBStructure.USER_TABLE_COLS._id.name()));
				dataObj.put(MongoDBStructure.USER_TABLE_COLS.fb_id.name(), dbObj.get(MongoDBStructure.USER_TABLE_COLS.fb_id.name()));
				responseArr.put(dataObj);
			}
		    response.setContentType("application/json");
		    response.getWriter().write(responseArr.toString());
		} catch (Exception e) {
			response.setContentType("application/text");
		    response.getWriter().write(e.getMessage());
	    	response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			e.printStackTrace();
	    	return;
		}
	}


}
