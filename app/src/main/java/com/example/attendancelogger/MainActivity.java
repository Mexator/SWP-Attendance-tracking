package com.example.attendancelogger;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.attendancelogger.system_logic.AttendanceBackend;
import com.example.attendancelogger.system_logic.SharedConstants;
import com.example.attendancelogger.system_logic.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences accessPreferences;
    private AttendanceBackend backend;
    private NavController navController;
    private final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        backend = AttendanceBackend.getInstance(this);

        String accessPreferenceDir = SharedConstants.ACCESS_PREFS_FILE;
        accessPreferences = this.getSharedPreferences(accessPreferenceDir, Context.MODE_PRIVATE);

        boolean isFirstLaunch = !accessPreferences.getBoolean("INITIALIZED", false);

        if (!isFirstLaunch) {
            String accessToken = accessPreferences.getString("ACCESS_TOKEN", null);
            String renewalToken = accessPreferences.getString("RENEWAL_TOKEN", null);

            DateFormat df = new SimpleDateFormat(SharedConstants.DATE_FORMAT, Locale.US);
            String lastUpdateStr = accessPreferences.getString("LAST_UPDATE", null);

            if (lastUpdateStr == null || accessToken == null || renewalToken == null) {
                isFirstLaunch = true;
            } else {
                Date lastUpdate = null;
                try {
                    lastUpdate = df.parse(lastUpdateStr);
                } catch (Exception ex) {
                    Log.e(SharedConstants.preLoginErrorTag, "Can not parse saved date or no date saved", ex);
                    isFirstLaunch = true;
                }
                backend.setToken(accessToken, renewalToken, lastUpdate);
            }
        }

        navController = Navigation.findNavController(this, R.id.my_nav_host_fragment);
        if (isFirstLaunch) {
            //Navigate to login
            navController.navigate(R.id.action_waiting_to_loginFragment);
        } else {
            // Send User query.
            // At this point both keys and lastUpdate should be set at the backend instance
            tryLogIn();
        }
    }

    private void tryLogIn() {
        backend.sendUserRequest(
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            User.parseUser(response.getJSONObject("user"));
                            backend.setUser(User.getInstance());
                            navController.navigate(backend.getUser().getLoginPath());
                        } catch (JSONException e) {
                            Log.e(SharedConstants.loginErrorTag,"Cannot parse server response",e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context,"Login failed. Retrying...",Toast.LENGTH_LONG).show();
                        tryLogIn();
                    }
                });
    }

    public SharedPreferences getAccessPreferences() {
        return accessPreferences;
    }
}
