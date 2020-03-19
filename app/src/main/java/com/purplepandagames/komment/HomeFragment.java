package com.purplepandagames.komment;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private ListView notesView;
    private MainActivity main;
    private SwipeRefreshLayout swiper;
    private TextView status;

    FloatingActionButton fab;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_home, container, false);
        NetworkHandler.homeFragment = this;

        fab = view.findViewById(R.id.createNote);
        swiper = view.findViewById(R.id.swipe_refresh);
        status = view.findViewById(R.id.home_status);

        swiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                NetworkHandler.GetNotes();
            }
        });

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

        return view;
    }

    void SetNoteViewContent(){
        main = (MainActivity) getActivity();

        status.setVisibility(View.INVISIBLE);

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
        swiper.setRefreshing(false);
    }

    void ReportError(String message){
        status.setVisibility(View.VISIBLE);
        status.setText(message);
    }
}
