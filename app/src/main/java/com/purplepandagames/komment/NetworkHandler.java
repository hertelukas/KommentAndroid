package com.purplepandagames.komment;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
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

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class NetworkHandler {
    private static String apiURL = "https://kommentapi.herokuapp.com";
    private static RequestQueue queue;

    static MainActivity main;
    static LoginFragment loginFragment;
    static HomeFragment homeFragment;
    static RegisterFragment registerFragment;



    public static void Initialize(){
        queue = Volley.newRequestQueue(main);
    }

    public static void Login(){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, apiURL + "/users", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    int code = response.getInt("code");

                    Log.i("code", "" + code);
                    if(code == 104){
                        JSONObject user = response.getJSONObject("user");
                        String id = user.getString("_id");
                        main.LoginUser();
                        main.user.id = id;
                    }
                    else if(code == 401){
                        Log.i("LOGIN", "Unauthenticated");
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
                loginFragment.LoginFailed("Login failed. Try again later.");
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

    public static void Register(){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, apiURL + "/users", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    int code = response.getInt("code");
                    if (code == 100) {
                        main.RegisterUser();
                    }
                    else if(code == 101){
                        registerFragment.RegisterFailed("Username exists");
                    }
                    else {
                        registerFragment.RegisterFailed("Registration failed. Try again later");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener(){
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

    public static void GetNote(String url){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                main.notes.clear();
                try {
                    Log.i("Response", response.toString());
                    JSONObject JNote = response.getJSONObject("note");
                    //int code = response.getInt("code");
                    int code = 0;
                    if(code == 0){
                        Note note = new Note();
                        note.title = JNote.getString("title");
                        note.content = JNote.getString("title");
                        note.id = JNote.getString("_id");
                        main.showNote(note);
                    }else if(code == 0){
                        homeFragment.ReportError(main.getResources().getString(R.string.no_notes));
                    }else{
                        Log.i("Server Error", "Code " + code);
                        homeFragment.ReportError(main.getResources().getString(R.string.server_error));
                    }
                } catch (JSONException e) {
                    Log.i("JSON Error", e.getMessage());
                    homeFragment.ReportError(main.getResources().getString(R.string.server_error));
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("Server Error", error.getMessage());
                homeFragment.ReportError(main.getResources().getString(R.string.server_error));

            }
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
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
                main.notes.clear();
                try {
                    JSONObject JResultUser = response.getJSONObject("user");
                    JSONArray JNotes = JResultUser.getJSONArray("notes");

                    int code = response.getInt("code");

                    for(int i = 0; i< JNotes.length(); i++) {
                        Note note = new Note();
                        JSONObject currentObject = JNotes.getJSONObject(i);
                        note.title = currentObject.getString("title");
                        note.content = currentObject.getString("content");
                        note.id = currentObject.getString("_id");
                        main.notes.add(note);
                    }
                    if(main.notes.size() > 0 && code == 0){
                        homeFragment.SetNoteViewContent();
                    }else if(code == 0){
                        homeFragment.ReportError(main.getResources().getString(R.string.no_notes));
                    }else{
                        Log.i("Server Error", "Code " + code);
                        homeFragment.ReportError(main.getResources().getString(R.string.server_error));
                    }
                } catch (JSONException e) {
                    Log.i("JSON Error", e.getMessage());
                    homeFragment.ReportError(main.getResources().getString(R.string.server_error));
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("Server Error", error.getMessage());
                homeFragment.ReportError(main.getResources().getString(R.string.server_error));

            }
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("username", main.user.username);
                headers.put("password", main.user.password);
                return headers;
            }
        };

        queue.add(jsonObjectRequest);
    }

    public static void deleteNote(final int index){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, apiURL + "/notes/" + main.notes.get(index).id, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    int code = response.getInt("code");
                    Log.i("code", "" + code);
                    if(code == 401){
                        homeFragment.showError("Unauthorized");
                    }
                    else if(code == 404){
                        homeFragment.showError("Not found");
                    }
                    else if(code != 200){
                        homeFragment.showError("Server error.");
                    }else {
                        main.notes.remove(index);
                        homeFragment.onSuccessDeleting(index);
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

    public static void deleteAccount(){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, apiURL + "/users/" + main.user.id, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    int code = response.getInt("code");
                    Log.i("code", "" + code);
                    if(code == 401){
                        homeFragment.showError("Unauthorized");
                    }
                    else if(code == 404){
                        homeFragment.showError("Not found");
                    }
                    else if(code != 200){
                        homeFragment.showError("Server error.");
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

    public static class makePublic  extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;
            JSONObject body = new JSONObject();
            try {
                body.put("title", urls[1]);
                body.put("content", urls[2]);
                body.put("public", "true");
                try {
                    url = new URL(urls[0]);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("PUT");
                    urlConnection.addRequestProperty("username", main.user.username);
                    urlConnection.addRequestProperty("password", main.user.password);
                    urlConnection.addRequestProperty("Content-Type", "application/json");
                    urlConnection.setDoOutput(true);

                    try(OutputStream os = urlConnection.getOutputStream()){
                        Log.i("Body String", body.toString());
                        byte[] input = body.toString().getBytes("utf-8");
                        Log.i("Body", input.toString());

                        os.write(input, 0, input.length);
                    }

                    InputStream in = urlConnection.getInputStream();
                    InputStreamReader reader = new InputStreamReader(in);
                    int data = reader.read();
                    while (data != -1){
                        char current = (char) data;
                        result += current;
                        data += current;
                        data = reader.read();
                    }
                    return result;

                }catch (Exception e){
                    e.printStackTrace();
                    return null;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return  null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.i("JSON", s);
        }
    }

    public static class UpdateNote  extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;
            JSONObject body = new JSONObject();
            try {
                body.put("title", main.currentNote.title);
                body.put("content", main.currentNote.content);
                try {
                    url = new URL(urls[0]);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("PUT");
                    urlConnection.addRequestProperty("username", main.user.username);
                    urlConnection.addRequestProperty("password", main.user.password);
                    urlConnection.addRequestProperty("Content-Type", "application/json");
                    urlConnection.setDoOutput(true);

                    try(OutputStream os = urlConnection.getOutputStream()){
                        Log.i("Body String", body.toString());
                        byte[] input = body.toString().getBytes("utf-8");
                        Log.i("Body", input.toString());

                        os.write(input, 0, input.length);
                    }

                    InputStream in = urlConnection.getInputStream();
                    InputStreamReader reader = new InputStreamReader(in);
                    int data = reader.read();
                    while (data != -1){
                        char current = (char) data;
                        result += current;
                        data += current;
                        data = reader.read();
                    }
                    return result;

                }catch (Exception e){
                    e.printStackTrace();
                    return null;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return  null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.i("JSON", s);
        }
    }

    public static class PostNote  extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;
            JSONObject body = new JSONObject();
            try {
                body.put("title", main.currentNote.title);
                body.put("content", main.currentNote.content);
                try {
                    url = new URL(urls[0]);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("POST");
                    urlConnection.addRequestProperty("username", main.user.username);
                    urlConnection.addRequestProperty("password", main.user.password);
                    urlConnection.addRequestProperty("Content-Type", "application/json");
                    urlConnection.setDoOutput(true);

                    try(OutputStream os = urlConnection.getOutputStream()){
                        Log.i("Body String", body.toString());
                        byte[] input = body.toString().getBytes("utf-8");
                        Log.i("Body", input.toString());

                        os.write(input, 0, input.length);
                    }

                    InputStream in = urlConnection.getInputStream();
                    InputStreamReader reader = new InputStreamReader(in);
                    int data = reader.read();
                    while (data != -1){
                        char current = (char) data;
                        result += current;
                        data += current;
                        data = reader.read();
                    }
                    return result;

                }catch (Exception e){
                    e.printStackTrace();
                    return null;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return  null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.i("JSON", s);
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) main.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }



}
