package com.example.weathersecond;



import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class FeedbackFragment extends Fragment {

    EditText nameField;
    EditText emailField;
    EditText feedbackField;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_feedback, container, false);
        nameField = rootView.findViewById(R.id.EditTextName);
        emailField = rootView.findViewById(R.id.EditTextEmail);
        feedbackField =rootView.findViewById(R.id.EditTextFeedbackBody);
        final Button button = rootView.findViewById(R.id.ButtonSendFeedback);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendFeedback(v);
            }
        });
        return rootView;
    }


    public void sendFeedback(View v) {
        Toast.makeText(getActivity(), "Сообщение отправлено", Toast.LENGTH_LONG).show();
        nameField.setText("");
        emailField.setText("");
        feedbackField.setText("");
    }





}
