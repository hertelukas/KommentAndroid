package com.purplepandagames.komment;

import android.content.Context;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
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
                        note.id = currentObject.getString("_id");
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
//                    urlConnection.addRequestProperty("Accept", "application/json");
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

        //        public static MainActivity main;
//        private static String apiURL = "https://kommentapi.herokuapp.com";
//
//
//        @Override
//        protected String doInBackground(String... strings) {
//            String urlString = apiURL + "/notes/" + main.currentNote.id;
//            Log.i("Info", "Updating note to " + urlString);
//            URL url = null;
//            try {
//                url = new URL(urlString);
//
//                try {
//                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
//                    con.setRequestMethod("PUT");
//                    con.setRequestProperty("username", main.user.username);
//                    con.setRequestProperty("password", main.user.password);
//                    con.setRequestProperty("Content-Type", "application/json; utf-8");
//
//                    con.setDoOutput(true);
//                    String jsonBodyString = String.format("{title: %s, content: %s}", main.currentNote.title, main.currentNote.content);
//                    Log.i("JSON String", jsonBodyString);
//                    try(OutputStream os = con.getOutputStream()){
//                        byte[] input = jsonBodyString.getBytes("utf-8");
//                        os.write(input, 0, input.length);
//                    }
//
//
//                } catch (IOException e) {
//                    Log.e("error", e.toString());
//                    e.printStackTrace();
//                }
//
//            } catch (MalformedURLException e) {
//                Log.e("error", e.toString());
//                e.printStackTrace();
//            }
//            return null;
//        }

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

        //        public static MainActivity main;
//        private static String apiURL = "https://kommentapi.herokuapp.com";
//
//
//        @Override
//        protected String doInBackground(String... strings) {
//            String urlString = apiURL + "/notes/" + main.currentNote.id;
//            Log.i("Info", "Updating note to " + urlString);
//            URL url = null;
//            try {
//                url = new URL(urlString);
//
//                try {
//                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
//                    con.setRequestMethod("PUT");
//                    con.setRequestProperty("username", main.user.username);
//                    con.setRequestProperty("password", main.user.password);
//                    con.setRequestProperty("Content-Type", "application/json; utf-8");
//
//                    con.setDoOutput(true);
//                    String jsonBodyString = String.format("{title: %s, content: %s}", main.currentNote.title, main.currentNote.content);
//                    Log.i("JSON String", jsonBodyString);
//                    try(OutputStream os = con.getOutputStream()){
//                        byte[] input = jsonBodyString.getBytes("utf-8");
//                        os.write(input, 0, input.length);
//                    }
//
//
//                } catch (IOException e) {
//                    Log.e("error", e.toString());
//                    e.printStackTrace();
//                }
//
//            } catch (MalformedURLException e) {
//                Log.e("error", e.toString());
//                e.printStackTrace();
//            }
//            return null;
//        }

    }



}
