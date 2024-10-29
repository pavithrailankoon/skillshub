package com.example.skillshub;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class Admin extends AppCompatActivity {

    Button logoutBtn;
    FirebaseFirestore db;
    TextView users;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin);

        db = FirebaseFirestore.getInstance();
        users = findViewById(R.id.users);
        fetchUserCount();

            logoutBtn = findViewById(R.id.logoutBtn);

            logoutBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                    startActivity(intent);
                }
            });

        }

    private void fetchUserCount() {
        db.collection("user")
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        QuerySnapshot snapshot = task.getResult();
                        if (snapshot != null) {
                            int userCount = snapshot.size();
                            users.setText(String.valueOf(userCount));
                        }
                    }else{
                        Log.e("Firestore", "Error getting documents: ", task.getException());

                        }
                    });
    }
}
