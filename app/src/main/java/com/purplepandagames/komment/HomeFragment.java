package com.purplepandagames.komment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private ListView notesView;
    private MainActivity main;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_home, container, false);
        NetworkHandler.homeFragment = this;

        notesView = view.findViewById(R.id.notes_view);
        main = (MainActivity) getActivity();
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
            }
        });
    }
}
