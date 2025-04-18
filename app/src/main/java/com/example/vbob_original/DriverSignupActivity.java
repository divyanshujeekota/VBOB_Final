package com.example.vbob_original;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class DriverSignupActivity extends AppCompatActivity {


    EditText reg,pass,bus_num;
    FirebaseAuth auth;
    Button signup_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_driver_signup);

        auth=FirebaseAuth.getInstance();
        reg=findViewById(R.id.regno_input);
        pass=findViewById(R.id.password_input);
        signup_button=findViewById(R.id.signup_button_signuppage);
        bus_num=findViewById(R.id.bus_input);


        signup_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String dri_num=reg.getText().toString();
                String password=pass.getText().toString();
                String bus_number=bus_num.getText().toString();
                storeDriverDetail(dri_num,bus_number);
                signupUser(dri_num,password);
            }
        });



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void signupUser(String reg,String pass)
    {
        reg=reg+"@driver.com";
        auth.createUserWithEmailAndPassword(reg,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful())
                {
                    Toast.makeText(DriverSignupActivity.this, "Succesfully Registered", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(DriverSignupActivity.this, DriverLoginActivity.class));
                }
                else
                {
                    Toast.makeText(DriverSignupActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    public void storeDriverDetail(String driver_number,String bus_number)
    {
        //storing driverID-bus number pair in Firebase Realtime Database
        FirebaseDatabase.getInstance().getReference("Driver_Detail").child(driver_number.toLowerCase()).setValue(bus_number);

    }


}