package com.tolpe.tolpe;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;

import io.paperdb.Paper;

/**
 * Created by Dell 3450 on 1/9/2016.
 */
public class BalanceAddActivity extends Activity {
    TextView balanceText;
    int balance = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance_add);
        balanceText = (TextView) findViewById(R.id.balText);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabButton1);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
}
