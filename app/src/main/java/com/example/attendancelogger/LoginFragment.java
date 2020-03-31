package com.example.attendancelogger;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import javax.security.auth.login.LoginException;

public class LoginFragment extends Fragment implements Response.Listener<JSONObject>,
        View.OnClickListener, Response.ErrorListener {
    private EditText loginEdit;
    private EditText passwordEdit;
    private View progressBar;
    private AttendanceBackend backend;

    public LoginFragment() {
        // Required empty public constructor
    }
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loginEdit = getView().findViewById(R.id.login_edit);
        passwordEdit = getView().findViewById(R.id.password_edit);
        progressBar = new ProgressBar(getActivity(), null, android.R.attr.progressBarStyleLarge);
        getView().findViewById(R.id.button_log_in).setOnClickListener(this);

        //TODO Change progress bar
        //TODO Forbid actions while waiting for response
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100, 100);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        LinearLayout l = getView().findViewById(R.id.login_layout);
        l.addView(progressBar, params);
        progressBar.setVisibility(View.INVISIBLE);

    }

    private void sendLogInRequest(){
        backend = AttendanceBackend.getInstance(getContext());
        String login = loginEdit.getText().toString();
        String password = passwordEdit.getText().toString();
        try {
                backend.logInRequest(login, password,this,this);
        }
        catch (Exception ex) {
            Toast.makeText(getActivity(), ex.toString(), Toast.LENGTH_SHORT).show();
        }
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onResponse(JSONObject response) {
        try {
            if(response.has("data")){
                JSONObject data = response.getJSONObject("data");
                backend.setToken(data.getString("token"),
                        data.getString("renewal_token"));
                backend.requestUser(this,this);
            }
            else if(response.has("user")){
                backend.parseUser(response.getJSONObject("user"));
                progressBar.setVisibility(View.INVISIBLE);
                if(backend.getUser().getRole() == User.Roles.STUDENT){
                    Navigation.findNavController(getView()).navigate(R.id.action_loginFragment_to_student_main);
                }
                else{
                    Navigation.findNavController(getView()).navigate(R.id.action_loginFragment_to_professor_main);
                }
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        progressBar.setVisibility(View.INVISIBLE);
        String message;
        if (error instanceof NetworkError)
            message = "Network Error";
        else if (error instanceof ParseError)
            message = "Server response parse error";
        else if (error instanceof TimeoutError)
            message = "Response Timed out";
        else if (error.networkResponse.statusCode == 401){
            message = "Wrong login or password";
        } else
            message = "Http error " + error.networkResponse.statusCode;
        Toast.makeText(getContext(),message,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_log_in:
                sendLogInRequest();
                break;
        }
    }
}
