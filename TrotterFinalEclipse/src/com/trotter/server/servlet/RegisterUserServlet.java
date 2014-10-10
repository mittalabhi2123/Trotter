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
import com.trotter.common.ManageConnection;
import com.trotter.common.MongoDBStructure;
import com.trotter.common.Utility;

@WebServlet("/RegisterUserServlet")
public class RegisterUserServlet extends HttpServlet {

    public RegisterUserServlet() {
    }

	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			if (Utility.isNullEmpty(request.getParameter("data"))) {
				throw new ServletException("Invalid/No request received"); 
			}
			JSONObject requestObj = new JSONObject(request.getParameter("data"));
			//TODO validations
			DB mongoDB = ManageConnection.getDBConnection();
			DBCollection userTblCol = mongoDB.getCollection(MongoDBStructure.USER_TBL);
			BasicDBObject doc = new BasicDBObject();
			for (MongoDBStructure.USER_TABLE_COLS col : MongoDBStructure.USER_TABLE_COLS.values()) {
				doc.append(col.name(), requestObj.get(col.name()));
			}
			userTblCol.insert(doc);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}
