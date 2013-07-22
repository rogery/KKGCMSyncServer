package com.kkbox.gcmsync.server;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.*;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.MulticastResult;
import com.google.android.gcm.server.Sender;
import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

@SuppressWarnings("serial")
public class KKGCMServerServlet extends HttpServlet {
	
	private static final String API_KEY = "AIzaSyAo9kSqy6ChA3D5hvFnwDvOpE5Y3Hi2bCM"; 

	private List<String> devices = new ArrayList<String>();
	private int count = 5;
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.setContentType("text/plain");
		
		final String act = req.getParameter("act");
		final JSONObject resJson = new JSONObject();
		
		try{
			if("register".equals(act)){
				// put regId to devices.
				final String regId = req.getParameter("regId");
				if(regId!=null && !devices.contains(regId)){
					devices.add(req.getParameter("regId"));
				}

				// dump devices to response JSON.
				final JSONArray jsonArr = new JSONArray();
				for(String device:devices){
					jsonArr.put(device);
				}
				resJson.put("regIds", jsonArr);
			}
			else if("pull".equals(act)){
				resJson.put("count", count);
			}
			else if("push".equals(act)){
				count = Integer.parseInt(req.getParameter("count"));
				resJson.put("count", count);
				sendGCMMessage(resJson);
			}
			else if("cplpush".equals(act)){
				sendGCMMessage(resJson);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		resp.getWriter().println(resJson.toString());
    }
	
	private void sendGCMMessage(JSONObject resJson) throws Exception {
		if(devices.size() > 0){
			final Sender sender = new Sender(API_KEY);
            final Message gcmMessage = new Message.Builder().addData("message", "sync").build();
            final MulticastResult result = sender.send(gcmMessage, devices, 5);
            resJson.put("sendResult", new JSONObject()
            			.put("total", result.getTotal())
            			.put("sucess", result.getSuccess())
            			.put("failure", result.getFailure())
    			);
		}
	}	
}
