package com.example.attendancelogger;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import javax.security.auth.login.LoginException;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {
    private EditText loginEdit;
    private EditText passwordEdit;

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
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
        getView().findViewById(R.id.button_log_in).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AttendanceBackend backend = AttendanceBackend.getInstance();
                String login = loginEdit.getText().toString();
                String password = passwordEdit.getText().toString();
                try {
                    backend.logIn(login, password);
                }
                catch (LoginException ex){
                    Toast.makeText(getActivity(),ex.toString(),Toast.LENGTH_SHORT).show();
                    return;
                }

                if(backend.getUser().getRole() == User.Roles.STUDENT)
                    Navigation.findNavController(v).navigate(
                            R.id.action_loginFragment_to_student_main);
                else if (backend.getUser().getRole() == User.Roles.PROFESSOR)
                    Navigation.findNavController(v).navigate(
                            R.id.action_loginFragment_to_professor_main);
            }
        });
    }
}
