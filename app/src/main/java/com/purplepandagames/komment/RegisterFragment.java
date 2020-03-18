package com.purplepandagames.komment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class RegisterFragment extends Fragment {

    private EditText username;
    private EditText password;
    private EditText passwordConfirm;
    private TextView warningText;

    private Button loginButton;
    private Button registerButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_register, container, false);
        username = view.findViewById(R.id.nameRegisterInput);
        password = view.findViewById(R.id.passwordRegisterInput);
        passwordConfirm = view.findViewById(R.id.passwordConfirmInput);
        loginButton = view.findViewById(R.id.loginButton);
        registerButton = view.findViewById(R.id.registerButton);
        warningText = view.findViewById(R.id.warningText);

        NetworkHandler.registerFragment = this;

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

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(password.getText().toString().equals(passwordConfirm.getText().toString())){
                    if(username.getText().toString().length() < 5){
                        warningText.setText(R.string.usernametooshort);
                    }else if(password.getText().toString().length() < 8){
                        warningText.setText(R.string.passwordtooshort);
                    }else{
                        main.user.username = username.getText().toString();
                        main.user.password = password.getText().toString();
                        NetworkHandler.Register();
                    }

                }
                else{
                    warningText.setText(R.string.passwordsDoNotMatch);
                }
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                main.showLogin();
            }
        });
        return  view;
    }

    public void RegisterFailed(String reason){
        warningText.setText(reason);
    }

}
