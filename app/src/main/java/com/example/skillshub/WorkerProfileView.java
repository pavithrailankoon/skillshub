package com.example.skillshub;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WorkerProfileView extends AppCompatActivity {
    TextView category, name, verified, mail,description;
    ImageView call, whatsapp, schedule;
    FirebaseAuth fAuth;
    ImageView back, workerImage;
    FirebaseFirestore fStore;
    String userID,phoneNumber;
    Dialog calenderView;
    RatingBar ratingBar;
    EditText reviewComment;
    Button submit_review,clear;
//    String userID;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_worker_profile_view);

        category = findViewById(R.id.category);
        name = findViewById(R.id.name);
        verified = findViewById(R.id.verified);
        mail = findViewById(R.id.mail);
        call = findViewById(R.id.call);
        whatsapp = findViewById(R.id.whatsapp);
        schedule = findViewById(R.id.schedule);
        description = findViewById(R.id.description);
        back = findViewById(R.id.back);
        workerImage = findViewById(R.id.workerImage);
        ratingBar = findViewById(R.id.ratingBar);
        reviewComment = findViewById(R.id.reviewComment);
        submit_review = findViewById(R.id.submit_review);
        clear = findViewById(R.id.clear);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        userID = fAuth.getCurrentUser().getUid();

        DocumentReference documentReference = fStore.collection("users").document("DbnaB8GfAsXmo7h1NR3ydo3EJgR2");
        DocumentReference documentReference1 = fStore.collection("user").document("DwZLfvGonlYDSHDwd95E");
        CollectionReference documentReference2 = documentReference1.collection("reviewsAsAWorker");
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                category.setText(value.getString("category"));
                name.setText(value.getString("firstName") + " " + value.getString("lastName"));
                verified.setText(value.getString("verified"));
                mail.setText(value.getString("email"));
                description.setText(value.getString("description"));
            }
        });
        documentReference1.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                phoneNumber = value.getString("phone");
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WorkerProfileView.this, clientHome2.class);
                startActivity(intent);
            }
        });
       whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWhatsapp();
            }
        });
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + phoneNumber));
                startActivity(intent);
            }
        });


        schedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WorkerProfileView.this, Shedule.class);
                startActivity(intent);
            }
        });
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reviewComment.setText("");
                ratingBar.setRating(0);
            }
        });

        submit_review.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                String s = String.valueOf(ratingBar.getRating());
                String t = reviewComment.getText().toString();
                CollectionReference collectionRef = documentReference1.collection("reviewsAsAWorker");
                Map<String, Object> data = new HashMap<>();
                data.put("rating", s);
                data.put("review", t);
                collectionRef.add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(WorkerProfileView.this, "Review submitted", Toast.LENGTH_SHORT).show();
                        Log.d("msg", "uploaded successfully" + documentReference.getId());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(WorkerProfileView.this, "Review not submitted", Toast.LENGTH_SHORT).show();
                        Log.d("msg", "not uploaded" + e.getMessage());
                    }
                });
                reviewComment.setText("");
                ratingBar.setRating(0);
            }
        });
    }

    private void openWhatsapp() {
        PackageManager pm = getPackageManager();
        try {
           pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            // You can specify a number (starting with country code) to open a specific chat
            String phoneNumber = ""; // replace with the actual number
            String url = "https://wa.me/" + phoneNumber; // WhatsApp API link format
            intent.setData(Uri.parse(url));
            startActivity(intent);

        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(WorkerProfileView.this, "WhatsApp is not installed on your device", Toast.LENGTH_SHORT).show();
        }
    }
}


