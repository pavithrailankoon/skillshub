package com.example.skillshub.firebaseModel;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.skillshub.ChangePassword;
import com.example.skillshub.clientHome;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

public class ReadData {
    private Context context;

    public ReadData(){
        this.context = context;
    }

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth auth = FirebaseAuth.getInstance();

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

    // Method to fetch all skills from Firestore
    public void getMainSkill(OnSuccessListener<List<String>> onSuccess, OnFailureListener onFailure) {
        db.collection("skills")
                .addSnapshotListener((queryDocumentSnapshots, error) -> {
                    if (error != null) {
                        // Call onFailure in case of an error
                        onFailure.onFailure(error);
                        return;
                    }

                    if (queryDocumentSnapshots != null) {
                        List<String> mainSkills = new ArrayList<>();
                        for (DocumentSnapshot document : queryDocumentSnapshots) {
                            mainSkills.add(document.getId()); // Each document ID represents a skill
                        }
                        // Call onSuccess with the updated list
                        onSuccess.onSuccess(mainSkills);
                    }
                });
    }


    // Method to fetch sub skills for a given skills from Firestore
    public void getSubSkill(String mainSkills, OnSuccessListener<List<String>> onSuccess, OnFailureListener onFailure) {
        db.collection("skills").document(mainSkills)
                .addSnapshotListener((documentSnapshot, error) -> {
                    if (error != null) {
                        // Handle error scenario
                        onFailure.onFailure(error);
                        return;
                    }

                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        // Retrieve subSkills array
                        List<String> subSkills = (List<String>) documentSnapshot.get("subCategories");

                        if (subSkills != null) {
                            onSuccess.onSuccess(subSkills);  // Pass the list of subSkills to the success listener
                        } else {
                            onFailure.onFailure(new Exception("No sub skills found for the main skill"));
                        }
                    } else {
                        // Document does not exist or is null
                        onFailure.onFailure(new Exception("Document does not exist"));
                    }
                });
    }


    // Method to retrieve job categories
    public void getSkillsList(final FirestoreSkillsCallback callback) {
        db.collection("users")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Set<String> uniqueCategories = new HashSet<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Check if the job_categories subcollection exists
                            document.getReference().collection("workerProfiles")
                                    .get()
                                    .addOnCompleteListener(categoryTask -> {
                                        if (categoryTask.isSuccessful()) {
                                            // Check if the result is not empty
                                            if (!categoryTask.getResult().isEmpty()) {
                                                for (QueryDocumentSnapshot categoryDoc : categoryTask.getResult()) {
                                                    String category = categoryDoc.getString("category_name");
                                                    if (category != null) {
                                                        uniqueCategories.add(category); // Add to Set to ensure uniqueness
                                                    }
                                                }
                                            }
                                        }
                                        // After iterating over all users, trigger callback with the unique categories
                                        callback.onSuccess(new ArrayList<>(uniqueCategories));
                                    });
                        }
                    }
                });
    }

    public interface FirestoreSkillsCallback {
        void onSuccess(List<String> uniqueCategories);
    }

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


    public void fetchSkillsRealtime(OnFirestoreDataListener listener) {
        db.collection("skills")
                .addSnapshotListener((querySnapshot, e) -> {
                    if (e != null) {
                        listener.onFailure(e.getMessage());  // Firestore listener failure
                        return;
                    }

                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        List<String> mainSkillsList = new ArrayList<>();
                        Map<String, List<String>> subSkillsMap = new HashMap<>();

                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            // Extract mainSkill (Document ID)
                            String mainSkill = document.getId();
                            mainSkillsList.add(mainSkill);  // Add to mainSkills list

                            // Extract subSkills (Assume it's an array field in Firestore)
                            List<String> subSkills = (List<String>) document.get("subSkills");
                            if (subSkills != null) {
                                subSkillsMap.put(mainSkill, subSkills);  // Map mainSkill -> subSkills
                            } else {
                                subSkillsMap.put(mainSkill, new ArrayList<>());  // Handle empty subSkills
                            }
                        }

                        // Notify the listener that data has been retrieved successfully
                        listener.onSuccess(mainSkillsList, subSkillsMap);
                    } else {
                        listener.onFailure("No skills found.");
                    }
                });
    }

    // Define a callback interface to handle success/failure
    public interface OnFirestoreDataListener {
        void onSuccess(List<String> mainSkillsList, Map<String, List<String>> subSkillsMap);
        void onFailure(String errorMessage);
    }

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

    // Method to retrieve all fields from the user's document
    public void getUserFields(FirestoreUserDataCallback callback) {
        if (auth.getCurrentUser() == null) {
            // If the user is not authenticated, return an error
            callback.onFailure(new Exception("User is not authenticated!"));
            return;
        }

        String uid = auth.getCurrentUser().getUid();  // Get the user ID (uid)
        DocumentReference docRef = db.collection("users").document(uid);

        // Get all fields from the document in a one-time read (no real-time updates)
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    Map<String, Object> userData = document.getData();
                    callback.onSuccess(userData);
                } else {
                    callback.onFailure(new Exception("Document not found for the user: " + uid));
                }
            } else {
                Exception taskException = task.getException();
                if (taskException != null) {
                    callback.onFailure(taskException);  // Pass the exception from Firestore
                } else {
                    callback.onFailure(new Exception("Unknown error occurred while fetching user data"));
                }
            }
        });
    }

    // Callback interface to handle Firestore operations results
    public interface FirestoreUserDataCallback {
        void onSuccess(Map<String, Object> userData);
        void onFailure(Exception e);
    }

}