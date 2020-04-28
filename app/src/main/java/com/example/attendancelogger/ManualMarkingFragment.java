package com.example.attendancelogger;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.attendancelogger.system_logic.*;
import com.example.attendancelogger.ui_components.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

public class ManualMarkingFragment extends Fragment implements View.OnClickListener, Response.Listener<JSONObject>, Response.ErrorListener {
    private EditText activityIdEdit,
            weekEdit;
    private AttendanceBackend backend;
    private View progressBar;
    private PromptSpinner classSpinner;
    private PromptSpinner studentSpinner;
    private Button confirmButton;
    public ManualMarkingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        backend = AttendanceBackend.getInstance(getContext());
        return inflater.inflate(R.layout.fragment_manual_marking, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        activityIdEdit = view.findViewById(R.id.activity_id_edit);
        weekEdit = view.findViewById(R.id.week_edit);

        confirmButton = view.findViewById(R.id.confirm_marking_button);
        confirmButton.setOnClickListener(this);

        progressBar = view.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.INVISIBLE);

        classSpinner = view.findViewById(R.id.class_spinner);
        studentSpinner = view.findViewById(R.id.user_spinner);
        setupClassSpinner();
    }

    //TODO think about how to generalize two following functions
    /**
     * Receives list of classes from server, fills the adapter
     * and connect it to the spinner
     */
    private void setupClassSpinner() {
        assert backend != null;
        final List<StudyClass> classes = new LinkedList<>();
        //Obtain list of classes
        backend.sendClassesRequest(new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray rawClasses = response.getJSONArray("classes");
                    for (int i = 0; i < rawClasses.length(); i++) {
                        classes.add(StudyClass.parseClass((JSONObject) rawClasses.get(i)));
                    }
                    //Add the list to the adapter
                    PromptSpinnerAdapter<StudyClass> adapter = new PromptSpinnerAdapter<>(
                            getContext(), R.layout.support_simple_spinner_dropdown_item,
                            classes, R.string.prompt_class_select);
                    classSpinner.setAdapter(adapter);
                    classSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        boolean initial_call = true;
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if(initial_call){
                                initial_call = false;
                                return;
                            }
                            StudyClass item = (StudyClass) (classSpinner.getSelectedItem());
                            setupStudentSpinner(item.getID());
                            studentSpinner.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, this);
    }

    /**
     * Receives list of students from server, fills the adapter
     * and connect it to the spinner
     */
    private void setupStudentSpinner(Long classID) {
        assert backend != null;
        final List<Student> students = new LinkedList<>();
        //Obtain list of users, assigned to selected class
        backend.sendUsersByClassRequest(classID, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    //Create list of students
                    JSONArray rawStudents = response.getJSONArray("students");
                    for (int i = 0; i < rawStudents.length(); i++) {
                        students.add(Student.parseStudent(rawStudents.getJSONObject(i)));
                    }
                    //Add the list to the adapter
                    PromptSpinnerAdapter<Student> adapter = new PromptSpinnerAdapter<>(
                            getContext(), R.layout.support_simple_spinner_dropdown_item,
                            students, R.string.prompt_user_select);
                    //Add the adapter to spinner
                    studentSpinner.setAdapter(adapter);
                    studentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        boolean initial_call = true;
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if(initial_call){
                                initial_call = false;
                                return;
                            }
                            activityIdEdit.setVisibility(View.VISIBLE);
                            weekEdit.setVisibility(View.VISIBLE);
                            confirmButton.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirm_marking_button:
                progressBar.setVisibility(View.VISIBLE);
                getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                StudyClass aClass = (StudyClass) classSpinner.getSelectedItem();
                Long classId = aClass.getID();

                Long activityId = Long.parseLong(activityIdEdit.getText().toString());

                Student student = (Student) studentSpinner.getSelectedItem();
                Long userId = student.getID();

                Integer weekNumber = Integer.parseInt(weekEdit.getText().toString());
                try {
                    backend.sendPresenceRequest(
                            classId, activityId, userId, weekNumber, this, this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public void onResponse(JSONObject response) {
        progressBar.setVisibility(View.INVISIBLE);
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        Toast.makeText(getContext(), "Successfully marked!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        progressBar.setVisibility(View.INVISIBLE);
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
    }
}
