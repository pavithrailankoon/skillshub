package com.example.skillshub.model;

import java.util.HashMap;
import java.util.Map;

public class User {

    // Helper method to create the user data map
    public Map<String, Object> createUserData(String fullName, String phone, String email, String nic, String profileUrl, String role, String address1, String address2, String district, String city) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("fullName", fullName);
        userData.put("phone", phone);
        userData.put("email", email);
        userData.put("nic", nic);
        userData.put("role", role);
        userData.put("profileImageURL", profileUrl);
        userData.put("address1", address1);
        userData.put("address2", address2);
        userData.put("district", district);
        userData.put("city", city);

        return userData;
    }

    public Map<String, Object> createWorkerNicData(String nicFontURL, String nicBackURL, boolean isNicVerified) {
        Map<String, Object> nicdata = new HashMap<>();
        nicdata.put("nicFontURL", nicFontURL);
        nicdata.put("nicBackURL", nicBackURL);
        nicdata.put("isNicVerified", isNicVerified);

        return nicdata;
    }

    public Map<String, Object> createWorkerBrData(String brURL, boolean isBrVerified) {
        Map<String, Object> brdata = new HashMap<>();
        brdata.put("brURL", brURL);
        brdata.put("isBrVerified", isBrVerified);

        return brdata;
    }

    public Map<String, Object> createWorkerData(Map<String, Object> nicVerification, Map<String, Object> brVerification) {
        Map<String, Object> workerdata = new HashMap<>();
        workerdata.put("BRVerification", nicVerification);
        workerdata.put("NICVerification", brVerification);

        return workerdata;
    }

}
