package com.example.basecalculator;

import android.content.Context;
import android.util.AttributeSet;

public class EditTextC extends androidx.appcompat.widget.AppCompatEditText {

    notes anouncement;

    public void setAnouncement(notes anouncement) {
        this.anouncement = anouncement;
    }

    public EditTextC(Context context, AttributeSet attrs,
                     int defStyle) {
        super(context, attrs, defStyle);

    }

    public EditTextC(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public EditTextC(Context context) {
        super(context);
    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        if (anouncement != null) {
            anouncement.setButtons(selStart, selEnd);
        }
    }

}
