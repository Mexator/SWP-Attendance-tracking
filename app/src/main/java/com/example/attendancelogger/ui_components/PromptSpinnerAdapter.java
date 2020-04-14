package com.example.attendancelogger.ui_components;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.attendancelogger.NameID;

import java.util.List;
/**
 * This was created in order to hide first prompt item for each spinner
 */
public class PromptSpinnerAdapter<T extends NameID> extends ArrayAdapter<String> {
    private List<T> objects;
    public PromptSpinnerAdapter(@NonNull Context context, @LayoutRes int resource,
                                @NonNull List<T> objects, @NonNull String selectionPrompt){
        super(context, resource);

        this.objects = objects;

        for (T item:objects) {
            this.add(item.getName());
        }
        this.add(selectionPrompt);
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
        position++;
        return objects.get(position);
    }
}

