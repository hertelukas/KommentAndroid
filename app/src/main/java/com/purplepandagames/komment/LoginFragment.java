package com.purplepandagames.komment;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class LoginFragment extends Fragment {

    private EditText username;
    private EditText password;
    private TextView warningText;

    private Button loginButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_login, container, false);
        username = view.findViewById(R.id.nameLoginInput);
        password = view.findViewById(R.id.passwordLoginInput);
        loginButton = view.findViewById(R.id.loginButton);
        warningText = view.findViewById(R.id.warningText);

        NetworkHandler.loginFragment = this;

        final MainActivity main = (MainActivity) getActivity();

        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                warningText.setText("");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                warningText.setText("");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main.user.username = username.getText().toString();
                main.user.password = password.getText().toString();
                NetworkHandler.Login();
            }

        });
        return  view;
    }

    public void LoginFailed(String reason){
        warningText.setText(reason);
    }

}
