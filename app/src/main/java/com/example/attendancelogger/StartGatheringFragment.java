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
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.attendancelogger.system_logic.AttendanceBackend;

import org.json.JSONException;
import org.json.JSONObject;

public class StartGatheringFragment extends Fragment implements View.OnClickListener, Response.Listener<JSONObject>, Response.ErrorListener{
    private EditText classIdEdit, activityIdEdit, weekEdit;
    private AttendanceBackend backend;
    private View progressBar;

    public StartGatheringFragment(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        backend = AttendanceBackend.getInstance(getContext());
        return inflater.inflate(R.layout.fragment_start_gathering, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        classIdEdit = view.findViewById(R.id.class_id_edit);
        activityIdEdit=view.findViewById(R.id.activity_id_edit);
        weekEdit = view.findViewById(R.id.week_edit);

        view.findViewById(R.id.button_start_gathering_attendance_bluetooth).setOnClickListener(this);

        progressBar = new ProgressBar(getActivity(), null, android.R.attr.progressBarStyleLarge);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100, 100);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        LinearLayout l = getView().findViewById(R.id.teacher_bluetooth_marking_layout);
        l.addView(progressBar, params);
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_start_gathering_attendance_bluetooth:
                progressBar.setVisibility(View.VISIBLE);
                Long classId = Long.parseLong(classIdEdit.getText().toString());
                Long activityId = Long.parseLong(activityIdEdit.getText().toString());

                Long userId;
                userId = backend.getUser().getID();

                Integer weekNumber = Integer.parseInt(weekEdit.getText().toString());
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
