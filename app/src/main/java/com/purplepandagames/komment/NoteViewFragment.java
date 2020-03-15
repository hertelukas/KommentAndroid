package com.purplepandagames.komment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class NoteViewFragment extends Fragment {

    public EditText noteTitle;
    public EditText noteContent;
    private MainActivity main;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note_view, container, false);
        main = (MainActivity) getActivity();
        noteTitle = view.findViewById(R.id.note_title);
        noteContent = view.findViewById(R.id.note_content);
        LoadNote(main.currentNote);


        noteTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                main.noteChanged = true;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        noteContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                main.noteChanged = true;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        main.noteChanged = false;
        return view;
    }

    private void LoadNote(Note note){
        noteTitle.setText(note.title);
        noteContent.setText(note.content);
    }
}
