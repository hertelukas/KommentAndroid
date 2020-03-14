package com.purplepandagames.komment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class LoginFragment extends Fragment {

    private EditText username;
    private EditText password;

    private Button loginButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_login, container, false);
        username = view.findViewById(R.id.nameLoginInput);
        password = view.findViewById(R.id.passwordLoginInput);
        loginButton = view.findViewById(R.id.loginButton);

        final MainActivity main = (MainActivity) getActivity();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main.user.username = username.getText().toString();
                main.user.password = password.getText().toString();
                main.LoginUser();
            }
        });


        return  view;


    }

}
