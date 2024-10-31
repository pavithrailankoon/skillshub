package com.example.skillshub;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class WorkerProfile extends AppCompatActivity {
    ImageView refreshbutton, tasks;
    DocumentReference documentReference1;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore, db;

    Reviewadapter adapter;
    ArrayList<ReviewModel> list;
    RecyclerView listView;
    String receivedWorkerUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_worker_profile);

        refreshbutton = findViewById(R.id.refreshButton);
        tasks = findViewById(R.id.shedule);

        listView = findViewById(R.id.ListView);
        list = new ArrayList<>();
        adapter = new Reviewadapter(list);
        listView.setLayoutManager(new LinearLayoutManager(this));
        listView.setAdapter(adapter);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        db = FirebaseFirestore.getInstance();

        documentReference1 = fStore.collection("user").document("DwZLfvGonlYDSHDwd95E");

        DocumentReference documentReference = fStore.collection("users").document("DbnaB8GfAsXmo7h1NR3ydo3EJgR2");

        CollectionReference documentReference2 = documentReference1.collection("reviewsAsAWorker");
        fetchreviewfromfirebase(documentReference2);

        refreshbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(WorkerProfile.this, "Refreshing...", Toast.LENGTH_SHORT).show();
                list.clear();
                fetchreviewfromfirebase(documentReference2);
            }
        });
        tasks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WorkerProfile.this, Schedule.class);
                startActivity(intent);
            }
        });


    }

    void fetchreviewfromfirebase(CollectionReference collectionRef) {
        collectionRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                    ReviewModel reviewModel = documentSnapshot.toObject(ReviewModel.class);
                    list.add(reviewModel);
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    public static class ReviewModel {
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

        public ReviewModel(String rating, String review) {
            this.rating = rating;
            this.review = review;

        }

    }

    public static class Reviewadapter extends RecyclerView.Adapter<Reviewadapter.ReviewadapterViewHolder>

    {
        private java.util.List<ReviewModel> List;
        private Reviewadapter.OnItemClickListener listener;

        public interface OnItemClickListener {

        }

        public Reviewadapter(List < ReviewModel > List, Reviewadapter.OnItemClickListener listener)
        {
            this.List = List;
            this.listener = listener;
        }
        public Reviewadapter(List < ReviewModel > List) {
        this.List = List;
    }
        public Reviewadapter.ReviewadapterViewHolder onCreateViewHolder (ViewGroup parent,
        int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_list_item_layout3, parent, false);
        return new Reviewadapter.ReviewadapterViewHolder(view);
    }
        public void onBindViewHolder(Reviewadapter.ReviewadapterViewHolder holder,int position){
        ReviewModel reviewModel = List.get(position);
        holder.ratingBar.setRating(Float.parseFloat(reviewModel.getRating()));
        holder.ratingBar.setIsIndicator(true);

        holder.reviewItem.setText(reviewModel.getReview());
    }
        public int getItemCount () {return List.size();}
        public class ReviewadapterViewHolder extends RecyclerView.ViewHolder {
            TextView reviewItem;
            RatingBar ratingBar;

            public ReviewadapterViewHolder(View itemView) {
                super(itemView);
                ratingBar = itemView.findViewById(R.id.submitratings);
                reviewItem = itemView.findViewById(R.id.submitreview);

            }


        }

    }
}
