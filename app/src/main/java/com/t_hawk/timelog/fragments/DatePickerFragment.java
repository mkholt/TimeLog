package com.t_hawk.timelog.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.util.Calendar;

/**
 * Project: TimeLog
 * Created by Morten on 1/30/2017.
 *
 * @author Morten
 */

public class DatePickerFragment extends DialogFragment {
    private DatePickerDialog.OnDateSetListener mListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        if (null == mListener) throw new NullPointerException("Listener not set");

        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), mListener, year, month, day);
    }

    public DatePickerFragment listener(DatePickerDialog.OnDateSetListener listener)
    {
        mListener = listener;

        return this;
    }
}
