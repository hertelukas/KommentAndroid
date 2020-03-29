package com.purplepandagames.komment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SharedFragment extends Fragment {

    private  View view;
    private MainActivity main;
    private RecyclerView notesView;
    private RecyclerViewAdapter adapter;




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_shared, container, false);
        notesView = view.findViewById(R.id.notes_view);

        SetNoteViewContent();

        return view;
    }

    void SetNoteViewContent(){
        main  = (MainActivity) getActivity();

        notesView.setAdapter(getAdapter());
        notesView.setLayoutManager(new LinearLayoutManager(main));
    }

    private RecyclerViewAdapter getAdapter(){
        main = (MainActivity) getActivity();

        ArrayList<String> noteTitles = new ArrayList<>();

        for (int i = 0; i < main.sharedNotes.size(); i++){
            Note note = main.sharedNotes.get(i);
            noteTitles.add(note.title);
        }

        adapter = new RecyclerViewAdapter(noteTitles, main);
        return adapter;
    }
}
