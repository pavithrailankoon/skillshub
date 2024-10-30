package com.example.skillshub.firebaseModel;

import android.content.Context;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.skillshub.ChangePassword;
import com.example.skillshub.clientHome;
import com.example.skillshub.model.Worker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class ReadData {
    private Context context;

    public ReadData(){
        this.context = context;
    }

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    ////////////////////////////////////////////////////////////////////////////////////
    // Method to fetch all districts from Firestore
    public void getDistricts(OnSuccessListener<List<String>> onSuccess, OnFailureListener onFailure) {
        db.collection("location").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<String> districts = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        districts.add(document.getId()); // Each document ID represents a district
                    }
                    onSuccess.onSuccess(districts);
                })
                .addOnFailureListener(onFailure);
    }

    // Method to fetch cities for a given district from Firestore
    public void getCities(String district, OnSuccessListener<List<String>> onSuccess, OnFailureListener onFailure) {
        db.collection("location").document(district).get()
                .addOnSuccessListener(documentSnapshot -> {
                    List<String> cities = (List<String>) documentSnapshot.get("cities"); // Assume cities field is an array
                    if (cities != null) {
                        onSuccess.onSuccess(cities);
                    } else {
                        onFailure.onFailure(new Exception("No cities found for the district"));
                    }
                })
                .addOnFailureListener(onFailure);
    }

    ////////////////////////////////////////////////////////////////////////////////////

    // Method to check if a NIC already exists in the users collection
    public void checkNicExists(String collection, String field, String value, final FirestoreNicCallback callback) {
        db.collection(collection)
                .whereEqualTo(field, value)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            // Handle the error
                            callback.onFailure(error);
                            return;
                        }
                        if (value != null && !value.isEmpty()) {
                            // NIC already exists
                            callback.onCallback(true);  // NIC found
                        } else {
                            // NIC is unique
                            callback.onCallback(false);  // NIC not found
                        }
                    }
                });
    }

    // Firestore callback interface
    public interface FirestoreNicCallback {
        void onCallback(boolean exists);
        void onFailure(Exception e);
    }

    ////////////////////////////////////////////////////////////////////////////////////

    public void fetchUniqueCategoryNames(final FirestoreCallback firestoreCallback) {
        // Create a HashSet to store unique category names
        Set<String> categoryNamesSet = new HashSet<>();

        // Reference the 'user' collection
        db.collection("user").addSnapshotListener((userSnapshot, error) -> {
            if (error != null) {
                // Handle error if there's a problem with fetching the users
                firestoreCallback.onFailure(error.getMessage());
                return;
            }

            // Clear the previous category names
            categoryNamesSet.clear();

            // Loop through all user documents
            if (userSnapshot != null) {
                for (QueryDocumentSnapshot userDoc : userSnapshot) {
                    String uid = userDoc.getId();

                    // Access workerProfiles sub-collection if it exists
                    db.collection("user").document(uid).collection("workerProfiles")
                            .addSnapshotListener((workerSnapshot, e) -> {
                                if (e != null) {
                                    // Handle error for this user
                                    return;
                                }

                                if (workerSnapshot != null && !workerSnapshot.isEmpty()) {
                                    for (QueryDocumentSnapshot workerDoc : workerSnapshot) {
                                        String categoryName = workerDoc.getId(); // Assuming categoryName is used as document ID
                                        categoryNamesSet.add(categoryName);
                                    }
                                }

                                // After processing, update the callback with unique category names
                                firestoreCallback.onSuccess(new ArrayList<>(categoryNamesSet));
                            });
                }
            }
        });
    }

    // Callback interface to handle Firestore data fetching asynchronously
    public interface FirestoreCallback {
        void onSuccess(ArrayList<String> categoryNames);
        void onFailure(String errorMessage);
    }

////////////////////////////////////////////////////////////////////////////////////


    public void fetchUniqueSubcategories(final String mainCategoryName, final FirestoreSubSkillCallback firestoreCallback) {
        // Create a HashSet to store unique subcategory names
        Set<String> subcategoryNamesSet = new HashSet<>();

        // Reference the 'user' collection
        db.collection("user").addSnapshotListener((userSnapshot, error) -> {
            if (error != null) {
                // Handle error if there's a problem with fetching the users
                firestoreCallback.onFailure(error.getMessage());
                return;
            }

            // Clear the previous subcategory names
            subcategoryNamesSet.clear();

            // Loop through all user documents
            if (userSnapshot != null) {
                for (QueryDocumentSnapshot userDoc : userSnapshot) {
                    String uid = userDoc.getId();

                    // Access workerProfiles sub-collection for the specific main category
                    db.collection("user").document(uid)
                            .collection("workerProfiles")
                            .document(mainCategoryName) // Access the document for the specific main category
                            .addSnapshotListener((workerSnapshot, e) -> {
                                if (e != null) {
                                    // Handle error for this user
                                    return;
                                }

                                if (workerSnapshot != null && workerSnapshot.exists()) {
                                    // Get the array field 'subcategories'
                                    List<String> subcategories = (List<String>) workerSnapshot.get("subcategories");
                                    if (subcategories != null) {
                                        // Add each subcategory to the HashSet
                                        subcategoryNamesSet.addAll(subcategories);
                                    }
                                }

                                // After processing, update the callback with unique subcategory names
                                firestoreCallback.onSuccess(new ArrayList<>(subcategoryNamesSet));
                            });
                }
            }
        });
    }

    // Callback interface to handle Firestore data fetching asynchronously
    public interface FirestoreSubSkillCallback {
        void onSuccess(ArrayList<String> subcategoryNames);
        void onFailure(String errorMessage);
    }

    ////////////////////////////////////////////////////////////////////////////////////

    // Method to retrieve all fields from the user's document
    public void getUserFields(FirestoreUserDataCallback callback) {
        if (auth.getCurrentUser() == null) {
            // If the user is not authenticated, return an error
            callback.onFailure(new Exception("User is not authenticated!"));
            return;
        }

        String uid = auth.getCurrentUser().getUid();  // Get the user ID (uid)
        DocumentReference docRef = db.collection("users").document(uid);

        // Use addSnapshotListener for real-time updates
        docRef.addSnapshotListener((documentSnapshot, e) -> {
            if (e != null) {
                // Handle the error
                callback.onFailure(e);
                return;
            }

            if (documentSnapshot != null && documentSnapshot.exists()) {
                // If the document exists, pass the data back to the callback
                Map<String, Object> userData = documentSnapshot.getData();
                callback.onSuccess(userData);
            } else {
                // Document does not exist or is null
                callback.onFailure(new Exception("Document not found for the user: " + uid));
            }
        });
    }

    // Callback interface to handle Firestore operations results
    public interface FirestoreUserDataCallback {
        void onSuccess(Map<String, Object> userData);
        void onFailure(Exception e);
    }

    ////////////////////////////////////////////////////////////////////////////////////

    public void getWorkersBySubcategory(String subcategory, FirestoreWorkerCallback callback) {
        db.collection("user")
                .get()
                .addOnSuccessListener(userDocuments -> {
                    List<Worker> workers = new ArrayList<>();
                    AtomicInteger workerCounter = new AtomicInteger(0);
                    int totalUsers = userDocuments.size();

                    if (totalUsers == 0) {
                        callback.onWorkerDataRetrieved(workers);
                        return;
                    }

                    for (DocumentSnapshot userDocument : userDocuments) {
                        String uid = userDocument.getId();

                        // Check workerProfiles sub-collection for the specified subcategory
                        db.collection("user").document(uid).collection("workerProfiles")
                                .whereArrayContains("subcategories", subcategory)
                                .get()
                                .addOnSuccessListener(queryDocumentSnapshots -> {
                                    if (!queryDocumentSnapshots.isEmpty()) {
                                        // If subcategory is found, retrieve main document data
                                        DocumentReference documentReference = db.collection("user").document(uid);
                                        documentReference.get().addOnSuccessListener(documentSnapshot -> {
                                            if (documentSnapshot.exists()) {
                                                Worker worker = new Worker(
                                                        uid,
                                                        documentSnapshot.getString("fullName"),
                                                        documentSnapshot.getString("district"),
                                                        documentSnapshot.getString("city"),
                                                        documentSnapshot.getString("profileImageURL")
                                                );

                                                // Calculate rating and add to workers list
                                                calculateWorkerRating(uid, worker, workers, callback, workerCounter, totalUsers);
                                            } else {
                                                Log.e("Firestore Error", "User document does not exist for UID: " + uid);
                                                if (workerCounter.incrementAndGet() == totalUsers) {
                                                    finalizeWorkerList(workers, callback);
                                                }
                                            }
                                        }).addOnFailureListener(e -> {
                                            Log.e("Firestore Error", "Failed to retrieve user document", e);
                                            if (workerCounter.incrementAndGet() == totalUsers) {
                                                finalizeWorkerList(workers, callback);
                                            }
                                        });
                                    } else if (workerCounter.incrementAndGet() == totalUsers) {
                                        // All users processed; invoke callback if no matching profiles found
                                        finalizeWorkerList(workers, callback);
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("Firestore Error", "Error getting worker profiles", e);
                                    if (workerCounter.incrementAndGet() == totalUsers) {
                                        finalizeWorkerList(workers, callback);
                                    }
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore Error", "Error getting user documents", e);
                    callback.onWorkerDataRetrieved(new ArrayList<>());
                });
    }


    ////////////////////////////////////////////////////////////////////////////////////

    private void calculateWorkerRating(String uid, Worker worker, List<Worker> workers, FirestoreWorkerCallback callback,
                                       AtomicInteger workerCounter, int totalUsers) {
        db.collection("user").document(uid).collection("reviewsAsAWorker")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    double totalRating = 0;
                    int reviewCount = queryDocumentSnapshots.size();

                    for (DocumentSnapshot review : queryDocumentSnapshots) {
                        Double rating = review.getDouble("reviewRate");
                        if (rating != null) {
                            totalRating += rating;
                        }
                    }

                    double averageRating = (reviewCount > 0) ? (totalRating / reviewCount) : 0;
                    worker.setAverageRating(averageRating);
                    workers.add(worker);

                    if (workerCounter.incrementAndGet() == totalUsers) {
                        finalizeWorkerList(workers, callback);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore Error", "Failed to get reviews", e);
                    if (workerCounter.incrementAndGet() == totalUsers) {
                        finalizeWorkerList(workers, callback);
                    }
                });
    }


    private void finalizeWorkerList(List<Worker> workers, FirestoreWorkerCallback callback) {
        workers.sort((w1, w2) -> Double.compare(w2.getAverageRating(), w1.getAverageRating()));
        callback.onWorkerDataRetrieved(workers);
    }


    public interface FirestoreWorkerCallback {
        void onWorkerDataRetrieved(List<Worker> workers);
    }


    ////////////////////////////////////////////////////////////////////////////////////
    public void getFilteredWorkers(String mainCategory, String subcategory, String district, String city, FirestoreWorkerCallback callback) {
        db.collection("user")
                .get()
                .addOnSuccessListener(userDocuments -> {
                    List<Worker> workers = new ArrayList<>();
                    AtomicInteger workerCounter = new AtomicInteger(0);
                    int totalUsers = userDocuments.size();

                    if (totalUsers == 0) {
                        callback.onWorkerDataRetrieved(workers);
                        return;
                    }

                    for (DocumentSnapshot userDocument : userDocuments) {
                        String uid = userDocument.getId();

                        // Check mainCategory and subcategory in workerProfiles sub-collection
                        db.collection("user").document(uid).collection("workerProfiles")
                                .whereEqualTo("mainCategory", mainCategory)
                                .whereArrayContains("subcategories", subcategory)
                                .get()
                                .addOnSuccessListener(queryDocumentSnapshots -> {
                                    if (!queryDocumentSnapshots.isEmpty()) {
                                        // Retrieve district and city from the main document
                                        DocumentReference documentReference = db.collection("user").document(uid);
                                        documentReference.get().addOnSuccessListener(documentSnapshot -> {
                                            if (documentSnapshot.exists()) {
                                                String workerDistrict = documentSnapshot.getString("district");
                                                String workerCity = documentSnapshot.getString("city");

                                                // Check district and city
                                                if (district.equals(workerDistrict) && city.equals(workerCity)) {
                                                    Worker worker = new Worker(
                                                            uid,
                                                            documentSnapshot.getString("fullName"),
                                                            workerDistrict,
                                                            workerCity,
                                                            documentSnapshot.getString("profileImageURL")
                                                    );

                                                    // Calculate rating and add to workers list
                                                    calculateWorkerRating(uid, worker, workers, callback, workerCounter, totalUsers);
                                                } else {
                                                    // Increment counter if no match on district and city
                                                    if (workerCounter.incrementAndGet() == totalUsers) {
                                                        finalizeWorkerList(workers, callback);
                                                    }
                                                }
                                            } else {
                                                Log.e("Firestore Error", "User document does not exist for UID: " + uid);
                                                if (workerCounter.incrementAndGet() == totalUsers) {
                                                    finalizeWorkerList(workers, callback);
                                                }
                                            }
                                        }).addOnFailureListener(e -> {
                                            Log.e("Firestore Error", "Failed to retrieve user document", e);
                                            if (workerCounter.incrementAndGet() == totalUsers) {
                                                finalizeWorkerList(workers, callback);
                                            }
                                        });
                                    } else if (workerCounter.incrementAndGet() == totalUsers) {
                                        // All users processed; invoke callback if no matching profiles found
                                        finalizeWorkerList(workers, callback);
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("Firestore Error", "Error getting worker profiles", e);
                                    if (workerCounter.incrementAndGet() == totalUsers) {
                                        finalizeWorkerList(workers, callback);
                                    }
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore Error", "Error getting user documents", e);
                    callback.onWorkerDataRetrieved(new ArrayList<>());
                });
    }


    ////////////////////////////////////////////////////////////////////////////////////

    public void getMainCategories(Context context, MainCategoryCallback callback) {
        CollectionReference skillCollection = db.collection("skills");
        List<String> mainCategories = new ArrayList<>();

        skillCollection.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    mainCategories.add(document.getId()); // Assuming document ID is the main category name
                }
                callback.onCallback(mainCategories);
            } else {
                Toast.makeText(context, "Failed to load categories", Toast.LENGTH_SHORT).show();
            }
        });
    }

    ////////////////////////////////////////////////////////////////////////////////////

    public void loadSubcategories(String mainCategory, LinearLayout checkboxContainer, Context context) {
        CollectionReference skillCollection = db.collection("skills");
        checkboxContainer.removeAllViews();

        skillCollection.document(mainCategory).get().addOnSuccessListener(document -> {
            if (document.exists()) {
                List<String> subcategories = (List<String>) document.get("subCategories");
                if (subcategories != null) {
                    ArrayList<CheckBox> checkBoxList = new ArrayList<>();

                    for (String subcategory : subcategories) {
                        CheckBox checkBox = new CheckBox(context);
                        checkBox.setText(subcategory);
                        checkBoxList.add(checkBox);
                        checkboxContainer.addView(checkBox);

                        // Allow only 3 subcategories to be selected
                        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                            long selectedCount = checkBoxList.stream().filter(CheckBox::isChecked).count();
                            if (selectedCount > 3) {
                                buttonView.setChecked(false);
                                Toast.makeText(context, "You can select only 3 subcategories", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            } else {
                Toast.makeText(context, "No subcategories available for " + mainCategory, Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(context, "Failed to load subcategories", Toast.LENGTH_SHORT).show();
        });
    }

    // Callback interface to handle main category retrieval
    public interface MainCategoryCallback {
        void onCallback(List<String> mainCategories);
    }

    ////////////////////////////////////////////////////////////////////////////////////
}