package com.example.skillshub;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.ComponentActivity;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;

import com.example.skillshub.firebaseModel.ReadData;
import com.example.skillshub.model.CustomDialog;

import org.w3c.dom.Text;

import java.util.Map;


public class ClientProfileActivity extends AppCompatActivity implements CustomDialog.CustomDialogInterface{

    ImageView backBtn;
    Button logOut,editDetails,editPassowrd;
    TextView newName,newPhoneNumber,newAddressLine1,newAddressLine2;
    private ReadData readData;

    private static final int REQUEST_IMAGE_GALLERY = 1;
    private static final int REQUEST_IMAGE_CAMERA = 2;
    private Uri imageUri;
    private Uri DEFAULT_IMAGE_URI;

    ChangePassword changePassword = new ChangePassword();

    public void openDialog(View view){
        CustomDialog customDialog = new CustomDialog();
        customDialog .show(getSupportFragmentManager(),"Test Customdialog");

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_client_profile);

        readData = new ReadData();

        backBtn = findViewById(R.id.backBtn);
        logOut = findViewById(R.id.logOut);
        editDetails = findViewById(R.id.editDetailsBtn);
        editPassowrd = findViewById(R.id.editPassword);

        newName = findViewById(R.id.name);
        newPhoneNumber = findViewById(R.id.phoneNumber);
        newAddressLine1 = findViewById(R.id.addressLine1);
        newAddressLine2 = findViewById(R.id.addressLine2);

        readData.getUserFieldRealtime("fullName", new ReadData.FirestoreUserDataCallback() {
            @Override
            public void onSuccess(Object fieldValue) {
                String fulName = fieldValue.toString();
                newName.setText(fulName);
            }

            @Override
            public void onError(String errorMessage) {
                System.err.println("Error: " + errorMessage);
            }
        });

        readData.getUserFieldRealtime("phone", new ReadData.FirestoreUserDataCallback() {
            @Override
            public void onSuccess(Object fieldValue) {
                String phoneNumber = fieldValue.toString();
                newPhoneNumber.setText(phoneNumber);
            }

            @Override
            public void onError(String errorMessage) {
                System.err.println("Error: " + errorMessage);
            }
        });

        readData.getUserFieldRealtime("location", new ReadData.FirestoreUserDataCallback() {
            @Override
            public void onSuccess(Object fieldValue) {
                if (fieldValue instanceof Map) {
                    // Cast the Object to a Map<String, Object>
                    Map<String, Object> mapValue = (Map<String, Object>) fieldValue;

                    // Example: Access a specific value by key
                    Object addressline1 = mapValue.get("addressLine1");
                    Object addressline2 = mapValue.get("addressLine2");
                    if (addressline1 != null) {
                        newAddressLine1.setText((CharSequence) addressline1);
                    } else if (addressline2 != null){
                        newAddressLine2.setText((CharSequence) addressline2);
                    } else {
                        System.out.println("Field does not exist in the map");
                    }

                } else {
                    System.out.println("Field is not a map.");
                }
            }

            @Override
            public void onError(String errorMessage) {
                System.err.println("Error: " + errorMessage);
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
            }
        });

        editPassowrd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),ChangePassword.class);
                startActivity(intent);
            }
        });


    }


    @Override
    public void applyTexts(String name, String phoneNumber,String addressLine1,String addressLine2) {
        newName.setText(name);
        newPhoneNumber.setText(phoneNumber);
        newAddressLine1.setText(addressLine1);
        newAddressLine2.setText(addressLine2);


    }
}