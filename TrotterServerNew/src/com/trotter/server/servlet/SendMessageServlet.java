package com.trotter.server.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bson.types.ObjectId;
import org.jivesoftware.smack.SASLAuthentication;
import org.json.JSONObject;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.trotter.common.Const.SendMessageRequestparams;
import com.trotter.common.ManageConnection;
import com.trotter.common.MongoDBStructure;
import com.trotter.common.Utility;
import com.trotter.server.servlet.functions.SmackCcsClient;
import com.trotter.server.servlet.functions.TripFunctions;
import com.trotter.server.servlet.functions.UserFunctions;

@WebServlet("/sendChat")
public class SendMessageServlet extends HttpServlet {

	  private static final int MULTICAST_SIZE = 1000;
	
	  private Sender sender;
	
	  private static final Executor threadPool = Executors.newFixedThreadPool(5);
	
	  @Override
	  public void init(ServletConfig config) throws ServletException {
	    super.init(config);
	    sender = newSender(config);
	  }
	
	  /**
	   * Creates the {@link Sender} based on the servlet settings.
	   */
	  protected Sender newSender(ServletConfig config) {
	    String key = "AIzaSyAZzCbHMNdYz3JHTPKp9xzYbyePfpIW08c";
	    return new Sender(key);
	  }
	  
    public SendMessageServlet() {
    }

	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			if (Utility.isNullEmpty(request.getParameter(SendMessageRequestparams.message.name()))) {
				System.out.println("Empty message request found...:(");
				throw new ServletException("Empty message request found...:("); 
			}
			if (Utility.isNullEmpty(request.getParameter(SendMessageRequestparams.senderTripId.name()))) {
				System.out.println("Empty senderTripId request found...:(");
				throw new ServletException("Empty senderTripId request found...:("); 
			}
			if (Utility.isNullEmpty(request.getParameter(SendMessageRequestparams.targetTripId.name()))) {
				System.out.println("Empty targetTripId request found...:(");
				throw new ServletException("Empty targetTripId request found...:("); 
			}
			if (Utility.isNullEmpty(request.getParameter(SendMessageRequestparams.messageId.name()))) {
				System.out.println("Empty messageId request found...:(");
				throw new ServletException("Empty messageId request found...:("); 
			}
			String message = request.getParameter(SendMessageRequestparams.message.name());
			String senderTripId = request.getParameter(SendMessageRequestparams.senderTripId.name());
			String targetTripId = request.getParameter(SendMessageRequestparams.targetTripId.name());
			String messageId = request.getParameter(SendMessageRequestparams.messageId.name());
			System.out.println(message);
			System.out.println(senderTripId);
			System.out.println(targetTripId);
			System.out.println(messageId);
			DB mongoDB = ManageConnection.getDBConnection();
			TripFunctions tripFunctions = new TripFunctions();
			UserFunctions userFunctions = new UserFunctions();
			DBCollection tripTbl = mongoDB.getCollection(MongoDBStructure.TRIP_TBL);
			BasicDBObject tripInQuery = new BasicDBObject();
			tripInQuery.put(MongoDBStructure.TRIP_TABLE_COLS._id.name(), new ObjectId(targetTripId));
			DBObject dbObject = tripTbl.findOne(tripInQuery);
		    if (dbObject == null) {
		    	response.setContentType("application/text");
			    response.getWriter().write("Trip doesn't exists!!!");
		    	response.setStatus(HttpServletResponse.SC_NO_CONTENT);
		    	return;
		    }
	    	JSONObject tripJsonObj = tripFunctions.createTripJsonObj(dbObject, userFunctions, mongoDB);
	    	List<JSONObject> userList = new ArrayList<>();
	    	userList.add(tripJsonObj.getJSONObject(MongoDBStructure.USER_TBL));
	    	if (tripJsonObj.getBoolean(MongoDBStructure.TRIP_TABLE_COLS.is_individual.name())) {
	    		BasicDBList groupMembers = (BasicDBList) dbObject.get(MongoDBStructure.TRIP_TABLE_COLS.group_members.name());
	    		if (groupMembers != null && groupMembers.size() > 0) {
	    			for (int i = 0 ; i < groupMembers.size() ; i++) {
	    	    		if (groupMembers.get(i) == null || groupMembers.get(i).toString().equals(""))
	    	    			continue;
	    	    		ObjectId userId = new ObjectId(((String)groupMembers.get(i)));
	    	    		userList.add(userFunctions.fetchUserById(mongoDB, userId));
	    	    	}
		    	}
	    	}
	    	// send messages to all registrationIds
	    	for (JSONObject userJsonObj : userList) {
	    		sendMessageHttp(messageId, userJsonObj.getString(MongoDBStructure.USER_TABLE_COLS.gcm_reg_id.name()), message,
	    				request.getParameter(SendMessageRequestparams.senderName.name()), senderTripId);
	    	}
		} catch (Exception e) {
			response.setContentType("application/text");
		    response.getWriter().write(e.getMessage());
	    	response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			e.printStackTrace();
	    	return;
		}
	}

	public void sendMessageCcs(String trotterMessageId, String registrationId, String chatMessage, String senderName, String senderTripId) throws Exception {
        final long senderId = 267023192902L; // your GCM sender id
        final String password = "AIzaSyAZzCbHMNdYz3JHTPKp9xzYbyePfpIW08c";

        SmackCcsClient ccsClient = new SmackCcsClient();
        SASLAuthentication.supportSASLMechanism("PLAIN", 0);
        ccsClient.connect(senderId, password);
        
        Map<String, String> payload = new HashMap<String, String>();
        if (Utility.isNullEmpty(senderTripId)) {
        	payload.put("senderTripId", "");
        } else {
        	String actualMessage = trotterMessageId;
            if (trotterMessageId.indexOf('[') == 0) {
            	actualMessage = trotterMessageId.substring(1, trotterMessageId.length() - 1); 
            }
            if (senderTripId.equalsIgnoreCase(actualMessage.split("_")[0])) {
            	payload.put("senderTripId", actualMessage.split("_")[1]);
            } else {
            	payload.put("senderTripId", actualMessage.split("_")[0]);
            }
        }
        // Send a sample hello downstream message to a device.
        String messageId = ccsClient.nextMessageId();
        payload.put("message", chatMessage);
        payload.put("embeddedMessageId", trotterMessageId);
        payload.put("senderName", senderName);
        Long timeToLive = 10000L;
        String message = SmackCcsClient.createJsonMessage(registrationId, messageId, payload,
                "", timeToLive, true);
        System.out.println(message);
        ccsClient.sendDownstreamMessage(message);
    }

	public void sendMessageHttp(String trotterMessageId, String registrationId, String chatMessage, String senderName, String senderTripId) throws Exception {
        final long senderId = 267023192902L; // your GCM sender id
        
        int timeToLive = 10000;
        Message.Builder messageBuilder = new Message.Builder().delayWhileIdle(true).timeToLive(timeToLive).collapseKey(senderName);
        if (Utility.isNullEmpty(senderTripId)) {
        	messageBuilder.addData("senderTripId", "");
        } else {
        	String actualMessage = trotterMessageId;
            if (trotterMessageId.indexOf('[') == 0) {
            	actualMessage = trotterMessageId.substring(1, trotterMessageId.length() - 1); 
            }
            if (senderTripId.equalsIgnoreCase(actualMessage.split("_")[0])) {
            	messageBuilder.addData("senderTripId", actualMessage.split("_")[1]);
            } else {
            	messageBuilder.addData("senderTripId", actualMessage.split("_")[0]);
            }
        }
        System.out.println(registrationId);
        messageBuilder.addData("message", chatMessage);
        messageBuilder.addData("embeddedMessageId", trotterMessageId);
        messageBuilder.addData("senderName", senderName);
        Result result = sender.send(messageBuilder.build(), registrationId, 5);
        System.out.println(result.getErrorCodeName());
    }
	
}
