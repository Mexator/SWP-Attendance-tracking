package com.swp.attendancelogger.ui_components;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.swp.attendancelogger.system_logic.NameID;

import java.util.List;
/**
 * This was created in order to hide first prompt item for each spinner
 */
public class PromptSpinnerAdapter<T extends NameID> extends ArrayAdapter<String> {
    private List<T> objects;
    private String prompt;
    public PromptSpinnerAdapter(@NonNull Context context, @LayoutRes int resource,
                                @NonNull List<T> objects, @StringRes int promptResource){
        this(context,resource,objects,context.getResources().getString(promptResource));
    }
    public PromptSpinnerAdapter(@NonNull Context context, @LayoutRes int resource,
                                @NonNull List<T> objects, @NonNull String selectionPrompt){
        super(context, resource);

        this.objects = objects;
        this.prompt = selectionPrompt;

        for (T item:objects) {
            this.add(item.getName());
        }
        this.add(prompt);
    }
/*
  Two tricks to not show first element of the list in drop-down list:
  1. Add prompt as last item.
  2. Override getCount() so that it returns number of items 1 less then actual
  3. Set default selected item with index = getCount
 */
    @Override
    public final int getCount(){
        return super.getCount()-1;
    }

    /**
     * Returns i_th object, that the spinner shows in drop-down list. The prompt is not included
     * @param position position of item to return. Starts from zero. zero_th element is actual zero_th
     *                 element, not the prompt
     * @return element
     */
    public T getSelectedObject(int position){
        if(position == getCount()){
            Toast.makeText(getContext(), prompt, Toast.LENGTH_SHORT).show();
            return null;
        }
        return objects.get(position);
    }
}

