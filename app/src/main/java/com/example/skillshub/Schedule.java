package com.example.skillshub;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Schedule extends AppCompatActivity {
    DatePicker datePicker;
    Button submit, clear;
    EditText task;
    FirebaseFirestore fStore;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        datePicker = findViewById(R.id.datePicker);
        submit = findViewById(R.id.set_btn);
        clear = findViewById(R.id.clear_btn);
        task = findViewById(R.id.task);
        fStore = FirebaseFirestore.getInstance();

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                task.setText("");
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String day, month, year, date;
                day = String.valueOf(datePicker.getDayOfMonth());
                month = String.valueOf(datePicker.getMonth() + 1);
                year = String.valueOf(datePicker.getYear());
                date = day + "/" + month + "/" + year;
                DocumentReference docRef = fStore.collection("user").document("DwZLfvGonlYDSHDwd95E");
                CollectionReference collectionRef = docRef.collection("schedule");
                Map<String, Object> data = new HashMap<>();
                data.put("date", date);
                data.put("task", task.getText().toString());
                collectionRef.add(data).addOnSuccessListener(documentReference -> {

                    collectionRef.add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(Schedule.this, "Task Submitted", Toast.LENGTH_SHORT).show();
                            Log.d("msg", "uploaded successfully" + documentReference.getId());
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Schedule.this, "Task not submitted", Toast.LENGTH_SHORT).show();
                            Log.d("msg", "not uploaded" + e.getMessage());
                        }
                    });
                    task.setText("");
                });
            }

        });
    }
}