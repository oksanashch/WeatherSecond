package com.example.weathersecond;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class FeedbackFragment extends Fragment {

    private EditText nameField;
    private EditText emailField;
    private EditText feedbackField;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_feedback, container, false);
        nameField = rootView.findViewById(R.id.EditTextName);
        emailField = rootView.findViewById(R.id.EditTextEmail);
        feedbackField = rootView.findViewById(R.id.EditTextFeedbackBody);
        final Button button = rootView.findViewById(R.id.ButtonSendFeedback);
        setHasOptionsMenu(true);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendFeedback();
            }
        });
        return rootView;
    }

    private void sendFeedback() {
        Toast.makeText(getActivity(), "Сообщение отправлено", Toast.LENGTH_LONG).show();
        nameField.setText("");
        emailField.setText("");
        feedbackField.setText("");
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item=menu.findItem(R.id.change_city);
        if(item!=null)
            item.setVisible(false);
    }

}

