package com.swp.attendancelogger;

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

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.swp.attendancelogger.system_logic.AttendanceBackend;

import org.json.JSONObject;


public class BluetoothMarkingFragment extends Fragment implements View.OnClickListener, Response.Listener<JSONObject>, Response.ErrorListener {
    private EditText classIdEdit, activityIdEdit, weekEdit;
    private AttendanceBackend backend;
    private View progressBar;

    public BluetoothMarkingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        backend = AttendanceBackend.getInstance(getContext());
        return inflater.inflate(R.layout.fragment_bluetooth_marking, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        classIdEdit = view.findViewById(R.id.class_id_edit);
        activityIdEdit=view.findViewById(R.id.activity_id_edit);
        weekEdit = view.findViewById(R.id.week_edit);

        view.findViewById(R.id.try_marking_button).setOnClickListener(this);

        progressBar = new ProgressBar(getActivity(), null, android.R.attr.progressBarStyleLarge);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100, 100);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        LinearLayout l = getView().findViewById(R.id.bluetooth_marking_layout);
        l.addView(progressBar, params);
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.confirm_marking_button:
                progressBar.setVisibility(View.VISIBLE);
                Long classId = Long.parseLong(classIdEdit.getText().toString());
                Long activityId = Long.parseLong(activityIdEdit.getText().toString());
                Long userId = backend.getUser().getID();
                Integer weekNumber = Integer.parseInt(weekEdit.getText().toString());
                break;
        }
    }

    @Override
    public void onResponse(JSONObject response) {
    }

    @Override
    public void onErrorResponse(VolleyError error) {
    }
}
