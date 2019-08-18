package com.testmap.limonbike;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class endTripBillActivity extends AppCompatActivity {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("918273645").child("startdate").child("hours");
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_trip_bill);
        textView = findViewById(R.id.textView);

/*        myRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int valuestart = dataSnapshot.getValue(int.class);

                myRef = database.getReference((String) getText(R.string.firebase_email)).child("enddate").child("hours");

                myRef.addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String valueend = dataSnapshot.getValue(String.class);

                        int valueend2 = Integer.parseInt(valueend);

                        String valueOfStartTimeString = valueend2 + "-endhour";
                        textView2.setText(valueOfStartTimeString);

                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });*/
    }


}
