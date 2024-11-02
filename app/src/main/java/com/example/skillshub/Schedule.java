package com.example.skillshub;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Schedule extends AppCompatActivity {
    DatePicker datePicker;
    Button submit, clear,delete_btn;
    EditText task;
    FirebaseFirestore fStore;
    FirebaseAuth auth;
    String userID;

    private ImageView backBtn, refresh_schedule;
    Scheduleadapter Scheduleadapter;
    ArrayList<ScheduleModel> list1;
    RecyclerView recyclerView;
    DocumentReference documentReference1;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        auth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userID = auth.getCurrentUser().getUid();

        datePicker = findViewById(R.id.datePicker);
        submit = findViewById(R.id.set_btn);
        clear = findViewById(R.id.clear_btn);
        task = findViewById(R.id.task);
        delete_btn = findViewById(R.id.delete_btn);
        fStore = FirebaseFirestore.getInstance();

        backBtn = findViewById(R.id.backBtn);
        refresh_schedule = findViewById(R.id.refresh_schedule);
        recyclerView = findViewById(R.id.recyclerView);
        refresh_schedule = findViewById(R.id.refresh_schedule);

        list1 = new ArrayList<>();
        Scheduleadapter = new Scheduleadapter(list1);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Scheduleadapter = new Scheduleadapter(list1, position -> {
            ScheduleModel scheduleToDelete = list1.get(position);
            deleteScheduleFromFirestore(scheduleToDelete);
            list1.remove(position);
            Scheduleadapter.notifyItemRemoved(position);
            Toast.makeText(Schedule.this, "Schedule deleted", Toast.LENGTH_SHORT).show();
        });
        recyclerView.setAdapter(Scheduleadapter);


        datePicker.setMinDate(System.currentTimeMillis() - 1000);

        documentReference1 = fStore.collection("users").document(userID);
        CollectionReference documentReference2 = documentReference1.collection("schedule");
        fetchreviewfromfirebase(documentReference2);


        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                task.setText("");
            }
        });
        refresh_schedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Schedule.this, "Refreshing...", Toast.LENGTH_SHORT).show();
                list1.clear();
                fetchreviewfromfirebase(documentReference2);
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
                DocumentReference docRef = fStore.collection("users").document(userID);
                CollectionReference collectionRef = docRef.collection("schedule");
                Map<String, Object> data = new HashMap<>();
                data.put("date", date);
                data.put("task", task.getText().toString());


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

    private void deleteScheduleFromFirestore(ScheduleModel schedule) {
        DocumentReference DocRef = fStore.collection("users").document(userID);
        CollectionReference scheduleRef = DocRef.collection("schedule");

        scheduleRef.whereEqualTo("task", schedule.getTask()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                DocumentSnapshot document = task.getResult().getDocuments().get(0);
                scheduleRef.document(document.getId()).delete();
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
            void onDeleteClick(int position);
        }

        public Scheduleadapter(List<ScheduleModel> List, Scheduleadapter.OnItemClickListener listener) {
            this.List = List;
            this.listener = listener;
        }

        public Scheduleadapter(List<ScheduleModel> List) {
            this.List = List;
        }

        public Scheduleadapter.ScheduleadapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shedule_card2, parent, false);
            return new Scheduleadapter.ScheduleadapterViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull Scheduleadapter.ScheduleadapterViewHolder holder, int position) {
            ScheduleModel ScheduleModel = List.get(position);
            holder.date.setText(ScheduleModel.getDate());
            holder.task.setText(ScheduleModel.getTask());

            holder.delete_btn.setOnClickListener(v -> {
                // Create an AlertDialog for confirmation
                new AlertDialog.Builder(holder.itemView.getContext())
                        .setTitle("Delete Schedule")
                        .setMessage("Are you sure you want to delete this schedule?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            // Only delete if the user confirms
                            if (listener != null) {
                                listener.onDeleteClick(position);
                            }
                        })
                        .setNegativeButton("No", (dialog, which) -> {
                            // Do nothing, just dismiss the dialog
                            dialog.dismiss();
                        })
                        .show();
            });
        }

        public int getItemCount() {
            return List.size();
        }

        public class ScheduleadapterViewHolder extends RecyclerView.ViewHolder {
            TextView date, task;
            Button delete_btn;

            public ScheduleadapterViewHolder(View itemView) {
                super(itemView);
                date = itemView.findViewById(R.id.date);
                task = itemView.findViewById(R.id.descriptionxs);
                delete_btn = itemView.findViewById(R.id.delete_btn);

            }


        }
    }
}