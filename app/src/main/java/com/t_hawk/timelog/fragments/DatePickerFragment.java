package com.t_hawk.timelog.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import com.t_hawk.timelog.R;

import java.util.Calendar;

/**
 * Project: TimeLog
 * Created by Morten on 1/30/2017.
 *
 * @author Morten
 */

public class DatePickerFragment extends DialogFragment implements DatePicker.OnDateChangedListener, DatePickerDialog.OnDateSetListener {
    public interface OnDateSetListener
    {
        void onDateSet(DatePicker datePicker, int year, int month, int date);
    }

    @Override
    public void onDateChanged(DatePicker datePicker, int year, int month, int date) {
        mListener.onDateSet(datePicker, year, month, date);
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int date) {
        mListener.onDateSet(datePicker, year, month, date);
    }

    private OnDateSetListener mListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.datepicker_fragment, container, false);

        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePicker datePicker = (DatePicker) v.findViewById(R.id.datepicker);
        datePicker.init(year, month, day, this);

        return v;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public DatePickerFragment listener(OnDateSetListener listener)
    {
        mListener = listener;

        return this;
    }
}
