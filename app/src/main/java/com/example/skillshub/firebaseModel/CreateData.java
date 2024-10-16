package com.example.skillshub.firebaseModel;

import android.net.Uri;

import com.google.firebase.firestore.FirebaseFirestore;

public class CreateData {
    private String firstName;
    private String lastName;
    private String phone;
    private String password;
    private Uri profileUri;
    private String email;
    private String nic;
    private String adderss1;
    private String adderss2;
    private String district;
    private String city;
    private FirebaseFirestore db;

    public CreateData(String email, String password){
        this.email=email;
        this.password=password;
    }

    public CreateData(String firstName, String lastName, String email, String phone,  Uri profileUri, String nic, String adderss1, String adderss2, String district, String city){
        this.firstName=firstName;
        this.lastName=lastName;
        this.email=email;
        this.phone=phone;
        this.profileUri=profileUri;
        this.nic=nic;
        this.adderss1=adderss1;
        this.adderss2=adderss2;
        this.district=district;
        this.city=city;
    }



    //Method to add user data to firestore
    private void addUserData(){

    }
}
