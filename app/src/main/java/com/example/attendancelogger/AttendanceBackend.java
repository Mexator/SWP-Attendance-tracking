package com.example.attendancelogger;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.security.auth.login.LoginException;

/**
 * Represents API to interact with backend. Uses Singleton pattern
 */
public class AttendanceBackend{
    private static AttendanceBackend instance;
    private User user;
    private String serverURL;
    private RequestQueue requestQueue;
    private Context context;

    private String token, renewal_token;
    private Date last_update;

    private AttendanceBackend(Context context) {
        user = User.getInstance();
        serverURL = "https://attendance-inno.herokuapp.com/api/";
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

    public void setServerURL(String serverURL) {
        this.serverURL = serverURL;
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

    public void parseUser(JSONObject user) throws JSONException{
        String name = user.getString("first_name") + user.getString("last_name");
        Long ID = user.getLong("id");
        User.Roles role = User.parseRole(user.getString("role"));
        this.user.init(name,ID,role);
    }

    public void logInRequest(String username, String password, Response.Listener<JSONObject> listener,
                             Response.ErrorListener errorListener)
            throws LoginException, JSONException {
        User.Roles role;

        JSONObject request = new JSONObject();
        JSONObject user = new JSONObject();
        user.put("email", username);
        user.put("password",password);
        request.put("user",user);

        String url = serverURL+"session/";
        sendRequest(Request.Method.POST,url,request,listener,errorListener);
    }

    public void requestUser(Response.Listener<JSONObject> listener,
                            Response.ErrorListener errorListener){
        assert token != null;
        sendRequest(Request.Method.GET,serverURL+"current_user",null,listener,errorListener);
    }

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

        String url = serverURL+"presences/";
        sendRequest(Request.Method.POST,url,body,listener,errorListener);
    }
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
