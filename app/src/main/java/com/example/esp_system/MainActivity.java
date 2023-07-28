package com.example.esp_system;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;



public class MainActivity extends AppCompatActivity {


    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference kharwan = db.collection("kharwan");
    private TextView textView_temp;
    private TextView textView_humi;
    private final Handler handler = new Handler();
    private ProgressBar progressBar_temp;
    private ProgressBar progressBar_humi;
    private int progressStatus = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FloatingActionButton fab = findViewById(R.id.setting);
        fab.setOnClickListener(view -> gotoSetting());
    }

    public void gotoSetting() {
        Intent intent = new Intent(this, settings.class);
        startActivity(intent);
    }

    public void signin_class() {
        Intent intent = new Intent(this, signIn.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
            temp();
            humi();
        }
        else {
            // No user is signed in
            signin_class();
        }
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    private void temp(){
        kharwan.addSnapshotListener(this, (queryDocumentSnapshots, e) -> {
            if (e != null) {
                return;
            }
            assert queryDocumentSnapshots != null;
            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                Note note = documentSnapshot.toObject(Note.class);
                double temperature = note.getTemperature();
                progressBar_temp = findViewById(R.id.progressBar_temp);
                textView_temp = findViewById(R.id.textView_temp);
                new Thread(() -> {
                    for (progressStatus = 0; progressStatus <= temperature; progressStatus++) {
                        handler.post(() -> {
                            progressBar_temp.setProgress(progressStatus);
                            textView_temp.setText(String.format("%.2f", temperature) + "°");
                        });
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                            Toast.makeText(this, e1.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }).start();
            }
        });
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    private void humi(){
        kharwan.addSnapshotListener(this, (queryDocumentSnapshots, e) -> {
            if (e != null) {
                return;
            }
            assert queryDocumentSnapshots != null;
            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                Note note = documentSnapshot.toObject(Note.class);
                int humidity = note.getHumidity();
                progressBar_humi = findViewById(R.id.progressBar_humi);
                textView_humi = findViewById(R.id.textView_humi);
                new Thread(() -> {
                   for(progressStatus = 0; progressStatus <= humidity; progressStatus++){
                       handler.post(() -> {
                           progressBar_humi.setProgress(progressStatus);
                           textView_humi.setText(progressStatus+"%");
                       });
                       try {
                           Thread.sleep(50);
                       } catch (InterruptedException e12) {
                           e12.printStackTrace();
                           Toast.makeText(this, e12.toString(), Toast.LENGTH_SHORT).show();
                       }
                   }
               }).start();
            }
        });
    }
}