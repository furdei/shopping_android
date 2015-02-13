package com.furdey.shopping.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

import com.furdey.shopping.R;
import com.furdey.shopping.ShoppingApplication;
import com.furdey.shopping.content.AlarmUtils;
import com.furdey.shopping.utils.PreferencesManager;

import java.util.Calendar;

public class AlarmTimePickerFragment extends DialogFragment {

    public interface AlarmTimePickerListener {
        void onAlarmTimeSet(int hourOfDay, int minute, int repeat);
    }

    public static AlarmTimePickerFragment newInstance(int alarmHour, int alarmMinute, int repeatDays) {
        Bundle params = new Bundle();
        params.putInt(PARAM_ALARM_HOUR, alarmHour);
        params.putInt(PARAM_ALARM_MINUTE, alarmMinute);
        params.putInt(PARAM_ALARM_REPEAT, repeatDays);
        AlarmTimePickerFragment fragment = new AlarmTimePickerFragment();
        fragment.setArguments(params);
        return fragment;
    }

    private final static String PARAM_ALARM_HOUR = "alarmHour";
    private final static String PARAM_ALARM_MINUTE = "alarmMinute";
    private final static String PARAM_ALARM_REPEAT = "alarmRepeat";

    private AlarmTimePickerListener listener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (AlarmTimePickerListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement "
                    + AlarmTimePickerListener.class.getSimpleName());
        }    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int hour = getArguments().getInt(PARAM_ALARM_HOUR);
        int minute = getArguments().getInt(PARAM_ALARM_MINUTE);
        final int repeat = getArguments().getInt(PARAM_ALARM_REPEAT);

        if (hour == PreferencesManager.ALARM_HOUR_UNSPECIFIED) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            hour = c.get(Calendar.HOUR_OF_DAY);
            minute = c.get(Calendar.MINUTE);
        }

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.alert_timer_dialog, null, false);

        final TimePicker timePicker = (TimePicker) dialogView.findViewById(R.id.alertTimerDialogTimePicker);
        timePicker.setIs24HourView(true);
        timePicker.setCurrentHour(hour);
        timePicker.setCurrentMinute(minute);

        final TextView repeatView = (TextView) dialogView.findViewById(R.id.alertTimerDialogRepeatInterval);
        repeatView.setText(AlarmUtils.getRepeatString(getActivity(), repeat));

        final String[] daysOfWeek = getActivity().getResources().getStringArray(R.array.daysOfWeek);

        View repeatButton = dialogView.findViewById(R.id.alertTimerDialogRepeatButton);
        repeatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int repeat = getArguments().getInt(PARAM_ALARM_REPEAT);
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setCancelable(true)
                        .setMultiChoiceItems(daysOfWeek, AlarmUtils.getRepeatArray(repeat), new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                int repeat = getArguments().getInt(PARAM_ALARM_REPEAT);

                                if (isChecked) {
                                    int mask = 1 << which;
                                    repeat = repeat | mask;
                                } else {
                                    int mask = 127 - (1 << which);
                                    repeat = repeat & mask;
                                }

                                getArguments().putInt(PARAM_ALARM_REPEAT, repeat);
                            }
                        })
                        .setNegativeButton(R.string.formButtonCancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setPositiveButton(R.string.formButtonSave, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int repeat = getArguments().getInt(PARAM_ALARM_REPEAT);
                                repeatView.setText(AlarmUtils.getRepeatString(getActivity(), repeat));
                            }
                        })
                        .setTitle(R.string.alertTimerDialogTitle);
                builder.show();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(true)
                .setNegativeButton(R.string.formButtonCancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setPositiveButton(R.string.formButtonSave, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int hour = timePicker.getCurrentHour();
                        int minute = timePicker.getCurrentMinute();
                        listener.onAlarmTimeSet(hour, minute, getArguments().getInt(PARAM_ALARM_REPEAT));
                    }
                })
                .setTitle(R.string.alertTimerDialogTitle)
                .setView(dialogView);

        ((ShoppingApplication) getActivity().getApplication())
                .trackViewScreen(AlarmTimePickerFragment.class);

        return builder.create();
    }

}
