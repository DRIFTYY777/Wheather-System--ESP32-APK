package com.example.esp_system;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class signIn extends AppCompatActivity {
    private EditText email;
    private EditText password;
    private boolean isPasswordVisible;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        Button create_one = findViewById(R.id.Register_Email);
        create_one.setOnClickListener(v ->{
            Intent intent = new Intent(this, register_accoount.class);
            startActivity(intent);
        });

        show_pass();
        Sign_in_existing_users();
    }

    private void goto_main(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void show_pass()
    {
        Button button = findViewById(R.id.show_pass);
        button.setOnClickListener(v -> {
            String pass = password.getText().toString();
            if (isPasswordVisible) {
                password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            } else {
                password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                password.setInputType(InputType.TYPE_CLASS_TEXT);
            }
            password.setText(pass);
            password.setSelection(pass.length());
            isPasswordVisible= !isPasswordVisible;
        });
    }

    private void Sign_in_existing_users(){
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Toast.makeText(this, "Already SignIn", Toast.LENGTH_SHORT).show();
            goto_main();
        }
        else{
            Button signIn = findViewById(R.id.SignIn);
            signIn.setOnClickListener(v -> {
                email = findViewById(R.id.EmailAddress);
                password = findViewById(R.id.Password);
                mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        user = FirebaseAuth.getInstance().getCurrentUser();
                        String uid = user.getUid();
                        boolean emailVerified = user.isEmailVerified();
                        create_db_refrence(email.getText().toString(),password.getText().toString(),uid.toString(),emailVerified);
                        Log.d(TAG, "signInWithEmail:success");
                        Toast.makeText(signIn.this, "signIn Success", Toast.LENGTH_SHORT).show();
                        user = mAuth.getCurrentUser();
                        goto_main();
                    }
                    else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        Toast.makeText(signIn.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        }
    }

    public String getPhoneName() {
        return Build.MANUFACTURER + " " + Build.MODEL+ " " + Build.ID;
    }

    private void create_db_refrence(String email, String pass, String uid, boolean verify){
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null)
        {
            String Device_Id = getPhoneName();
            Map<String, Object> account = new HashMap<>();
            Map<String, Object> Device = new HashMap<>();
            account.put("email", email);
            account.put("pass", pass);
            account.put("verify", verify);
            account.put("uid", uid);
            Device.put("DeviceId", Device_Id);
            db.collection(email).document("account").set(account).addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully written!")).addOnFailureListener(e ->
            {
                Log.w(TAG, "Error writing document", e);
                Toast.makeText(this,"Error writing document"+ e,Toast.LENGTH_SHORT).show();
            });
            if(currentUser != null) {
                db.collection(email).document("Main").set(Device).addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully written!")).addOnFailureListener(e ->
                {
                    Log.w(TAG, "Error writing document", e);
                    Toast.makeText(this, "Error writing document" + e, Toast.LENGTH_SHORT).show();
                });
            }
        }
    }
}