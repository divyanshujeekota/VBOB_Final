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

public class DriverLoginActivity extends AppCompatActivity {

    EditText reg,pass;
    Button login;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_driver_login);

        auth=FirebaseAuth.getInstance();
        reg=findViewById(R.id.reg_input_loginpage);
        pass=findViewById(R.id.pass_input_loginpage);
        login=findViewById(R.id.login_button_loginpage);


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String reg_num=reg.getText().toString();
                String password=pass.getText().toString();
                loginDriver(reg_num,password);
            }
        });







        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void loginDriver(String regNum, String password) {

        regNum=regNum+"@driver.com";
        auth.signInWithEmailAndPassword(regNum,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful())
                {
                    Toast.makeText(DriverLoginActivity.this, "Succesfully login", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(DriverLoginActivity.this, DriverPage.class));
                }
                else
                {
                    Toast.makeText(DriverLoginActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


}