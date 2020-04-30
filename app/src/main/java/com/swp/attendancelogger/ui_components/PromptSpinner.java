package com.swp.attendancelogger.ui_components;

import android.content.Context;
import android.util.AttributeSet;

public class PromptSpinner extends androidx.appcompat.widget.AppCompatSpinner {
    private PromptSpinnerAdapter adapter;
    public PromptSpinner(Context context) {
        super(context);
    }
    public PromptSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setAdapter(PromptSpinnerAdapter adapter){
        super.setAdapter(adapter);
        this.adapter = adapter;
        this.setSelection(adapter.getCount(),false);
    }

    @Override
    public PromptSpinnerAdapter getAdapter() {
        return adapter;
    }

    @Override
    public Object getSelectedItem() {
        int position = this.getSelectedItemPosition();
        return this.adapter.getSelectedObject(position);
    }
}
