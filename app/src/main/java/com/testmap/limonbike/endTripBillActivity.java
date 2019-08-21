package com.testmap.limonbike;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class endTripBillActivity extends AppCompatActivity {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    TextView textView;
    TextView textView2;
    TextView textView3;
    String Mon = "Mon";
    String Tue = "Tue";
    String Wed = "Wed";
    String Thu = "Thu";
    String Fri = "Fri";
    String Sat = "Sat";
    String Sun = "Sun";
    DatabaseReference myRef = database.getReference("locations").child("startdate").child("hours");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_trip_bill);
        textView = findViewById(R.id.textView);
        textView2 = findViewById(R.id.textView9);
        textView3 = findViewById(R.id.textView10);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String name = user.getDisplayName();
        String[] output = name.split("start:");

        myRef = database.getReference("user").child(output[1]).child("enddate");

        myRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String valuestart = dataSnapshot.getValue(String.class);
                textView3.setText(valuestart);

            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName("end")
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                        }
                    }
                });


    }

    public void calculate(View view){

        SimpleDateFormat f = new SimpleDateFormat("MMM");
        String month = f.format(new Date());
        SimpleDateFormat f1 = new SimpleDateFormat("dd");
        String monthDayNumber = f1.format(new Date());
        Date now = new Date();

        SimpleDateFormat simpleDateformat = new SimpleDateFormat("E"); // the day of the week abbreviated
        String day = simpleDateformat.format(now);


        String[] output = textView3.getText().toString().split("time:");
        String[] output2 = output[1].split(monthDayNumber);
        String[] output3 = output2[1].split("GMT");

        textView3.setText(output3[0]);

    }

}
