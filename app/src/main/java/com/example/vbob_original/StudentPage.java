package com.example.vbob_original;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class StudentPage extends AppCompatActivity {

    FirebaseAuth auth;
    String studentID,studentName,studentBus;
    TextView detail_board;
    String arrivalTime="Bus not reached";
    String text="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_student_page);



        detail_board=findViewById(R.id.detail_board);
        //getting student details from firebase
        getUserDetails();

        //getting Bus arival time
        getBusArrivalTime();


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void getUserDetails()
    {
        auth=FirebaseAuth.getInstance();
        studentID=auth.getCurrentUser().getEmail().toString();
        //removing @student.com from studentID
        int index=studentID.indexOf("@");
        studentID=studentID.substring(0,index);

        //reading student details from Firebase
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().child("Student_Detail").child(studentID);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                studentName=snapshot.child("Name").getValue(String.class);
                studentBus=snapshot.child("Bus").getValue(String.class);
                text="StudentID: "+studentID+"\n\nStudent Name: "+studentName+"\n\nStudent Bus: "+studentBus+"\n\nBus Arrival: "+arrivalTime;
                detail_board.setText(text);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    public void getBusArrivalTime()
    {
        //getting Date First

        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String dateString = currentDateTime.format(dateFormatter);

        //reading of bus arriving time if data available
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference().child("Arrival_Detail").child(dateString);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren())
                {
                    if (dataSnapshot.getKey().toString().equals(studentBus))
                    {
                        arrivalTime=dataSnapshot.getValue(String.class);
                        text=text+"\n\nBus Arrival: "+arrivalTime;
                        detail_board.setText(text);
                        Toast.makeText(StudentPage.this, arrivalTime, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }



}