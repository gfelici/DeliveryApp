package com.delivery.deliveryapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.delivery.deliveryapp.Firebase.DbManager;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class LoginActivity extends Activity {

    private EditText usernameTxt;
    private EditText pwTxt;
    private Activity ctx;

    private static String TAG = "INFO";

    FirebaseAuth auth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        this.ctx = this;
        usernameTxt = this.findViewById(R.id.username);
        pwTxt = this.findViewById(R.id.password);
    }

    @Override
    protected void onResume() {
        super.onResume();
        auth = FirebaseAuth.getInstance();

        Button button = this.findViewById(R.id.register);
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                auth.createUserWithEmailAndPassword(usernameTxt.getText().toString(),
                        pwTxt.getText().toString())
                        .addOnCompleteListener(ctx, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Log.v(TAG, "Utente registrato");
                                    FirebaseUser user = auth.getCurrentUser();
                                    Toast.makeText(getApplicationContext(), "Utente registrato", Toast.LENGTH_SHORT);
                                } else {
                                    Log.v(TAG, "Utente NON registrato");
                                    Toast.makeText(getApplicationContext(), "Errore: utente non registrato", Toast.LENGTH_SHORT);
                                    Log.e(TAG, task.getException().getMessage());
                                }
                            }
                        });
            }
        });

        Button b = this.findViewById(R.id.login);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.signInWithEmailAndPassword(usernameTxt.getText().toString(),
                        pwTxt.getText().toString()).addOnCompleteListener(ctx, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Autenticazione andata a buon fine!");
                            //LOGIN
                            DbManager.user = auth.getCurrentUser();

                            Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
                            LoginActivity.this.startActivity(myIntent);
                            //myIntent.setFlags(Inte)
                            finish();
                        } else {
                            Log.e(TAG, "Username o password errati " + task.getException().getMessage());
                            Toast.makeText(getApplicationContext(), "Autenticazione fallita", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }

}
