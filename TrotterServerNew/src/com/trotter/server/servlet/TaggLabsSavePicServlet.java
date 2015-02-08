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

@WebServlet("/taggLabsSavePic")
public class TaggLabsSavePicServlet extends HttpServlet {

    public TaggLabsSavePicServlet() {
    }

	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			if (Utility.isNullEmpty(request.getParameter("bibNo"))) {
				System.out.println("Empty trip_id request found...:(");
				throw new ServletException("Empty bibNo request found...:("); 
			}
			if (Utility.isNullEmpty(request.getParameter("email"))) {
				System.out.println("Empty result request found...:(");
				throw new ServletException("Empty email request found...:("); 
			}
			if (Utility.isNullEmpty(request.getParameter("picUrl"))) {
				System.out.println("Empty resultType request found...:(");
				throw new ServletException("Empty picUrl request found...:("); 
			}
			String bibNo = request.getParameter("bibNo");
			String email = request.getParameter("email");
			String picUrl = request.getParameter("picUrl");
			System.out.println(bibNo);
			System.out.println(email);
			System.out.println(picUrl);
			DB mongoDB = ManageConnection.getDBConnection();
			DBCollection responseTbl = mongoDB.getCollection("TAGGLABS_DATA");
			BasicDBObject doc = new BasicDBObject();
			doc.append("bib_no", bibNo);
			doc.append("email", email);
			doc.append("picture", picUrl);
			responseTbl.insert(doc);
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
