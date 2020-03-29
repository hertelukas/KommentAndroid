package com.purplepandagames.komment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.w3c.dom.Text;

import java.util.ArrayList;

import static com.android.volley.VolleyLog.TAG;

public class HomeFragment extends Fragment {

    private RecyclerView notesView;
    private MainActivity main;
    private SwipeRefreshLayout swiper;
    private TextView status;
    public static boolean reloading = false;

    private  View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view =inflater.inflate(R.layout.fragment_home, container, false);
        NetworkHandler.homeFragment = this;

        FloatingActionButton fab = view.findViewById(R.id.createNote);
        swiper = view.findViewById(R.id.swipe_refresh);
        notesView = view.findViewById(R.id.notes_view);

        swiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                reloading = true;
                NetworkHandler.GetNotes();
            }
        });


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

        SetNoteViewContent();

        return view;
    }

    private boolean delete = true;
    private boolean makePublic = true;

    private boolean localIsPublic;


    void SetNoteViewContent(){
        main = (MainActivity) getActivity();

        notesView.setAdapter(getAdapter());
        notesView.setLayoutManager(new LinearLayoutManager(main));


        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {
                if(direction == ItemTouchHelper.LEFT){
                    delete = true;
                    if(main.confirmDelete){
                        new MaterialAlertDialogBuilder(main)
                                .setMessage(R.string.confirm_delete)
                                .setTitle(R.string.delete)
                                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        NetworkHandler.deleteNote(viewHolder.getAdapterPosition());
                                    }
                                })
                                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        notesView.setAdapter(getAdapter());
                                    }
                                })
                                .show();
                    }
                    else{
                        Snackbar.make(view, R.string.deleted_note, Snackbar.LENGTH_LONG)
                                .setAction(R.string.undo, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        delete = false;
                                        notesView.setAdapter(getAdapter());
                                    }
                                }).show();

                        Handler handler = new Handler();

                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if(delete){
                                    NetworkHandler.deleteNote(viewHolder.getAdapterPosition());
                                }

                            }
                        }, 2750);
                    }

                }
                else{
                    makePublic = true;
                    final int index = viewHolder.getAdapterPosition();
                    final Note note = main.notes.get(index);
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text) + " https://kommentapi.herokuapp.com/notes/" + note.id);
                    sendIntent.setType("text/plain");

                    Intent shareIntent = Intent.createChooser(sendIntent, null);
                    startActivity(shareIntent);


                    if(!note.isPublic) {
                        Snackbar.make(view, R.string.make_public, Snackbar.LENGTH_LONG)
                                .setAction(R.string.undo, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        makePublic = false;
                                    }
                                }).show();

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                notesView.setAdapter(getAdapter());

                            }
                        }, 200);
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (makePublic) {

                                    NetworkHandler.makePublic updateTask = new NetworkHandler.makePublic();
                                    updateTask.execute("https://kommentapi.herokuapp.com/notes/" + note.id, note.title, note.content, "true");
                                    localIsPublic = true;
                                }
                            }
                        }, 2750);
                    }else {
                        Snackbar.make(view, R.string.note_already_public, Snackbar.LENGTH_LONG)
                                .setAction(R.string.make_private, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        NetworkHandler.makePublic updateTask = new NetworkHandler.makePublic();
                                        updateTask.execute("https://kommentapi.herokuapp.com/notes/" + note.id, note.title, note.content, "false");
                                        localIsPublic = false;
                                    }
                                }).show();
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                notesView.setAdapter(getAdapter());
                                main.notes.get(index).isPublic = localIsPublic;

                            }
                        }, 200);
                    }
                }
            }
        }).attachToRecyclerView(notesView);

        reloading = false;
        swiper.setRefreshing(false);
    }

    void ReportError(String message){
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
        swiper.setRefreshing(false);
    }

    void showError(String message){
        Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT).show();
    }

    private RecyclerViewAdapter getAdapter(){
        main = (MainActivity) getActivity();

        Log.i(TAG, "getAdapter: " + main.notes.size());

        ArrayList<String> noteTitles = new ArrayList<>();

        for (int i = 0; i< main.notes.size(); i++){
            Note note = main.notes.get(i);
            noteTitles.add(note.title);
        }

        RecyclerViewAdapter adapter = new RecyclerViewAdapter(noteTitles, main);
        return adapter;
    }
    void onSuccessDeleting(int index){
        notesView.removeViewAt(index);
        SetNoteViewContent();

    }
}
