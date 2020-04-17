package com.example.attendancelogger;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.attendancelogger.system_logic.AttendanceBackend;
import com.example.attendancelogger.system_logic.Class;
import com.example.attendancelogger.system_logic.User;
import com.example.attendancelogger.ui_components.PromptSpinner;
import com.example.attendancelogger.ui_components.PromptSpinnerAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ManualMarkingFragment extends Fragment implements View.OnClickListener, Response.Listener<JSONObject>, Response.ErrorListener {
    private EditText classIdEdit, activityIdEdit, userIdEdit, weekEdit;
    private AttendanceBackend backend;
    private View progressBar;
    private PromptSpinner classSpinner;

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

        userIdEdit = view.findViewById(R.id.user_id_edit);
        classIdEdit = view.findViewById(R.id.class_id_edit);
        activityIdEdit=view.findViewById(R.id.activity_id_edit);
        weekEdit = view.findViewById(R.id.week_edit);

        view.findViewById(R.id.confirm_marking_button).setOnClickListener(this);

        progressBar = new ProgressBar(getActivity(), null, android.R.attr.progressBarStyleLarge);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100, 100);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        LinearLayout l = getView().findViewById(R.id.professor_manual_layout);
        l.addView(progressBar, params);
        progressBar.setVisibility(View.INVISIBLE);

        classSpinner = view.findViewById(R.id.class_spinner);
        setupClassSpinner();
    }

    /**
     * Receives list of classes from server, fills the adapter
     * and connect it to the spinner
     */
    private void setupClassSpinner(){
        assert backend != null;
        final List<Class> classes = new LinkedList<>();
        //Obtain list of classes
        backend.sendClassesRequest(new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray rawClasses = (JSONArray)response.get("classes");
                    for (int i=0;i<rawClasses.length();i++){
                        classes.add(Class.parseClass((JSONObject)rawClasses.get(i)));
                    }
                    //Add the list to the adapter
                    PromptSpinnerAdapter<Class> adapter = new PromptSpinnerAdapter<>(
                            getContext(),R.layout.support_simple_spinner_dropdown_item,
                            classes,R.string.prompt_class_select);
                    classSpinner.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.confirm_marking_button:
                progressBar.setVisibility(View.VISIBLE);

                int pos = classSpinner.getSelectedItemPosition();
                Class aClass = ((PromptSpinnerAdapter<Class>)classSpinner.getAdapter()).
                        getSelectedObject(pos);
                Long classId = aClass.getID();

                Long activityId = Long.parseLong(activityIdEdit.getText().toString());

                Long userId;
                if(backend.getUser().getRole() == User.Roles.STUDENT)
                    userId = backend.getUser().getID();
                else
                    userId = Long.parseLong(userIdEdit.getText().toString());

                Integer weekNumber = Integer.parseInt(weekEdit.getText().toString());
                try {
                    backend.sendPresenceRequest(
                            classId,activityId,userId,weekNumber,this,this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public void onResponse(JSONObject response) {
        progressBar.setVisibility(View.INVISIBLE);
        Toast.makeText(getContext(),"Successfully marked!",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(getContext(),"Error",Toast.LENGTH_SHORT).show();
    }
}
