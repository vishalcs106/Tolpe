package com.tolpe.tolpe;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.dlazaro66.qrcodereaderview.QRCodeReaderView;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity implements QRCodeReaderView.OnQRCodeReadListener {
    Context context;
    QRCodeReaderView qrcodeReader;
    boolean detected = false;
    TextView payButton;
    Dialog dialog;
    String selectedType = "";
    String tollID, amount1, amount2, amount;
    @Override
    protected  void onPause(){
        super.onPause();
        qrcodeReader.getCameraManager().stopPreview();
    }
    @Override
    protected  void onResume(){
        super.onResume();
        qrcodeReader.getCameraManager().startPreview();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        context = this;
        Paper.init(context);
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.layout_dialog);
        qrcodeReader = (QRCodeReaderView) findViewById(R.id.qrdecoderview);
        qrcodeReader.setOnQRCodeReadListener(this);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, BalanceAddActivity.class);
                startActivity(i);
            }
        });

    }

  @Override
    public void onQRCodeRead(final String text, PointF[] points) {
      final String detectedJSON = text;
      JsonParser jsonParser = new JsonParser();
      JsonElement element = jsonParser.parse(detectedJSON);
      tollID = element.getAsJsonObject().get("toll_id").toString();
      amount1 = element.getAsJsonObject().get("amount_1").toString();
      amount2 = element.getAsJsonObject().get("amount_2").toString();
      Log.d("Test", "detected ");
      if(!detected) {
          Log.d("Test", "detected " + detected);
          detected = true;
          qrcodeReader.getCameraManager().stopPreview();
          dialog.show();
          payButton = (TextView) dialog.findViewById(R.id.payButton);
          RelativeLayout oneWay = (RelativeLayout) dialog.findViewById(R.id.oneWay);
          RelativeLayout twoWay = (RelativeLayout) dialog.findViewById(R.id.twoWay);
          View.OnClickListener wayClickListener = new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                  payButton.setTextColor(Color.parseColor("#ffffff"));
                  payButton.setBackgroundColor(getResources().getColor(R.color.appColor));
                    switch (view.getId()){
                        case R.id.oneWay:
                            selectedType = "1";
                            amount = amount1;
                            RelativeLayout selected = (RelativeLayout) dialog.findViewById(R.id.oneWay);
                            selected.setBackground(getDrawable(R.drawable.shape_selected_circle));
                            TextView textView = (TextView) dialog.findViewById(R.id.oneWayText);
                            textView.setTextColor(Color.parseColor("#ffffff"));
                            RelativeLayout unSelected = (RelativeLayout) dialog.findViewById(R.id.twoWay);
                            unSelected.setBackground(getDrawable(R.drawable.shape_circle));
                            TextView textViewUn = (TextView) dialog.findViewById(R.id.twoWayText);
                            textViewUn.setTextColor(getResources().getColor(R.color.appColor));
                            break;
                        case R.id.twoWay:
                            selectedType = "2";
                            amount = amount2;
                            selected = (RelativeLayout) dialog.findViewById(R.id.twoWay);
                            selected.setBackground(getDrawable(R.drawable.shape_selected_circle));
                            textView = (TextView) dialog.findViewById(R.id.twoWayText);
                            textView.setTextColor(Color.parseColor("#ffffff"));
                            unSelected = (RelativeLayout) dialog.findViewById(R.id.oneWay);
                            unSelected.setBackground(getDrawable(R.drawable.shape_circle));
                            textViewUn = (TextView) dialog.findViewById(R.id.oneWayText);
                            textViewUn.setTextColor(getResources().getColor(R.color.appColor));
                            break;
                    }
              }
          };
          oneWay.setOnClickListener(wayClickListener);
          twoWay.setOnClickListener(wayClickListener);
          payButton.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                  payMoney(amount, selectedType, tollID);
                  dialog.dismiss();
                  qrcodeReader.getCameraManager().startPreview();
                  detected = false;
                  Log.d("Test", "detected "+detected);
              }
          });
      }
    }

    @Override
    public void cameraNotFound() {

    }

    @Override
    public void QRCodeNotFoundOnCamImage() {

    }

    public void payMoney(String amount, String type, String tollID){
        String userID = getString(R.string.user_id);
        String url = "http://10.10.30.161/pay_money.php?amount="+amount+"&user_id="+userID+"&ride_type="+type+"&toll_id="+tollID;
        JsonObjectRequest payRequest = new JsonObjectRequest(Request.Method.GET, url, (String)null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getBoolean("status")) {
                                Paper.book().write("balance", response.getString("balance"));
                                Toast.makeText(context, "Payment Successful", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                if(response.getString("reason").equals("low_balance")){
                                    Toast.makeText(context, "Insufficient Balance. Please add money.", Toast.LENGTH_LONG).show();
                                }
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
