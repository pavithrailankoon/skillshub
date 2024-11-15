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

    public Map<String, Object> createWorkerNicData(String nicFontURL, String nicBackURL, boolean isNicVerified, String brUrl, boolean isBrVerified) {
        Map<String, Object> verifyData = new HashMap<>();
        verifyData.put("nicFontURL", nicFontURL);
        verifyData.put("nicBackURL", nicBackURL);
        verifyData.put("isNicVerified", isNicVerified);
        verifyData.put("brURL", brUrl);
        verifyData.put("isBrVerified", isBrVerified);

        return verifyData;
    }

}
