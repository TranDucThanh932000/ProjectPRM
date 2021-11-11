package com.example.budgetmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    private TextView registerRedirect;
    private Button btnLogin;
    private EditText etUserName, etPass;
    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        registerRedirect = findViewById(R.id.tvLoginRedirect);


        registerRedirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });
        auth = FirebaseAuth.getInstance();
        btnLogin = findViewById(R.id.btnLogin);
        etPass = findViewById(R.id.etLogin_pass);
        etUserName = findViewById(R.id.etLogin_username);
        btnLogin.setOnClickListener(new View.OnClickListener() {
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
                    auth.signInWithEmailAndPassword(username, password).
                            addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseUser user = auth.getCurrentUser();
                                        Intent intent = new Intent(LoginActivity.this,MainPageActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Log.w("SignIn","Login failed",task.getException());
                                        Toast.makeText(LoginActivity.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

    }
}