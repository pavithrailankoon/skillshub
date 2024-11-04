package com.example.skillshub;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skillshub.firebaseModel.ReadData;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkerProfileView extends AppCompatActivity {
    TextView category, name, verified, mail,description;
    ImageView call, whatsapp, schedule, verifiedIcon;
    FirebaseAuth fAuth;
    ImageView back, workerImage,refresh;
    FirebaseFirestore fStore,db;
    String userID,phoneNumber;
    RatingBar ratingBar;
    EditText reviewComment;
    Button submit_review;
    Reviewadapter adapter;
    ArrayList<ReviewModel> list;
    RecyclerView listView;
    String receivedWorkerUid;
    ReadData readData;

    String uid;

    int color = Color.BLUE;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
       // EdgeToEdge.enable(this);
        setContentView(R.layout.activity_worker_profile_view);

        if (user != null) {
            uid = user.getUid();
        }

        readData = new ReadData();
        category = findViewById(R.id.category);
        name = findViewById(R.id.name);
        verified = findViewById(R.id.verified);
        mail = findViewById(R.id.mail);
        call = findViewById(R.id.call);
        whatsapp = findViewById(R.id.whatsapp);
        schedule = findViewById(R.id.schedule);
        description = findViewById(R.id.descriptionxs);
        back = findViewById(R.id.back);
        workerImage = findViewById(R.id.workerImage);
        ratingBar = findViewById(R.id.ratingBar);
        reviewComment = findViewById(R.id.reviewComment);
        submit_review = findViewById(R.id.submit_review);
        refresh = findViewById(R.id.refresh_btn);
        verifiedIcon = findViewById(R.id.imageView10);


        receivedWorkerUid = getIntent().getStringExtra("SELECTED_WORKER");

        listView = findViewById(R.id.ListView);
        list = new ArrayList<>();
        adapter = new Reviewadapter(list);
        listView.setLayoutManager(new LinearLayoutManager(this));
        listView.setAdapter(adapter);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        db = FirebaseFirestore.getInstance();

        userID = fAuth.getCurrentUser().getUid();

        setProfileImage();
        checkVerifications();

        DocumentReference documentReference1 = fStore.collection("users").document(receivedWorkerUid);
        CollectionReference documentReference2 = documentReference1.collection("reviewsAsAWorker");
        fetchreviewfromfirebase(documentReference2);
        documentReference1.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                category.setText(value.getString("category"));
                name.setText(value.getString("fullName"));
                verified.setText(value.getString("verified"));
                mail.setText(value.getString("email"));
                description.setText(value.getString("description"));
                phoneNumber = value.getString("phone");
            }
        });

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(WorkerProfileView.this, "Refreshing...", Toast.LENGTH_SHORT).show();
                list.clear();
                fetchreviewfromfirebase(documentReference2);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
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
                intent.putExtra("workerId", receivedWorkerUid);
                startActivity(intent);
            }
        });

        submit_review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (receivedWorkerUid != null && user.getUid() != null && !receivedWorkerUid.equalsIgnoreCase(user.getUid())) {
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
                } else {
                    // Your code when the UIDs are not equal or one of them is null
                    Toast.makeText(WorkerProfileView.this, "You can not review on your own worker profile", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void fetchreviewfromfirebase(CollectionReference collectionRef) {
        collectionRef.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                    ReviewModel reviewModel = documentSnapshot.toObject(ReviewModel.class);
                    list.add(reviewModel);
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    public static class ReviewModel{
        private String rating;
        private String review;

        public ReviewModel() {
        }

        public String getRating() {
            return rating;
        }

        public String getReview() {
            return review;
        }

        public ReviewModel (String rating, String review){
            this.rating = rating;
            this.review = review;

        }

    }
    public static class Reviewadapter extends RecyclerView.Adapter<Reviewadapter.ReviewadapterViewHolder>{
        private List<ReviewModel> List;
        private OnItemClickListener listener;

        public interface OnItemClickListener{

        }

        public Reviewadapter(List<ReviewModel> List, OnItemClickListener listener){
            this.List = List;
            this.listener = listener;
        }
        public Reviewadapter(List<ReviewModel> List){
            this.List = List;
        }
        public ReviewadapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_list_item_layout3, parent, false);
            return new ReviewadapterViewHolder(view);
        }
        public void onBindViewHolder(ReviewadapterViewHolder holder, int position){
            ReviewModel reviewModel = List.get(position);
            holder.ratingBar.setRating(Float.parseFloat(reviewModel.getRating()));
            holder.ratingBar.setIsIndicator(true);

            holder.reviewItem.setText(reviewModel.getReview());
        }
        public int getItemCount(){
            return List.size();
        }
        public class ReviewadapterViewHolder extends RecyclerView.ViewHolder{
            TextView reviewItem;
            RatingBar ratingBar;
            public ReviewadapterViewHolder(View itemView){
                super(itemView);
                ratingBar = itemView.findViewById(R.id.submitratings);
                reviewItem = itemView.findViewById(R.id.submitreview);
            }
        }
    }

    private void openWhatsapp() {
        String countryCode = "+94";
        if (phoneNumber != null && phoneNumber.length() > 1) {
            String modifiedNumber = countryCode + phoneNumber.substring(1);

            PackageManager pm = getPackageManager();
            try {
                pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
                String url = "https://wa.me/" + modifiedNumber; // WhatsApp API link format
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                intent.setPackage("com.whatsapp");
                startActivity(intent);

            } catch (PackageManager.NameNotFoundException e) {
                Toast.makeText(WorkerProfileView.this, "WhatsApp is not installed on your device", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(WorkerProfileView.this, "Invalid phone number", Toast.LENGTH_SHORT).show();
        }
    }

    private void setProfileImage() {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference()
                .child(receivedWorkerUid + "/profile-image");

        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            Picasso.get()
                    .load(uri)
                    .placeholder(R.drawable.avatar)
                    .error(R.drawable.avatar)
                    .into(workerImage);
        }).addOnFailureListener(exception -> {
            Log.e("FirebaseStorage", "Error retrieving image", exception);
        });
    }

    private void checkVerifications(){
        DocumentReference userDocRef = db.collection("users").document(receivedWorkerUid);
        CollectionReference workerInfoRef = userDocRef.collection("workerInformation");

        workerInfoRef.get().addOnSuccessListener(querySnapshot -> {
            if (!querySnapshot.isEmpty()) {
                DocumentSnapshot workerDoc = querySnapshot.getDocuments().get(0);

                Boolean isNicVerified = workerDoc.getBoolean("isNicVerified");
                Boolean isBrVerified = workerDoc.getBoolean("isBrVerified");
                String desc = workerDoc.getString("description");

                if (isBrVerified == null) {
                    verified.setText("Pending");
                } else if (isBrVerified) {
                    verified.setText("Verified");
                    verifiedIcon.setColorFilter(color, PorterDuff.Mode.SRC_IN);
                } else {
                    verified.setText("Not Verified");
                }

                if (desc != null ){
                    description.setText(desc);
                }
            }
        }).addOnFailureListener(e ->
                Toast.makeText(this, "Failed to load worker information", Toast.LENGTH_SHORT).show()
        );
    }
}