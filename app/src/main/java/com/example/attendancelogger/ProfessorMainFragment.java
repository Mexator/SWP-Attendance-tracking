package com.example.attendancelogger;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class ProfessorMainFragment extends Fragment implements View.OnClickListener {
    public ProfessorMainFragment() {
        // Required empty public constructor
    }
    public static ProfessorMainFragment newInstance(String param1, String param2) {
        return new ProfessorMainFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_professor_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.button_mark_manually).setOnClickListener(this);
        view.findViewById(R.id.button_start_gathering_attendance).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_mark_manually:
                Navigation.findNavController(getView()).navigate(R.id.action_professor_mark_manually);
                break;
            case R.id.button_start_gathering_attendance:
                Navigation.findNavController(getView()).navigate(
                        R.id.action_professorMain_to_startGatheringAttendanceFragment);
                break;
        }
    }
}
