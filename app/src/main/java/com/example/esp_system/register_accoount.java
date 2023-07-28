package com.example.esp_system;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class register_accoount extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private boolean isPasswordVisible;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_accoount);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        alert();
        show_pass();
        Register_new_account();
    }

    @Override
    public void onStart() {
        super.onStart();
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            goto_SignIn();
        }
    }

    private void goto_SignIn(){
        Intent intent = new Intent(this, signIn.class);
        startActivity(intent);
    }

    public void alert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(register_accoount.this);
        builder.setTitle("Alert!");
        builder.setMessage("Don't use your original password");
        builder.setCancelable(false);
        builder.setPositiveButton("ok", (dialog, which) -> { /*enter ok code here*/ });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
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

    private void Register_new_account(){
        Button login = findViewById(R.id.Register_Acc);
        login.setOnClickListener(v -> {
            email = findViewById(R.id.Register_Email_Input);
            password = findViewById(R.id.Register_Password_Input);
            mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Toast.makeText(register_accoount.this, "Registered successfully", Toast.LENGTH_SHORT).show();
                    goto_SignIn();
                }
                else {
                    // If sign in fails, display a message to the user.
                    Exception e =  task.getException();
                    Toast.makeText(register_accoount.this, "failure      "+e.toString(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}