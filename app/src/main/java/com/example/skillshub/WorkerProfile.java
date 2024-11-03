package com.example.skillshub;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skillshub.firebaseModel.AuthManager;
import com.example.skillshub.firebaseModel.FirebaseStorageManager;
import com.example.skillshub.firebaseModel.ReadData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkerProfile extends AppCompatActivity {

    ImageView backBtn, profileImage;
    Button logOut, editDetails, editPassword, buttonUploadPhoto,deletebtn;
    TextView newName, newPhoneNumber, newAddressLine1, newAddressLine2, city, district, verifyBr,destription;
    ReadData readData;
    FirebaseStorageManager storageManager;
    String uid;
    ImageView refreshbutton, tasks;
    AuthManager authManager;

    DocumentReference documentReference1;
    FirebaseAuth fAuth;
    FirebaseUser user;
    FirebaseFirestore fStore, db;
    StorageReference storageReference;

    Reviewadapter adapter;
    ArrayList<ReviewModel> list;
    RecyclerView listView;
    private ArrayAdapter<String> districtAdapter;
    private ArrayAdapter<String> cityAdapter;

    private static final int REQUEST_IMAGE_GALLERY = 1;
    private Uri DEFAULT_IMAGE_URI;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_worker_profile);

        refreshbutton = findViewById(R.id.refreshButton);
        tasks = findViewById(R.id.shedule);
        backBtn = findViewById(R.id.backBtn);
        newName = findViewById(R.id.name);
        newPhoneNumber = findViewById(R.id.phoneNumber);
        newAddressLine1 = findViewById(R.id.addressLine1);
        newAddressLine2 = findViewById(R.id.addressLine2);
        city = findViewById(R.id.city);
        district = findViewById(R.id.district);
        destription = findViewById(R.id.editTextText3);
        deletebtn = findViewById(R.id.deleteAccount);
        backBtn = findViewById(R.id.backBtn);
        verifyBr = findViewById(R.id.verificationState);

        editPassword = findViewById(R.id.editPassword);
        profileImage = findViewById(R.id.client_profile_image);
        logOut = findViewById(R.id.logOut);
        editDetails = findViewById(R.id.editDetailsBtn);
        buttonUploadPhoto = findViewById(R.id.button);

        backBtn.setOnClickListener(view -> onBackPressed());

        readData = new ReadData();
        listView = findViewById(R.id.ListView);
        list = new ArrayList<>();
        adapter = new Reviewadapter(list);
        listView.setLayoutManager(new LinearLayoutManager(this));
        listView.setAdapter(adapter);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        db = FirebaseFirestore.getInstance();
        user = fAuth.getCurrentUser();

        if (user != null) {
            uid = user.getUid();
            storageReference = FirebaseStorage.getInstance().getReference().child(uid + "/profile-image");
        }

        documentReference1 = fStore.collection("users").document(uid);
        DocumentReference documentReference = fStore.collection("users").document(uid);
        CollectionReference documentReference2 = documentReference1.collection("reviewsAsAWorker");
        fetchreviewfromfirebase(documentReference2);

        backBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        deletebtn.setOnClickListener(v -> {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:skillhubdevelopers@gmail.com"));
            if (emailIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(emailIntent);
            } else {
                Toast.makeText(this, "No email app available", Toast.LENGTH_SHORT).show();
            }
        });
        logOut.setOnClickListener(v -> {
            authManager.logOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
        });


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
        editDetails.setOnClickListener(v -> showUpdateUserDialog2());

        retrieveUserData();
        checkVerifications();
        retrieveReviews();
        buttonUploadPhoto.setOnClickListener(v -> openGallery());
        //editDetails.setOnClickListener(v -> showUpdateUserDialog());
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, REQUEST_IMAGE_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_GALLERY && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            if (imageUri != null) {
                storageManager.uploadImageFiles(user.getUid(), "profile-image", imageUri, new FirebaseStorageManager.OnImageUploadCompleteListener() {
                    @Override
                    public void onSuccess(String profileUrl) {
                        updateImageUrl(profileUrl);
                        loadImageFromFirebase(profileImage);
                        profileImage.setImageURI(imageUri);
                    }
                    @Override
                    public void onFailure(String errorMessage) {
                        Toast.makeText(WorkerProfile.this, "Failed to upload profile image: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
        if (imageUri == null) {
            imageUri = DEFAULT_IMAGE_URI;
            profileImage.setImageURI(imageUri);
        }
    }

    private void loadImageFromFirebase(ImageView imageView) {
        // Get download URL from Firebase Storage
        storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
            // Use Picasso to load the image into the ImageView
            Picasso.get()
                    .load(uri)
                    .resize(200, 200)
                    .centerCrop()
                    .placeholder(R.drawable.avatar)
                    .error(R.drawable.avatar)
                    .into(imageView);

        }).addOnFailureListener(exception -> {
            // Handle any errors
            exception.printStackTrace();
        });
    }

    private void updateImageUrl(String imageUrl){
        if (fAuth.getCurrentUser() != null) {
            String uid = fAuth.getCurrentUser().getUid();
            DocumentReference documentReference = db.collection("user").document(uid);

            // Create a map to update just the profileImageURL field
            Map<String, Object> updates = new HashMap<>();
            updates.put("profileImageURL", imageUrl);

            // Perform the update
            documentReference.update(updates)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Profile image URL updated successfully", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to update profile image URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("ClientProfileActivity", "Error updating profile image URL", e);
                    });
        } else {
            Toast.makeText(this, "No user is signed in", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkVerifications(){
        DocumentReference userDocRef = db.collection("users").document(uid);
        CollectionReference workerInfoRef = userDocRef.collection("workerInformation");

        workerInfoRef.get().addOnSuccessListener(querySnapshot -> {
            if (!querySnapshot.isEmpty()) {
                DocumentSnapshot workerDoc = querySnapshot.getDocuments().get(0);

                Boolean isNicVerified = workerDoc.getBoolean("isNicVerified");
                Boolean isBrVerified = workerDoc.getBoolean("isBrVerified");

                if (isBrVerified == null) {
                    verifyBr.setText("Pending");
                } else if (isBrVerified) {
                    verifyBr.setText("Verified");
                } else {
                    verifyBr.setText("Not Verified");
                }
            }
        }).addOnFailureListener(e ->
                Toast.makeText(this, "Failed to load worker information", Toast.LENGTH_SHORT).show()
        );
    }

    private void retrieveUserData() {
        readData.getUserFields(new ReadData.FirestoreUserDataCallback() {
            @Override
            public void onSuccess(Map<String, Object> userData) {
                if (userData != null) {
                    newName.setText(userData.getOrDefault("fullName", "No name available").toString());
                    newPhoneNumber.setText(userData.getOrDefault("phone", "No phone available").toString());
                    newAddressLine1.setText(userData.getOrDefault("address1", "No address available").toString());
                    newAddressLine2.setText(userData.getOrDefault("address2", "No address available").toString());
                    city.setText(userData.getOrDefault("city", "No city available").toString());
                    district.setText(userData.getOrDefault("district", "No district available").toString());
                    destription.setText(userData.getOrDefault("description", "No description available").toString());
                    String profileImageURL = userData.getOrDefault("profileImageURL", "No profile image available").toString();

                    if (!profileImageURL.isEmpty()) {
                        Picasso.get()
                                .load(profileImageURL)
                                .placeholder(R.drawable.avatar)
                                .error(R.drawable.avatar)
                                .into(profileImage);
                    } else {
                        profileImage.setImageResource(R.drawable.avatar);
                    }
                } else {
                    Toast.makeText(WorkerProfile.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(WorkerProfile.this, "Error retrieving user data: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
    private void loadDistricts() {
        readData.getDistricts(districts -> {
            districtAdapter.clear();
            districtAdapter.addAll(districts);
            districtAdapter.notifyDataSetChanged();
        }, e -> Log.e("Activity", "Failed to load districts", e));
    }
    private void setupDistrictSelectionListener(AutoCompleteTextView district, AutoCompleteTextView city) {
        district.setOnItemClickListener((parent, view, position, id) -> {
            String selectedDistrict = (String) parent.getItemAtPosition(position);
            loadCities(selectedDistrict);
            city.setEnabled(true);
        });
    }
    private void setupCityClickListener(AutoCompleteTextView city) {
        city.setOnClickListener(v -> {
            if (!city.isEnabled()) {
                Toast.makeText(this, "Please, select district first", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void loadCities(String district) {
        readData.getCities(district, cities -> {
            cityAdapter.clear();
            cityAdapter.addAll(cities);
            cityAdapter.notifyDataSetChanged();
        }, e -> Log.e("MainActivity", "Failed to load cities", e));
    }
    private void showUpdateUserDialog2() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.activity_edit_custom_dialog2, null);

        EditText editTextName = dialogView.findViewById(R.id.full_name);
        EditText editTextPhoneNumber = dialogView.findViewById(R.id.phone_number);
        EditText editTextAddressLine1 = dialogView.findViewById(R.id.address1);
        EditText editTextAddressLine2 = dialogView.findViewById(R.id.address2);
        EditText editTextDescription = dialogView.findViewById(R.id.descriptionxs);
        ImageButton brUpload = dialogView.findViewById(R.id.br_upload);
        AutoCompleteTextView editTextDistrict = dialogView.findViewById(R.id.district);
        AutoCompleteTextView editTextCity = dialogView.findViewById(R.id.city);

        districtAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, new ArrayList<>());
        editTextDistrict.setAdapter(districtAdapter);

        cityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, new ArrayList<>());
        editTextCity.setAdapter(cityAdapter);

        editTextCity.setEnabled(false);

        loadDistricts();

        setupDistrictSelectionListener(editTextDistrict, editTextCity);
        setupCityClickListener(editTextCity);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            String uid = auth.getCurrentUser().getUid();
            DocumentReference documentReference = db.collection("users").document(uid);

            documentReference.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    editTextName.setText(documentSnapshot.getString("fullName"));
                    editTextPhoneNumber.setText(documentSnapshot.getString("phone"));
                    editTextAddressLine1.setText(documentSnapshot.getString("addressLine1"));
                    editTextAddressLine2.setText(documentSnapshot.getString("addressLine2"));
                    editTextCity.setText(documentSnapshot.getString("city"));
                    editTextDistrict.setText(documentSnapshot.getString("district"));
                    editTextDescription.setText(documentSnapshot.getString("description"));
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(this, "Failed to load data", Toast.LENGTH_SHORT).show();
            });

            new AlertDialog.Builder(this)
                    .setTitle("Update User Information")
                    .setView(dialogView)
                    .setPositiveButton("Update", (dialog, which) -> {
                        String name = editTextName.getText().toString().trim();
                        String phoneNumber = editTextPhoneNumber.getText().toString().trim();
                        String addressLine1 = editTextAddressLine1.getText().toString().trim();
                        String addressLine2 = editTextAddressLine2.getText().toString().trim();
                        String city = editTextCity.getText().toString().trim();
                        String district = editTextDistrict.getText().toString().trim();
                        String description = editTextDescription.getText().toString().trim();

                        Map<String, Object> updatedUserData = new HashMap<>();
                        updatedUserData.put("fullName", name);
                        updatedUserData.put("phoneNumber", phoneNumber);
                        updatedUserData.put("addressLine1", addressLine1);
                        updatedUserData.put("addressLine2", addressLine2);
                        updatedUserData.put("city", city);
                        updatedUserData.put("district", district);
                        updatedUserData.put("description", description);

                        documentReference.update(updatedUserData)
                                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Data updated successfully", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e -> Toast.makeText(this, "Failed to update data", Toast.LENGTH_SHORT).show());
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .create()
                    .show();
        } else {
            Toast.makeText(this, "No user is signed in", Toast.LENGTH_SHORT).show();
        }
    }


    private void fetchreviewfromfirebase(CollectionReference collectionRef) {
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

    private void retrieveReviews(){
        CollectionReference collectionRef = documentReference1.collection("reviewsAsAWorker");

        List<ReviewModel> reviewList = new ArrayList<>();

        collectionRef.get().addOnSuccessListener(querySnapshot -> {
            if (!querySnapshot.isEmpty()) {
                // Loop through each document in the collection
                for (DocumentSnapshot document : querySnapshot) {
                    String rating = document.getString("rating");
                    String review = document.getString("review");

                    // Create a ReviewModel object and add it to the list
                    ReviewModel reviewModel = new ReviewModel(rating, review);
                    reviewList.add(reviewModel);
                }

                // Initialize the adapter with the list of reviews
                Reviewadapter reviewAdapter = new Reviewadapter(reviewList);

                // Find RecyclerView and set the adapter
                RecyclerView recyclerView = findViewById(R.id.recyclerView); // Ensure this ID matches your XML layout
                recyclerView.setLayoutManager(new LinearLayoutManager(this));// Set layout manager
                recyclerView.setAdapter(reviewAdapter); // Set the adapter

            } else {
                Toast.makeText(this, "No reviews available", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to load reviews", Toast.LENGTH_SHORT).show();
            Log.d("msg", "Error loading reviews: " + e.getMessage());
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

        public Reviewadapter(List < ReviewModel > List, Reviewadapter.OnItemClickListener listener) {
            this.List = List;
            this.listener = listener;
        }

        public Reviewadapter(List < ReviewModel > List) {
            this.List = List;
        }

        public Reviewadapter.ReviewadapterViewHolder onCreateViewHolder (ViewGroup parent, int viewType){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_list_item_layout3, parent, false);
            return new Reviewadapter.ReviewadapterViewHolder(view);
        }

        public void onBindViewHolder(Reviewadapter.ReviewadapterViewHolder holder,int position){
            ReviewModel reviewModel = List.get(position);
            holder.ratingBar.setRating(Float.parseFloat(reviewModel.getRating()));
            holder.ratingBar.setIsIndicator(true);

            holder.reviewItem.setText(reviewModel.getReview());
        }

        public int getItemCount () {
            return List.size();
        }

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