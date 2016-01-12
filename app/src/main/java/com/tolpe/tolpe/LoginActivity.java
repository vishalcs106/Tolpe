package com.tolpe.tolpe;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {
    EditText username, password;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        context = this;
        Paper.init(context);
        String userId = Paper.book().read("user_id");
        if(userId != null){
            Intent i = new Intent(context, MainActivity.class);
            startActivity(i);
            finish();
        }
        else {
            TextView login = (TextView) findViewById(R.id.login);
            username = (EditText) findViewById(R.id.userTextBox);
            password = (EditText) findViewById(R.id.passTextBox);
            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    login();
                }
            });
        }
    }

    private void login() {
        String url = "http://10.10.21.237/login_mob.php?user_id="+username.getText().toString()+"&pass="+password.getText().toString();
        JsonObjectRequest payRequest = new JsonObjectRequest(Request.Method.GET, url, (String)null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getBoolean("status")) {
                                Paper.book().write("user_id", username.getText().toString());
                                Intent i = new Intent(context, MainActivity.class);
                                startActivity(i);
                                finish();
                            }
                            else {

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("getInterestDigest", "Error: " + error.getMessage());

            }
        });
        AppController.getInstance().addToRequestQueue(payRequest);
    }
}
