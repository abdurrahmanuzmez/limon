package com.testmap.limonbike;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Date;

public class attentionActivity extends AppCompatActivity {
    FirebaseDatabase firebaseDatabase;
    DatabaseReference myRef;
    private FirebaseAuth mAuth;

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attention);

        textView = textView.findViewById(R.id.textView2);

    }
    public void startPasswordAct(View view){
        mAuth = FirebaseAuth.getInstance();

        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference();

        //Intent intent = getIntent();
        //String qrCodeNameValue = intent.getStringExtra("qrCodeNameValue");
        //String emailValue = intent.getStringExtra("emailValue");

        //Date currentTime = Calendar.getInstance().getTime();

        //myRef.child("user").child("emailValue").child("startdate").setValue(currentTime);

        //intent = new Intent(getApplicationContext(),showPasswordActivity.class);
        //intent.putExtra("confirmValue", qrCodeNameValue);

        //textView.setText(qrCodeNameValue);
        //startActivity(intent);
    }


}