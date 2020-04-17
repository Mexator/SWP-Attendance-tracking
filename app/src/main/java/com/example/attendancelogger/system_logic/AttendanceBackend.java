package com.example.attendancelogger.system_logic;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents API to interact with backend. Uses Singleton pattern
 */
public class AttendanceBackend{
    private static AttendanceBackend instance;
    private User user;
    private String APIEndpoint;
    private RequestQueue requestQueue;
    private Context context;

    private String token, renewal_token;
    private Date last_update;

    private AttendanceBackend(Context context) {
        user = User.getInstance();
        APIEndpoint = "https://attendance-inno.herokuapp.com/api/";
        this.context = context;
        requestQueue = Volley.newRequestQueue(context);
        requestQueue.start();
    }

    public static AttendanceBackend getInstance(Context context) {
        synchronized (AttendanceBackend.class){
            if (instance == null){
                instance = new AttendanceBackend(context);
            }
        }
        return instance;
    }

    public void setAPIEndpoint(String APIEndpoint) {
        this.APIEndpoint = APIEndpoint;
    }

    public boolean isLoggedIn() {
        return user != null;
    }

    public User getUser() {
        return user;
    }

    public void setToken(String token, String renewal_token)
    {
        this.token = token;
        this.renewal_token = renewal_token;
        this.last_update = new Date();
    }
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Sends request for receiving auth token, as described in the API
     * @param username      same as in API description
     * @param password      same as in API description
     * @param listener      callback to handle response
     * @param errorListener callback to handle errors
     * @throws JSONException
     */
    public void sendLogInRequest(String username, String password, Response.Listener<JSONObject> listener,
                                 Response.ErrorListener errorListener)
            throws JSONException {
        JSONObject request = new JSONObject();
        JSONObject user = new JSONObject();
        user.put("email", username);
        user.put("password",password);
        request.put("user",user);

        String url = APIEndpoint +"session/";
        sendRequest(Request.Method.POST,url,request,listener,errorListener);
    }

    /**
     * Sends request to get current user
     * @param listener      callback to handle response
     * @param errorListener callback to handle errors
     * @throws JSONException
     */
    public void sendUserRequest(Response.Listener<JSONObject> listener,
                                Response.ErrorListener errorListener){
        assert token != null;
        sendRequest(Request.Method.GET, APIEndpoint +"current_user",null,listener,errorListener);
    }

    /**
     * Sends request for receiving auth token, as described in the API
     * @param classId    same as in API description
     * @param activityId same as in API description
     * @param userId     same as in API description
     * @param week       same as in API description
     * @param listener      callback to handle response
     * @param errorListener callback to handle errors
     * @throws JSONException
     */
    public void sendPresenceRequest(Long classId, Long activityId, Long userId, Integer week,
                                    Response.Listener<JSONObject> listener,
                                    Response.ErrorListener errorListener) throws JSONException{

        JSONObject body = new JSONObject();
        JSONObject presence = new JSONObject();
        presence.put("class_id", classId);
        presence.put("activity_id",activityId);
        presence.put("user_id",userId);
        presence.put("week",week);
        body.put("presence",presence);

        String url = APIEndpoint +"presences/";
        sendRequest(Request.Method.POST,url,body,listener,errorListener);
    }

    /**
     * Sends request to get list of classes
     * @param listener      callback to handle response
     * @param errorListener callback to handle errors
     */
    public void sendClassesRequest(Response.Listener<JSONObject> listener,
                               Response.ErrorListener errorListener){
        String url = APIEndpoint + "classes";
        sendRequest(Request.Method.GET,url,null,listener,errorListener);
    }

    /**
     * General purpose method to send any request
     * @param method        POST, GET, etc.
     * @param url           target url
     * @param body          json body
     * @param listener      callback to handle response
     * @param errorListener callback to handle errors
     */
    private void sendRequest(int method, String url, JSONObject body,
                             Response.Listener<JSONObject> listener,
                             Response.ErrorListener errorListener){
        //TODO Add token renewal
        JsonObjectRequest request;
        if (token != null) {
            request = new JsonObjectRequest(method, url, body, listener, errorListener) {
                @Override
                public Map getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Authorization", token);
                    return headers;
                }
            };
        }
        else{
            request = new JsonObjectRequest(method, url, body, listener, errorListener);
        }
        requestQueue.add(request);
    }
}
