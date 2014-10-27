package com.trotter.server.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
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
			BasicDBObject doc = new BasicDBObject();
			for (MongoDBStructure.USER_TABLE_COLS col : MongoDBStructure.USER_TABLE_COLS.values()) {
				if (requestObj.has(col.name()))
					doc.append(col.name(), requestObj.get(col.name()));
				else
					doc.append(col.name(), "");
			}
			WriteResult wr = userTbl.insert(doc);
			response.setHeader(MongoDBStructure.USER_TABLE_COLS._id.name(),
					(String) wr.getField(MongoDBStructure.USER_TABLE_COLS._id.name()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}
