package com.swp.attendancelogger.system_logic;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Represents API to interact with backend. Uses Singleton pattern
 */
public class AttendanceBackend {
    private static AttendanceBackend instance;
    private User user;
    private String APIEndpoint;
    private RequestQueue requestQueue;

    private final long RENEWAL_INTERVAL = 1000 * 60 * 60 / 2;
    private final String RENEWAL_ENDPOINT = "session/renew";

    private String token,
            renewalToken;
    private Date lastUpdate;

    private AttendanceBackend(Context context) {
        user = User.getInstance();
        APIEndpoint = "https://attendance-inno.herokuapp.com/api/";
        requestQueue = Volley.newRequestQueue(context);
        requestQueue.start();
    }

    public static AttendanceBackend getInstance(Context context) {
        if (instance == null) {
            synchronized (AttendanceBackend.class) {
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

    public void setToken(String token, String renewalToken) {
        Date lastUpdate = new Date();
        setToken(token, renewalToken, lastUpdate);
    }

    public void setToken(String token, String renewalToken, Date lastUpdate) {
        this.token = token;
        this.renewalToken = renewalToken;
        this.lastUpdate = lastUpdate;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void saveAccessInfo(Context context) {
        synchronized (AttendanceBackend.class) {
            SharedPreferences prefs = context.getSharedPreferences(
                    SharedConstants.ACCESS_PREFS_FILE,
                    Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("INITIALIZED", true);
            editor.putString("ACCESS_TOKEN", token);
            editor.putString("RENEWAL_TOKEN", renewalToken);
            DateFormat df = new SimpleDateFormat(SharedConstants.DATE_FORMAT, Locale.US);
            editor.putString("LAST_UPDATE", df.format(lastUpdate));
            editor.apply();
        }
    }

    /**
     * Sends request for receiving auth token, as described in the API
     *
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
        user.put("password", password);
        request.put("user", user);

        String url = APIEndpoint + "session/";
        sendRequest(Request.Method.POST, url, request, listener, errorListener);
    }

    /**
     * Sends request to get current user
     *
     * @param listener      callback to handle response
     * @param errorListener callback to handle errors
     * @throws JSONException
     */
    public void sendUserRequest(Response.Listener<JSONObject> listener,
                                Response.ErrorListener errorListener) {
        assert token != null;
        sendRequest(Request.Method.GET, APIEndpoint + "current_user", null, listener, errorListener);
    }

    /**
     * Sends request for receiving auth token, as described in the API
     *
     * @param classId       same as in API description
     * @param activityId    same as in API description
     * @param userId        same as in API description
     * @param week          same as in API description
     * @param listener      callback to handle response
     * @param errorListener callback to handle errors
     * @throws JSONException
     */
    public void sendPresenceRequest(Long classId, Long activityId, Long userId, Integer week,
                                    Response.Listener<JSONObject> listener,
                                    Response.ErrorListener errorListener) throws JSONException {

        JSONObject body = new JSONObject();
        JSONObject presence = new JSONObject();
        presence.put("class_id", classId);
        presence.put("activity_id", activityId);
        presence.put("user_id", userId);
        presence.put("week", week);
        body.put("presence", presence);

        String url = APIEndpoint + "presences/";
        sendRequest(Request.Method.POST, url, body, listener, errorListener);
    }

    /**
     * Sends request to get list of classes
     *
     * @param listener      callback to handle response
     * @param errorListener callback to handle errors
     */
    public void sendClassesRequest(Response.Listener<JSONObject> listener,
                                   Response.ErrorListener errorListener) {
        String url = APIEndpoint + "user_classes";
        sendRequest(Request.Method.GET, url, null, listener, errorListener);
    }

    public void sendUsersByClassRequest(Long classID, Response.Listener<JSONObject> listener,
                                        Response.ErrorListener errorListener) {
        String url = APIEndpoint + "class_students/" + classID;
        sendRequest(Request.Method.GET, url, null, listener, errorListener);
    }
    private void sendRenewalRequest(){
        JsonObjectRequest renewalRequest = new JsonObjectRequest(
                Request.Method.POST,
                APIEndpoint + RENEWAL_ENDPOINT,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject data = response.getJSONObject("data");
                            setToken(data.getString("token"),
                                    data.getString("renewal_token"));
                        } catch (JSONException e) {
                            Log.e("", e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("",error.toString());
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", renewalToken);
                return headers;
            }
        };
        requestQueue.add(renewalRequest);
    }
    /**
     * General purpose method to send any request
     *
     * @param method        POST, GET, etc.
     * @param url           target url
     * @param body          json body
     * @param listener      callback to handle response
     * @param errorListener callback to handle errors
     */
    private void sendRequest(int method, String url, JSONObject body,
                             Response.Listener<JSONObject> listener,
                             Response.ErrorListener errorListener) {
        if (lastUpdate != null && (new Date().getTime() - lastUpdate.getTime()) > RENEWAL_INTERVAL) {
            sendRenewalRequest();
        }
        JsonObjectRequest request;
        if (token != null) {
            request = new JsonObjectRequest(method, url, body, listener, errorListener) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Authorization", token);
                    return headers;
                }
            };
        } else {
            request = new JsonObjectRequest(method, url, body, listener, errorListener);
        }
        requestQueue.add(request);
    }
}


    //curl -X POST -H "Authorization: SFMyNTY.YzJmMDQwODMtOTY3Ni00MTFhLWIyMDctZjZkMWIzOWQyNjk4.rsEf0ijmJX6xMlS0p2iEjogWdGTTaHScp8oVEB4Z0M0" https://attendance-inno.herokuapp.com/api/api/session/renew
//curl -X POST -H "Authorization: SFMyNTY.NWVhODlhODUtNWMzNS00NDE1LWE0NzEtZmExZWMzYWE0NTU3.RToGGIQ5DtZFEPfBm_MRy20rNpNzVOparuNUhlkdFcs" https://attendance-inno.herokuapp.com/api/api/session/renew
//SFMyNTY.NWVhODlhODUtNWMzNS00NDE1LWE0NzEtZmExZWMzYWE0NTU3.RToGGIQ5DtZFEPfBm_MRy20rNpNzVOparuNUhlkdFcs