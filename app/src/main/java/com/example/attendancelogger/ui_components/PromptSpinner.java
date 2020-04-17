package com.example.attendancelogger.ui_components;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

public class PromptSpinner extends androidx.appcompat.widget.AppCompatSpinner {
    public PromptSpinner(Context context) {
        super(context);
    }
    public PromptSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public void setAdapter(PromptSpinnerAdapter adapter){
        super.setAdapter(adapter);
        this.setSelection(adapter.getCount());
    }
}
