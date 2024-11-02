package com.example.skillshub;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
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

public class Shedule extends AppCompatActivity {

    private ImageView backBtn, refresh_schedule;
    Sheduleadapter Sheduleadapter;
    ArrayList<SheduleModel> list1;
    RecyclerView recyclerView;
    DocumentReference documentReference1;
    FirebaseFirestore fStore;
    FirebaseAuth auth;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_shedule);

        backBtn = findViewById(R.id.backBtn);
        refresh_schedule = findViewById(R.id.refresh_schedule);
        recyclerView = findViewById(R.id.recyclerView22);
        refresh_schedule = findViewById(R.id.refresh_schedule);

        auth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userID = getIntent().getStringExtra("workerId");

        list1 = new ArrayList<>();
        Sheduleadapter = new Sheduleadapter(list1);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(Sheduleadapter);

        documentReference1 = fStore.collection("users").document(userID);
        CollectionReference documentReference2 = documentReference1.collection("schedule");
        fetchreviewfromfirebase(documentReference2);

        backBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        refresh_schedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Shedule.this, "Refreshing...", Toast.LENGTH_SHORT).show();
                list1.clear();
                fetchreviewfromfirebase(documentReference2);
            }
        });

    }

    void fetchreviewfromfirebase(CollectionReference collectionRef) {
        collectionRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                    SheduleModel SheduleModel = documentSnapshot.toObject(SheduleModel.class);
                    list1.add(SheduleModel);
                }
                Sheduleadapter.notifyDataSetChanged();
            }
        });
    }

    public static class SheduleModel {
        private String date;
        private String task;

        public SheduleModel() {
        }

        public String getDate() {
            return date;
        }

        public String getTask() {
            return task;
        }

        public SheduleModel(String date, String task) {
            this.date = date;
            this.task = task;

        }

    }

    public static class Sheduleadapter extends RecyclerView.Adapter<Sheduleadapter.SheduleadapterViewHolder> {
        private List<SheduleModel> List;
        private OnItemClickListener listener;

        public interface OnItemClickListener {

        }

        public Sheduleadapter(List<SheduleModel> List, OnItemClickListener listener) {
            this.List = List;
            this.listener = listener;
        }

        public Sheduleadapter(List<SheduleModel> List) {
            this.List = List;
        }

        public SheduleadapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shedule_card1, parent, false);
            return new SheduleadapterViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull SheduleadapterViewHolder holder, int position) {
            SheduleModel SheduleModel = List.get(position);
            holder.date.setText(SheduleModel.getDate());
            holder.task.setText(SheduleModel.getTask());
        }

        public int getItemCount() {
            return List.size();
        }

        public class SheduleadapterViewHolder extends RecyclerView.ViewHolder {
            TextView date, task;

            public SheduleadapterViewHolder(View itemView) {
                super(itemView);
                date = itemView.findViewById(R.id.date);
                task = itemView.findViewById(R.id.descriptionxs);

            }


        }
    }
}