package com.purplepandagames.komment;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class NetworkHandler {
    private static String apiURL = "https://kommentapi.herokuapp.com";
    private static RequestQueue queue;

    static MainActivity main;
    static LoginFragment loginFragment;
    static HomeFragment homeFragment;



    public static void Initialize(){
        queue = Volley.newRequestQueue(main);
    }

    public static void Login(){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, apiURL + "/users", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    int code = response.getInt("code");
                    if(code == 104){
                        main.LoginUser();
                    }
                    else if(code == 401){
                        loginFragment.LoginFailed("Wrong password or username");
                    }
                    else{
                        loginFragment.LoginFailed("Login failed. Try again later.");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("username", main.user.username);
                headers.put("password", main.user.password);
                return headers;
            }
        };

        queue.add(jsonObjectRequest);
    }

    public static void GetNotes() {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, apiURL + "/notes", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject JResultUser = response.getJSONObject("user");
                    JSONArray JNotes = JResultUser.getJSONArray("notes");
                    for(int i = 0; i< JNotes.length(); i++) {
                        Note note = new Note();
                        JSONObject currentObject = JNotes.getJSONObject(i);
                        note.title = currentObject.getString("title");
                        note.content = currentObject.getString("content");
                        main.notes.add(note);
                    }
                    homeFragment.SetNoteViewContent();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("username", main.user.username);
                headers.put("password", main.user.password);
                return headers;
            }
        };

        queue.add(jsonObjectRequest);
    }
}
