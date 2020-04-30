package com.swp.attendancelogger;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.swp.attendancelogger.system_logic.AttendanceBackend;
import com.swp.attendancelogger.system_logic.User;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginFragment extends Fragment implements Response.Listener<JSONObject>,
        View.OnClickListener, Response.ErrorListener {
    private EditText loginEdit;
    private EditText passwordEdit;
    private View progressBar;
    private AttendanceBackend backend;
    private NavController navController;

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
        passwordEdit.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP){
                    sendLogInRequest();
                    return true;
                }
                return false;
            }
        });

        progressBar = view.findViewById(R.id.progress_bar);
        getView().findViewById(R.id.button_log_in).setOnClickListener(this);

        progressBar.setVisibility(View.INVISIBLE);
        navController = Navigation.findNavController(getActivity(), R.id.my_nav_host_fragment);
    }

    private void sendLogInRequest(){
        progressBar.setVisibility(View.VISIBLE);
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        backend = AttendanceBackend.getInstance(getContext());
        String login = loginEdit.getText().toString();
        String password = passwordEdit.getText().toString();
        try {
                backend.sendLogInRequest(login, password,this,this);
        }
        catch (Exception ex) {
            Toast.makeText(getActivity(), ex.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResponse(JSONObject response) {
        try {
            if(response.has("data")){
                JSONObject data = response.getJSONObject("data");
                backend.setToken(data.getString("token"),
                        data.getString("renewal_token"));
                backend.sendUserRequest(this,this);
                backend.saveAccessInfo(getContext());
            }
            else if(response.has("user")){
                User.parseUser(response.getJSONObject("user"));
                backend.setUser(User.getInstance());
                if(backend.getUser().getRole() == User.Roles.STUDENT){
                    navController.navigate(R.id.action_loginFragment_to_student_main);
                }
                else{
                    navController.navigate(R.id.action_loginFragment_to_professor_main);
                }
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        progressBar.setVisibility(View.INVISIBLE);
    }
    
    //TODO change login architecture

    @Override
    public void onErrorResponse(VolleyError error) {
        progressBar.setVisibility(View.INVISIBLE);
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
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

    /*
    TODO Change the event so that the finishing edit the password will send login request
    */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_log_in:
                sendLogInRequest();
                break;
        }
    }
}
