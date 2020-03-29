package com.purplepandagames.komment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SharedFragment extends Fragment {

    private MainActivity main;
    private RecyclerView notesView;
    private SwipeRefreshLayout swiper;
    private View view;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_shared, container, false);
        notesView = view.findViewById(R.id.notes_view);
        swiper = view.findViewById(R.id.swipe_refresh);

        swiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Snackbar.make(view, R.string.missing_implementation, Snackbar.LENGTH_SHORT).show();
                if(main.sharedNotesId.length() > 6){
                    String[] noteIds = main.sharedNotesId.split("\\s*, \\s*");

                    for (String id: noteIds) {
                        id = id.substring(0, id.indexOf(","));
                        Log.i("LOADING SHARED", "onCreate: getting" + id);
                        main.linkClick = false;
                        NetworkHandler.GetNote("https://kommentapi.herokuapp.com/notes/" + id);
                    }
                }
                swiper.setRefreshing(false);
            }
        });

        SetNoteViewContent();

        return view;
    }

    private void SetNoteViewContent(){
        main  = (MainActivity) getActivity();

        notesView.setAdapter(getAdapter());
        notesView.setLayoutManager(new LinearLayoutManager(main));
        swiper.setRefreshing(false);
    }

    private RecyclerSharedViewAdapter getAdapter(){
        main = (MainActivity) getActivity();

        ArrayList<String> noteTitles = new ArrayList<>();

        for (int i = 0; i < main.sharedNotes.size(); i++){
            Note note = main.sharedNotes.get(i);
            noteTitles.add(note.title);
        }

        return new RecyclerSharedViewAdapter(noteTitles, main);
    }
}
