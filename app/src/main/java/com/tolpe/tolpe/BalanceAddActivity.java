package com.tolpe.tolpe;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.getbase.floatingactionbutton.AddFloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import io.paperdb.Paper;

/**
 * Created by Dell 3450 on 1/9/2016.
 */
public class BalanceAddActivity extends Activity {
    TextView balanceText;
    int balance = 0;
    Context context;
    EditText amountEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance_add);
        context = this;
        balanceText = (TextView) findViewById(R.id.balText);
        final LinearLayout addMoneyLayout = (LinearLayout) findViewById(R.id.addMoneyLayout);
        amountEditText = (EditText) findViewById(R.id.amountEditText);
        TextView addMoneyButton = (TextView) addMoneyLayout.findViewById(R.id.addButton);
        addMoneyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMoney();
            }
        });
        AddFloatingActionButton fab = (AddFloatingActionButton) findViewById(R.id.fabButton1);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMoneyLayout.setVisibility(View.VISIBLE);
            }
        });
        String balanceStr = Paper.book().read("balance");
        try{
            balance = Integer.parseInt(balanceStr);
        }catch (Exception e){
            e.printStackTrace();
        }
        if(balance < 30){
            balanceText.setTextColor(Color.parseColor("#c0392b"));
        }
        balanceText.setText(balance+"");
    }

    private void addMoney() {
        String userID = getString(R.string.user_id);
        String amount = amountEditText.getText().toString();
        if(amount.equals("")){
            Toast.makeText(context, "Enter Valid Amount", Toast.LENGTH_SHORT).show();
        }
        else{
            try {
                int amountInt = Integer.parseInt(amount);
                String url = "http://10.10.30.161/add_money.php?amount="+amount+"&user_id="+userID;
                JsonObjectRequest payRequest = new JsonObjectRequest(Request.Method.GET, url, (String)null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    if(response.getBoolean("status")) {
                                        Paper.book().write("balance", response.getString("balance"));
                                        Toast.makeText(context, "Payment Successful", Toast.LENGTH_SHORT).show();
                                        balanceText.setText(response.getString("balance"));
                                        if(Integer.parseInt(response.getString("balance")) > 30){
                                            balanceText.setTextColor(Color.parseColor("#2ecc71"));
                                        }
                                    }
                                    else {
                                        Toast.makeText(context, "Unable to add money.", Toast.LENGTH_LONG).show();

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
            }catch (Exception e){
                Toast.makeText(context, "Enter Valid Amount", Toast.LENGTH_SHORT).show();
            }
        }

    }
}
