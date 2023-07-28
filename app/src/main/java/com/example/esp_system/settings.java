package com.example.esp_system;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.util.Objects;

public class settings extends AppCompatActivity {
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAuth = FirebaseAuth.getInstance();

        Button button ,verify_acc, del_acc, reset_password, SignOut;

        button = findViewById(R.id.Account);
        button.setOnClickListener(v -> {
            // Do something in response to button click
            get_info();
        });

        verify_acc = findViewById(R.id.Verifivation);
        verify_acc.setOnClickListener(v -> {
            // Do something in response to button click
            verify_acc();
        });

        del_acc = findViewById(R.id.Del_Acc);
        del_acc.setOnClickListener(v -> {
            // Do something in response to button click
            del_acc();
        });

        reset_password = findViewById(R.id.Reset_Pass);
        reset_password.setOnClickListener(v -> {
            // Do something in response to button click
            goto_reset_pass();
        });

        SignOut = findViewById(R.id.SignOut);
        SignOut.setOnClickListener(v -> {
            // Do something in response to button click
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, signIn.class);
            startActivity(intent);
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    public void del_acc() {
        AlertDialog.Builder builder = new AlertDialog.Builder(settings.this);
        builder.setMessage("If your unregister your account you'r unable to get Temp/Humidity!");
        builder.setTitle("Do You Want to Delete Your Account!");
        builder.setCancelable(false);
        builder.setPositiveButton("Delete", (dialog, which) -> {
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            final FirebaseUser currentUser = firebaseAuth.getCurrentUser();
            assert currentUser != null;
            currentUser.delete().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(settings.this, "Removed Sucessfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(settings.this, MainActivity.class));
                    finish();
                    clearApplicationData();
                }
            }).addOnFailureListener(e -> Toast.makeText(settings.this, e.toString(), Toast.LENGTH_SHORT).show());
        });
        builder.setNegativeButton("No", (dialog, which) -> dialog.cancel());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void clearApplicationData() {
        File cache = getCacheDir();
        File appDir = new File(Objects.requireNonNull(cache.getParent()));
        if (appDir.exists()) {
            String[] children = appDir.list();
            for (String s : Objects.requireNonNull(children)) {
                if (!s.equals("lib")) {
                    deleteDir(new File(appDir, s));
                    Log.i("EEEEEERRRRRRROOOOOOORRRR", "**************** File /data/data/APP_PACKAGE/" + s + " DELETED *******************");
                }
            }
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            assert children != null;
            for (String child : children) {
                boolean success = deleteDir(new File(dir, child));
                if (!success) {
                    return false;
                }
            }
        }
        return Objects.requireNonNull(dir).delete();
    }

    private void goto_reset_pass() {
        setContentView(R.layout.dialog_signin);
        EditText email = findViewById(R.id.EmailAddress_for_reset);
        Button reset_btn = findViewById(R.id.Reset);
        reset_btn.setOnClickListener(v ->{
            FirebaseAuth auth = FirebaseAuth.getInstance();
            String emailAddress = email.getText().toString();
            auth.sendPasswordResetEmail(emailAddress).addOnCompleteListener(task -> {
                try {
                    if (task.isSuccessful()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(settings.this);
                        builder.setTitle("Open Email");
                        builder.setMessage("Request Send To Email");
                        builder.setCancelable(false);
                        builder.setPositiveButton("Ok", (dialog, which) -> {
                            Intent intent = new Intent(settings.this, MainActivity.class);
                            startActivity(intent);
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }
                }
                catch (Exception kuk) {
                    Toast.makeText(settings.this, "Try Again! "+    kuk, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @SuppressLint("SetTextI18n")
    private void get_info() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();
            boolean emailVerified = user.isEmailVerified();
            String uid = user.getUid();
            final TextView helloTextView = findViewById(R.id.Show_Things);
            helloTextView.setText("name     " + name + "\nemail     " + email + "\nemailVerified        " + emailVerified + "\nuid        " + uid);
        }
    }

    private void verify_acc() {
        if (!user.isEmailVerified()) {
            user.sendEmailVerification().addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Go to your Gmail/Email and Verifiy registered Account" + user.getEmail(), Toast.LENGTH_SHORT).show();
                    Log.d("Verification", "Verification email sent to " + user.getEmail());
                }
                else {
                    Log.e(TAG, "sendEmailVerification", task.getException());
                    Toast.makeText(getApplicationContext(), "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}