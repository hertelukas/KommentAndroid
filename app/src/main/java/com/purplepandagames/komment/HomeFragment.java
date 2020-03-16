package com.purplepandagames.komment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private ListView notesView;
    private MainActivity main;

    FloatingActionButton fab;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_home, container, false);
        NetworkHandler.homeFragment = this;

        fab = view.findViewById(R.id.createNote);

        main = (MainActivity) getActivity();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Note note = new Note();
                note.title = "";
                note.content = "";
                main.newNote = true;
                main.notes.add(note);
                main.showNote(main.notes.size()-1);
            }
        });

        notesView = view.findViewById(R.id.notes_view);
        SetNoteViewContent();

        return view;
    }

    void SetNoteViewContent(){
        main = (MainActivity) getActivity();

        ArrayList<String> noteTitles = new ArrayList<>();

        for (int i = 0; i< main.notes.size(); i++){
            Note note = main.notes.get(i);
            noteTitles.add(note.title);
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(main, android.R.layout.simple_list_item_1, noteTitles);

        notesView.setAdapter(arrayAdapter);

        notesView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                main.showNote(position);
                main.newNote = false;
            }
        });
    }
}
