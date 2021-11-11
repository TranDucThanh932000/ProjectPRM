package com.example.budgetmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.util.Strings;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private TextView tvLoginRedirect;
    private Button btnRegister;
    private EditText etUserName, etPass;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        tvLoginRedirect = findViewById(R.id.tvLoginRedirect);
        tvLoginRedirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent redirectIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(redirectIntent);
            }
        });

        auth = FirebaseAuth.getInstance();
        btnRegister = findViewById(R.id.btnRegister);
        etPass = findViewById(R.id.etRegister_pass);
        etUserName = findViewById(R.id.etRegister_username);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = etUserName.getText().toString();
                String password = etPass.getText().toString();

                if (TextUtils.isEmpty(username)) {
                    etUserName.setError("User name can not be empty");
                }
                if (TextUtils.isEmpty(password)) {
                    etPass.setError("User name can not be empty");
                } else {
                    auth.createUserWithEmailAndPassword(username, password).
                            addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                                        startActivity(intent);
                                        finish();

                                    } else {
                                        Log.w("SignUp","Create user failed",task.getException());
                                        Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }
}