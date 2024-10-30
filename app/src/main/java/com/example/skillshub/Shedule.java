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
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
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
    Scheduleadapter Scheduleadapter;
    ArrayList<ScheduleModel> list1;
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
        recyclerView = findViewById(R.id.recyclerView);
        refresh_schedule = findViewById(R.id.refresh_schedule);

        auth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userID = auth.getCurrentUser().getUid();

        list1 = new ArrayList<>();
        Scheduleadapter = new Scheduleadapter(list1);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(Scheduleadapter);

        documentReference1 = fStore.collection("users").document(userID);
        CollectionReference documentReference2 = documentReference1.collection("schedule");
        fetchreviewfromfirebase(documentReference2);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Shedule.this, WorkerProfileView.class);
                startActivity(intent);
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
                    ScheduleModel ScheduleModel = documentSnapshot.toObject(ScheduleModel.class);
                    list1.add(ScheduleModel);
                }
                Scheduleadapter.notifyDataSetChanged();
            }
        });
    }

    public static class ScheduleModel {
        private String date;
        private String task;

        public ScheduleModel() {
        }

        public String getDate() {
            return date;
        }

        public String getTask() {
            return task;
        }

        public ScheduleModel(String date, String task) {
            this.date = date;
            this.task = task;

        }

    }

    public static class Scheduleadapter extends RecyclerView.Adapter<Scheduleadapter.ScheduleadapterViewHolder> {
        private List<ScheduleModel> List;
        private OnItemClickListener listener;

        public interface OnItemClickListener {

        }

        public Scheduleadapter(List<ScheduleModel> List, OnItemClickListener listener) {
            this.List = List;
            this.listener = listener;
        }

        public Scheduleadapter(List<Shedule.ScheduleModel> List) {
            this.List = List;
        }

        public ScheduleadapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shedule_card1, parent, false);
            return new ScheduleadapterViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ScheduleadapterViewHolder holder, int position) {
            ScheduleModel ScheduleModel = List.get(position);
            holder.date.setText(ScheduleModel.getDate());
            holder.task.setText(ScheduleModel.getTask());
        }

        public int getItemCount() {
            return List.size();
        }

        public class ScheduleadapterViewHolder extends RecyclerView.ViewHolder {
            TextView date, task;

            public ScheduleadapterViewHolder(View itemView) {
                super(itemView);
                date = itemView.findViewById(R.id.date);
                task = itemView.findViewById(R.id.description);

            }


        }
    }
}