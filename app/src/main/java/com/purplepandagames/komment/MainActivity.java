package com.purplepandagames.komment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MAIN";
    private DrawerLayout drawer;
    public User user = new User();
    public List<Note> notes = new ArrayList<>();
    public String sharedNotesId;
    public List <Note> sharedNotes = new ArrayList<>();
    SharedPreferences sharedPreferences;
    ActionBarDrawerToggle toggle;
    View headerView;
    Boolean showingNote = false;
    public Note currentNote = null;
    public int currentIndex;
    public Boolean noteChanged = false;
    public Boolean newNote = false;
    public Boolean showingSettings = false;

    public static Boolean confirmDelete;

    NoteViewFragment noteViewFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();

        NetworkHandler.main = this;
        NetworkHandler.Initialize();

        sharedPreferences = this.getSharedPreferences("com.purplepandagames.komment", Context.MODE_PRIVATE);
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);


        //Load settings
        confirmDelete = settings.getBoolean("confirm_delete", true);


        user.username = sharedPreferences.getString("username", "");
        user.password = sharedPreferences.getString("password", "");

//        sharedNotesId = sharedPreferences.getString("sharedNotes", "");


//        if(sharedNotesId.length() > 6){
//            List<String> noteIds = Arrays.asList(sharedNotesId.split("\\s*, \\s*"));
//
//            for (String id: noteIds) {
//                id = id.substring(0, id.indexOf(","));
//                Log.i("LOADING SHARED", "onCreate: getting" + id);
//                NetworkHandler.GetNote("https://kommentapi.herokuapp.com/notes/" + id);
//            }
//        }


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        headerView = navigationView.getHeaderView(0);


        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }

        if(user.username.length() < 1)
        {
            showLogin();
        }else{
            TextView usernameTextView = headerView.findViewById(R.id.username_header);
            usernameTextView.setText(String.format("%s %s!", getResources().getString(R.string.welcome), user.username));
            //Handle link clicks
            String action = intent.getAction();
            Uri data = intent.getData();
            if(data != null){
                Log.i(TAG, "onCreate: Started with a link");
                newNote = false;
                NetworkHandler.GetNote(data.toString());
            }
            else{
                Log.i(TAG, "onCreate: Getting notes now");
                NetworkHandler.GetNotes();
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        this.setTitle(R.string.app_name);
        switch (item.getItemId()){
            case R.id.nav_home:
                ShowHome();
                break;

            case R.id.nav_folder:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new FolderFragment()).commit();
                break;

            case R.id.nav_shared_notes:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new SharedFragment()).commit();
                break;

            case R.id.nav_logout:
                Logout();
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void showNote(int index){
        currentNote = notes.get(index);
        currentIndex = index;
        showingNote = true;

        noteViewFragment = new NoteViewFragment();

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                noteViewFragment).commit();

        showingNote = true;
    }

    public void showNote(Note note){
        currentNote = note;
        currentIndex = -1;
        showingNote = true;

        noteViewFragment = new NoteViewFragment();

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                noteViewFragment).commit();

        showingNote = true;
    }

    public void showRegister(){
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new LoginFragment()).commit();
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        toggle.setDrawerIndicatorEnabled(false);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new RegisterFragment()).commit();
    }

    public void showLogin(){
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new LoginFragment()).commit();
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        toggle.setDrawerIndicatorEnabled(false);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new LoginFragment()).commit();
    }

    private void ShowHome(){
        NetworkHandler.GetNotes();
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        toggle.setDrawerIndicatorEnabled(true);

        TextView usernameTextView = headerView.findViewById(R.id.username_header);
        usernameTextView.setText(String.format("%s %s!", getResources().getString(R.string.welcome), user.username));

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new HomeFragment()).commit();
        showingNote = false;
        currentIndex = 1;
        currentNote = null;
        Log.i("Info", "Showing home done successfully");
    }

    private void ShowLogin(){
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new LoginFragment()).commit();
    }

    private void showSettings(){
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new SettingsFragment()).commit();
        this.setTitle(R.string.settings);
        showingSettings = true;
    }

    public void LoginUser(){
        sharedPreferences.edit().putString("username", user.username).apply();
        sharedPreferences.edit().putString("password", user.password).apply();

        ShowHome();
        NetworkHandler.GetNotes();
    }

    public void RegisterUser(){
        sharedPreferences.edit().putString("username", user.username).apply();
        sharedPreferences.edit().putString("password", user.password).apply();

        ShowHome();
    }

    public void Logout(){

        new MaterialAlertDialogBuilder(this)
                .setMessage(R.string.confirm_logout)
                .setTitle(R.string.logout)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sharedPreferences.edit().putString("username", "").apply();
                        sharedPreferences.edit().putString("password", "").apply();
                        sharedPreferences.edit().putString("sharedNotes", "").apply();
                        finish();
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();

    }

    public void DeleteAccount(){
        new MaterialAlertDialogBuilder(this)
                .setMessage(R.string.delete_account_summary)
                .setTitle(R.string.delete_account)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        NetworkHandler.deleteAccount();
                        sharedPreferences.edit().putString("username", "").apply();
                        sharedPreferences.edit().putString("password", "").apply();
                        finish();
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }

    public void SaveShared(){

        Log.i(TAG, "SAVING: " + sharedNotesId);
        sharedPreferences.edit().putString("sharedNotes", sharedNotesId).apply();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public void onBackPressed(){
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else if(showingNote){
            if(noteChanged && noteViewFragment.noteTitle != null && noteViewFragment.noteTitle.getText().toString().length() > 0  && noteViewFragment.noteTitle != null){
                new MaterialAlertDialogBuilder(this)
                        .setTitle(R.string.save)
                        .setMessage(R.string.saveDialog)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(currentIndex != -1){
                                    notes.get(currentIndex).content = noteViewFragment.noteContent.getText().toString();
                                    notes.get(currentIndex).title = noteViewFragment.noteTitle.getText().toString();
                                    currentNote = notes.get(currentIndex);
                                }
                                else{
                                    currentNote.content = noteViewFragment.noteContent.getText().toString();
                                    currentNote.title = noteViewFragment.noteTitle.getText().toString();
                                }
                                if(newNote){
                                    NetworkHandler.PostNote postTask = new NetworkHandler.PostNote();
                                    postTask.execute("https://kommentapi.herokuapp.com/notes/");
                                }else{
                                    NetworkHandler.UpdateNote updateTask = new NetworkHandler.UpdateNote();
                                    updateTask.execute("https://kommentapi.herokuapp.com/notes/" + currentNote.id);
                                }
                                ShowHome();
                            }
                        })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(newNote){
                                    notes.remove(currentIndex);
                                }
                                ShowHome();
                            }
                        })
                        .show();
            }
            else if(newNote){
                notes.remove(currentIndex);
                ShowHome();
            }
            else{
                ShowHome();
            }
        }else if(showingSettings){
            Log.i("USER", "Placeholder");
            this.setTitle(R.string.app_name);
            showingSettings = false;
            if(user.username != null && user.username.length() > 1){
                ShowHome();
            }
            else{
                ShowLogin();
            }
        }else{
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.settings:
                showSettings();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
