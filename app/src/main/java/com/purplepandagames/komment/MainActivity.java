package com.purplepandagames.komment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    public User user = new User();
    public List<Note> notes;
    SharedPreferences sharedPreferences;
    ActionBarDrawerToggle toggle;
    View headerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        notes = new ArrayList<Note>();
        NetworkHandler.main = this;
        NetworkHandler.Initialize();


        sharedPreferences = this.getSharedPreferences("com.purplepandagames.komment", Context.MODE_PRIVATE);
        user.username = sharedPreferences.getString("username", "");
        user.password = sharedPreferences.getString("password", "");

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
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new LoginFragment()).commit();
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            toggle.setDrawerIndicatorEnabled(false);
        }else{
            TextView usernameTextView = headerView.findViewById(R.id.username_header);
            usernameTextView.setText(getResources().getString(R.string.welcome) + " "  + user.username + "!");
            NetworkHandler.GetNotes();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new HomeFragment()).commit();
                break;

            case R.id.nav_account:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new AccountFragment()).commit();
                break;

            case R.id.nav_folder:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new FolderFragment()).commit();
                break;

            case R.id.nav_logout:
                Logout();
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed(){
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }
    }

    public void LoginUser(){
        Log.i("Info", "Logging in user!");
        sharedPreferences.edit().putString("username", user.username).apply();
        sharedPreferences.edit().putString("password", user.password).apply();

        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        toggle.setDrawerIndicatorEnabled(true);

        TextView usernameTextView = headerView.findViewById(R.id.username_header);
        usernameTextView.setText(getResources().getString(R.string.welcome) + " "  + user.username + "!");

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new HomeFragment()).commit();


    }



    public void Logout(){
        sharedPreferences.edit().putString("username", "").apply();
        sharedPreferences.edit().putString("password", "").apply();
    }
}
