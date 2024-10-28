package com.example.skillshub;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkerProfileView extends AppCompatActivity {
    TextView category, name, verified, mail,description;
    Button call, whatsapp, schedule, review;
    FirebaseAuth fAuth;
    ImageView back, workerImage;
    FirebaseFirestore fStore;
    String userID;
  
    ImageView back, workerImage,refresh;
    FirebaseFirestore fStore,db;
    String userID,phoneNumber;
    RatingBar ratingBar;
    EditText reviewComment;
    Button submit_review;
    DocumentReference documentReference1;
    //CollectionReference collectionReference;

    Reviewadapter adapter;
    ArrayList<ReviewModel> list;
    RecyclerView listView;

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
        review = findViewById(R.id.review);
        description = findViewById(R.id.description);
        back = findViewById(R.id.back);
        workerImage = findViewById(R.id.workerImage);

        ratingBar = findViewById(R.id.ratingBar);
        reviewComment = findViewById(R.id.reviewComment);
        submit_review = findViewById(R.id.submit_review);
        refresh = findViewById(R.id.refresh_btn);

        listView = findViewById(R.id.ListView);
        list = new ArrayList<>();
        adapter = new Reviewadapter(list);
        listView.setLayoutManager(new LinearLayoutManager(this));
        listView.setAdapter(adapter);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        db = FirebaseFirestore.getInstance();

        userID = fAuth.getCurrentUser().getUid();


//        userID = fAuth.getCurrentUser().getUid();
        DocumentReference documentReference = fStore.collection("users").document("ngMJbGb3mwbjEdQUtE278L7sB0I2");
        documentReference1 = fStore.collection("user").document("DwZLfvGonlYDSHDwd95E");

        DocumentReference documentReference = fStore.collection("users").document("DbnaB8GfAsXmo7h1NR3ydo3EJgR2");
        DocumentReference documentReference1 = fStore.collection("user").document("DwZLfvGonlYDSHDwd95E");
        CollectionReference documentReference2 = documentReference1.collection("reviewsAsAWorker");
        fetchreviewfromfirebase(documentReference2);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                category.setText(value.getString("category"));
                name.setText(value.getString("fullName"));
                verified.setText(value.getString("verified"));
                mail.setText(value.getString("email"));
                description.setText(value.getString("description"));
                call.setText(value.getString("phone"));
                phoneNumber = value.getString("phone");
            }

        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
            }
        });
        review.setOnClickListener(new View.OnClickListener() {

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
                Toast.makeText(WorkerProfileView.this, "Write Your Review", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(WorkerProfileView.this, Review.class);
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
                intent.setData(Uri.parse("tel:" + call.getText().toString()));
                startActivity(intent);
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


    void fetchreviewfromfirebase(CollectionReference collectionRef) {
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
        PackageManager pm = getPackageManager();
        try {
           pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
            // WhatsApp is installed, proceed with intent
            Intent intent = new Intent(Intent.ACTION_VIEW);
            // You can specify a number (starting with country code) to open a specific chat
            String phoneNumber = "1234567890"; // replace with the actual number
            pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            // You can specify a number (starting with country code) to open a specific chat
            intent.setData(Uri.parse("tel:" + phoneNumber));

            String url = "https://wa.me/" + phoneNumber; // WhatsApp API link format

            // Intent to open WhatsApp
            intent.setData(Uri.parse(url));
            startActivity(intent);

        } catch (PackageManager.NameNotFoundException e) {
            // WhatsApp not installed, show a message
            Toast.makeText(WorkerProfileView.this, "WhatsApp is not installed on your device", Toast.LENGTH_SHORT).show();
        }


//    private void setUserAvatar(){
  //      FirebaseStoarageManager imageManager = new FirebaseStoarageManager();

   //     imageManager.loadProfileImage(this, p)
   // }
}}

    }
}



